package org.citymoon.dynamics.synthapi.exemptions;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class ExemptionSpec {
    public enum ScopeType {
        CHECKS,
        CHECK_TYPE,
        ALL
    }

    private final ScopeType scopeType;
    private final Set<String> checks;
    private final String checkType;
    private final long durationMillis;
    private final String reason;

    private ExemptionSpec(ScopeType scopeType, Set<String> checks, String checkType, long durationMillis, String reason) {
        this.scopeType = scopeType;
        this.checks = checks == null ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet<String>(checks));
        this.checkType = checkType;
        this.durationMillis = durationMillis;
        this.reason = reason;
    }

    public static ExemptionSpec forChecks(long duration, TimeUnit unit, String reason, String... friendlyNames) {
        Objects.requireNonNull(unit, "unit");
        Objects.requireNonNull(reason, "reason");
        Objects.requireNonNull(friendlyNames, "friendlyNames");
        if (friendlyNames.length == 0) {
            throw new IllegalArgumentException("friendlyNames must not be empty");
        }
        Set<String> normalized = new HashSet<String>();
        for (String name : friendlyNames) {
            Objects.requireNonNull(name, "check name");
            normalized.add(name.trim());
        }
        return new ExemptionSpec(ScopeType.CHECKS, normalized, null, toMillis(duration, unit), requireReason(reason));
    }

    public static ExemptionSpec forCheckType(String checkType, long duration, TimeUnit unit, String reason) {
        Objects.requireNonNull(checkType, "checkType");
        Objects.requireNonNull(unit, "unit");
        return new ExemptionSpec(ScopeType.CHECK_TYPE, Collections.<String>emptySet(), checkType.trim().toUpperCase(), toMillis(duration, unit), requireReason(reason));
    }

    public static ExemptionSpec forAll(long duration, TimeUnit unit, String reason) {
        Objects.requireNonNull(unit, "unit");
        return new ExemptionSpec(ScopeType.ALL, Collections.<String>emptySet(), null, toMillis(duration, unit), requireReason(reason));
    }

    public ScopeType getScopeType() {
        return scopeType;
    }

    public Set<String> getChecks() {
        return checks;
    }

    public String getCheckType() {
        return checkType;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public String getReason() {
        return reason;
    }

    private static long toMillis(long duration, TimeUnit unit) {
        if (duration <= 0) {
            throw new IllegalArgumentException("duration must be positive");
        }
        long millis = unit.toMillis(duration);
        if (millis <= 0) {
            throw new IllegalArgumentException("duration must be positive");
        }
        return millis;
    }

    private static String requireReason(String reason) {
        Objects.requireNonNull(reason, "reason");
        if (reason.trim().isEmpty()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
        return reason.trim();
    }
}
