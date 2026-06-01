/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils.Premium.bukkit;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketHandler;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.crypto.MinecraftEncryptionUtil;
import com.github.retrooper.packetevents.util.crypto.SignatureData;
import com.github.retrooper.packetevents.wrapper.login.client.WrapperLoginClientEncryptionResponse;
import com.github.retrooper.packetevents.wrapper.login.client.WrapperLoginClientLoginStart;
import com.github.retrooper.packetevents.wrapper.login.server.WrapperLoginServerEncryptionRequest;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class PacketEventListener implements PacketListener {

    private final Map<User, loginUser> onLoginUsers = new HashMap<>();

    private final Plugin plugin;
    private final PremiumCache cacheDB;

    public PacketEventListener(Plugin plugin) {
        this.plugin = plugin;

        this.cacheDB = new PremiumCache(plugin);
    }

    @PacketHandler()
    public void onPacketReceive(PacketReceiveEvent event) {

        PacketTypeCommon packetType = event.getPacketType();

        User user = event.getUser();

        if (packetType == PacketType.Login.Client.LOGIN_START) {
            WrapperLoginClientLoginStart packetLoginStart = new WrapperLoginClientLoginStart(event);

            if (Bukkit.getPluginManager().getPlugin("floodgate") != null) {

                boolean success = FloodgateUtils.bedrockHandler(event, plugin);

                if (success) {
                    return;
                }

            }
            event.setCancelled(true);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

                String pName = packetLoginStart.getUsername();
                UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + pName).getBytes(StandardCharsets.UTF_8));

                //Execute an http request to mojang servers for knowing if the players if premium or not, returns the response code code
                int mojangNameApiResCode = ProtocolUtils.isUserNamePremium(pName, plugin, cacheDB);

                //Check if mojang is rate limiting this ip
                if (mojangNameApiResCode == 429) {
                    user.closeConnection();
                    plugin.getLogger().severe(String.format("IMPORTANT: Mojang is limiting your server's ip, the server won't know if the username is premium or not, the player %s disconnected", pName));
                    return;
                }
                //If the res code is not 429 or 200, the player is cracked.
                if (mojangNameApiResCode != 200) {
                    plugin.getLogger().info("Cracked");
                    //Run this in sync mode to avoid problems
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        ProtocolUtils.authenticatedPlayer.put(offlineUUID, new AuthenticatedPlayer(
                                offlineUUID,
                                false,
                                false
                        ));
                        ProtocolUtils.receiveFakeStartPacket(pName, null, event.getChannel(), offlineUUID);
                    });
                    return;
                }

                try {
                    KeyPair pair = EncryptionUtils.generateRSAPair();
                    byte[] verifyToken = EncryptionUtils.generateVerifyToken();
                    Optional<SignatureData> signDataOpt = packetLoginStart.getSignatureData();
                    SignatureData signData;
                    signData = signDataOpt.orElse(null);

                    onLoginUsers.put(user, new loginUser(
                            pName,
                            offlineUUID,
                            pair,
                            verifyToken,
                            signData
                    ));

                    WrapperLoginServerEncryptionRequest packetEncReq = new WrapperLoginServerEncryptionRequest(
                            "",
                            pair.getPublic(),
                            verifyToken
                    );

                    PacketEvents.getAPI().getProtocolManager().sendPacket(event.getChannel(), packetEncReq);

                    // Map cleaning, this is used because, if the player is cracked, he will get disconnected at this point
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        onLoginUsers.remove(user);
                    }, 200);

                } catch (NoSuchAlgorithmException e) {
                    user.closeConnection();
                    onLoginUsers.remove(user);
                    throw new RuntimeException(e);
                }
            });
        }

        if (packetType == PacketType.Login.Client.ENCRYPTION_RESPONSE) {
            event.setCancelled(true);
            WrapperLoginClientEncryptionResponse packetResponse = new WrapperLoginClientEncryptionResponse(event);
            byte[] encSharedSecret = packetResponse.getEncryptedSharedSecret();
            byte[] encVerifyToken = packetResponse.getEncryptedVerifyToken().orElse(null);
            loginUser userTarget = onLoginUsers.get(user);
            byte[] sharedSecret = MinecraftEncryptionUtil.decryptRSA(userTarget.pair.getPrivate(), encSharedSecret);
            byte[] verifyToken = null;
            if (encVerifyToken != null) {
                verifyToken = MinecraftEncryptionUtil.decryptRSA(userTarget.pair.getPrivate(), encVerifyToken);
            }

            if (user.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_19_3)) {
                if (!Arrays.equals(verifyToken, userTarget.verifyToken)) {
                    plugin.getLogger().severe("Different verify token");
                    user.closeConnection();
                    return;
                }
            }

            try {
                if (!ProtocolUtils.enableEncryption(sharedSecret, event.getChannel())) {
                    plugin.getLogger().severe("Error enabling encryption");
                    user.closeConnection();
                    onLoginUsers.remove(user);
                    return;
                }
            } catch (Exception e) {
                user.closeConnection();
                onLoginUsers.remove(user);
                throw new RuntimeException(e);
            }

            String serverHash = ProtocolUtils.getServerIdHashString("", sharedSecret, userTarget.pair.getPublic());
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    String sessionURL = String.format(
                            "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=%s&serverId=%s",
                            userTarget.name,
                            serverHash
                    );
                    URL url = URI.create(sessionURL).toURL();
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    int status = connection.getResponseCode();
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (status != 200) {
                            /*
                            plugin.getLogger().info("Cracked");
                            ProtocolUtils.authenticatedPlayer.put(userTarget.uuid, new AuthenticatedPlayer(
                                    userTarget.uuid,
                                    false,
                                    false
                            ));
                             */
                            //Instead of letting the player connect as cracked, they shouldn't be able to use this username,
                             //so the connection is closed
                            plugin.getLogger().warning(String.format("The user %s tried to join the server with a premium username, but his instance is cracked", userTarget.name));
                            user.closeConnection();
                            return;
                        } else {
                            plugin.getLogger().info("Premium");
                            ProtocolUtils.authenticatedPlayer.put(userTarget.uuid, new AuthenticatedPlayer(
                                    userTarget.uuid,
                                    true,
                                    false
                            ));
                        }

                        ProtocolUtils.receiveFakeStartPacket(userTarget.name, userTarget.signData, event.getChannel(), userTarget.uuid);
                        onLoginUsers.remove(user);

                        //Clean up if the player disconnect after the server verified them
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            ProtocolUtils.authenticatedPlayer.remove(userTarget.uuid);
                        }, 200);
                    });
                } catch (IOException e) {
                    user.closeConnection();
                    onLoginUsers.remove(user);
                    throw new RuntimeException(e);
                }
            });
        }

    }


    /**
     * Class for storing temporary the user that is trying to authenticate<br>
     */
    private class loginUser {
        String name;
        UUID uuid;
        KeyPair pair;
        byte[] verifyToken;
        SignatureData signData;

        /**
         * Disclaimer: Do not initialize this class with the generi packet data, first wrap that packet using the Wrapper
         *
         * @param name        The player's username
         * @param uuid        The player's uuid
         * @param pair        The player's RSA key pair (used for sharing the secret)
         * @param verifyToken Unique 4 byte verification token that will be sent to the player for encryption
         */
        public loginUser(String name, UUID uuid, KeyPair pair, byte[] verifyToken, SignatureData signData) {
            this.name = name;
            this.uuid = uuid;
            this.pair = pair;
            this.verifyToken = verifyToken;
            this.signData = signData;
        }
    }


}