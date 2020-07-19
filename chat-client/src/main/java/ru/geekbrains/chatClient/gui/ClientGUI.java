package ru.geekbrains.chatClient.gui;

import ru.geekbrains.chatServer.data.Message;
import ru.geekbrains.chatServer.data.User;
import ru.geekbrains.net.MessageSocketThread;
import ru.geekbrains.net.MessageSocketThreadListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;

public class ClientGUI extends JFrame implements ActionListener, Thread.UncaughtExceptionHandler, MessageSocketThreadListener {


    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    private final JTextArea chatArea = new JTextArea();
    private final JPanel panelTop = new JPanel(new GridLayout(2, 3));
    private final JTextField ipAddressField = new JTextField("127.0.0.1");
    private final JTextField portField = new JTextField("8181");
    private final JCheckBox cbAlwaysOnTop = new JCheckBox("Always on top", true);
    private final JTextField loginField = new JTextField("login");
    private final JPasswordField passwordField = new JPasswordField("123");
    private final JButton buttonLogin = new JButton("Login");

    private final JPanel panelBottom = new JPanel(new BorderLayout());
    private final JButton buttonDisconnect = new JButton("<html><b>Disconnect</b></html>");
    private final JTextField messageField = new JTextField();
    private final JButton buttonSend = new JButton("Send");

    private final JList<String> listUsers = new JList<>();

    private MessageSocketThread socketThread;
    private Message message;

    private User user;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGUI();
            }
        });

    }


    ClientGUI() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat");
        setSize(WIDTH, HEIGHT);
        setAlwaysOnTop(true);

        listUsers.setListData(new String[]{"user1", "user2", "user3", "user4",
                "user5", "user6", "user7", "user8", "user9", "user-with-too-long-name-in-this-chat"});
        JScrollPane scrollPaneUsers = new JScrollPane(listUsers);
        JScrollPane scrollPaneChatArea = new JScrollPane(chatArea);
        scrollPaneUsers.setPreferredSize(new Dimension(100, 0));

        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setEditable(false);

        panelTop.add(ipAddressField);
        panelTop.add(portField);
        panelTop.add(cbAlwaysOnTop);
        panelTop.add(loginField);
        panelTop.add(passwordField);
        panelTop.add(buttonLogin);
        panelBottom.add(buttonDisconnect, BorderLayout.WEST);
        panelBottom.add(messageField, BorderLayout.CENTER);
        panelBottom.add(buttonSend, BorderLayout.EAST);
        panelBottom.setVisible(false);

        add(scrollPaneChatArea, BorderLayout.CENTER);
        add(scrollPaneUsers, BorderLayout.EAST);
        add(panelTop, BorderLayout.NORTH);
        add(panelBottom, BorderLayout.SOUTH);

        cbAlwaysOnTop.addActionListener(this);

        buttonSend.addActionListener(this);
        messageField.addActionListener(this);
        buttonLogin.addActionListener(this);
        buttonDisconnect.addActionListener(this);

        setVisible(true);

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();


        if (src == cbAlwaysOnTop) {
            setAlwaysOnTop(cbAlwaysOnTop.isSelected());
        } else if (src == buttonSend || src == messageField) {
            sendMessage();
        } else if (src == buttonLogin) {
            try {
                Socket socket = new Socket(ipAddressField.getText(), Integer.parseInt(portField.getText()));
                socketThread = new MessageSocketThread(this, "Client " + loginField.getText(), socket);
                user = new User(loginField.getText());
                onSocketReady();
            } catch (IOException ioException) {
                showError(ioException.getMessage());
            }

        } else if (src == buttonDisconnect) {
            onSocketClosed();
        } else {
            throw new RuntimeException("Unsupported action: " + src);
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        StackTraceElement[] ste = e.getStackTrace();
        String msg = String.format("Exception in \"%s\": %s %s%n\t %s",
                t.getName(), e.getClass().getCanonicalName(), e.getMessage(), ste[0]);
        JOptionPane.showMessageDialog(this, msg, "Exception!", JOptionPane.ERROR_MESSAGE);
    }

    private void sendMessage() {
        message = new Message(
                messageField.getText().trim(),
                user.getNickName(),
                System.currentTimeMillis()
        );
        if (message.getMessage().length() > 0) {
            System.out.println("Message was sent to server: " + message.toString());
            drawMessage(message.toString());
            socketThread.sendMessage(message.toString());
        }
        messageField.setText("");
        messageField.grabFocus();
    }

    private void drawMessage(String message) {
        chatArea.append(message);
        logMessage(message);
    }

    private static void logMessage(String message) {
        String logDir = System.getProperty("user.dir");
        try (FileWriter fw = new FileWriter(logDir + "/chatLog.txt", true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write(message);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Cannot log message, logFile does not exist or cannot access to logsFolder");
        }
    }

    private void showError(String errorMsg) {
        JOptionPane.showMessageDialog(this, errorMsg, "Error!", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void onMessageReceived(String message) {
        drawMessage(message);
    }

    @Override
    public void onException(Throwable throwable) {
        throwable.printStackTrace();
        showError(throwable.getMessage());
    }

    @Override
    public void onSocketClosed() {
        panelTop.setVisible(true);
        panelBottom.setVisible(false);

    }

    @Override
    public void onSocketReady() {
        panelTop.setVisible(false);
        panelBottom.setVisible(true);
    }
}