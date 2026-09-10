package org.citymoon.dynamics.synthapi.violations;

import java.util.Objects;
import java.util.UUID;
import org.citymoon.dynamics.synthapi.SynthAPI;

public final class VLService {
    public double getVL(UUID playerUUID, String friendlyName) {
        Objects.requireNonNull(playerUUID, "playerUUID");
        Objects.requireNonNull(friendlyName, "friendlyName");
        return SynthAPI.requireBackend().readVL(playerUUID, friendlyName.trim());
    }
}
