package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class OrderItemData {
    private int id;
    private int itemId;
    private String productName;
    private String barcode;
    private int quantity;
    private double total;
    private Date date;

}
