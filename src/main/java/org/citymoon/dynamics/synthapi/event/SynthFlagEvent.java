package org.citymoon.dynamics.synthapi.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SynthFlagEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final String checkFriendlyName;
    private final String checkName;
    private final String checkType;
    private final double violations;
    private final double maxViolations;
    private boolean cancelled;

    public SynthFlagEvent(Player player, String checkFriendlyName, String checkName, String checkType, double violations, double maxViolations) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.checkFriendlyName = checkFriendlyName;
        this.checkName = checkName;
        this.checkType = checkType;
        this.violations = violations;
        this.maxViolations = maxViolations;
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

    public double getMaxViolations() {
        return maxViolations;
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
