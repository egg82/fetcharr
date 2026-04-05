package me.egg82.fuiplugin;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import me.egg82.fetcharr.api.FetcharrAPIProvider;
import me.egg82.fetcharr.api.model.plugin.EnabledPlugin;
import me.egg82.fetcharr.api.model.update.Updater;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class UIServer {
    private static final Logger logger = LoggerFactory.getLogger(UIServer.class);

    private final int port;
    private final EventLog eventLog;
    private final Set<BlockingQueue<String>> sseClients = ConcurrentHashMap.newKeySet();

    private HttpServer httpServer;

    public UIServer(int port, @NotNull EventLog eventLog) {
        this.port = port;
        this.eventLog = eventLog;
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.setExecutor(Executors.newCachedThreadPool());

        httpServer.createContext("/api/status", this::handleStatus);
        httpServer.createContext("/api/events", this::handleEvents);
        httpServer.createContext("/api/stream", this::handleStream);
        httpServer.createContext("/", this::handleStatic);

        httpServer.start();
        logger.info("Fetcharr UI available at http://localhost:{}/", port);
    }

    public void stop() {
        if (httpServer != null) {
            // Unblock all SSE clients
            for (BlockingQueue<String> queue : sseClients) {
                queue.offer("__CLOSE__");
            }
            httpServer.stop(1);
        }
    }

    /** Push a JSON payload to all connected SSE clients. */
    public void broadcast(@NotNull String eventsJson) {
        String message = "event: events\ndata: " + eventsJson + "\n\n";
        for (BlockingQueue<String> queue : sseClients) {
            queue.offer(message);
        }
    }

    // -------------------------------------------------------------------------
    // Handlers
    // -------------------------------------------------------------------------

    private void handleStatus(@NotNull HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        addCorsHeaders(exchange);
        String json = buildStatusJson();
        sendJson(exchange, 200, json);
    }

    private void handleEvents(@NotNull HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        addCorsHeaders(exchange);
        sendJson(exchange, 200, eventLog.toJson());
    }

    private void handleStream(@NotNull HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        exchange.getResponseHeaders().set("Content-Type", "text/event-stream; charset=utf-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-cache");
        exchange.getResponseHeaders().set("Connection", "keep-alive");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, 0);

        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        sseClients.add(queue);

        try (OutputStream out = exchange.getResponseBody()) {
            // Send initial data immediately
            String initial = "event: events\ndata: " + eventLog.toJson() + "\n\n";
            out.write(initial.getBytes(StandardCharsets.UTF_8));
            out.flush();

            while (true) {
                String message;
                try {
                    message = queue.poll(25, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                if (message == null) {
                    // Send a heartbeat comment to keep the connection alive
                    out.write(": heartbeat\n\n".getBytes(StandardCharsets.UTF_8));
                } else if (message.equals("__CLOSE__")) {
                    break;
                } else {
                    out.write(message.getBytes(StandardCharsets.UTF_8));
                }
                out.flush();
            }
        } catch (IOException ignored) {
            // Client disconnected
        } finally {
            sseClients.remove(queue);
        }
    }

    private void handleStatic(@NotNull HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        if (path.equals("/") || path.isEmpty()) {
            path = "/web/index.html";
        } else {
            path = "/web" + path;
        }

        byte[] content = loadResource(path);
        if (content == null) {
            byte[] notFound = "404 Not Found".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(404, notFound.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(notFound);
            }
            return;
        }

        String contentType = guessContentType(path);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, content.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(content);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private @Nullable byte[] loadResource(@NotNull String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) return null;
            return is.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }

    private @NotNull String guessContentType(@NotNull String path) {
        if (path.endsWith(".html")) return "text/html; charset=utf-8";
        if (path.endsWith(".css"))  return "text/css; charset=utf-8";
        if (path.endsWith(".js"))   return "application/javascript; charset=utf-8";
        return "application/octet-stream";
    }

    private void sendJson(@NotNull HttpExchange exchange, int status, @NotNull String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    private void addCorsHeaders(@NotNull HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
    }

    private @NotNull String buildStatusJson() {
        var api = FetcharrAPIProvider.instance();
        boolean dryRun = api.updateManager().dryRun();

        StringBuilder sb = new StringBuilder("{");
        sb.append("\"dryRun\":").append(dryRun).append(",");

        // Updaters
        sb.append("\"updaters\":[");
        var updaters = api.updateManager().updaters();
        for (int i = 0; i < updaters.size(); i++) {
            if (i > 0) sb.append(",");
            appendUpdaterJson(sb, updaters.get(i));
        }
        sb.append("],");

        // Plugins
        sb.append("\"plugins\":[");
        var plugins = api.pluginManager().plugins();
        int pi = 0;
        for (EnabledPlugin plugin : plugins) {
            if (pi++ > 0) sb.append(",");
            appendPluginJson(sb, plugin);
        }
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }

    private void appendUpdaterJson(@NotNull StringBuilder sb, @NotNull Updater u) {
        var cfg = u.config();
        sb.append("{");
        sb.append("\"type\":\"").append(jsonEscape(cfg.type().name())).append("\",");
        sb.append("\"url\":\"").append(jsonEscape(cfg.url())).append("\",");
        sb.append("\"id\":").append(cfg.id()).append(",");
        sb.append("\"searchAmount\":").append(cfg.searchAmount()).append(",");
        sb.append("\"searchInterval\":\"").append(jsonEscape(cfg.searchInterval().toString())).append("\",");
        sb.append("\"monitoredOnly\":").append(cfg.monitoredOnly()).append(",");
        sb.append("\"missingStatus\":\"").append(jsonEscape(cfg.missingStatus().name())).append("\",");
        sb.append("\"useCutoff\":").append(cfg.useCutoff());
        sb.append("}");
    }

    private void appendPluginJson(@NotNull StringBuilder sb, @NotNull EnabledPlugin p) {
        var desc = p.descriptor();
        sb.append("{");
        sb.append("\"id\":\"").append(jsonEscape(desc.id())).append("\",");
        sb.append("\"name\":\"").append(jsonEscape(desc.name())).append("\",");
        sb.append("\"version\":\"").append(jsonEscape(desc.version())).append("\",");
        sb.append("\"description\":\"").append(jsonEscape(desc.description() != null ? desc.description() : "")).append("\"");
        sb.append("}");
    }

    private static @NotNull String jsonEscape(@NotNull String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
