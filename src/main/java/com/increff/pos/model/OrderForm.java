package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderForm {
    private String barcode;
    private int quantity;
    private float sellingPrice;

}
