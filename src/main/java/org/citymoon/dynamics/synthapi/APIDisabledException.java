package org.citymoon.dynamics.synthapi;

public class APIDisabledException extends APIException {
    public APIDisabledException() {
        super("SynthAPI is disabled. Enable api.enabled in config.yml");
    }
}
