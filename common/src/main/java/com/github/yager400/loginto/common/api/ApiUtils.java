/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.api;

import com.github.yager400.loginto.api.utils.EventManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class ApiUtils {

    private static EventManagerImpl eventManagerImpl;

    public static void initApi() {
        ApiUtils.eventManagerImpl = new EventManagerImpl();
        com.github.yager400.loginto.api.LoginTo.setEventManager(ApiUtils.eventManagerImpl);
    }

    public static <T> void callEvent(T event) {
        ApiUtils.eventManagerImpl.callEvent(event);
    }

    public static class EventManagerImpl implements EventManager {

        private final Map<Class<?>, List<Consumer<?>>> listeners = new ConcurrentHashMap<>();

        @Override
        public <T> void register(Class<T> eventClass, Consumer<T> consumer) {
            listeners.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>()).add(consumer);
        }

        public <T> void callEvent(T event) {
            List<Consumer<?>> eventConsumers = listeners.get(event.getClass());
            if (eventConsumers == null) return;
            for (Consumer<?> consumer : eventConsumers) {
                ((Consumer<T>) consumer).accept(event);
            }
        }

    }

}
