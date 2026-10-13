/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit.playerutils.kyorihandlers;

import org.bukkit.entity.Player;

public interface KyoriHandler {
    void sendMessage(Player player, String message);
    void sendConsole(String message);
}
