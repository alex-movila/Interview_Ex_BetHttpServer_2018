package com.alexm.ex.BetServer.session;

import static com.alexm.ex.BetServer.session.BetHttpSession.getExpirationTimeOutSeconds;

public class BetHttpSessionItem {
    private final long expirationTimestamp;
    private final int customerId;
    private boolean expired;

    public BetHttpSessionItem(int customerId) {
        this.expirationTimestamp = System.currentTimeMillis() + getExpirationTimeOutSeconds() * 1000;
        this.customerId = customerId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public boolean isExpired() {
        if (!expired) {
            expired = System.currentTimeMillis() >= expirationTimestamp;
        }

        return expired;
    }
}
