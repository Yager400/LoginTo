/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.api.utils;

import java.util.function.Consumer;

public interface EventManager {
    /**
     * Register a Consumer to listen at a LoginTo event
     * @param eventClass The Event class you want to listen (for getting the class do <b>PlayerLoginEvent.class</b> for example)
     * @param consumer The consumer that will run when this event will be called
     * @param <T> The Event
     */
    <T> void register(Class<T> eventClass, Consumer<T> consumer);
}
