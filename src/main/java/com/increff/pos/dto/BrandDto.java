package com.increff.pos.dto;

import com.increff.pos.model.*;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.reverse;

@Component
public class BrandDto {
            @Autowired
            private ProductService productService;

            @Autowired
            private BrandService brandService;

            //Done
            public BrandData add(BrandForm form) throws ApiException {

                if(form.getName().equals("") || form.getCategory().equals(""))
                {
                    throw new ApiException("Name or Category cannot be empty");
                }

                BrandPojo p = convert(form);
                normalize(p);
                brandService.add(p);

                return convert(p);

            }


            public void delete(int id) throws ApiException{
                brandService.delete(id);
            }

            //Done
            public BrandData get(int id) throws ApiException {
                BrandPojo p = brandService.get(id);
                return convert(p);
            }

            //Done
            public List<BrandData> getAll() {
                List<BrandPojo> list = brandService.getAll();
                List<BrandData> list2 = new ArrayList<BrandData>();
                for (BrandPojo p : list) {
                    list2.add(convert(p));
                }
                return list2;
            }


            //Done
            public BrandData update(int id, BrandForm form) throws ApiException {

                BrandPojo p = convert(form);
                normalize(p);

                BrandPojo pojo = brandService.update(id,p);
                return convert(pojo);
            }

            //Done
            public BrandPojo get(String name,String category) throws ApiException{
                return brandService.get(name,category);
            }

            //Done
            public List<String> getCategory(){
                return brandService.getCategory();

            }

            //Done
            public List<String> getBrandList(){
                return brandService.getBrandList();
            }


            //Done
            public List<String> getBrandByCategory(String category){
                List<BrandPojo> brandList = brandService.getBrandByCategory(category);
                List<String> list = new ArrayList<>();

                for(BrandPojo p:brandList){
                    if(p.getCategory().equals(category))
                    {
                        list.add(p.getName());
                    }
                }

                return list;

            }


            //Done
            public List<String> getCategoryByBrand(String brand){
                List<BrandPojo> brandList = brandService.getCategoryByBrand(brand);
                List<String> list = new ArrayList<>();

                for(BrandPojo p:brandList){
                    if(p.getName().equals(brand))
                    {
                        list.add(p.getCategory());
                    }
                }

                return list;


            }



            //Done
            protected static void normalize(BrandPojo p) {
                p.setCategory(p.getCategory().trim().toLowerCase());
                p.setName(p.getName().toLowerCase().trim());
            }


            //Done
            protected static BrandData convert(BrandPojo p) {
                BrandData d = new BrandData();
                d.setCategory(p.getCategory());
                d.setName(p.getName());
                d.setId(p.getId());
                return d;
            }
            //Done
            protected static BrandPojo convert(BrandForm f) {
                BrandPojo p = new BrandPojo();
                p.setCategory(f.getCategory());
                p.setName(f.getName());
                return p;
            }





}
