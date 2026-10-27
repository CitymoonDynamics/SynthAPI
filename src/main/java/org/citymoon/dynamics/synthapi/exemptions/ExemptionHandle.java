package org.citymoon.dynamics.synthapi.exemptions;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.plugin.Plugin;

public final class ExemptionHandle implements AutoCloseable {
    private final Plugin owner;
    private final UUID playerUUID;
    private final ExemptionSpec spec;
    private final long expiresAt;
    private final long createdAtNanos;
    private final long lifetimeNanos;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public ExemptionHandle(Plugin owner, UUID playerUUID, ExemptionSpec spec, long expiresAt) {
        this.owner = Objects.requireNonNull(owner, "owner");
        this.playerUUID = Objects.requireNonNull(playerUUID, "playerUUID");
        this.spec = Objects.requireNonNull(spec, "spec");
        this.expiresAt = expiresAt;
        this.createdAtNanos = System.nanoTime();
        this.lifetimeNanos = TimeUnit.MILLISECONDS.toNanos(Math.max(0L, expiresAt - System.currentTimeMillis()));
    }

    public Plugin getOwner() {
        return owner;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public ExemptionSpec getSpec() {
        return spec;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        return closed.get() || System.nanoTime() - createdAtNanos >= lifetimeNanos;
    }

    public long getRemainingMillis() {
        if (closed.get()) {
            return 0L;
        }
        long remainingNanos = lifetimeNanos - (System.nanoTime() - createdAtNanos);
        return remainingNanos <= 0L ? 0L : TimeUnit.NANOSECONDS.toMillis(remainingNanos);
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            try {
                ExemptionManager.unexemptHandle(this);
            } catch (RuntimeException ignored) {
            }
        }
    }
}
