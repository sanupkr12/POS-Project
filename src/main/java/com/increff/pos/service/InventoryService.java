package com.increff.pos.service;


import com.increff.pos.dao.InventoryDao;
import com.increff.pos.model.*;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryService {
    @Autowired
    private InventoryDao dao;

    @Autowired
    private ProductService service;


    @Autowired
    private BrandService brandService;

    @Transactional
    public InventoryData get(String barcode) throws ApiException {

        InventoryPojo p = dao.select(barcode);


        if(p==null)
        {
            throw new ApiException("Inventory with current barcode does not exist");
        }

        return convert(p);

    }

    @Transactional
    public List<InventoryData> get() throws ApiException {

        List<InventoryPojo> list = dao.selectAll();

        List<InventoryData> data = new ArrayList<InventoryData>();

        for (InventoryPojo l:list){
            data.add(convert(l));
        }

        return data;
    }

    @Transactional
    public List<InventoryData> get(InventoryReportForm inventoryReportForm) throws ApiException {
        List<String> barcodeList = brandService.get(inventoryReportForm);



        List<InventoryPojo> list = dao.selectInventory(barcodeList);



        List<InventoryData> inventoryList = new ArrayList<>();

        for(InventoryPojo p:list)
        {
            inventoryList.add(convert(p));


        }

       return inventoryList;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void create(InventoryForm form) throws ApiException {

        if(!service.checkAny(form.getBarcode()))
        {
           throw new ApiException("No Product exists with current barcode");
        }

        if(dao.select(form.getBarcode())!=null)
        {
            throw new ApiException("Inventory already exists");
        }

        if(form.getQuantity()<=0)
        {
            throw new ApiException("Quantity cannot be less than zero");
        }

        normalizeInventory(form);

        dao.insert(convert(form));
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update (InventoryForm form) throws ApiException{
        if(!service.checkAny(form.getBarcode()))
        {
            throw new ApiException("No Product exists with current barcode");
        }

        if(dao.select(form.getBarcode())==null)
        {
            throw new ApiException("Inventory does not exist");
        }





        InventoryPojo p1 = dao.select(form.getBarcode());

        int newQuantity = p1.getQuantity() + form.getQuantity();
        p1.setQuantity(newQuantity);



    }


    public boolean checkForInsufficientInventory(OrderForm form) throws ApiException {
        InventoryPojo p1 = dao.select(form.getBarcode());

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

    @Transactional(rollbackOn = ApiException.class)
    public void updateWithOrder (OrderForm form) throws ApiException{
        if(!service.checkAny(form.getBarcode()))
        {
            throw new ApiException("No Product exists with current barcode");
        }

        if(dao.select(form.getBarcode())==null)
        {
            throw new ApiException("Inventory does not exist");
        }


        InventoryPojo p1 = dao.select(form.getBarcode());

        int orgQuantity = p1.getQuantity();
        int newQuantity = form.getQuantity();

        p1.setQuantity(orgQuantity - newQuantity);


    }



    public InventoryData convert(InventoryPojo p) throws ApiException {
        InventoryData data = new InventoryData();



        ProductData d = service.get(p.getBarcode());


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
