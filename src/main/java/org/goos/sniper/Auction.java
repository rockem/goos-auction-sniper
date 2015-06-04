package org.goos.sniper;

import org.jivesoftware.smack.XMPPException;

public interface Auction {

    void bid(int i);

    void join() throws XMPPException;
}
