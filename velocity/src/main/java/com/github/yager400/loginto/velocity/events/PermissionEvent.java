/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.velocity.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.Tristate;

public class PermissionEvent {

    @Subscribe
    public void onPermissionCheck(PermissionsSetupEvent event) {
        PermissionFunction luckPermsFunction = event.getProvider()
                .createFunction(event.getSubject());

        event.setProvider(subject -> permission -> {
            Tristate lpResult = luckPermsFunction.getPermissionValue(permission);
            return switch (permission) {
                case "loginto.register", "loginto.login", "loginto.changepassword", "loginto.cracked.me",
                     "loginto.premium.me" -> {
                    if (lpResult != Tristate.UNDEFINED) {
                        yield lpResult;
                    }
                    yield Tristate.TRUE;
                }
                case "loginto.unregister", "loginto.cracked.other", "loginto.premium.other", "loginto.loginto" -> {
                    if (lpResult != Tristate.UNDEFINED) {
                        yield lpResult;
                    }
                    yield Tristate.FALSE;
                }
                default -> Tristate.UNDEFINED;
            };
        });
    }

}
