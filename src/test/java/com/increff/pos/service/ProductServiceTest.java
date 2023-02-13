package com.increff.pos.service;

import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProductServiceTest extends AbstractUnitTest{

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private BrandService brandService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Test
    public void testAdd() throws ApiException {
        BrandPojo pojo = generateBrand("speakers","jabra");
        Double mrp = 2100.00;
        ProductPojo p = new ProductPojo();
        p.setMrp(mrp);
        p.setBarcode("a1");
        p.setName("jabra high end");
        p.setBrandId(pojo.getId());


        ProductPojo productPojo = productService.add(p);

        assertEquals(productPojo.getBarcode(),"a1");

    }

    @Test
    public void testGetByProductId() throws ApiException {
        BrandPojo brandPojo = generateBrand("speakers","jabra");
        ProductPojo pojo = createProduct(brandPojo,14000,"a1","jabra high end speaker");

        ProductPojo productPojo = productService.get(pojo.getId());

        assertEquals(productPojo.getName(),"jabra high end speaker");

    }

    @Test
    public void testGetByBrandId() throws ApiException {
        BrandPojo brandPojo = generateBrand("speakers","jabra");

        ProductPojo productPojo1 = createProduct(brandPojo,14000,"a1","jabra high end speaker");
        ProductPojo productPojo2 = createProduct(brandPojo,18000,"a2","jabra low end speaker");

        List<ProductPojo> productList = productService.getByBrandId(brandPojo.getId());

        assertEquals(productList.size(),2);
    }

    @Test
    public void testGet() throws ApiException {
        BrandPojo brandPojo = generateBrand("speakers","jabra");

        ProductPojo productPojo1 = createProduct(brandPojo,14000,"a1","jabra high end speaker");
        ProductPojo productPojo2 = createProduct(brandPojo,18000,"a2","jabra low end speaker");

        List<ProductPojo> productList = productService.get();

        assertEquals(productList.size(),2);
    }

    @Test
    public void testGetByBarcode() throws ApiException {
        BrandPojo brandPojo = generateBrand("speakers","jabra");

        ProductPojo productPojo1 = createProduct(brandPojo,14000,"a1","jabra high end speaker");


        ProductData data = productService.get(productPojo1.getBarcode());

        assertEquals(data.getBrandName(),"jabra");

    }

    @Test
    public void testUpdate() throws ApiException{
        BrandPojo brandPojo = generateBrand("speakers","jabra");

        ProductPojo productPojo1 = createProduct(brandPojo,14000,"a1","jabra high end speaker");

        ProductForm form = new ProductForm();

        form.setName("jabra high end speaker");
        form.setBrandCategory("speakers");
        form.setBrandName("jabra");
        form.setBarcode("a2");
        form.setMrp(15000);


        ProductPojo updatedPojo = productService.update(form,productPojo1.getId(),brandPojo.getId());


        assertEquals(updatedPojo.getBarcode(),"a2");


    }

    @Test
    public void testCheckAny() throws ApiException {
        BrandPojo brandPojo = generateBrand("speakers","jabra");

        ProductPojo productPojo1 = createProduct(brandPojo,14000,"a1","jabra high end speaker");

        assertEquals(productService.checkAny("a100"),false);
    }

    public ProductPojo createProduct(BrandPojo brand,double mrp,String barcode,String productName) throws ApiException {

        ProductPojo p = new ProductPojo();
        p.setMrp(mrp);
        p.setBarcode(barcode);
        p.setName(productName);
        p.setBrandId(brand.getId());


        ProductPojo productPojo = productService.add(p);

        InventoryForm form = new InventoryForm();

        form.setBarcode(p.getBarcode());
        form.setQuantity(0);

        inventoryService.create(form);

        return productPojo;
    }

    public BrandPojo generateBrand(String category, String brandName) throws ApiException {
        BrandPojo p = new BrandPojo();

        p.setCategory(category);
        p.setName(brandName);


        BrandPojo pojo = brandService.add(p);

        return pojo;
    }

}
