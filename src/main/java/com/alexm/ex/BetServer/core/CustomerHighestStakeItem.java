package com.alexm.ex.BetServer.core;

import java.util.Objects;

public class CustomerHighestStakeItem {
    private int customerid;
    private int highestStake;

    public CustomerHighestStakeItem(final int customerid, final int highestStake) {
        this.customerid = customerid;
        this.highestStake = highestStake;
    }


    public int getHighestStake() {
        return highestStake;
    }

    public int getHighestStakeNegated() {
        return -highestStake;
    }


    @Override
    public String toString() {
        return Integer.toUnsignedString(customerid) + "=" + Integer.toUnsignedString(highestStake);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerHighestStakeItem that = (CustomerHighestStakeItem) o;

        return Objects.equals(this.customerid, that.customerid)
                && Objects.equals(this.highestStake, that.highestStake);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerid, highestStake);
    }
}
