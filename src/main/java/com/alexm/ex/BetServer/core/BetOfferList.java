package com.alexm.ex.BetServer.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class BetOfferList {
    private final Map<Integer, BetOfferStakeData> storage;

    private BetOfferList() {
        storage = new ConcurrentHashMap<>();
    }

    public static BetOfferList getInstance() {
        return BetOfferList.LazyHolder.INSTANCE;
    }

    public void createBetOffer(final int customerId, final String sStake, final String sBetOfferId) {

        int stake = Integer.parseUnsignedInt(sStake);
        int betOfferId = Integer.parseUnsignedInt(sBetOfferId);

        storage.computeIfAbsent(betOfferId,
                betOfferStakeList -> new BetOfferStakeData());

        BetOfferStakeData betOfferStakeData = storage.get(betOfferId);
        betOfferStakeData.createBetOffer(customerId, stake);
    }

    public String getBetOfferTopHighestStakeList(final String sBetOfferId) {
        int betOfferId = Integer.parseUnsignedInt(sBetOfferId);
        BetOfferStakeData betOfferStakeData = storage.get(betOfferId);
        return betOfferStakeData.getTopCustomerListWithHighestStakes();
    }

    private static class LazyHolder {
        static final BetOfferList INSTANCE = new BetOfferList();
    }


}
