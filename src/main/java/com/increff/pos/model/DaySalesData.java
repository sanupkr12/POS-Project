package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
public class DaySalesData {
    private Date date;
    private int orderCount;
    private int itemCount;
    private double revenue;

}
