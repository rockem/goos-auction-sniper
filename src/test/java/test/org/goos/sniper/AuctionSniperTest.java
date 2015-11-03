package test.org.goos.sniper;

import org.goos.sniper.*;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.goos.sniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static org.goos.sniper.AuctionEventListener.PriceSource.FromSniper;
import static org.hamcrest.core.IsEqual.equalTo;

public class AuctionSniperTest {
    private static final String ITEM_ID = "KUKU";
    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();

    private final States sniperState = context.states("sniper");

    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);
    private AuctionSniper sniper;

    @Before
    public void setUp() throws Exception {
        context.checking(new Expectations() {{
            oneOf(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, 0, 0, SniperState.JOINING));
        }});
        sniper = new AuctionSniper(ITEM_ID, auction, sniperListener);
    }

    @Test
    public void reportsLostWhenAuctionClosesImmediately() throws Exception {
        context.checking(new Expectations() {{
            oneOf(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, 0, 0, SniperState.LOST));
        }});

        sniper.auctionClosed();
    }

    @Test
    public void bidsHigherAbdReportsBidWhenPriceArrives() throws Exception {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;

        context.checking(new Expectations() {{
            oneOf(auction).bid(price + increment);
            atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));
        }});

        sniper.currentPrice(price, increment, FromOtherBidder);
    }

    @Test
    public void reportsIsWinningWhenCurrentPriceComesFromSniper() throws Exception {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING)));
            then(sniperState.is("bidding"));
            atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, 135, 135, SniperState.WINNING));
            when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 12, FromOtherBidder);
        sniper.currentPrice(135, 45, FromSniper);
    }

    @Test
    public void reportsLostIfAuctionClosesWhenBidding() throws Exception {
        final int price = 123;
        final int increment = 45;
        final int bid = price + increment;
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(
                    with(aSniperThatIs(SniperState.BIDDING))); then(sniperState.is("bidding"));
            atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, price, bid, SniperState.LOST)); when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(price, increment, AuctionEventListener.PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    private Matcher<SniperSnapshot> aSniperThatIs(SniperState state) {
        return new FeatureMatcher<SniperSnapshot, SniperState>(equalTo(state), "sniper that is", "was") {
            @Override
            protected SniperState featureValueOf(SniperSnapshot actual) {
                return actual.state;
            }
        };
    }

    @Test
    public void reportsWonIfAuctionClosesWhenWinning() throws Exception {
        final int price = 123;
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(
                    with(aSniperThatIs(SniperState.WINNING))); then(sniperState.is("winning"));
            atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, price, price, SniperState.WON)); when(sniperState.is("winning"));
        }});

        sniper.currentPrice(price, 45, AuctionEventListener.PriceSource.FromSniper);
        sniper.auctionClosed();
    }

}
