package cn.net.rms.serverpulse;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class PulseHttpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger("ServerPulse");
    private HttpServer server;

    public void start(int port) {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/tps", this::handleTps);
            server.setExecutor(null);
            server.start();
            LOGGER.info("ServerPulse HTTP server listening on port {}", port);
        } catch (IOException e) {
            LOGGER.error("Failed to start ServerPulse HTTP server on port {}", port, e);
            server = null;
        }
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
