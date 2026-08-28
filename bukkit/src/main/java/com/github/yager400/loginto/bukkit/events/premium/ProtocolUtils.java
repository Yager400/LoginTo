/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit.events.premium;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.crypto.SignatureData;
import com.github.retrooper.packetevents.util.reflection.ReflectionObject;
import com.github.retrooper.packetevents.wrapper.login.client.WrapperLoginClientLoginStart;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import io.netty.channel.Channel;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.List;
import java.util.UUID;

public class ProtocolUtils {

    protected static String getServerIdHashString(String serverId, byte[] sharedSecret, PublicKey publicKey) {
        byte[] serverHash = getServerIdHash(serverId, publicKey, sharedSecret);
        return (new BigInteger(serverHash)).toString(16);
    }

    protected static byte[] getServerIdHash(String sessionId, PublicKey publicKey, byte[] sharedSecret) {
        Hasher hasher = Hashing.sha1().newHasher();

        hasher.putBytes(sessionId.getBytes(StandardCharsets.ISO_8859_1));
        hasher.putBytes(sharedSecret);
        hasher.putBytes(publicKey.getEncoded());

        return hasher.hash().asBytes();
    }

    protected static void receiveFakeStartPacket(String username, SignatureData data, Object channel, UUID uuid, User user) {
        final ClientVersion clientVersion = user.getClientVersion();

        WrapperLoginClientLoginStart startPacket = new WrapperLoginClientLoginStart(clientVersion, username, data, uuid);
        PacketEvents.getAPI().getProtocolManager().receivePacketSilently(channel, startPacket);
    }

    protected static boolean enableEncryption(byte[] sharedSecret, Object channel) {
        try {
            SecretKey secretKey = new SecretKeySpec(sharedSecret, "AES");
            IvParameterSpec iv = new IvParameterSpec(sharedSecret);

            Cipher decrypt = Cipher.getInstance("AES/CFB8/NoPadding");
            decrypt.init(Cipher.DECRYPT_MODE, secretKey, iv);

            Cipher encrypt = Cipher.getInstance("AES/CFB8/NoPadding");
            encrypt.init(Cipher.ENCRYPT_MODE, secretKey, iv);

            Channel nettyChannel = (Channel) channel;
            nettyChannel.pipeline().addBefore("splitter", "decrypt", new EncryptionUtils.CipherDecoder(decrypt));
            nettyChannel.pipeline().addBefore("prepender", "encrypt", new EncryptionUtils.CipherEncoder(encrypt));

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    protected static Channel getChannel(Object networkManager) {
        ReflectionObject wrapper = new ReflectionObject(networkManager, SpigotReflectionUtil.NETWORK_MANAGER_CLASS);
        return (Channel) wrapper.readObject(0, SpigotReflectionUtil.CHANNEL_CLASS);
    }

    protected static Object findNetworkManager(Object channel) {
        List<Object> managers = SpigotReflectionUtil.getNetworkManagers();
        for (Object manager : managers) {
            Channel managerChannel = getChannel(manager);
            if (managerChannel.remoteAddress().equals(((Channel) channel).remoteAddress())) {
                return manager;
            }
        }
        return null;
    }
}
