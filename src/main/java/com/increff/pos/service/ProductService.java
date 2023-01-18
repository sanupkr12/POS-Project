package com.increff.pos.service;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Service
public class ProductService {
    @Autowired
    private ProductDao dao;


    @Transactional(rollbackOn = ApiException.class)
    public void add(ProductPojo prod) throws ApiException{
        if(dao.checkAny(prod.getBarcode()))
        {
            throw new ApiException("Product with current barcode already exists");
        }

        dao.insert(prod);

    }

    @Transactional(rollbackOn = ApiException.class)
    public void delete(int id) throws ApiException{

        if(!dao.checkAny(id))
        {
            throw new ApiException("Product with current barcode does not exist");
        }


        dao.deleteProduct(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public ProductPojo get(String barcode) throws ApiException{
        return dao.select(barcode);
    }

    @Transactional(rollbackOn = ApiException.class)
    public ProductPojo get(int id) throws ApiException{
        return dao.selectById(id);
    }


    public List<ProductPojo> getByBrandId(int brandId) throws ApiException {

        List<ProductPojo> list = dao.selectByBrandId(brandId);

        return list;
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<ProductPojo> get() throws ApiException{
        List<ProductPojo> p = dao.selectAll();
        return p;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(ProductForm p,int id,int brandId) throws ApiException{
        ProductPojo prod = dao.selectById(id);

        if(prod==null)
        {
            throw new ApiException("No Product Found with current barcode");
        }

        prod.setName(p.getName());
        prod.setMrp(p.getMrp());
        prod.setBarcode(p.getBarcode());
        prod.setBrandId(brandId);

    }


    @Transactional
    public boolean checkAny(String barcode){
        ProductPojo p = dao.select(barcode);


        if(p==null)
        {
            return false;
        }


        return true;
    }



}
