package cn.net.rms.serverpulse;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class PulseHttpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger("ServerPulse");
    private static final int MAX_PORT = 65535;
    private HttpServer server;

    public void start(int port) {
        if (port < 0 || port > MAX_PORT) {
            LOGGER.error("Invalid port {}, must be in range 0–{}", port, MAX_PORT);
            return;
        }
        for (int candidate = port; candidate <= MAX_PORT; candidate++) {
            HttpServer created = null;
            try {
                created = HttpServer.create(new InetSocketAddress(candidate), 0);
                created.createContext("/tps", this::handleTps);
                created.setExecutor(null);
                created.start();
                server = created;
                if (candidate != port) {
                    LOGGER.warn("Configured port {} was occupied, fell back to port {}", port, candidate);
                }
                LOGGER.info("ServerPulse HTTP server listening on port {}", candidate);
                return;
            } catch (BindException e) {
                LOGGER.debug("Port {} occupied, trying next", candidate);
            } catch (Exception e) {
                LOGGER.error("Failed to start ServerPulse HTTP server on port {}", candidate, e);
                if (created != null) created.stop(0);
                return;
            }
        }
        LOGGER.error("No available port found in range {}–{}", port, MAX_PORT);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            LOGGER.info("ServerPulse HTTP server stopped");
            server = null;
        }
    }

    private void handleTps(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        TickMonitor monitor = TickMonitor.getInstance();
        String json = String.format(
                Locale.US,
                "{\"tps\":%.2f,\"mspt\":%.2f,\"mspt_min\":%.2f,\"mspt_max\":%.2f}",
                monitor.getTps(),
                monitor.getMspt(),
                monitor.getMsptMin(),
                monitor.getMsptMax()
        );

        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, body.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body);
        }
    }
}
