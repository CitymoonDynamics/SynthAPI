package org.citymoon.dynamics.synthapi.scheduler;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class APIScheduler {
    private static final boolean FOLIA;
    private static final Method ASYNC_SCHEDULER;
    private static final Method RUN_NOW;
    private static final Method GLOBAL_SCHEDULER;
    private static final Method GLOBAL_EXECUTE;
    private static final Method PLAYER_SCHEDULER;
    private static final Method PLAYER_EXECUTE;

    static {
        boolean folia;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (Throwable ignored) {
            folia = false;
        }
        FOLIA = folia;

        Method asyncScheduler = null;
        Method runNow = null;
        Method globalScheduler = null;
        Method globalExecute = null;
        Method playerScheduler = null;
        Method playerExecute = null;
        if (FOLIA) {
            try {
                asyncScheduler = Bukkit.class.getMethod("getAsyncScheduler");
                Object scheduler = asyncScheduler.invoke(null);
                runNow = scheduler.getClass().getMethod("runNow", Plugin.class, Consumer.class);
            } catch (Throwable ignored) {
                asyncScheduler = null;
                runNow = null;
            }
            try {
                globalScheduler = Bukkit.class.getMethod("getGlobalRegionScheduler");
                Object scheduler = globalScheduler.invoke(null);
                globalExecute = scheduler.getClass().getMethod("execute", Plugin.class, Runnable.class);
            } catch (Throwable ignored) {
                globalScheduler = null;
                globalExecute = null;
            }
            try {
                playerScheduler = Player.class.getMethod("getScheduler");
                Class<?> entityScheduler = Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
                playerExecute = entityScheduler.getMethod("execute", Plugin.class, Runnable.class, Runnable.class, long.class);
            } catch (Throwable ignored) {
                playerScheduler = null;
                playerExecute = null;
            }
        }
        ASYNC_SCHEDULER = asyncScheduler;
        RUN_NOW = runNow;
        GLOBAL_SCHEDULER = globalScheduler;
        GLOBAL_EXECUTE = globalExecute;
        PLAYER_SCHEDULER = playerScheduler;
        PLAYER_EXECUTE = playerExecute;
    }

    private APIScheduler() {
    }

    public static boolean isFolia() {
        return FOLIA;
    }

    public static void runAsync(Plugin plugin, Runnable task) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(task, "task");
        if (FOLIA && ASYNC_SCHEDULER != null && RUN_NOW != null) {
            try {
                Object scheduler = ASYNC_SCHEDULER.invoke(null);
                Consumer<Object> consumer = ignored -> task.run();
                RUN_NOW.invoke(scheduler, plugin, consumer);
                return;
            } catch (Throwable throwable) {
                warn(plugin, "async", throwable);
            }
        }
        try {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        } catch (Throwable throwable) {
            warn(plugin, "async-fallback", throwable);
            task.run();
        }
    }

    public static void runSync(Plugin plugin, Runnable task) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(task, "task");
        if (FOLIA && GLOBAL_SCHEDULER != null && GLOBAL_EXECUTE != null) {
            try {
                Object scheduler = GLOBAL_SCHEDULER.invoke(null);
                GLOBAL_EXECUTE.invoke(scheduler, plugin, task);
                return;
            } catch (Throwable throwable) {
                warn(plugin, "sync", throwable);
            }
        }
        try {
            if (Bukkit.isPrimaryThread()) {
                task.run();
            } else {
                Bukkit.getScheduler().runTask(plugin, task);
            }
        } catch (Throwable throwable) {
            warn(plugin, "sync-fallback", throwable);
            task.run();
        }
    }

    public static void runForPlayer(Plugin plugin, Player player, Runnable task) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(task, "task");
        if (FOLIA && PLAYER_SCHEDULER != null && PLAYER_EXECUTE != null) {
            try {
                Object scheduler = PLAYER_SCHEDULER.invoke(player);
                PLAYER_EXECUTE.invoke(scheduler, plugin, task, null, 1L);
                return;
            } catch (Throwable throwable) {
                warn(plugin, "player", throwable);
            }
        }
        runSync(plugin, task);
    }

    private static void warn(Plugin plugin, String operation, Throwable throwable) {
        try {
            plugin.getLogger().warning("APIScheduler could not schedule " + operation + ": " + throwable);
        } catch (Throwable ignored) {
        }
    }
}
