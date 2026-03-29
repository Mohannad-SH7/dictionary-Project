package client;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

/**
 * ClientGUI
 * واجهة المستخدم الرسومية + التواصل مع السيرفر
 */
public class ClientGUI {

    // ── UI Components ──
    private JFrame frame;
    private JTextField wordInput;
    private JTextArea resultArea;
    private JButton searchButton;
    private JButton clearButton;
    private JLabel statusLabel;

    // ── Network ──
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private final String SERVER_IP;
    private final int SERVER_PORT;

    public ClientGUI(String serverIp, int serverPort) {
        this.SERVER_IP = serverIp;
        this.SERVER_PORT = serverPort;
        buildUI();
        connectToServer();
    }

    // ── بناء الواجهة ──
    private void buildUI() {
        frame = new JFrame("📚 Dictionary Client");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(700, 550);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));
        frame.getContentPane().setBackground(new Color(245, 245, 250));

        // ── Header ──
        JLabel title = new JLabel("📖 Dictionary Lookup", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(44, 62, 80));
        title.setBorder(new EmptyBorder(15, 0, 5, 0));
        frame.add(title, BorderLayout.NORTH);

        // ── Center: Result Area ──
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        resultArea.setBackground(new Color(255, 255, 255));
        resultArea.setForeground(new Color(44, 62, 80));
        resultArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        resultArea.setText("Welcome! Type a word and press Search.\n");

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(new CompoundBorder(
            new EmptyBorder(5, 15, 5, 15),
            new LineBorder(new Color(189, 195, 199), 1, true)
        ));
        frame.add(scrollPane, BorderLayout.CENTER);

        // ── Bottom Panel ──
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setBackground(new Color(245, 245, 250));
        bottomPanel.setBorder(new EmptyBorder(5, 15, 10, 15));

        // Input Panel
        JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
        inputPanel.setBackground(new Color(245, 245, 250));

        wordInput = new JTextField();
        wordInput.setFont(new Font("Arial", Font.PLAIN, 15));
        wordInput.setBorder(new CompoundBorder(
            new LineBorder(new Color(52, 152, 219), 1, true),
            new EmptyBorder(6, 8, 6, 8)
        ));

        // البحث بالضغط Enter
        wordInput.addActionListener(e -> search());

        JLabel searchIcon = new JLabel("🔍 Word: ");
        searchIcon.setFont(new Font("Arial", Font.BOLD, 14));

        inputPanel.add(searchIcon, BorderLayout.WEST);
        inputPanel.add(wordInput, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(245, 245, 250));

        searchButton = new JButton("🔍 Search");
        styleButton(searchButton, new Color(52, 152, 219));
        searchButton.addActionListener(e -> search());

        clearButton = new JButton("🗑️ Clear");
        styleButton(clearButton, new Color(149, 165, 166));
        clearButton.addActionListener(e -> resultArea.setText(""));

        JButton closeButton = new JButton("❌ Close");
        styleButton(closeButton, new Color(231, 76, 60));
        closeButton.addActionListener(e -> closeConnection());

        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(closeButton);

        // Status Label
        statusLabel = new JLabel("⚪ Disconnected", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.GRAY);

        bottomPanel.add(inputPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // ── Close Window ──
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeConnection();
            }
        });

        frame.setVisible(true);
    }

    // ── تنسيق الأزرار ──
    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBorder(new EmptyBorder(8, 18, 8, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ── الاتصال بالسيرفر ──
    private void connectToServer() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            in = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

            statusLabel.setText("🟢 Connected to " + SERVER_IP + ":" + SERVER_PORT);
            statusLabel.setForeground(new Color(39, 174, 96));
            appendResult("✅ Connected to server successfully!\n");

        } catch (IOException e) {
            statusLabel.setText("🔴 Connection failed!");
            statusLabel.setForeground(Color.RED);
            appendResult("❌ Cannot connect to server: " + e.getMessage() + "\n");
            searchButton.setEnabled(false);
        }
    }

    // ── البحث عن كلمة ──
    private void search() {
        String word = wordInput.getText().trim();

        if (word.isEmpty()) {
            appendResult("⚠️ Please enter a word!\n");
            return;
        }

        try {
            out.println(word);
            String response = in.readLine();
            appendResult("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            appendResult("🔎 Word: " + word + "\n");
            appendResult("📖 " + response + "\n");
            wordInput.setText("");
            wordInput.requestFocus();

        } catch (IOException e) {
            appendResult("❌ Error: " + e.getMessage() + "\n");
            statusLabel.setText("🔴 Connection lost!");
            statusLabel.setForeground(Color.RED);
        }
    }

    // ── إضافة نص للـ Result Area ──
    private void appendResult(String text) {
        resultArea.append(text);
        resultArea.setCaretPosition(resultArea.getDocument().getLength());
    }

    // ── إغلاق الاتصال ──
    private void closeConnection() {
        try {
            if (out != null) out.println("close");
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("Error closing: " + e.getMessage());
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        String ip = "127.0.0.1";
        int port = 3000;

        if (args.length == 2) {
            ip = args[0];
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid port number");
                System.exit(1);
            }
        }

        final String finalIp = ip;
        final int finalPort = port;

        SwingUtilities.invokeLater(() -> new ClientGUI(finalIp, finalPort));
    }
}
