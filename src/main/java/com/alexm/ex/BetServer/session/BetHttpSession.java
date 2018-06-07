package com.alexm.ex.BetServer.session;

import com.alexm.ex.BetServer.core.Utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BetHttpSession {

    private static Map<String, BetHttpSessionItem> storage;

    //set by default to 10 mins
    private static volatile int expirationTimeOutSeconds = 10 * 60;

    private BetHttpSession() {
        storage = new ConcurrentHashMap();
        ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
        cleanupExecutor.scheduleWithFixedDelay(
                this::cleanup,
                1,
                5,
                TimeUnit.SECONDS);
    }

    public static BetHttpSession getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static int getExpirationTimeOutSeconds() {
        return expirationTimeOutSeconds;
    }

    public static void setExpirationTimeOutSeconds(int expirationTimeOutSeconds) {
        BetHttpSession.expirationTimeOutSeconds = expirationTimeOutSeconds;
    }

    private void cleanup() {
        storage.forEach((k, v) -> {
            if (v.isExpired()) {
                storage.remove(k);
            }
        });
    }

    public String create(String customerId) {
        String sessionId = Utils.randomAlphaNumeric(10);
        Integer unsignedCustomerId = Integer.parseUnsignedInt(customerId);
        storage.put(sessionId, new BetHttpSessionItem(unsignedCustomerId));
        return sessionId;
    }

    public Integer getCustomerId(String sessionId) {
        BetHttpSessionItem sessionItem = storage.get(sessionId);
        if (sessionItem == null) {
            return null;
        }

        return sessionItem.getCustomerId();
    }

    private static class LazyHolder {
        static final BetHttpSession INSTANCE = new BetHttpSession();
    }
}
