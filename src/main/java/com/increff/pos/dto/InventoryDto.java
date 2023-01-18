package com.increff.pos.dto;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.model.*;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class InventoryDto {

    @Autowired
    private InventoryService service;

    @Autowired
    private ProductDto productDto;

    @Autowired
    private BrandDto brandDto;


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

        List<String> barcodeList = brandDto.get(inventoryReportForm);

        List<InventoryPojo> list = service.get(barcodeList);

        List<InventoryData> inventoryList = new ArrayList<>();

        for(InventoryPojo p:list)
        {
            inventoryList.add(convert(p));

        }

        return inventoryList;
    }


    public void create(InventoryForm form) throws ApiException {


        if(form.getBarcode().equals(""))
        {
            throw new ApiException("Barcode cannot be empty");
        }

        if(form.getQuantity()<0)
        {
            throw new ApiException("Quantity cannot be negative");
        }

        if(!productDto.checkAny(form.getBarcode()))
        {
            throw new ApiException("No Product exists with current barcode");
        }

        if(form.getQuantity()<0)
        {
            throw new ApiException("Quantity cannot be less than zero");
        }

        normalizeInventory(form);
        service.create(form);
    }


    public void update (InventoryForm form) throws ApiException{
        if(form.getBarcode().equals(""))
        {
            throw new ApiException("Barcode cannot be empty");
        }

        if(form.getQuantity()<0)
        {
            throw new ApiException("Quantity cannot be negative");
        }

        if(!productDto.checkAny(form.getBarcode()))
        {
            throw new ApiException("No Product exists with current barcode");
        }

        service.update(form);
    }





    public void updateWithOrder (OrderForm form) throws ApiException{
        if(!productDto.checkAny(form.getBarcode()))
        {
            throw new ApiException("No Product exists with current barcode");
        }

        service.updateWithOrder(form);

    }

    public boolean checkForInsufficientInventory(OrderForm form) throws ApiException {
        InventoryPojo p1 = service.get(form.getBarcode());

        int orgQuantity = p1.getQuantity();
        int newQuantity = form.getQuantity();


        if(newQuantity<=orgQuantity)
        {
            return false;
        }
        else{
            return true;
        }


    }

    public InventoryData convert(InventoryPojo p) throws ApiException {
        InventoryData data = new InventoryData();



        ProductData d = productDto.get(p.getBarcode());


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

    private void normalizeInventory(InventoryForm form){
        form.setBarcode(form.getBarcode().trim());
    }


}
