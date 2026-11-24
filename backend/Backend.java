import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Backend {
    public Backend() {
    }

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8081), 0);

        server.createContext("/submit", (exchange) -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1L);
            } else {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                String text = body.replaceAll(".*\"text\"\\s*:\\s*\"(.*?)\".*", "$1");
                Files.writeString(Paths.get("data.txt"), text + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                send(exchange, "{\"message\":\"Данные сохранены\"}");
            }
        });

        server.createContext("/read", (exchange) -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            Path path = Paths.get("data.txt");
            String content = Files.exists(path, new LinkOption[0]) ? Files.readString(path) : "";
            String escapedContent = content.replace("\\", "\\\\").replace("\"", "\\\"");
            String jsonResponse = "{\"content\":\"" + escapedContent.replace("\n", "\\n") + "\"}";
            send(exchange, jsonResponse);
        });

        server.start();
        System.out.println("Backend v2.0 запущен на http://localhost:8081");
    }

    static void send(HttpExchange exchange, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, (long)bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}