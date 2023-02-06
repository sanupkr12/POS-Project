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
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import static com.increff.pos.util.NormalizeUtil.normalizeProduct;
import static com.increff.pos.util.validationUtil.checkProductValidity;

@Service
public class ProductDto {
    @Autowired
    private ProductService service;
    @Autowired
    private BrandService brandService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ProductService productService;

    public ProductData addProduct(ProductForm productForm) throws ApiException{
        try{
            checkProductValidity(productForm);
            normalizeProduct(productForm);
            BrandPojo brand  = brandService.get(productForm.getBrandName(),productForm.getBrandCategory());
            ProductPojo prod = new ProductPojo();
            prod.setBarcode(productForm.getBarcode());
            prod.setBrandId(brand.getId());
            prod.setName(productForm.getName());
            prod.setMrp(productForm.getMrp());
            ProductPojo pojo = service.add(prod);
            InventoryForm inventoryForm = new InventoryForm();
            inventoryForm.setBarcode(prod.getBarcode());
            inventoryForm.setQuantity(0);
            inventoryService.create(inventoryForm);
            return convert(pojo);
        } catch (ApiException e){
            throw new ApiException(e.getMessage());
        }
    }

    public void deleteProduct(int id) throws ApiException{
        service.delete(id);
    }

    public ProductData getProductById(int id) throws ApiException{
        ProductPojo p = service.get(id);
        if(p==null)
        {
            throw new ApiException("No Product Found");
        }
        return convert(p);
    }

    public List<ProductData> getAllProduct() throws ApiException{
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

    public ProductData updateProduct(ProductForm productForm,int id) throws ApiException{

        try{
            normalizeProduct(productForm);
            checkProductValidity(productForm);
            ProductData data = productService.get(productForm.getBarcode());
            ProductPojo pojo = productService.get(id);
            if(data!=null)
            {
                if(!data.getBarcode().equals(pojo.getBarcode()))
                {
                    throw new ApiException("Barcode already exists");
                }
            }
            BrandPojo b = brandService.get(productForm.getBrandName(),productForm.getBrandCategory());
            if(b==null)
            {
                throw new ApiException("No brand exist with current details");
            }
            if(!pojo.getBarcode().equals(productForm.getBarcode()))
            {
                inventoryService.updateWithProduct(pojo.getBarcode(),productForm.getBarcode());
            }
            return convert(service.update(productForm,id,b.getId()));
        } catch (ApiException e)
        {
            throw new ApiException(e.getMessage());
        }

    }

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
}
