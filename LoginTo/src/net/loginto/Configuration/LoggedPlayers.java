package net.loginto.Configuration;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class LoggedPlayers {

    public static List<Player> loggedPlayers = new ArrayList<>();

    public static void addPlayer(Player player) {
        loggedPlayers.add(player);
    }

    public static void removePlayer(Player player) {
        loggedPlayers.remove(player);
    }

    public static boolean isPlayerLogged(Player player) {
        return loggedPlayers.contains(player);
    }
}
