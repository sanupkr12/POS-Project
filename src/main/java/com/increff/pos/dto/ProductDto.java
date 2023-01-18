package com.increff.pos.dto;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
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
public class ProductDto {

    @Autowired
    private ProductService service;

    @Autowired
    private BrandDto brandDto;

    @Autowired
    private InventoryDto inventoryDto;


    @Transactional(rollbackOn = ApiException.class)
    public void add(ProductForm p) throws ApiException{
        if(p.getName().equals(""))
        {
            throw new ApiException("Product Name cannot be empty");
        }
        if(p.getBarcode().equals(""))
        {
            throw new ApiException("Barcode  cannot be empty");
        }

        if(p.getBrandName().equals(""))
        {
            throw new ApiException("Brand Name cannot be empty");
        }

        if(p.getMrp()<=0)
        {
            throw new ApiException("Price cannot be negative");
        }

        normalizeProduct(p);

        BrandPojo p1  = brandDto.get(p.getBrandName(),p.getBrandCategory());

        ProductPojo prod = new ProductPojo();
        prod.setBarcode(p.getBarcode());
        prod.setBrandId(p1.getId());
        prod.setName(p.getName());
        prod.setMrp(p.getMrp());

        service.add(prod);


        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setBarcode(p.getBarcode());
        inventoryForm.setQuantity(0);
        inventoryDto.create(inventoryForm);


    }

    @Transactional(rollbackOn = ApiException.class)
    public void delete(int id) throws ApiException{
        service.delete(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public ProductData get(String barcode) throws ApiException{
        ProductPojo p = service.get(barcode);
        return convert(p);
    }

    @Transactional(rollbackOn = ApiException.class)
    public ProductData get(int id) throws ApiException{
        ProductPojo p = service.get(id);

        if(p==null)
        {
            throw new ApiException("No Product Found");
        }


        return convert(p);
    }

    public List<ProductPojo> getByBrandId(int brandId) throws ApiException {
        return service.getByBrandId(brandId);
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<ProductData> get() throws ApiException{
        List<ProductPojo> p = service.get();

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
        if(p.getName().equals(""))
        {
            throw new ApiException("Product Name cannot be empty");
        }
        if(p.getBarcode().equals(""))
        {
            throw new ApiException("Barcode  cannot be empty");
        }

        if(p.getBrandName().equals(""))
        {
            throw new ApiException("Brand Name cannot be empty");
        }

        if(p.getMrp()<=0)
        {
            throw new ApiException("Price cannot be negative");
        }

        BrandPojo b = brandDto.get(p.getBrandName(),p.getBrandCategory());

        if(b==null)
        {
            throw new ApiException("No brand exist with current details");
        }

        service.update(p,id,b.getId());
    }

    @Transactional
    public boolean checkAny(String barcode){
        return service.checkAny(barcode);
    }

    @Transactional
    ProductData convert(ProductPojo p) throws ApiException{
        BrandData b = brandDto.get(p.getBrandId());

        ProductData d = new ProductData();

        d.setId(p.getId());
        d.setName(p.getName());
        d.setMrp(p.getMrp());
        d.setBrandName(b.getName());
        d.setBrandCategory(b.getCategory());
        d.setBarcode(p.getBarcode());

        return d;
    }


    void normalizeProduct(ProductForm form){
        form.setName(form.getName().toLowerCase().trim());
        form.setBrandCategory(form.getBrandCategory().toLowerCase().trim());
        form.setBrandName(form.getBrandName().toLowerCase().trim());
    }

}
