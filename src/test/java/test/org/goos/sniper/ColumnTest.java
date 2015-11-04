package test.org.goos.sniper;

import org.goos.sniper.SniperSnapshot;
import org.goos.sniper.SniperState;
import org.junit.Test;

import static org.goos.sniper.SnipersTableModel.Column;
import static org.goos.sniper.SnipersTableModel.textFor;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ColumnTest {

    public static final int LAST_PRICE = 34;
    public static final int LAST_BID = 3;
    public static final String ITEM_ID = "item id";
    public static final SniperState STATE = SniperState.JOINING;

    @Test
    public void extractValuesOfSnapshot() throws Exception {
        SniperSnapshot snapshot = new SniperSnapshot(ITEM_ID, LAST_PRICE, LAST_BID, STATE);

        assertThat(Column.at(Column.ITEM_IDENTIFIER.ordinal()).valueIn(snapshot), equalTo(ITEM_ID));
        assertThat(Column.at(Column.LAST_PRICE.ordinal()).valueIn(snapshot), equalTo(LAST_PRICE));
        assertThat(Column.at(Column.LAST_BID.ordinal()).valueIn(snapshot), equalTo(LAST_BID));
        assertThat(Column.at(Column.SNIPER_STATUS.ordinal()).valueIn(snapshot), equalTo(textFor(STATE)));

    }
}