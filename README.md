# SynthAPI

Lightweight integration library for Synth Anticheat. It lets external plugins temporarily exempt players from checks and react to detection events.

The jar is dependency free, around 19 KB, and is used `compileOnly`. At runtime the classes are provided by Synth itself, so server owners install nothing extra. The backend only becomes active while Synth is enabled and `api.enabled` is set to `true` in its `config.yml`.

## Requirements

- Java 8 or higher
- A server running Synth (Spigot, Paper or Folia)
- `api.enabled: true` in the Synth configuration

## Installation

Download `synth-api-<version>.jar` from GitHub Releases and reference it as a compile only dependency. Never shade it into your plugin.

Gradle:

```gradle
dependencies {
    compileOnly files('libs/synth-api-1.0.0.jar')
}
```

Maven:

```bash
mvn install:install-file -Dfile=synth-api-1.0.0.jar -DgroupId=org.citymoon.dynamics -DartifactId=synth-api -Dversion=1.0.0 -Dpackaging=jar
```

```xml
<dependency>
    <groupId>org.citymoon.dynamics</groupId>
    <artifactId>synth-api</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

Add a soft dependency in your `plugin.yml` so your plugin loads after Synth when both are present:

```yaml
softdepend: [Synth]
```

## Usage

### Check availability

Synth may be absent, disabled, or running with the API bridge closed. Always guard entry points:

```java
if (!SynthAPI.isAvailable()) {
    return;
}
SynthAPI api = SynthAPI.get();
```

`getService()` resolves the instance through Bukkit `ServicesManager` and falls back to `get()`.

### Exempt a player

Exemptions are scoped and time bound. The handle closes the exemption early, which makes try with resources the recommended pattern:

```java
ExemptionSpec spec = ExemptionSpec.forChecks(5, TimeUnit.SECONDS, "boss cutscene", "FlyA", "SpeedA");

try (ExemptionHandle handle = api.exemptions().exemptPlayer(this, player.getUniqueId(), spec)) {
    playCutscene(player);
}
```

Available scopes:

```java
ExemptionSpec.forChecks(10, TimeUnit.SECONDS, "reason", "FlyA", "SpeedA");
ExemptionSpec.forCheckType("MOVEMENT", 10, TimeUnit.SECONDS, "reason");
ExemptionSpec.forAll(3, TimeUnit.SECONDS, "reason");
```

Rules:

- `owner` (your plugin instance), player UUID and a non blank reason are required.
- Duration is capped by `api.max-exempt-seconds` (default 30).
- Exemptions expire automatically and are revoked on quit, reload with the bridge closed, and plugin shutdown.
- Query state with `api.exemptions().isExempt(uuid, "FlyA")`.

### Listen to detections

```java
@EventHandler
public void onFlag(SynthFlagEvent event) {
    if (event.getCheckFriendlyName().equalsIgnoreCase("FlyA")) {
        event.setCancelled(true);
    }
}
```

Cancelling `SynthFlagEvent` suppresses that alert and its punishment. Cancelling `SynthPunishEvent` suppresses the ban. Flag events fire off the main thread, so do not touch world state in the handler. Use `APIScheduler` below or schedule back to the main thread.

`SynthExemptionChangeEvent` is fired for audit purposes whenever an exemption is added or removed.

### Read violation levels and check metadata

```java
double vl = api.violations().getVL(playerUUID, "FlyA");

for (CheckSnapshot check : api.checks().getAll()) {
    String id = check.getFriendlyName();
    boolean enabled = check.isEnabled();
}
```

Only metadata is exposed. Check implementations stay internal and may be obfuscated without breaking consumers.

### Schedule work across Spigot, Paper and Folia

```java
APIScheduler.runAsync(this, () -> queryDatabase(player));
APIScheduler.runSync(this, () -> player.sendMessage("Done"));
APIScheduler.runForPlayer(this, player, () -> player.teleport(arena));
```

`runForPlayer` uses the entity scheduler on Folia and falls back to the main thread scheduler elsewhere.

## Synth side configuration

```yaml
api.enabled: false
api.log-usage: true
api.max-exempt-seconds: 30
api.debug: false
```

With `api.enabled: false` every call fails fast with `APIDisabledException` and no Bukkit events are fired. This is the default.

## Versioning

SynthAPI follows semantic versioning. Package and method names are stable within a major version. The `SynthAPI.API_VERSION` constant reports the contract your plugin compiled against.

## License

See the Synth license for terms.
