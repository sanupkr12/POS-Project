package com.increff.pos.dto;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.model.*;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import static com.increff.pos.util.NormalizeUtil.normalizeInventory;
@Service
public class InventoryDto {
    @Autowired
    private InventoryService service;
    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;

    public InventoryData create(InventoryForm form) throws ApiException {
        try{
            validationCheck(form);
            normalizeInventory(form);
            return convert(service.create(form));
        } catch(ApiException e) {
            throw new ApiException(e.getMessage());
        }
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
        List<BrandPojo> brand = brandService.getBrandByCategoryAndBrand(inventoryReportForm.getBrand(),inventoryReportForm.getCategory());
        List<String> barcodeList = new ArrayList<>();
        for(BrandPojo pojo:brand)
        {
            List<ProductPojo> productList = productService.getByBrandId(pojo.getId());
            for(ProductPojo product:productList)
            {
                barcodeList.add(product.getBarcode());
            }
        }
        List<InventoryPojo> list = service.get(barcodeList);
        List<InventoryData> inventoryList = new ArrayList<>();
        for(InventoryPojo pojo:list)
        {
            inventoryList.add(convert(pojo));
        }
        return inventoryList;
    }

    public InventoryData update (InventoryForm form) throws ApiException{
        try{
            validationCheck(form);
            return convert(service.update(form));
        } catch(ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public InventoryData replaceInventory (InventoryForm form) throws ApiException{
        try{
            validationCheck(form);
            return convert(service.replaceInventory(form));
        } catch(ApiException e){
            throw new ApiException(e.getMessage());
        }

    }

    private InventoryData convert(InventoryPojo pojo) throws ApiException {
        InventoryData data = new InventoryData();
        ProductData d = productService.get(pojo.getBarcode());
        data.setId(pojo.getId());
        data.setName(d.getName());
        data.setBarcode(pojo.getBarcode());
        data.setQuantity(pojo.getQuantity());
        return data;
    }

    private void validationCheck(InventoryForm form) throws ApiException {
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
    }
}
