package org.citymoon.dynamics.synthapi.scheduler;

import java.lang.reflect.Method;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class APIScheduler {
    private static final boolean FOLIA;

    static {
        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (Throwable ignored) {
            folia = false;
        }
        FOLIA = folia;
    }

    private APIScheduler() {
    }

    public static boolean isFolia() {
        return FOLIA;
    }

    public static void runAsync(Plugin plugin, Runnable task) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(task, "task");
        if (FOLIA) {
            try {
                Object asyncScheduler = Bukkit.class.getMethod("getAsyncScheduler").invoke(null);
                Method runNow = null;
                for (Method method : asyncScheduler.getClass().getMethods()) {
                    if (method.getName().equals("runNow") && method.getParameterCount() == 2) {
                        runNow = method;
                        break;
                    }
                }
                if (runNow != null) {
                    final Runnable work = task;
                    Object consumer = java.lang.reflect.Proxy.newProxyInstance(
                            APIScheduler.class.getClassLoader(),
                            new Class<?>[]{java.util.function.Consumer.class},
                            new java.lang.reflect.InvocationHandler() {
                                @Override
                                public Object invoke(Object proxy, Method invoked, Object[] args) {
                                    work.run();
                                    return null;
                                }
                            });
                    runNow.invoke(asyncScheduler, plugin, consumer);
                    return;
                }
            } catch (Throwable ignored) {
            }
        }
        try {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        } catch (Throwable ignored) {
            task.run();
        }
    }

    public static void runSync(Plugin plugin, Runnable task) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(task, "task");
        if (FOLIA) {
            try {
                Object globalScheduler = Bukkit.class.getMethod("getGlobalRegionScheduler").invoke(null);
                Method execute = globalScheduler.getClass().getMethod("execute", Plugin.class, Runnable.class);
                execute.invoke(globalScheduler, plugin, task);
                return;
            } catch (Throwable ignored) {
            }
        }
        try {
            if (Bukkit.isPrimaryThread()) {
                task.run();
            } else {
                Bukkit.getScheduler().runTask(plugin, task);
            }
        } catch (Throwable ignored) {
            task.run();
        }
    }

    public static void runForPlayer(Plugin plugin, Player player, Runnable task) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(task, "task");
        if (FOLIA) {
            try {
                Object entityScheduler = player.getClass().getMethod("getScheduler").invoke(player);
                for (Method method : entityScheduler.getClass().getMethods()) {
                    if (method.getName().equals("execute") && method.getParameterCount() == 4) {
                        method.invoke(entityScheduler, plugin, task, null, 1L);
                        return;
                    }
                }
            } catch (Throwable ignored) {
            }
        }
        runSync(plugin, task);
    }
}
