package org.goos.sniper;

import org.goos.sniper.AuctionEventListener.PriceSource;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.HashMap;
import java.util.Map;

import static org.goos.sniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static org.goos.sniper.AuctionEventListener.PriceSource.FromSniper;

public class AuctionMessageTranslator implements MessageListener {

    public static final String CLOSE_EVENT = "CLOSE";
    public static final String PRICE_EVENT = "PRICE";

    private final AuctionEventListener listener;
    private String sniperId;

    public AuctionMessageTranslator(AuctionEventListener listener, String sniperId) {
        this.listener = listener;
        this.sniperId = sniperId;
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        AuctionEvent event = AuctionEvent.from(message.getBody());

        String type = event.type();
        if (CLOSE_EVENT.equals(type)) {
            listener.auctionClosed();
        } else if (PRICE_EVENT.equals(type)) {
            listener.currentPrice(
                    event.getCurrentPrice(),
                    event.getIncrement(),
                    event.isFrom(sniperId));
        }
    }

    private static class AuctionEvent {
        private final Map<String, String> fields = new HashMap<>();
        public static final String CURRENT_PRICE_FIELD = "CurrentPrice";
        public static final String INCREMENT_FIELD = "Increment";
        public static final String EVENT_FIELD = "Event";

        static AuctionEvent from(String body) {
            AuctionEvent event = new AuctionEvent();
            for (String element : fieldsIn(body)) {
                event.addField(element);
            }
            return event;
        }

        private static String[] fieldsIn(String body) {
            return body.split(";");
        }

        private void addField(String field) {
            String[] pair = field.split(":");
            fields.put(pair[0].trim(), pair[1].trim());
        }

        public String type() {
            return get(EVENT_FIELD);
        }

        private String get(String key) {
            return fields.get(key);
        }

        public int getCurrentPrice() {
            return getInt(get(CURRENT_PRICE_FIELD));
        }

        private int getInt(String strInt) {
            return Integer.parseInt(strInt);
        }

        public int getIncrement() {
            return getInt(get(INCREMENT_FIELD));
        }

        public PriceSource isFrom(String sniperId) {
            return sniperId.equals(bidder()) ? FromSniper : FromOtherBidder;
        }

        private String bidder() {
            return get("Bidder");
        }
    }
}
