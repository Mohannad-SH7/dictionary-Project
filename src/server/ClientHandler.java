package server;

import java.io.*;
import java.net.Socket;

/**
 * ClientHandler
 * كل client بيتصل بالـ Server بتتعمله Thread مستقل من هاد الكلاس
 */
public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private DictionaryLoader dictionary;
    private String clientAddress;

    public ClientHandler(Socket clientSocket, DictionaryLoader dictionary) {
        this.clientSocket = clientSocket;
        this.dictionary = dictionary;
        this.clientAddress = clientSocket.getInetAddress().getHostAddress()
                             + ":" + clientSocket.getPort();
    }

    @Override
    public void run() {
        System.out.println("🔗 New client connected: " + clientAddress);

        try (
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            PrintWriter out = new PrintWriter(
                new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true)
        ) {

            String request;

            // نستمر في استقبال طلبات الـ client
            while ((request = in.readLine()) != null) {

                request = request.trim();
                System.out.println("📩 [" + clientAddress + "] requested: " + request);

                // إذا الـ client طلب إغلاق الاتصال
                if (request.equalsIgnoreCase("close")) {
                    out.println("👋 Connection closed. Goodbye!");
                    System.out.println("🔌 Client disconnected: " + clientAddress);
                    break;
                }

                // إذا الطلب فاضي
                if (request.isEmpty()) {
                    out.println("⚠️ Please enter a word to search.");
                    continue;
                }

                // البحث في القاموس
                String result = dictionary.search(request);

                if (result != null) {
                    out.println("✅ " + result);
                } else {
                    out.println("❌ Word not found: \"" + request + "\"");
                }
            }

        } catch (IOException e) {
            System.out.println("⚠️ Connection error with " + clientAddress + ": " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("⚠️ Error closing socket: " + e.getMessage());
            }
            System.out.println("🔴 Client handler stopped: " + clientAddress);
        }
    }
}
