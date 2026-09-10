package org.citymoon.dynamics.synthapi.spi;

import java.util.Set;
import java.util.UUID;
import org.bukkit.plugin.Plugin;
import org.citymoon.dynamics.synthapi.checks.CheckSnapshot;
import org.citymoon.dynamics.synthapi.exemptions.ExemptionHandle;
import org.citymoon.dynamics.synthapi.exemptions.ExemptionSpec;

public interface SynthAPIBackend {
    boolean isEnabled();

    ExemptionHandle exempt(Plugin owner, UUID playerUUID, ExemptionSpec spec);

    void remove(ExemptionHandle handle);

    void clear(Plugin requester, UUID playerUUID);

    boolean isExempt(UUID playerUUID, String friendlyName);

    Set<CheckSnapshot> snapshots();

    double readVL(UUID playerUUID, String friendlyName);
}
