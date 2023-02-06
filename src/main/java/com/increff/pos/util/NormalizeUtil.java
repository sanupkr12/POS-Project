package com.increff.pos.util;

import com.increff.pos.model.DaySalesData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.DaySalesPojo;

public class NormalizeUtil {
    public static void normalize(BrandPojo p) {
        p.setCategory(p.getCategory().trim().toLowerCase());
        p.setName(p.getName().toLowerCase().trim());
    }

    public static void normalizeInventory(InventoryForm form){
        form.setBarcode(form.getBarcode().trim());
    }

    public static Float normalizeMrp(Float input) {
        String[] parts = input.toString().split("\\.");
        if (parts.length == 1) return input;

        String integerPart = parts[0];
        String decimalPart = parts[1];

        if (decimalPart.length() <= 2) return input;
        decimalPart = decimalPart.substring(0, 2);

        String doubleStr = integerPart + "." + decimalPart;
        return Float.parseFloat(doubleStr);
    }

    public static Double normalizeMrp(Double input) {
        String[] parts = input.toString().split("\\.");
        if (parts.length == 1) return input;
        String integerPart = parts[0];
        String decimalPart = parts[1];
        if (decimalPart.length() <= 2) return input;
        decimalPart = decimalPart.substring(0, 2);

        String doubleStr = integerPart + "." + decimalPart;
        return Double.parseDouble(doubleStr);
    }

    public static void normalizeProduct(ProductForm form){
        form.setMrp(normalizeMrp(form.getMrp()));
        form.setName(form.getName().toLowerCase().trim());
        form.setBrandCategory(form.getBrandCategory().toLowerCase().trim());
        form.setBrandName(form.getBrandName().toLowerCase().trim());
    }


}
