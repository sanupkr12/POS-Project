package com.increff.pos.service;

import static org.junit.Assert.assertEquals;

import com.increff.pos.dto.BrandDto;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.AbstractUnitTest;
import com.increff.pos.service.ApiException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.increff.pos.service.BrandService;


public class BrandAppService extends AbstractUnitTest {

    @Autowired
    private BrandDto brandDto;

//    @Test
//    public void testAdd() throws ApiException {
//        BrandPojo p = new BrandPojo();
//        p.setName(" Romil Jain ");
//        brandDto.add(p);
//    }
//
//    @Test
//    public void testNormalize() {
//        BrandPojo p = new BrandPojo();
//        p.setName(" Romil Jain ");
//        brandDto.normalize(p);
//        assertEquals("romil jain", p.getName());
//    }

}