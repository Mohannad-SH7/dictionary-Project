package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * DictionaryServer
 * الـ Main Server — يفتح ServerSocket ويدير ThreadPool
 */
public class DictionaryServer {

    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {

        // ── التحقق من الـ Arguments ──
        if (args.length != 2) {
            System.out.println("Usage: java -jar DictionaryServer.jar <port> <dictionary-file>");
            System.exit(1);
        }

        int port;
        try {
            port = Integer.parseInt(args[0]);
            if (port < 1024 || port > 65535) {
                throw new NumberFormatException("Port out of range");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid port: must be a number between 1024 and 65535");
            System.exit(1);
            return;
        }

        String dictionaryFile = args[1];

        // ── تحميل القاموس ──
        DictionaryLoader dictionary;
        try {
            dictionary = new DictionaryLoader(dictionaryFile);
        } catch (IOException e) {
            System.out.println("❌ Cannot load dictionary file: " + e.getMessage());
            System.exit(1);
            return;
        }

        // ── Worker Pool ──
        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        // ── فتح الـ ServerSocket ──
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("🚀 Server started on port " + port);
            System.out.println("📚 Dictionary loaded with " + dictionary.getSize() + " words");
            System.out.println("👥 Thread pool size: " + THREAD_POOL_SIZE);
            System.out.println("⏳ Waiting for clients...\n");

            // ── Loop الرئيسي — قبول الـ Clients ──
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    // إعطاء كل client thread من الـ pool
                    threadPool.execute(new ClientHandler(clientSocket, dictionary));
                } catch (IOException e) {
                    System.out.println("⚠️ Error accepting client: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("❌ Server error: " + e.getMessage());
        } finally {
            threadPool.shutdown();
            System.out.println("🔴 Server stopped.");
        }
    }
}
