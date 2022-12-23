package com.increff.pos.service;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.model.ProductUpdateForm;
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
    @Autowired
    private BrandService brandService;

    @Transactional(rollbackOn = ApiException.class)
    public void add(ProductForm p) throws ApiException{
        if(dao.checkAny(p.getBarcode()))
        {
            throw new ApiException("Product with current barcode already exists");
        }



        BrandPojo p1  = brandService.get(p.getBrandName(),p.getBrandCategory());

        ProductPojo prod = new ProductPojo();
        prod.setBarcode(p.getBarcode());
        prod.setBrandId(p1.getId());
        prod.setName(p.getName());
        prod.setMrp(p.getMrp());


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
    public ProductData get(String barcode) throws ApiException{
        ProductPojo p = dao.select(barcode);
        if(p==null)
        {
            throw new ApiException("No Product Found");
        }



        return convert(p);
    }

    @Transactional(rollbackOn = ApiException.class)
    public ProductData get(int id) throws ApiException{
        ProductPojo p = dao.selectById(id);
        if(p==null)
        {
            throw new ApiException("No Product Found");
        }



        return convert(p);
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<ProductData> get() throws ApiException{
        List<ProductPojo> p = dao.selectAll();

        if(p==null)
        {
            throw new ApiException("No Product Found");
        }

        List<ProductData> list = new ArrayList<ProductData>();

        for(ProductPojo p1:p){
            list.add(convert(p1));
        }

        return list;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(ProductForm p,int id) throws ApiException{
        ProductPojo prod = dao.selectById(id);


        if(prod==null)
        {
            throw new ApiException("No Product Found with current barcode");
        }

        BrandPojo b = brandService.get(p.getBrandName(),p.getBrandCategory());

        if(b==null)
        {
            throw new ApiException("No brand exist with current details");
        }


        prod.setName(p.getName());
        prod.setMrp(p.getMrp());
        prod.setBarcode(p.getBarcode());
        prod.setBrandId(b.getId());

    }

    @Transactional
    ProductData convert(ProductPojo p) throws ApiException{
        BrandPojo b = brandService.get(p.getBrandId());

        ProductData d = new ProductData();

        d.setId(p.getId());
        d.setName(p.getName());
        d.setMrp(p.getMrp());
        d.setBrandName(b.getName());
        d.setBrandCategory(b.getCategory());
        d.setBarcode(p.getBarcode());

        return d;
    }




}
