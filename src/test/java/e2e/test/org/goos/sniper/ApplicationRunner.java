package e2e.test.org.goos.sniper;

import org.goos.sniper.Main;
import org.goos.sniper.MainWindow;

public class ApplicationRunner {
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    private static final String STATUS_JOINING = "Joining";
    private static final String STATUS_LOST = "Lost";
    public static final String SNIPER_XMPP_ID = "sniper@elis-macbook-pro.local/Smack";

    private AuctionSniperDriver driver;
    private String itemId;

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
        driver = new AuctionSniperDriver(1000);
        driver.showsSniperStatus(STATUS_JOINING);
    }

    public void hasShownSniperHasLostAuction() {
        driver.showsSniperStatus(STATUS_LOST);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void hasShownSniperIsBidding(int lastPrice, int lastBid) {
        driver.showsSniperStatus(
                itemId,
                lastPrice,
                lastBid,
                MainWindow.STATUS_BIDDING);
    }

    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(MainWindow.STATUS_LOST);
    }

    public void hasShownSniperIsWinning(int winningBid) {
        driver.showsSniperStatus(
                itemId,
                winningBid,
                winningBid,
                MainWindow.STATUS_WINNING);
    }

    public void showsSniperHasWonAuction(int lastPrice) {
        driver.showsSniperStatus(
                itemId,
                lastPrice,
                lastPrice,
                MainWindow.STATUS_WON);
    }
}
