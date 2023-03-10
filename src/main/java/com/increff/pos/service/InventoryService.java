package com.increff.pos.service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.model.*;
import com.increff.pos.pojo.BrandPojo;
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
    private ProductService productService;

    @Transactional(rollbackOn = ApiException.class)
    public InventoryPojo create(InventoryForm form) throws ApiException {
        if(dao.select(form.getBarcode())!=null)
        {
            throw new ApiException("Inventory already exists");
        }
        InventoryPojo p = convert(form);
        dao.insert(p);
        return p;
    }

    @Transactional
    public InventoryPojo get(String barcode) throws ApiException {
        InventoryPojo p = dao.select(barcode);
        if(p==null)
        {
            throw new ApiException("Inventory with current barcode does not exist");
        }
        return p;
    }

    public List<InventoryPojo> get() throws ApiException {
        List<InventoryPojo> list = dao.selectAll();
        return list;
    }

    public List<InventoryPojo> get(List<String> barcodeList) throws ApiException {
        List<InventoryPojo> list = dao.selectInventory(barcodeList);
        return list;
    }

    @Transactional(rollbackOn = ApiException.class)
    public InventoryPojo update(InventoryForm form) throws ApiException{
        InventoryPojo p1 = dao.select(form.getBarcode());
        if(p1==null)
        {
            throw new ApiException("Inventory does not exist");
        }
        int newQuantity = p1.getQuantity() + form.getQuantity();
        p1.setQuantity(newQuantity);
        return p1;
    }
    @Transactional(rollbackOn = ApiException.class)
    public InventoryPojo replaceInventory (InventoryForm form) throws ApiException{
        if(dao.select(form.getBarcode())==null)
        {
            throw new ApiException("Inventory does not exist");
        }
        InventoryPojo p1 = dao.select(form.getBarcode());
        p1.setQuantity(form.getQuantity());
        return p1;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void updateWithOrder (OrderForm form) throws ApiException{
        if(!productService.checkAny(form.getBarcode()))
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

    @Transactional(rollbackOn = ApiException.class)
    public void updateWithProduct(String oldBarcode,String newBarcode){
        InventoryPojo pojo = dao.select(oldBarcode);
        pojo.setBarcode(newBarcode);
    }

    public boolean checkForInsufficientInventory(OrderForm form) throws ApiException {
        InventoryPojo p1 = get(form.getBarcode());
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


    private static InventoryPojo convert(InventoryForm form){
        InventoryPojo p = new InventoryPojo();
        p.setBarcode(form.getBarcode());
        p.setQuantity(form.getQuantity());
        return p;
    }
}
