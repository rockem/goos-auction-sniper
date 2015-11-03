package org.goos.sniper;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel {

    public enum Column {
        ITEM_IDENTIFIER,
        LAST_PRICE,
        LAST_BID,
        SNIPER_STATUS;

        public static Column at(int offset) {
            return values()[offset];
        }
    }

    public static final String[] STATUS_TEXT = {
            MainWindow.STATUS_JOINING,
            MainWindow.STATUS_BIDDING,
            MainWindow.STATUS_WINNING
    };

    private static final SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.BIDDING);

    private SniperSnapshot snapshot = STARTING_UP;
    private String state = MainWindow.STATUS_JOINING;

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (Column.at(columnIndex)) {
            case ITEM_IDENTIFIER:
                return snapshot.itemId;
            case LAST_PRICE:
                return snapshot.lastPrice;
            case LAST_BID:
                return snapshot.lastBid;
            case SNIPER_STATUS:
                return state;
            default:
                throw new IllegalArgumentException("No column at " + columnIndex);
        }
    }

    public void setState(String state) {
        this.state = state;
        fireTableRowsUpdated(0, 0);
    }

    public void sniperStateChanged(SniperSnapshot newSnapshot) {
        this.snapshot = newSnapshot;
        this.state = STATUS_TEXT[newSnapshot.state.ordinal()];
        fireTableRowsUpdated(0, 0);
    }
}
