package com.increff.pos.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class DaySalesPojo {

    @Id
    private Date date;
    private int orderCount;
    private int itemCount;
    private double revenue;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }


    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }
}
