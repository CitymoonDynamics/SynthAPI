package org.citymoon.dynamics.synthapi.checks;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.citymoon.dynamics.synthapi.SynthAPI;

public final class CheckRegistry {
    public Set<CheckSnapshot> getAll() {
        Set<CheckSnapshot> snapshots = SynthAPI.requireBackend().snapshots();
        return Collections.unmodifiableSet(new LinkedHashSet<CheckSnapshot>(snapshots));
    }

    public boolean isEnabled(String friendlyName) {
        Objects.requireNonNull(friendlyName, "friendlyName");
        for (CheckSnapshot snapshot : SynthAPI.requireBackend().snapshots()) {
            if (snapshot.getFriendlyName().equalsIgnoreCase(friendlyName.trim())) {
                return snapshot.isEnabled();
            }
        }
        return false;
    }
}
