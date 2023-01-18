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
import java.util.List;

@Component
public class BrandDto {
    @Autowired
    private ProductDto productDto;

    @Autowired
    private BrandService brandService;


    public void add(BrandForm form) throws ApiException {

        if(form.getName().equals("") || form.getCategory().equals(""))
        {
            throw new ApiException("Name or Category cannot be empty");
        }

        BrandPojo p = convert(form);
        normalize(p);
        brandService.add(p);
    }


    public void delete(int id) throws ApiException{
        brandService.delete(id);
    }


    public BrandData get(int id) throws ApiException {
        BrandPojo p = brandService.get(id);
        return convert(p);
    }


    public List<BrandData> getAll() {
        List<BrandPojo> list = brandService.getAll();
        List<BrandData> list2 = new ArrayList<BrandData>();
        for (BrandPojo p : list) {
            list2.add(convert(p));
        }
        return list2;
    }


    public void update(int id, BrandForm form) throws ApiException {

        BrandPojo p = convert(form);
        normalize(p);

        brandService.update(id,p);
    }


    public BrandPojo get(String name,String category) throws ApiException{
        return brandService.get(name,category);
    }

    public List<String> getCategory(){
        return brandService.getCategory();

    }
    public List<String> getBrandList(){
        return brandService.getBrandList();
    }

    public BrandPojo getCheck(int id) throws ApiException {
        return brandService.getCheck(id);
    }



    public List<String> get(InventoryReportForm form) throws ApiException {

        List<BrandPojo> list = brandService.get(form);

        List<String> barcodeList = new ArrayList<>();


        for(BrandPojo p:list)
        {


            List<ProductPojo> productList = productDto.getByBrandId(p.getId());

            for(ProductPojo d:productList)
            {

                barcodeList.add(d.getBarcode());
            }
        }

        return barcodeList;
    }

    public List<BrandData> getBrand(BrandReportForm form) throws ApiException {
        List<BrandPojo> list = brandService.getBrand(form);

        List<BrandData> data = new ArrayList<>();

        for(BrandPojo p:list)
        {
            data.add(convert(p));
        }


        return data;

    }

    public List<BrandPojo> getSales(SalesReportForm form) throws ApiException {
        return brandService.getSales(form);
    }

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




    protected static void normalize(BrandPojo p) {
        p.setCategory(p.getCategory().trim().toLowerCase());
        p.setName(p.getName().toLowerCase().trim());
    }

    private static BrandData convert(BrandPojo p) {
        BrandData d = new BrandData();
        d.setCategory(p.getCategory());
        d.setName(p.getName());
        d.setId(p.getId());
        return d;
    }

    private static BrandPojo convert(BrandForm f) {
        BrandPojo p = new BrandPojo();
        p.setCategory(f.getCategory());
        p.setName(f.getName());
        return p;
    }





}
