package com.alexm.ex.BetServer;

import com.alexm.ex.BetServer.core.BetAPI;
import com.alexm.ex.BetServer.core.BetOfferStakeData;
import com.alexm.ex.BetServer.session.BetHttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/*
   Quick multithreaded test without slow server part
 */
public class CoreTest {

    private AtomicInteger maxVal = new AtomicInteger(0);

    @BeforeAll
    public static void setupCore() {
        BetHttpSession.setExpirationTimeOutSeconds(10 * 60);
        BetOfferStakeData.setTopMaxSize(1);
    }

    @Test
    public void concurrentPostTest() {

        concurrentPostTest("11");

        String result = BetAPI.getBetOfferTopHighestStakeList("11");
        System.out.println(result);
        Assertions.assertEquals(maxVal.get(), Integer.parseInt(result.substring(3)));

        maxVal.set(0);
        concurrentPostTest("12");
        result = BetAPI.getBetOfferTopHighestStakeList("12");
        System.out.println(result);
        Assertions.assertEquals(maxVal.get(), Integer.parseInt(result.substring(3)));
    }


    public void concurrentPostTest(String betOfferId) {
        String session1 = BetAPI.getNewSessionKey("1");
        String session2 = BetAPI.getNewSessionKey("2");
        String session3 = BetAPI.getNewSessionKey("3");

        //TODO too few parallel threads
        IntStream.range(0, 1000).parallel().forEach(i ->
        {

            Random rand = new Random();
            Integer value = rand.nextInt(1000000);
            maxVal.updateAndGet(n -> (value > n) ? value : n);

            String session = session1;
            switch (rand.nextInt(3)) {
                case 0:
                    session = session1;
                    break;
                case 1:
                    session = session2;
                    break;
                case 2:
                    session = session3;
                    break;
            }

            BetAPI.createCustomerStakeForBetOffer(session, value.toString(), betOfferId);
            BetAPI.getBetOfferTopHighestStakeList(betOfferId);
        });
    }

}
