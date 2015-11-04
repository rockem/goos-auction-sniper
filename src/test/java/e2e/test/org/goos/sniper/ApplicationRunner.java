package e2e.test.org.goos.sniper;

import org.goos.sniper.Main;
import org.goos.sniper.MainWindow;
import org.goos.sniper.SniperSnapshot;
import org.goos.sniper.SniperState;

import static org.goos.sniper.SnipersTableModel.textFor;

public class ApplicationRunner {
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    public static final String SNIPER_XMPP_ID = "sniper@elis-macbook-pro.local/Smack";
    public static final int TIMEOUT_1_SEC = 1000;

    private AuctionSniperDriver driver;
    private String itemId;
    private int lastPrice;
    private int lastBid;

    public void startBiddingIn(FakeAuctionServer auction) {
        itemId = auction.getItemId();
        Thread thread = new Thread("Test Application") {
            @Override public void run() {
                try {
                    Main.main(FakeAuctionServer.XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(TIMEOUT_1_SEC);
        driver.hasTitle(MainWindow.APPLICATION_TITLE);
        driver.hasColumnTitles();
        showSniperStatusFor(SniperState.JOINING);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void hasShownSniperIsBidding(int lastPrice, int lastBid) {
        this.lastPrice = lastPrice;
        this.lastBid = lastBid;
        showSniperStatusFor(SniperState.BIDDING);
    }

    private void showSniperStatusFor(SniperState state) {
        driver.showsSniperStatus(
                itemId,
                lastPrice,
                lastBid,
                textFor(state));
    }

    public void showsSniperHasLostAuction() {
        showSniperStatusFor(SniperState.LOST);
    }

    public void hasShownSniperIsWinning(int winningBid) {
        this.lastPrice = winningBid;
        this.lastBid = winningBid;
        showSniperStatusFor(SniperState.WINNING);
    }

    public void showsSniperHasWonAuction(int lastPrice) {
        this.lastPrice = lastPrice;
        this.lastBid = lastPrice;
        showSniperStatusFor(SniperState.WON);
    }
}
