package org.goos.sniper;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    public static final String STATUS_JOINING = "Joining";
    public static final String STATUS_LOST = "Lost";
    public static final String STATUS_BIDDING = "Bidding";
    public static final String STATUS_WINNING = "Winning";
    public static final String STATUS_WON = "Won";
    private SnipersTableModel snipers = new SnipersTableModel();

    public MainWindow() {
        super("Auction Sniper");
        setName(Main.MAIN_WINDOW_NAME);
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
        snipersTable.setName(Main.SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    public void showStatus(String status) {
        snipers.setState(status);
    }

    public void sniperStatusChanged(SniperSnapshot state) {
        snipers.sniperStateChanged(state);
    }
}
