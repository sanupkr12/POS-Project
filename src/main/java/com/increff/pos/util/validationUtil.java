package com.increff.pos.util;

import com.increff.pos.model.BrandForm;
import com.increff.pos.model.ProductForm;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.ProductService;

public class validationUtil {
    public static void checkProductValidity(ProductForm productForm) throws ApiException {
        if(productForm.getName().equals(""))
        {
            throw new ApiException("Product Name cannot be empty");
        }
        if(productForm.getBarcode().equals(""))
        {
            throw new ApiException("Barcode  cannot be empty");
        }
        if(!productForm.getBarcode().trim().matches("\\w+"))
        {
            throw new ApiException("Invalid Barcode");
        }
        if(productForm.getBrandName().equals(""))
        {
            throw new ApiException("Brand Name cannot be empty");
        }
        if(productForm.getBrandCategory().equals(""))
        {
            throw new ApiException("Brand Category cannot be empty");
        }
        if(productForm.getMrp()<=0)
        {
            throw new ApiException("Price cannot be negative");
        }
    }

    public static void checkBrand(BrandForm brandForm) throws ApiException {
        if(brandForm.getName().equals("") || brandForm.getCategory().equals(""))
        {
            throw new ApiException("Name or Category cannot be empty");
        }
    }
}
