package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditOrderForm {
    int itemId;
    String barcode;
    int quantity;
}
