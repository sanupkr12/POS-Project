package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductForm {

    private int id;
    private String barcode;
    private String name;
    private String brandName;
    private String brandCategory;
    private double mrp;

}
