package org.goos.sniper;

public interface SniperListener {

    void sniperLost();

    void sniperWon();

    void sniperStateChanged(SniperSnapshot state);
}
