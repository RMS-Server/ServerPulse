# ServerPulse

**English** | [简体中文](README-CN.md)

A lightweight Fabric server-side mod that exposes real-time server performance metrics via an HTTP API.

## Supported Versions

| Minecraft | Java |
|-----------|------|
| 1.17.1    | 16   |
| 1.21.4    | 21   |

## Features

- Built-in HTTP server starts alongside the Minecraft server (default port `25580`)
- Zero external dependencies — uses JDK's built-in HTTP server
- Tracks tick durations with a 100-tick ring buffer for accurate rolling averages

## API Endpoints

### `GET /tps`

Returns current server performance metrics.

**Response:**

```json
{
  "tps": 20.00,
  "mspt": 12.34,
  "mspt_min": 8.12,
  "mspt_max": 22.34
}
```

| Field      | Description                                    |
|------------|------------------------------------------------|
| `tps`      | Ticks per second (capped at 20.0)              |
| `mspt`     | Average milliseconds per tick (last 100 ticks)  |
| `mspt_min` | Minimum tick duration (last 100 ticks)          |
| `mspt_max` | Maximum tick duration (last 100 ticks)          |

## Configuration

On first launch, a config file is created at `config/serverpulse.properties`:

```properties
# ServerPulse configuration
port=25580
```

## Building

```bash
./gradlew 1.17.1:build    # Build for Minecraft 1.17.1
./gradlew 1.21.4:build    # Build for Minecraft 1.21.4
```

Output JARs are located in `versions/<version>/build/libs/`.

## License

This project is available under the GPL-3.0 license.
