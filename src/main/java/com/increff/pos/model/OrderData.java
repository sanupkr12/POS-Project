package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class OrderData {
    int id;
    double total;
    Date date;
    boolean invoiceGenerated;
}
