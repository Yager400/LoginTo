/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit.playerutils;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.dialog.*;
import com.github.retrooper.packetevents.protocol.dialog.action.DynamicCustomAction;
import com.github.retrooper.packetevents.protocol.dialog.body.*;
import com.github.retrooper.packetevents.protocol.dialog.button.ActionButton;
import com.github.retrooper.packetevents.protocol.dialog.button.CommonButtonData;
import com.github.retrooper.packetevents.protocol.dialog.input.Input;
import com.github.retrooper.packetevents.protocol.dialog.input.TextInputControl;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.wrapper.common.server.WrapperCommonServerShowDialog;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCustomClickAction;
import com.github.yager400.loginto.bukkit.LoginTo;
import com.github.yager400.loginto.bukkit.commands.LoginCommand;
import com.github.yager400.loginto.bukkit.commands.RegisterCommand;
import com.github.yager400.loginto.bukkit.fileskeys.ConfigKeys;
import com.github.yager400.loginto.bukkit.fileskeys.DialogKeys;
import com.github.yager400.loginto.bukkit.fileskeys.MessagesKeys;
import com.github.yager400.loginto.common.players.Sessions;
import com.github.yager400.loginto.common.players.Tries;
import org.bukkit.entity.Player;

import java.util.*;

/*
 * This feature is experimental, every contribution
 * that helps to improve the code quality and stability
 * of it is well accepted.
 * Thank you.
 */

public class DialogsHandler extends PacketListenerAbstract {

    private static final String NAMESPACE = "loginto";
    private static final String SEND_PSW = "sendpsw";
    private static final String DISCONNECT = "disconnect";
    private static final String PASSWORD = "psw1";
    private static final String CONFIRMED_PASSWORD = "psw2";
    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CUSTOM_CLICK_ACTION) {
            handleDialogResponse(event.getPlayer(), new WrapperPlayClientCustomClickAction(event));
        }
    }

    public static void handleDialogResponse(Player player, WrapperPlayClientCustomClickAction packet) {
        if (!packet.getId().toString().startsWith(NAMESPACE)) {
            // This is not a LoginTo dialog
            return;
        }
        if (Sessions.isPlayerLogged(player.getUniqueId())) {
            return;
        }
        LoginTo.getFoliaLib().getScheduler().runAsync(wrappedTask -> {
            try {
                if (LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId())) {
                    handleResponseLogin(player, packet);
                } else {
                    handleResponseRegister(player, packet);
                }
            } catch (Exception e) {
                LoginTo.getFoliaLib().getScheduler().runAtEntity(player, wrappedTask2 -> {
                    player.kickPlayer(Messages.getLegacyFormattedMessage(LoginTo.getDialogReader().getString(DialogKeys.MESSAGES_KICK_ERRORWHILEMANAGINGDIALOG)));
                });
            }
        });
    }

    private static void handleResponseLogin(Player player, WrapperPlayClientCustomClickAction packet) throws Exception {
        String action = packet.getId().toString();
        if (action.endsWith(SEND_PSW)) {
            if (!(packet.getPayload() instanceof NBTCompound)) {
                return;
            }
            NBTCompound compound = (NBTCompound) packet.getPayload();
            String password = compound.getStringTagValueOrThrow(PASSWORD);

            String message = LoginCommand.handleLogin(player, password);
            if (message != null) {
                if (message.equalsIgnoreCase(LoginTo.getMessageReader().getString(MessagesKeys.LOGIN_LOGINSUCCESS))) {
                    Messages.player.sendText(message, player, null);
                } else {
                    DialogUtils.sendDialog(player, true, message);
                }
            }
        } else if (action.endsWith(DISCONNECT)) {
            LoginTo.getFoliaLib().getScheduler().runAtEntity(player, wrappedTask -> {
                player.kickPlayer(Messages.getLegacyFormattedMessage(LoginTo.getDialogReader().getString(DialogKeys.MESSAGES_KICK_LOGINKICK)));
            });
        }
    }

    private static void handleResponseRegister(Player player, WrapperPlayClientCustomClickAction packet) throws Exception {
        String action = packet.getId().toString();
        if (action.endsWith(SEND_PSW)) {
            if (!(packet.getPayload() instanceof NBTCompound)) {
                return;
            }
            NBTCompound compound = (NBTCompound) packet.getPayload();
            String password = compound.getStringTagValueOrThrow(PASSWORD);
            String password2 = compound.getStringTagValueOrThrow(CONFIRMED_PASSWORD);

            String message = RegisterCommand.handleRegistration(player, password, password2);
            if (!message.equalsIgnoreCase(LoginTo.getMessageReader().getString(MessagesKeys.REGISTER_REGISTRATIONSUCCESS))) {
                DialogUtils.sendDialog(player, false, message);
            } else {
                Messages.player.sendText(message, player, null);
            }
        } else if (action.endsWith(DISCONNECT)) {
            LoginTo.getFoliaLib().getScheduler().runAtEntity(player, wrappedTask -> {
                player.kickPlayer(Messages.getLegacyFormattedMessage(LoginTo.getDialogReader().getString(DialogKeys.MESSAGES_KICK_REGISTERKICK)));
            });
        }
    }

    public static class DialogUtils {
        public static void sendDialog(Player player, boolean forLogin, String errorMessage) {
            List<DialogBody> bodies = new ArrayList<>();
            if (errorMessage != null) {
                bodies.add(
                        new PlainMessageDialogBody(
                                new PlainMessage(Messages.toCompForm(errorMessage, player, null), 200)
                        )
                );
            }

            List<Input> inputs = new ArrayList<>();
            inputs.add(new Input(
                    PASSWORD,
                    new TextInputControl(
                            200,
                            Messages.toCompForm(LoginTo.getDialogReader().getString(DialogKeys.MESSAGES_LABELS_PASSWORD), player, null),
                            true,
                            "",
                            200,
                            null
                    )
            ));
            if (!forLogin) {
                inputs.add(new Input(
                        CONFIRMED_PASSWORD,
                        new TextInputControl(
                                200,
                                Messages.toCompForm(LoginTo.getDialogReader().getString(DialogKeys.MESSAGES_LABELS_RETYPEPASSWORD), player, null),
                                true,
                                "",
                                200,
                                null
                        )
                ));
            }
            CommonDialogData data = new CommonDialogData(
                    (forLogin)
                            ? Messages.toCompForm(LoginTo.getDialogReader().getString(DialogKeys.MESSAGES_DIALOGTITLES_LOGIN), player, null)
                            : Messages.toCompForm(LoginTo.getDialogReader().getString(DialogKeys.MESSAGES_DIALOGTITLES_REGISTER), player, null),
                    null,
                    false,
                    true,
                    DialogAction.CLOSE,
                    bodies,
                    inputs
            );

            ActionButton yes = new ActionButton(
                    new CommonButtonData(
                            Messages.toCompForm(LoginTo.getDialogReader().getString(DialogKeys.MESSAGES_BUTTONS_SENDPASSWORD), player,null),
                            null,
                            150
                    ),
                    new DynamicCustomAction(
                            new ResourceLocation(NAMESPACE, SEND_PSW),
                            null
                    )
            );

            ActionButton no = new ActionButton(
                    new CommonButtonData(
                            Messages.toCompForm(LoginTo.getDialogReader().getString(DialogKeys.MESSAGES_BUTTONS_DISCONNECT), player, null),
                            null,
                            150
                    ),
                    new DynamicCustomAction(
                            new ResourceLocation(NAMESPACE, DISCONNECT),
                            null
                    )
            );

            Dialog dialog = new ConfirmationDialog(data, yes, no);

            PacketEvents.getAPI().getPlayerManager().sendPacket(
                    player,
                    new WrapperCommonServerShowDialog<>(
                            PacketType.Play.Server.SHOW_DIALOG,
                            dialog
                    )
            );
        }
    }
}
