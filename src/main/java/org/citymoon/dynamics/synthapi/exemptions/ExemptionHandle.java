package org.citymoon.dynamics.synthapi.exemptions;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.plugin.Plugin;

public final class ExemptionHandle implements AutoCloseable {
    private final Plugin owner;
    private final UUID playerUUID;
    private final ExemptionSpec spec;
    private final long expiresAt;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public ExemptionHandle(Plugin owner, UUID playerUUID, ExemptionSpec spec, long expiresAt) {
        this.owner = Objects.requireNonNull(owner, "owner");
        this.playerUUID = Objects.requireNonNull(playerUUID, "playerUUID");
        this.spec = Objects.requireNonNull(spec, "spec");
        this.expiresAt = expiresAt;
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
        return System.currentTimeMillis() >= expiresAt || closed.get();
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
