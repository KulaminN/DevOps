import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class Backend {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0",8081), 0);

        server.createContext("/submit", exchange -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String text = body.replaceAll(".*\"text\"\\s*:\\s*\"(.*?)\".*", "$1");
            Files.writeString(Paths.get("data.txt"), text + "\n",
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            send(exchange, "{\"message\":\"Данные сохранены\"}");
        });

        server.createContext("/read", exchange -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            Path p = Paths.get("data.txt");
            String content = Files.exists(p) ? Files.readString(p) : "";
            // экранируем переводы строк для JSON
            String json = "{\"content\":\"" + content.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n") + "\"}";
            send(exchange, json);
        });

        server.start();
        System.out.println("Backend v2.0 запущен на http://localhost:8081");
    }

    static void send(com.sun.net.httpserver.HttpExchange ex, String json) throws java.io.IOException {
        byte[] b = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json");
        ex.sendResponseHeaders(200, b.length);
        ex.getResponseBody().write(b);
        ex.close();
    }
}