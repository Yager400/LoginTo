/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils.Premium.bukkit;

import java.util.UUID;

public class AuthenticatedPlayer {

    public final UUID playerUUID;
    public final boolean isPremium;
    public final boolean isBedrock;

    public AuthenticatedPlayer(UUID playerUUID, boolean isPremium, boolean isBedrock) {
        this.playerUUID = playerUUID;
        this.isPremium = isPremium;
        this.isBedrock = isBedrock;
    }
}
