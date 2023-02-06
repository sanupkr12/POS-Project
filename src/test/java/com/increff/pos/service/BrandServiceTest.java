package com.increff.pos.service;

import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandReportForm;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.InventoryReportForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class BrandServiceTest extends AbstractUnitTest {


    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private BrandService brandService;

    @Autowired
    private ProductService productService;


    @Test
    public void testAdd() throws ApiException {
        String category = "speakers";
        String brandName = "zabra";
        BrandPojo p = new BrandPojo();

        p.setCategory(category);
        p.setName(brandName);


        BrandPojo pojo = brandService.add(p);

        assertEquals(pojo.getName(),brandName);
    }

    @Test
    public void testAddWithInvalidInformation() throws ApiException {
        String category = "";
        String brandName = "";
        BrandPojo p = new BrandPojo();

        p.setCategory(category);
        p.setName(brandName);

        expectedException.expect(ApiException.class);
        BrandPojo pojo = brandService.add(p);


        assertEquals(pojo.getName(),brandName);
    }

    @Test
    public void testGet() throws ApiException {

        BrandPojo pojo = generateBrand("speakers","jabra");

        BrandPojo brandPojo = brandService.get(pojo.getId());

        assertEquals(brandPojo.getName(),pojo.getName());
    }

    @Test
    public void testGetAll() throws ApiException {

        BrandPojo pojo = generateBrand("speakers","jabra");
        BrandPojo pojo2 = generateBrand("smartphone","one plus");

        List<BrandPojo> brandList = brandService.getAll();

        assertEquals(brandList.get(0).getName(),pojo.getName());
        assertEquals(brandList.get(1).getName(),pojo2.getName());
    }

    @Test
    public void testUpdate() throws ApiException {
        BrandPojo pojo = generateBrand("speakers","jabra");

        String updatedCategory = "speaker";

        BrandPojo brandPojo = new BrandPojo();

        brandPojo.setName("jabra");
        brandPojo.setCategory(updatedCategory);

        BrandPojo updatedPojo = brandService.update(pojo.getId(),brandPojo);

        assertEquals(updatedPojo.getCategory(),updatedCategory);

    }

    @Test
    public void testGetByCategoryAndBrand() throws ApiException {
        BrandPojo pojo = generateBrand("speakers","jabra");

        BrandPojo brandPojo = brandService.get(pojo.getName(),pojo.getCategory());


        assertEquals(pojo.getId(),brandPojo.getId());
    }


    @Test
    public void testGetCategoryAndBrandWithInvalidInfo() throws ApiException {
        BrandPojo pojo = generateBrand("speakers","jabra");
        System.out.println(pojo.getName());

        expectedException.expect(ApiException.class);
        BrandPojo brandPojo = brandService.get(pojo.getName(),"smartphone");

    }


    @Test
    public void testGetCategoryList() throws ApiException {
        BrandPojo pojo = generateBrand("speakers","jabra");
        BrandPojo pojo2 = generateBrand("smartphones","one plus");

        List<String> categoryList = brandService.getCategory();

        assertEquals(categoryList.get(1),pojo.getCategory());
        assertEquals(categoryList.get(0),pojo2.getCategory());
    }

    @Test
    public void testGetBrandList() throws ApiException {
        BrandPojo pojo = generateBrand("speakers","jabra");
        BrandPojo pojo2 = generateBrand("smartphones","one plus");

        List<String> categoryList = brandService.getBrandList();

        assertEquals(categoryList.get(0),pojo.getName());
        assertEquals(categoryList.get(1),pojo2.getName());
    }

    @Test
    public void testGetCheck() throws ApiException {

        expectedException.expect(ApiException.class);
        BrandPojo p = brandService.getCheck(500);

    }


    @Test
    public void testGetBrandReport() throws ApiException {
        BrandPojo pojo = generateBrand("speakers","jabra");
        BrandPojo pojo2 = generateBrand("smartphone","one plus");

        BrandReportForm form = new BrandReportForm();
        form.setBrand("jabra");
        form.setCategory("speakers");

        List<BrandData> data = brandService.getBrand(form);

        assertEquals(data.get(0).getName(),pojo.getName());

    }

//    public List<BrandPojo> getBrandByCategory(String category){
//        List<BrandPojo> brandList = dao.selectAll();
//
//        return brandList;
//    }
//
//    public List<BrandPojo> getCategoryByBrand(String brand){
//        List<BrandPojo> brandList = dao.selectAll();
//        return brandList;
//
//    }

    @Test
    public void testGetBrandByCategory() throws ApiException {
        BrandPojo pojo = generateBrand("speakers","jabra");
        BrandPojo pojo2 = generateBrand("speakers","jbl");

        List<BrandPojo> brandList = brandService.getBrandByCategory("speakers");

        assertEquals(brandList.size(),2);


    }

    @Test
    public void testGetCategoryByBrand() throws ApiException {
        BrandPojo pojo = generateBrand("speakers","jbl");
        BrandPojo pojo2 = generateBrand("headphones","jbl");

        List<BrandPojo> brandList = brandService.getCategoryByBrand("jbl");

        assertEquals(brandList.size(),2);


    }




    public BrandPojo generateBrand(String category,String brandName) throws ApiException {
        BrandPojo p = new BrandPojo();

        p.setCategory(category);
        p.setName(brandName);


        BrandPojo pojo = brandService.add(p);

        return pojo;
    }





}
