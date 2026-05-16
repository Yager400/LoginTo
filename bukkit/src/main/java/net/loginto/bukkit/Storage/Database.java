/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Storage;

public interface Database {
    boolean isPlayerPresentInDB(String playerName);

    boolean isPasswordCorrect(String playerName, String password) throws Exception;

    boolean insertPlayer(String playerName, String password) throws Exception;

    void changePlayerPassword(String playerName, String newPassword) throws Exception;

    boolean removePlayer(String playerName);

    String getSecret(String playerName);

    void setSecret(String playerName, String secret);

    void close();

    void connect();
}
