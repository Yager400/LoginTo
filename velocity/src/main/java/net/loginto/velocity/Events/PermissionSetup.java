/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.velocity.Events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.Tristate;

public class PermissionSetup {

    @Subscribe
    public void onPermissionsSetup(PermissionsSetupEvent event) {
        PermissionFunction luckPermsFunction = event.getProvider()
                .createFunction(event.getSubject());

        event.setProvider(subject -> permission -> {
            switch (permission) {
                case "loginto.login":
                case "loginto.register":
                case "loginto.premium":
                case "loginto.cracked":
                case "loginto.otp":
                case "loginto.changepassword":
                    return Tristate.TRUE;

                case "loginto.unregister":
                    Tristate lpResult = luckPermsFunction.getPermissionValue(permission);
                    if (lpResult != Tristate.UNDEFINED) {
                        return lpResult;
                    }
                    return Tristate.FALSE;

                default:
                    return Tristate.UNDEFINED;
            }
        });
    }
}
