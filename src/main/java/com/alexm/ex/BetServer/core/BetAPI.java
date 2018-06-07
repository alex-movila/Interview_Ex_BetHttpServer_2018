package com.alexm.ex.BetServer.core;

import com.alexm.ex.BetServer.session.BetHttpSession;

public final class BetAPI {

    private BetAPI() {
    }

    public static String getBetOfferTopHighestStakeList(String betOfferId) {
        return BetOfferList.getInstance().getBetOfferTopHighestStakeList(betOfferId);
    }

    public static String getNewSessionKey(String customerId) {
        return BetHttpSession.getInstance().create(customerId);
    }

    public static boolean createCustomerStakeForBetOffer(final String sessionId,
                                                         final String stake,
                                                         final String betOfferId) {
        Integer customerId = BetHttpSession.getInstance().getCustomerId(sessionId);
        if (customerId == null) {
            return false;
        }
        BetOfferList.getInstance().createBetOffer(customerId, stake, betOfferId);
        return true;
    }


}
