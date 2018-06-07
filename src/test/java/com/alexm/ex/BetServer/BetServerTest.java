package com.alexm.ex.BetServer;

import com.alexm.ex.BetServer.core.BetOfferStakeData;
import com.alexm.ex.BetServer.httpclient.BetHttpClient;
import com.alexm.ex.BetServer.httpclient.BetHttpClientResponse;
import com.alexm.ex.BetServer.httpserver.BetHttpServer;
import com.alexm.ex.BetServer.session.BetHttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class BetServerTest {

    @BeforeAll
    public static void setupServer() {
        // Lambda Runnable
        Runnable task2 = () -> {
            BetHttpServer betHttpServer = new BetHttpServer();
            betHttpServer.start();
        };

        Thread thr = new Thread(task2);
        thr.start();

    }

    private boolean postStake(Integer betOfferID, Integer stake, String sessionId) throws IOException, InterruptedException, URISyntaxException {
        BetHttpClientResponse response = BetHttpClient.sendPostRequest(betOfferID + "/stake", sessionId, stake.toString());
        return (response.getStatus() == 200);
    }

    private String postStakeList(Integer betOfferID, Integer customerId, List<Integer> stakeList, String sessionId) throws IOException, InterruptedException, URISyntaxException {

        if (sessionId == null) {
            sessionId = BetHttpClient.sendGetRequest(customerId + "/session").getBody();
        }

        for (Integer stake : stakeList) {
            postStake(betOfferID, stake, sessionId);
        }
        return sessionId;
    }

    @Test
    public void oneBigITTest() throws InterruptedException, IOException, URISyntaxException {

        BetHttpSession.setExpirationTimeOutSeconds(10);

        //set top list max size to 2 instead of 20
        BetOfferStakeData.setTopMaxSize(2);

        postStakeList(888, 1, Arrays.asList(300, 400, 100, 200), null);
        postStakeList(888, 2, Arrays.asList(300, 500, 600, 200), null);
        String session3 = postStakeList(888, 3, Arrays.asList(300, 500, 700, 200), null);

        String result = getHighStakes(888);
        Assertions.assertEquals(",3=700,2=600", result);
        System.out.println("Top list for stake " + "888" + " is:" + result);

        postStakeList(777, 3, Arrays.asList(100, 500, 850, 200), session3);

        postStakeList(777, 4, Arrays.asList(100, 500, 750, 200), null);

        result = getHighStakes(777);

        Assertions.assertEquals(",3=850,4=750", result);
        System.out.println("Top list for stake " + "777" + " is:" + result);

        postStake(888, 851, session3);

        result = getHighStakes(888);
        Assertions.assertEquals(",3=851,2=600", result);
        System.out.println("Top list for stake " + "888" + " is:" + result);
    }

    private String getHighStakes(Integer betOfferId) throws IOException, InterruptedException, URISyntaxException {
        return BetHttpClient.sendGetRequest(betOfferId + "/highstakes").getBody();
    }


    @Test
    public void sessionExpireTest() throws InterruptedException, IOException, URISyntaxException {

        //set expire session to 5 secs
        BetHttpSession.setExpirationTimeOutSeconds(5);

        //set top list max size to 1 instead of 20
        BetOfferStakeData.setTopMaxSize(2);

        String session = postStakeList(111, 5, Arrays.asList(100), null);

        Thread.currentThread().sleep(3 * 1000);

        //post stake after 3 secs
        boolean isPosted = postStake(111, 150, session);
        Assertions.assertTrue(isPosted);

        //check that 150 stake post succeeded
        String result = getHighStakes(111);

        Assertions.assertEquals(",5=150", result);

        //sleep another 6 sec
        Thread.currentThread().sleep(6 * 1000);

        //check session expired
        isPosted = postStake(111, 200, session);
        Assertions.assertFalse(isPosted);

        //check that 200 stake post failed
        result = getHighStakes(111);
        Assertions.assertEquals(",5=150", result);

    }
}
