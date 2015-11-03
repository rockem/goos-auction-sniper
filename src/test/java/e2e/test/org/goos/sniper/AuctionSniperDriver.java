package e2e.test.org.goos.sniper;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JLabelDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;
import org.goos.sniper.Main;

import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static java.lang.String.valueOf;
import static org.hamcrest.Matchers.equalTo;

public class AuctionSniperDriver extends JFrameDriver {

    public AuctionSniperDriver(int timeoutMillis) {
        super(new GesturePerformer(), JFrameDriver.topLevelFrame(
                named(Main.MAIN_WINDOW_NAME),
                showingOnScreen()), new AWTEventQueueProber(timeoutMillis, 100));
    }

    public void showsSniperStatus(String itemId, int lastPrice, int lastBid, String status) {
        new JTableDriver(this).hasRow(
                matching(withLabelText(itemId),
                        withLabelText(valueOf(lastPrice)),
                        withLabelText(valueOf(lastBid)),
                        withLabelText(status)));

    }

    public void showsSniperStatus(String status) {
        new JTableDriver(this).hasCell(withLabelText(equalTo(status)));
    }
}
