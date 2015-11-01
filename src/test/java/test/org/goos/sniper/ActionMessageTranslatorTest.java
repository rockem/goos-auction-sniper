package test.org.goos.sniper;

import org.goos.sniper.AuctionEventListener;
import org.goos.sniper.AuctionMessageTranslator;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import static e2e.test.org.goos.sniper.ApplicationRunner.SNIPER_ID;
import static org.goos.sniper.AuctionEventListener.PriceSource;

public class ActionMessageTranslatorTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private static final Chat UNUSED_CHAT = null;
    private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
    private final AuctionMessageTranslator translator =
            new AuctionMessageTranslator(listener, SNIPER_ID);

    @Test
    public void notifyAuctionCloseWhenCloseMessageReceived() throws Exception {
        context.checking(new Expectations() {{
            oneOf(listener).auctionClosed();
        }});

        translator.processMessage(UNUSED_CHAT, createMessage("Event: CLOSE"));
    }

    private Message createMessage(String body) {
        Message message = new Message();
        message.setBody("SQLVersion: 1.1;" + body);
        return message;
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() throws Exception {
        context.checking(new Expectations() {{
            exactly(1).of(listener).currentPrice(192, 7, PriceSource.FromOtherBidder);
        }});

        translator.processMessage(UNUSED_CHAT,
                createMessage("Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;"));

    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() throws Exception {
        context.checking(new Expectations() {{
            exactly(1).of(listener).currentPrice(234, 5, PriceSource.FromSniper);
        }});

        translator.processMessage(UNUSED_CHAT,
                createMessage("Event: PRICE; CurrentPrice: 234; Increment: 5; Bidder:" + SNIPER_ID + ";"));

    }
}