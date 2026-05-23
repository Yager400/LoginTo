/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Commands;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import net.loginto.bukkit.Storage.Database;
import net.loginto.bukkit.Utils.Files.ConfigKeys;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import net.loginto.bukkit.Utils.Files.MessageKeys;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OTP implements CommandExecutor {

    private final Plugin plugin;
    private final Database database;

    private final List<Player> players = new ArrayList<>();

    public OTP(Plugin plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Not a player");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("loginto.otp")) {
            sender.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.ERRORS_GENERAL_NO_PERMISSION.path(), player, plugin));
            return true;
        }

        if (!database.isPlayerPresentInDB(player.getName())) {
            sender.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.OTP_ERROR_REQUEST_WITHOUT_ACCOUNT.path(), player, plugin));
            return true;
        }

        String possibleSecret = database.getSecret(player.getName());

        if (possibleSecret != null && !possibleSecret.isEmpty()) {
            sender.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.OTP_ERROR_ALREADY_CREATED.path(), player, plugin));
            return true;
        }

        if (!players.contains(player)) {
            sender.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.OTP_ALERT.path(), player, plugin));
            players.add(player);
            return true;
        }
        GoogleAuthenticator auth = new GoogleAuthenticator();

        GoogleAuthenticatorKey key = auth.createCredentials();
        String secret = key.getKey();


        String otpUrl = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(
                player.getName(),
                LoginToFiles.Config.getString(ConfigKeys.OTP_CONFIG_SERVER_NAME.path(), plugin),
                key
        );


        database.setSecret(player.getName(), secret);

        BitMatrix matrix;
        try {
            matrix = new MultiFormatWriter().encode(otpUrl, BarcodeFormat.QR_CODE, 100, 100);
        } catch (WriterException e) {
            plugin.getLogger().severe(e.getMessage());
            return false;
        }

        WorldCreator creator = getWorldCreator(player, matrix);

        World world = creator.createWorld();
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);

        world.getBlockAt(0, 59, 0).setType(Material.BARRIER);

        Location loc = new Location(world, 49.5, 60, 49.5);
        loc.setYaw(180f);
        loc.setPitch(90f);

        if (player.isInsideVehicle()) {
            player.leaveVehicle();
        }

        player.teleport(loc);


        players.remove(player);

        return true;
    }

    private WorldCreator getWorldCreator(Player player, BitMatrix matrix) {
        WorldCreator creator = new WorldCreator(player.getName() + "-qrcode");
        int size = matrix.getWidth();

        creator.generator(new ChunkGenerator() {
            @Override
            public ChunkData generateChunkData(World world, Random random, int cx, int cz, BiomeGrid biome) {
                ChunkData data = createChunkData(world);
                int chunkStartX = cx * 16;
                int chunkStartZ = cz * 16;

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        int worldX = chunkStartX + x;
                        int worldZ = chunkStartZ + z;

                        if (worldX >= 0 && worldZ >= 0 && worldX < size && worldZ < size) {
                            Material mat = matrix.get(worldX, worldZ) ? Material.BLACK_CONCRETE : Material.WHITE_CONCRETE;
                            data.setBlock(x, 0, z, mat);
                        }
                    }
                }
                return data;
            }

            @Override
            public boolean canSpawn(World world, int x, int z) {
                return true;
            }
        });

        creator.type(WorldType.FLAT);

        return creator;
    }

}
