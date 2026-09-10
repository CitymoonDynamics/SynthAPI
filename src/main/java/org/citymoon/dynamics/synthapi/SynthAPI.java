package org.citymoon.dynamics.synthapi;

import org.bukkit.Bukkit;
import org.citymoon.dynamics.synthapi.checks.CheckRegistry;
import org.citymoon.dynamics.synthapi.exemptions.ExemptionManager;
import org.citymoon.dynamics.synthapi.spi.SynthAPIBackend;
import org.citymoon.dynamics.synthapi.violations.VLService;

public final class SynthAPI {
    public static final String API_VERSION = "1.0.0";
    private static final ExemptionManager EXEMPTION_MANAGER = new ExemptionManager();
    private static final CheckRegistry CHECK_REGISTRY = new CheckRegistry();
    private static final VLService VL_SERVICE = new VLService();
    private static volatile SynthAPIBackend backend;

    private SynthAPI() {
    }

    public static void installBackend(SynthAPIBackend newBackend) {
        if (newBackend == null) {
            throw new IllegalArgumentException("newBackend must not be null");
        }
        backend = newBackend;
    }

    public static void uninstallBackend() {
        backend = null;
    }

    public static SynthAPIBackend requireBackend() {
        SynthAPIBackend current = backend;
        if (current == null) {
            throw new APIDisabledException();
        }
        return current;
    }

    public static boolean isAvailable() {
        try {
            SynthAPIBackend current = backend;
            return current != null && current.isEnabled();
        } catch (Throwable t) {
            return false;
        }
    }

    public static SynthAPI get() {
        if (!isAvailable()) {
            throw new APIDisabledException();
        }
        return Holder.INSTANCE;
    }

    public static SynthAPI getService() {
        try {
            org.bukkit.plugin.RegisteredServiceProvider<SynthAPI> provider = Bukkit.getServicesManager().getRegistration(SynthAPI.class);
            if (provider != null && provider.getProvider() != null) {
                return provider.getProvider();
            }
        } catch (Throwable ignored) {
        }
        return get();
    }

    public ExemptionManager exemptions() {
        return EXEMPTION_MANAGER;
    }

    public CheckRegistry checks() {
        return CHECK_REGISTRY;
    }

    public VLService violations() {
        return VL_SERVICE;
    }

    public String getAPIVersion() {
        return API_VERSION;
    }

    private static final class Holder {
        private static final SynthAPI INSTANCE = new SynthAPI();
    }
}
