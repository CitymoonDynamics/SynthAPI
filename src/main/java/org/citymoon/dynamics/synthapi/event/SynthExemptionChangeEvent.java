package org.citymoon.dynamics.synthapi.event;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SynthExemptionChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final UUID playerUUID;
    private final String pluginName;
    private final String scope;
    private final long ttlSeconds;
    private final boolean added;

    public SynthExemptionChangeEvent(UUID playerUUID, String pluginName, String scope, long ttlSeconds, boolean added) {
        super(!Bukkit.isPrimaryThread());
        this.playerUUID = playerUUID;
        this.pluginName = pluginName;
        this.scope = scope;
        this.ttlSeconds = ttlSeconds;
        this.added = added;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getScope() {
        return scope;
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }

    public boolean isAdded() {
        return added;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
