package org.goos.sniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPERS_TABLE_NAME = "SniperStatus";
    public static final String JOIN_COMMAND_FORMAT = "SQLVersion: 1.1; Command: JOIN;";
    public static final String BID_COMMAND_FORMAT = "SQLVersion: 1.1; Command: BID; Price: %d;";

    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_RESOURCE = "Auction";
    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;


    private final SnipersTableModel snipers = new SnipersTableModel();
    private MainWindow ui;
    private Chat notToBeCGd;


    public Main(XMPPConnection connection) {
        startUserInterface(connection);

    }

    private void startUserInterface(XMPPConnection connection) {
        SwingUtilities.invokeLater(() -> {
            ui = new MainWindow(snipers);
            disconnectWhenUICloses(connection);
        });
    }

    private void disconnectWhenUICloses(XMPPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                connection.disconnect();
            }
        });
    }

    public static void main(String... args) throws Exception {
        XMPPConnection connection = connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
        Main main = new Main(connection);
        main.joinAction(
                connection,
                args[ARG_ITEM_ID]);
    }

    private void joinAction(XMPPConnection connection, String itemId) throws XMPPException {
        final Chat chat = connection.getChatManager().createChat(auctionId(itemId, connection), null);
        this.notToBeCGd = chat;

        Auction auction = new XMPPAuction(chat);
        chat.addMessageListener(new AuctionMessageTranslator(
                new AuctionSniper(itemId, auction, new SwingThreadSniperListener(snipers)),
                connection.getUser()));
        auction.join();
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

    public static class XMPPAuction implements Auction {

        private Chat chat;

        public XMPPAuction(Chat chat) {
            this.chat = chat;
        }

        @Override
        public void bid(int amount) {
            try {
                chat.sendMessage(String.format(BID_COMMAND_FORMAT, amount));
            } catch (XMPPException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void join() throws XMPPException {
            chat.sendMessage(JOIN_COMMAND_FORMAT);
        }
    }

    public class SwingThreadSniperListener implements SniperListener {

        private final SnipersTableModel snipers;

        public SwingThreadSniperListener(SnipersTableModel snipers) {
            this.snipers = snipers;
        }

        @Override
        public void sniperStateChanged(SniperSnapshot state) {
            SwingUtilities.invokeLater(() ->
                    snipers.sniperStateChanged(state));
        }
    }

}
