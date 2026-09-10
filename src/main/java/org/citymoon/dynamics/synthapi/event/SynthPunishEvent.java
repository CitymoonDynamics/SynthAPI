package org.citymoon.dynamics.synthapi.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SynthPunishEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final String checkFriendlyName;
    private final String checkName;
    private final String checkType;
    private final double violations;
    private boolean cancelled;

    public SynthPunishEvent(Player player, String checkFriendlyName, String checkName, String checkType, double violations) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.checkFriendlyName = checkFriendlyName;
        this.checkName = checkName;
        this.checkType = checkType;
        this.violations = violations;
    }

    public Player getPlayer() {
        return player;
    }

    public String getCheckFriendlyName() {
        return checkFriendlyName;
    }

    public String getCheckName() {
        return checkName;
    }

    public String getCheckType() {
        return checkType;
    }

    public double getViolations() {
        return violations;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
