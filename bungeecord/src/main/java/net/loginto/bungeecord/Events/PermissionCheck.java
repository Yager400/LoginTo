/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Events;

import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class PermissionCheck implements Listener {

    private final Plugin plugin;

    public PermissionCheck(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPermsCheck(PermissionCheckEvent event) {

        String perm = event.getPermission();

        System.out.println(perm);

        switch (perm) {
            case "loginto.changepassword":
            case "loginto.cracked":
            case "loginto.login":
            case "loginto.premium":
            case "loginto.register":
                event.setHasPermission(true);
                break;
        }
    }

}
