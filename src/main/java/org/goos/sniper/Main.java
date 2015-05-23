package org.goos.sniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Main {
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPER_STATUS_NAME = "SniperStatus";

    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_RESOURCE = "Auction";
    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;

    private MainWindow ui;
    private Chat notToBeCGd;

    public Main() {
        startUserInterface();

    }

    private void startUserInterface() {
        SwingUtilities.invokeLater(() -> ui = new MainWindow());
    }

    private class MainWindow extends JFrame {
        public static final String STATUS_JOINING = "Joining";
        public static final String STATUS_LOST = "Lost";
        private final JLabel sniperStatus = createLabel(STATUS_JOINING);

        private JLabel createLabel(String initialText) {
            JLabel label = new JLabel(initialText);
            label.setName(SNIPER_STATUS_NAME);
            label.setBorder(new LineBorder(Color.BLACK));
            return label;
        }

        public MainWindow() {
            super("Auction Sniper");
            setName(MAIN_WINDOW_NAME);
            add(sniperStatus);
            pack();
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setVisible(true);
        }

        public void showStatus(String status) {
            sniperStatus.setText(status);
        }
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        main.joinAction(
                connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]),
                args[ARG_ITEM_ID]);
    }

    private void joinAction(XMPPConnection connection, String itemId) throws XMPPException {
        Chat chat = connection.getChatManager().createChat(
                auctionId(itemId, connection),
                (aChat, message) -> {
                    SwingUtilities.invokeLater(() -> {
                        ui.showStatus(MainWindow.STATUS_LOST);
                    });
                }
        );
        this.notToBeCGd = chat;
        chat.sendMessage(new Message());
    }

    private static XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password);
        return connection;
    }

    private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }

    private static XMPPConnection connectTo(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password);
        return connection;
    }
}
