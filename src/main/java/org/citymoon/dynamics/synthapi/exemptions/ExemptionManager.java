package org.citymoon.dynamics.synthapi.exemptions;

import java.util.Objects;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.citymoon.dynamics.synthapi.SynthAPI;

public final class ExemptionManager {
    public ExemptionHandle exemptPlayer(Plugin owner, UUID playerUUID, ExemptionSpec spec) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(playerUUID, "playerUUID");
        Objects.requireNonNull(spec, "spec");
        return SynthAPI.requireBackend().exempt(owner, playerUUID, spec);
    }

    public ExemptionHandle exemptPlayer(Plugin owner, Player player, ExemptionSpec spec) {
        Objects.requireNonNull(player, "player");
        return exemptPlayer(owner, player.getUniqueId(), spec);
    }

    public boolean isExempt(UUID playerUUID, String friendlyName) {
        Objects.requireNonNull(playerUUID, "playerUUID");
        Objects.requireNonNull(friendlyName, "friendlyName");
        return SynthAPI.requireBackend().isExempt(playerUUID, friendlyName.trim());
    }

    public void unexemptPlayer(Plugin owner, UUID playerUUID) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(playerUUID, "playerUUID");
        SynthAPI.requireBackend().clear(owner, playerUUID);
    }

    static void unexemptHandle(ExemptionHandle handle) {
        SynthAPI.requireBackend().remove(handle);
    }
}
