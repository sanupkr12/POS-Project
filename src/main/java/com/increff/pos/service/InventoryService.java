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

    @Transactional
    public InventoryPojo get(String barcode) throws ApiException {

        InventoryPojo p = dao.select(barcode);


        if(p==null)
        {
            throw new ApiException("Inventory with current barcode does not exist");
        }

        return p;

    }

    @Transactional
    public List<InventoryPojo> get() throws ApiException {

        List<InventoryPojo> list = dao.selectAll();

        return list;
    }

    @Transactional
    public List<InventoryPojo> get(List<String> barcodeList) throws ApiException {

        List<InventoryPojo> list = dao.selectInventory(barcodeList);
        return list;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void create(InventoryForm form) throws ApiException {

        if(dao.select(form.getBarcode())!=null)
        {
            throw new ApiException("Inventory already exists");
        }


        dao.insert(convert(form));
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update (InventoryForm form) throws ApiException{


        if(dao.select(form.getBarcode())==null)
        {
            throw new ApiException("Inventory does not exist");
        }





        InventoryPojo p1 = dao.select(form.getBarcode());

        int newQuantity = p1.getQuantity() + form.getQuantity();
        p1.setQuantity(newQuantity);



    }


    @Transactional(rollbackOn = ApiException.class)
    public void updateWithOrder (OrderForm form) throws ApiException{


        if(dao.select(form.getBarcode())==null)
        {
            throw new ApiException("Inventory does not exist");
        }


        InventoryPojo p1 = dao.select(form.getBarcode());

        int orgQuantity = p1.getQuantity();
        int newQuantity = form.getQuantity();

        p1.setQuantity(orgQuantity - newQuantity);


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
