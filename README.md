# SynthAPI

Lightweight integration library for Synth Anticheat. It lets external plugins temporarily exempt players from checks and react to detection events.

The jar is dependency free, around 21 KB, and is used `compileOnly`. At runtime the classes are provided by Synth itself, so server owners install nothing extra. The backend only becomes active while Synth is enabled and `api.enabled` is set to `true` in its `config.yml`.

## Requirements

- Java 8 or higher
- A server running Synth (Spigot, Paper or Folia)
- `api.enabled: true` in the Synth configuration

## Installation

Download `synth-api-<version>.jar` from GitHub Releases and reference it as a compile only dependency. Never shade it into your plugin.

Gradle:

```gradle
dependencies {
    compileOnly files('libs/synth-api-1.1.0.jar')
}
```

Maven:

```bash
mvn install:install-file -Dfile=synth-api-1.1.0.jar -DgroupId=org.citymoon.dynamics -DartifactId=synth-api -Dversion=1.1.0 -Dpackaging=jar
```

```xml
<dependency>
    <groupId>org.citymoon.dynamics</groupId>
    <artifactId>synth-api</artifactId>
    <version>1.1.0</version>
    <scope>provided</scope>
</dependency>
```

Add a soft dependency in your `plugin.yml` so your plugin loads after Synth when both are present. The plugin name is **`SynthAnticheat`**:

```yaml
softdepend: [SynthAnticheat]
```

Never shade this library. Synth provides the API classes at runtime; a shaded copy would have its own static backend and would never receive the installed instance.

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
ExemptionSpec spec = ExemptionSpec.forChecks(5, TimeUnit.SECONDS, "boss cutscene", "Reach", "Simulation");

try (ExemptionHandle handle = api.getExemptions().exemptPlayer(this, player.getUniqueId(), spec)) {
    playCutscene(player);
}
```

Available scopes:

```java
ExemptionSpec.forChecks(10, TimeUnit.SECONDS, "reason", "Reach", "Scaffold");
ExemptionSpec.forCheckType("MOVEMENT", 10, TimeUnit.SECONDS, "reason");
ExemptionSpec.forAll(3, TimeUnit.SECONDS, "reason");
```

Rules:

- `owner` (your plugin instance), player UUID and a non blank reason are required.
- Duration is capped by `api.max-exempt-seconds` (default 30).
- The number of simultaneous exemptions per player is capped by `api.max-exemptions-per-player` (default 16).
- Exemptions expire automatically and are revoked on quit, reload with the bridge closed, and plugin shutdown.
- Query state with `api.getExemptions().isExempt(uuid, "ReachHitbox")`.
- Check names are matched case-insensitively. Base names and known aliases are accepted, so `Reach` and `ReachHitbox` resolve to the same check and `Scaffold` resolves to every Scaffold subcheck.
- A family or alias name resolves to every matching check. `isExempt` returns true only when all of them are covered, and `getVL` returns the highest bucket among them.

### Listen to detections

```java
@EventHandler
public void onFlag(SynthFlagEvent event) {
    if (event.getCheckFriendlyName().equalsIgnoreCase("ReachHitbox")) {
        event.setCancelled(true);
    }
}
```

Cancelling `SynthFlagEvent` suppresses the whole flag: it does not increment the violation level, does not run mitigation, does not alert, does not log and does not punish. Cancelling `SynthPunishEvent` suppresses the commands, broadcast and punishment log; it does not reset the violation level or the cooldown. Flag and punishment events fire on the thread that detected them (usually a packet thread), so do not touch world state in the handler. Use `APIScheduler` below or schedule back to the main thread.

`SynthExemptionChangeEvent` is fired for audit purposes whenever an exemption is added, removed, expires or is cleared.

Event handler exceptions are logged and treated as "not cancelled" so a broken integration cannot silently disable enforcement.

### Read violation levels and check metadata

```java
double vl = api.getViolations().getVL(playerUUID, "Reach");

for (CheckSnapshot check : api.getChecks().getAll()) {
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
api:
  enabled: false
  log-usage: true
  max-exempt-seconds: 30
  max-exemptions-per-player: 16
  debug: false
```

With `api.enabled: false` every call fails fast with `APIDisabledException` and no Bukkit events are fired. This is the default.

## Versioning

SynthAPI follows semantic versioning. Package and method names are stable within a major version. The `SynthAPI.API_VERSION` constant reports the contract your plugin compiled against.

## License

See the Synth license for terms.
