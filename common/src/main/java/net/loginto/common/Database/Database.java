/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.common.Database;

public interface Database {
    boolean isPlayerPresentInDB(String playerName);

    boolean isPasswordCorrect(String playerName, String password) throws Exception;

    boolean insertPlayer(String playerName, String password) throws Exception;

    void changePlayerPassword(String playerName, String newPassword) throws Exception;

    boolean canBypassLogin(String playerName);

    boolean isPremium(String playerName);

    boolean removePlayer(String playerName);

    void chanceAccStatusToPremium(String playerName, boolean isPremium);

    void chanceAccStatusToBedrock(String playerName, boolean isBedrock);

    void close();

    void connect(String host, int port, String password, String username, String databaseName);

    boolean ping();

    void setSecret(String playerName, String secret);

    String getSecret(String playerName);
}
