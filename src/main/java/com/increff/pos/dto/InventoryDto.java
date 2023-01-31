package com.increff.pos.dto;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.model.*;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class InventoryDto {

    @Autowired
    private InventoryService service;

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    public InventoryData create(InventoryForm form) throws ApiException {


        if(form.getBarcode().equals(""))
        {
            throw new ApiException("Barcode cannot be empty");
        }

        if(form.getQuantity()<0)
        {
            throw new ApiException("Quantity cannot be negative");
        }

        if(!productService.checkAny(form.getBarcode()))
        {
            throw new ApiException("No Product exists with current barcode");
        }

        normalizeInventory(form);

        return convert(service.create(form));
    }


    public InventoryData get(String barcode) throws ApiException {
        InventoryPojo p = service.get(barcode);
        return convert(p);
    }


    public List<InventoryData> get() throws ApiException {

        List<InventoryPojo> list = service.get();

        List<InventoryData> data = new ArrayList<InventoryData>();

        for (InventoryPojo l:list){
            data.add(convert(l));
        }
        return data;
    }


    public List<InventoryData> get(InventoryReportForm inventoryReportForm) throws ApiException {

        List<String> barcodeList = brandService.get(inventoryReportForm);

        List<InventoryPojo> list = service.get(barcodeList);

        List<InventoryData> inventoryList = new ArrayList<>();

        for(InventoryPojo p:list)
        {
            inventoryList.add(convert(p));

        }

        return inventoryList;
    }





    public InventoryData update (InventoryForm form) throws ApiException{
        if(form.getBarcode().equals(""))
        {
            throw new ApiException("Barcode cannot be empty");
        }

        if(!form.getBarcode().trim().matches("\\w+"))
        {
            throw new ApiException("Invalid Barcode");
        }

        if(form.getQuantity()<0)
        {
            throw new ApiException("Quantity cannot be negative");
        }

        if(!productService.checkAny(form.getBarcode()))
        {
            throw new ApiException("No Product exists with current barcode");
        }

        return convert(service.update(form));
    }

    public InventoryData replaceInventory (InventoryForm form) throws ApiException{
        if(form.getBarcode().equals(""))
        {
            throw new ApiException("Barcode cannot be empty");
        }

        if(form.getQuantity()<0)
        {
            throw new ApiException("Quantity cannot be negative");
        }

        if(!productService.checkAny(form.getBarcode()))
        {
            throw new ApiException("No Product exists with current barcode");
        }

        return convert(service.replaceInventory(form));
    }





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

    protected void normalizeInventory(InventoryForm form){
        form.setBarcode(form.getBarcode().trim());
    }


}
