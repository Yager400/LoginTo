/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Storage;

import org.bukkit.entity.Player;

public interface Database {
    boolean isPlayerPresentInDB(Player player);
    boolean isPasswordCorrect(Player player, String password) throws Exception;
    boolean insertPlayer(Player player, String password) throws Exception;
    String changePlayerPassword(Player player, String oldPassword, String newPassword) throws Exception;
    boolean removePlayer(Player player);
    void close();
    void connect();
}
