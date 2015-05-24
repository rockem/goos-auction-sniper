package test.org.goos.sniper;

import org.goos.sniper.AuctionEventListener;
import org.goos.sniper.AuctionMessageTranslator;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class ActionMessageTranslatorTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private static final Chat UNUSED_CHAT = null;
    private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
    private final AuctionMessageTranslator translator = new AuctionMessageTranslator(listener);

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
    public void notifiesBidDetailsWhenCurrentPriceMessageReceived() throws Exception {
        context.checking(new Expectations() {{
            exactly(1).of(listener).currentPrice(192, 7);
        }});

        translator.processMessage(UNUSED_CHAT,
                createMessage("Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;"));

    }
}