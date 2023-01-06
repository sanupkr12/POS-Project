package com.increff.pos.dto;

import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.ProductData;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InventoryDto {
    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductService productService;

    public InventoryData convert(InventoryPojo p) throws ApiException {
        InventoryData data = new InventoryData();

        ProductData d = productService.get(p.getBarcode());

        data.setId(p.getId());
        data.setName(d.getName());
        data.setBarcode(p.getBarcode());
        data.setQuantity(p.getQuantity());

        return data;
    }

    private InventoryPojo convert(InventoryForm form){
        InventoryPojo p = new InventoryPojo();
        p.setBarcode(form.getBarcode());
        p.setQuantity(form.getQuantity());

        return p;
    }


}
