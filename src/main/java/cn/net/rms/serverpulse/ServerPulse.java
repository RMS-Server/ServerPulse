package cn.net.rms.serverpulse;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class ServerPulse implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("ServerPulse");
    private static final String CONFIG_FILE = "serverpulse.properties";
    private static final int DEFAULT_PORT = 25580;

    private static int port = DEFAULT_PORT;

    public static int getPort() {
        return port;
    }

    @Override
    public void onInitialize() {
        loadConfig();
        LOGGER.info("ServerPulse loaded — HTTP API will bind to port {} on first tick", port);
    }

    private void loadConfig() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path configFile = configDir.resolve(CONFIG_FILE);

        Properties props = new Properties();

        if (Files.exists(configFile)) {
            try (InputStream in = Files.newInputStream(configFile)) {
                props.load(in);
            } catch (IOException e) {
                LOGGER.error("Failed to read config, using defaults", e);
            }
        }

        String portStr = props.getProperty("port");
        if (portStr != null) {
            try {
                port = Integer.parseInt(portStr.trim());
            } catch (NumberFormatException e) {
                LOGGER.warn("Invalid port '{}' in config, using default {}", portStr, DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        }

        if (!Files.exists(configFile)) {
            props.setProperty("port", String.valueOf(port));
            try (OutputStream out = Files.newOutputStream(configFile)) {
                props.store(out, "ServerPulse configuration");
            } catch (IOException e) {
                LOGGER.warn("Failed to write default config", e);
            }
        }
    }
}
