package org.citymoon.dynamics.synthapi.checks;

import java.util.Objects;

public final class CheckSnapshot {
    private final String friendlyName;
    private final String name;
    private final String type;
    private final String category;
    private final boolean enabled;

    public CheckSnapshot(String friendlyName, String name, String type, String category, boolean enabled) {
        this.friendlyName = Objects.requireNonNull(friendlyName, "friendlyName");
        this.name = Objects.requireNonNull(name, "name");
        this.type = Objects.requireNonNull(type, "type");
        this.category = category == null ? "UNKNOWN" : category;
        this.enabled = enabled;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
