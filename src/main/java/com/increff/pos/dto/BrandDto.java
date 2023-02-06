package com.increff.pos.dto;
import com.increff.pos.model.*;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.google.common.collect.Lists.reverse;
import static com.increff.pos.util.ConvertUtil.convert;
import static com.increff.pos.util.NormalizeUtil.normalize;
@Service
public class BrandDto {
    @Autowired
    private BrandService brandService;

    public BrandData addBrand(BrandForm form) throws ApiException {
        try{
            BrandPojo brand = convert(form);
            normalize(brand);
            brandService.add(brand);
            return convert(brand);
        } catch(ApiException e)
        {
            throw new ApiException(e.getMessage());
        }
    }

    public void deleteBrand(int id) throws ApiException{
        brandService.delete(id);
    }

    public BrandData getBrand(int id) throws ApiException {
        BrandPojo brand = brandService.get(id);
        return convert(brand);
    }

    public List<BrandData> getAllBrand() {
        List<BrandPojo> list = brandService.getAll();
        List<BrandData> brandList = new ArrayList<BrandData>();
        for (BrandPojo brand : list) {
            brandList.add(convert(brand));
        }
        return brandList;
    }

    public BrandData updateBrand(int id, BrandForm form) throws ApiException {
        BrandPojo brand = convert(form);
        normalize(brand);
        try{
            BrandPojo pojo = brandService.update(id,brand);
            return convert(pojo);
        } catch (ApiException e)
        {
            throw new ApiException(e.getMessage());
        }
    }

    public BrandPojo getBrand(String name,String category) throws ApiException{
        return brandService.get(name,category);
    }

    public List<String> getCategory(){
        return brandService.getCategory();
    }

    public List<String> getBrandList(){
        return brandService.getBrandList();
    }

    public List<String> getBrandByCategory(String category){
        List<BrandPojo> brandList = brandService.getBrandByCategory(category);
        List<String> list = new ArrayList<>();
        for(BrandPojo brand:brandList){
            if(brand.getCategory().equals(category))
            {
                list.add(brand.getName());
            }
        }
        return list;
    }

    public List<String> getCategoryByBrand(String brand){
        List<BrandPojo> brandList = brandService.getCategoryByBrand(brand);
        List<String> list = new ArrayList<>();
        for(BrandPojo pojo:brandList){
            if(pojo.getName().equals(brand))
            {
                list.add(pojo.getCategory());
            }
        }
        return list;
    }
}
