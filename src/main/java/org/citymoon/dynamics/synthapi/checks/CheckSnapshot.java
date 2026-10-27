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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CheckSnapshot)) {
            return false;
        }
        CheckSnapshot snapshot = (CheckSnapshot) other;
        return enabled == snapshot.enabled
                && friendlyName.equals(snapshot.friendlyName)
                && name.equals(snapshot.name)
                && type.equals(snapshot.type)
                && category.equals(snapshot.category);
    }

    @Override
    public int hashCode() {
        int result = friendlyName.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + category.hashCode();
        result = 31 * result + (enabled ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CheckSnapshot{friendlyName='" + friendlyName + "', name='" + name
                + "', type='" + type + "', category='" + category + "', enabled=" + enabled + '}';
    }
}
