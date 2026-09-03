/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit.playerutils;

import com.github.yager400.loginto.folia.FoliaLib;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class OTPCodeMapUtils {

    public static GoogleAuthenticatorKey getRandomKey() {
        GoogleAuthenticator auth = new GoogleAuthenticator();

        return auth.createCredentials();
    }

    public static BitMatrix getBitMatrix(String playerName, String serverName, GoogleAuthenticatorKey key) {
        String otpUrl = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(
                playerName,
                serverName,
                key
        );

        try {
            return new MultiFormatWriter().encode(otpUrl, BarcodeFormat.QR_CODE, 128, 128);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void handleMapCreationAndDeletion(BitMatrix matrix, Player player) {
        MapView map = Bukkit.createMap(player.getWorld());
        MapRenderer rendered = new Render(matrix);
        map.getRenderers().clear();
        map.addRenderer(rendered);

        ItemStack item = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) item.getItemMeta();
        assert meta != null;
        meta.setMapId(map.getId());
        item.setItemMeta(meta);

        FoliaLib.get().addItemToInventory(player, item);
        FoliaLib.get().runTaskLater( () -> {
            map.getRenderers().clear();
            map.removeRenderer(rendered);
        }, 1200L); // Clear the map render after 60 seconds
    }

    public static class Render extends MapRenderer {

        private final BitMatrix matrix;

        public Render(BitMatrix matrix) {
            this.matrix = matrix;
        }

        @Override
        public void render(MapView map, MapCanvas canvas, Player player) {
            for (int x = 0; x < 128; x++) {
                for (int y = 0; y < 128; y++) {
                    boolean black = matrix.get(x, y);
                    canvas.setPixel(
                            x,
                            y,
                            black ? MapPalette.DARK_GRAY : MapPalette.WHITE
                    );
                }
            }
        }

    }


}
