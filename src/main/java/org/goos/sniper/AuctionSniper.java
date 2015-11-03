package org.goos.sniper;


public class AuctionSniper implements AuctionEventListener {

    private final SniperListener sniperListener;
    private final Auction auction;
    private final String itemId;

    private SniperSnapshot snapshot;
    private boolean isWinning;

    public AuctionSniper(String itemId, Auction auction, SniperListener sniperListener) {
        this.itemId = itemId;
        this.auction = auction;
        this.sniperListener = sniperListener;
        this.snapshot = SniperSnapshot.joining(itemId);
    }

    public void auctionClosed() {
        if (isWinning) {
            sniperListener.sniperWon();
        } else {
            sniperListener.sniperLost();
        }
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        isWinning = priceSource == PriceSource.FromSniper;
        if (isWinning) {
            snapshot = snapshot.winning(price);
            //sniperListener.sniperBidding(new SniperSnapshot(itemId, price, price, SniperState.WINNING));
        } else {
            int bid = price + increment;
            auction.bid(bid);
            snapshot = snapshot.bidding(price, bid);
            //sniperListener.sniperBidding(new SniperSnapshot(itemId, price, bid, SniperState.BIDDING));
        }
        sniperListener.sniperStateChanged(snapshot);
    }
}
