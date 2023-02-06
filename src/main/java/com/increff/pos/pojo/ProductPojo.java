package com.increff.pos.pojo;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class ProductPojo {
    String barcode;
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private int brandId;
    @Column(nullable = false)
    private String name;
    private Double mrp;


}
