package com.ryletech.supaorders.model;

/**
 * Created by sydney on 8/26/16.
 */

public class Order {

    private String orderId, orderedDate;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(String orderedDate) {
        this.orderedDate = orderedDate;
    }
}
