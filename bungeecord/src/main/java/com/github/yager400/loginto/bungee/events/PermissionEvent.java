/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bungee.events;

import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PermissionEvent implements Listener {

    @EventHandler
    public void onPermissionCheck(PermissionCheckEvent event) {
        String perm = event.getPermission();

        switch (perm) {
            case "loginto.register":
            case "loginto.login":
            case "loginto.changepassword":
            case "loginto.cracked.me":
            case "loginto.premium.me":
                event.setHasPermission(true);
                break;
        }
    }

}
