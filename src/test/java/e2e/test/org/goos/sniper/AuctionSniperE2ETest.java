package e2e.test.org.goos.sniper;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AuctionSniperE2ETest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();


    @Before
    public void setUp() throws Exception {
        System.setProperty("com.objogate.wl.keyboard", "US");

    }

    @Test public void
    sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper();
        auction.announceClosed();
        application.hasShownSniperHasLostAuction();
    }

    @After
    public void stopAuction() {
        auction.stop();
    }

    @After public void stopApplication() {
        application.stop();
    }

}