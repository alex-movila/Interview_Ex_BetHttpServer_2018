package com.alexm.ex.BetServer.core;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BetOfferStakeData {

    private static volatile int topMaxSize = 20;
    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);
    private Map<Integer, CustomerHighestStakeItem> customerListWithHighestStakes;
    private SortedSet<CustomerHighestStakeItem> topCustomerListWithHighestStakes;

    public BetOfferStakeData() {
        customerListWithHighestStakes = new HashMap<>();
        topCustomerListWithHighestStakes = new TreeSet<>(Comparator.comparing(CustomerHighestStakeItem::getHighestStakeNegated));
    }

    public static void setTopMaxSize(int topMaxSize) {
        BetOfferStakeData.topMaxSize = topMaxSize;
    }


    public void createBetOffer(int customerId, int stake) {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();

        try {
            createBetOfferInternal(customerId, stake);
        } finally {

            writeLock.unlock();
        }

    }

    private void createBetOfferInternal(int customerId, int stake) {

        CustomerHighestStakeItem prevCustomerHighestStakeItem = customerListWithHighestStakes.get(customerId);

        if (prevCustomerHighestStakeItem == null || stake > prevCustomerHighestStakeItem.getHighestStake()) {
            CustomerHighestStakeItem newCustomerHighestStakeItem = new CustomerHighestStakeItem(customerId, stake);
            //replace with new highest stake for this customer
            customerListWithHighestStakes.put(customerId, newCustomerHighestStakeItem);

            //maintain sorted top list
            updateSortedTopList(prevCustomerHighestStakeItem, newCustomerHighestStakeItem);
        }
    }


    private void updateSortedTopList(CustomerHighestStakeItem prevCustomerHighestStakeItem,
                                     CustomerHighestStakeItem newCustomerHighestStakeItem) {
        if (prevCustomerHighestStakeItem != null) {
            topCustomerListWithHighestStakes.remove(prevCustomerHighestStakeItem);
        }

        if (newCustomerHighestStakeItem != null) {
            topCustomerListWithHighestStakes.add(newCustomerHighestStakeItem);
        }

        if (topCustomerListWithHighestStakes.size() > topMaxSize) {
            topCustomerListWithHighestStakes.remove(topCustomerListWithHighestStakes.last());
        }
    }

    public String getTopCustomerListWithHighestStakes() {
        Lock readLock = rwLock.readLock();
        readLock.lock();

        try {
            return topCustomerListWithHighestStakes.stream().map(CustomerHighestStakeItem::toString).reduce("", (a, b) -> a + "," + b);
        } finally {

            readLock.unlock();
        }
    }
}
