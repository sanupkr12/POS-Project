package com.increff.pos.util;

import com.increff.pos.model.*;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

public class ConvertUtil {

    public static BrandData convert(BrandPojo p) {
        BrandData d = new BrandData();
        d.setCategory(p.getCategory());
        d.setName(p.getName());
        d.setId(p.getId());
        return d;
    }

    public static BrandPojo convert(BrandForm f) {
        BrandPojo p = new BrandPojo();
        p.setCategory(f.getCategory());
        p.setName(f.getName());
        return p;
    }


    public static DaySalesData convert(DaySalesPojo p)
    {
        DaySalesData data = new DaySalesData();
        data.setDate(p.getDate());
        data.setItemCount(p.getItemCount());
        data.setRevenue(p.getRevenue());
        data.setOrderCount(p.getOrderCount());
        return data;
    }


}
