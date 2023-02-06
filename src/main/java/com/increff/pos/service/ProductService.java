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
    @Autowired
    private BrandService brandService;
    @Autowired
    private InventoryService inventoryService;

    @Transactional(rollbackOn = ApiException.class)
    public ProductPojo add(ProductPojo prod) throws ApiException{
        if(dao.checkAny(prod.getBarcode()))
        {
            throw new ApiException("Product with current barcode already exists");
        }
        dao.insert(prod);
        return prod;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void delete(int id) throws ApiException{
        if(!dao.checkAny(id))
        {
            throw new ApiException("Product with current barcode does not exist");
        }
        dao.deleteProduct(id);
    }

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

    public ProductData get(String barcode) throws ApiException{
        ProductPojo p = dao.select(barcode);
        if(p==null)
        {
            return null;
        }
        return convert(p);
    }

    @Transactional(rollbackOn = ApiException.class)
    public ProductPojo update(ProductForm p,int id,int brandId) throws ApiException{
        ProductPojo prod = dao.selectById(id);
        if(prod==null)
        {
            throw new ApiException("No Product Found with current barcode");
        }
        prod.setName(p.getName());
        prod.setMrp(p.getMrp());
        prod.setBarcode(p.getBarcode());
        prod.setBrandId(brandId);
        return prod;
    }

    //TODO make it a private
    private ProductData convert(ProductPojo p) throws ApiException{
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

    public boolean checkAny(String barcode){
        ProductPojo p = dao.select(barcode);
        if(p==null)
        {
            return false;
        }
        return true;
    }
}
