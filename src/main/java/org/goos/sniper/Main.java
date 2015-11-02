package org.goos.sniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPER_STATUS_NAME = "SniperStatus";
    public static final String JOIN_COMMAND_FORMAT = "SQLVersion: 1.1; Command: JOIN;";
    public static final String BID_COMMAND_FORMAT = "SQLVersion: 1.1; Command: BID; Price: %d;";

    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_RESOURCE = "Auction";
    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;

    private MainWindow ui;
    private Chat notToBeCGd;


    public Main(XMPPConnection connection) {
        startUserInterface(connection);

    }

    private void startUserInterface(XMPPConnection connection) {
        SwingUtilities.invokeLater(() -> {
            ui = new MainWindow();
            disconnectWhenUICloses(connection);
        });
    }

    public class MainWindow extends JFrame {

        public static final String STATUS_JOINING = "Joining";
        public static final String STATUS_LOST = "Lost";
        public static final String STATUS_BIDDING = "Bidding";
        public static final String STATUS_WINNING = "Winning";
        public static final String STATUS_WON = "Won";
        private static final String SNIPERS_TABLE_NAME = "snipersTable";
        private SnipersTableModel snipers = new SnipersTableModel();

        public MainWindow() {
            super("Auction Sniper");
            setName(MAIN_WINDOW_NAME);
            fillContentPane(makeSnipersTable());
            pack();
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setVisible(true);
        }

        private void fillContentPane(JTable table) {
            final Container contentPane = getContentPane();
            contentPane.setLayout(new BorderLayout());
            contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
        }

        private JTable makeSnipersTable() {
            final JTable snipersTable = new JTable(snipers);
            snipersTable.setName(SNIPERS_TABLE_NAME);
            return snipersTable;
        }

        public void showStatus(String status) {
            snipers.setStatusText(status);
        }

    }

    private class SnipersTableModel extends AbstractTableModel {

        private String statusText = MainWindow.STATUS_JOINING;

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return statusText;
        }

        public void setStatusText(String statusText) {
            this.statusText = statusText;
            fireTableRowsUpdated(0, 0);
        }
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
                new AuctionSniper(auction, new SniperStateDisplay()),
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

    public class SniperStateDisplay implements SniperListener {

        @Override
        public void sniperLost() {
            showStatus(MainWindow.STATUS_LOST);
        }

        private void showStatus(String status) {
            SwingUtilities.invokeLater(() -> ui.showStatus(status));
        }

        @Override
        public void sniperBidding() {
            showStatus(MainWindow.STATUS_BIDDING);
        }

        @Override
        public void sniperWinning() {
            showStatus(MainWindow.STATUS_WINNING);
        }

        @Override
        public void sniperWon() {
            showStatus(MainWindow.STATUS_WON);
        }
    }

}
