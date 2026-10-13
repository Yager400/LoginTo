/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit.playerutils.kyorihandlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

/**
 * Handler for paper > V1.18.2 with native support for minimessage
 */
public class NativeKyoriHandler implements KyoriHandler {

    // Replace {} with a dot (.) so the relocation won't relocate those classes

    public final Object miniMessage;
    private static final String miniMessageClass =         "net{}kyori{}adventure{}text{}minimessage{}MiniMessage"         .replace("{}", ".");
    private static final String componentSerializerClass = "net{}kyori{}adventure{}text{}serializer{}ComponentSerializer"  .replace("{}", ".");
    private static final String componentClass =           "net{}kyori{}adventure{}text{}Component"                        .replace("{}", ".");

    public NativeKyoriHandler() throws Exception {
        Method method = Class.forName(miniMessageClass)
                .getMethod("miniMessage");
        this.miniMessage = method.invoke(null);
    }

    public Object getMessageAsComponent(String message) {
        try {
            Method method = Class.forName(componentSerializerClass)
                    .getMethod("deserialize", Object.class);
            return method.invoke(this.miniMessage, message);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void sendMessage(Player player, String message) {
        try {
            Method method = Player.class.getMethod("sendMessage", Class.forName(componentClass));
            method.invoke(player, this.getMessageAsComponent(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendConsole(String message) {
        try {
            Method method = Bukkit.getServer().getConsoleSender().getClass().getMethod("sendMessage", Class.forName(componentClass));
            method.invoke(Bukkit.getServer().getConsoleSender(), this.getMessageAsComponent(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
