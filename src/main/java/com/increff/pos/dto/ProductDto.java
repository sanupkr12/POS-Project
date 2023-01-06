package com.increff.pos.dto;

import com.increff.pos.model.ProductData;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

public class ProductDto {
    @Autowired
    private BrandService brandService;

    @Autowired
    private ProductService productService;




    ProductData convert(ProductPojo p) throws ApiException {
        BrandPojo b = brandService.get(p.getBrandId());

        ProductData d = new ProductData();

        d.setId(p.getId());
        d.setName(p.getName());
        d.setMrp(p.getMrp());
        d.setBrandName(b.getName());
        d.setBrandCategory(b.getCategory());
        d.setBarcode(p.getBarcode());

        return d;
    }

}
