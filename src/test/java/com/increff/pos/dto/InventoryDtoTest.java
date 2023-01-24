package com.increff.pos.dto;

import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.InventoryReportForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class InventoryDtoTest extends AbstractUnitTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Autowired
    private InventoryDto inventoryDto;

    @Autowired
    private ProductDto productDto;

    @Autowired
    private BrandService brandService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Test
    public void testNormalizeInventory(){
        InventoryForm form = new InventoryForm();

        form.setQuantity(10);
        form.setBarcode("efgh1234 ");

        inventoryDto.normalizeInventory(form);

        assertEquals(form.getBarcode(),"efgh1234");
    }

    @Test
    public void testCreate() throws ApiException {

        String barcode = "efgh1234";
        int quantity = 15;
        String brandName = "boat";
        String category = "headphones";
        double mrp = 2000;
        String name = "boat rockerz 200";




        BrandPojo brandPojo = new BrandPojo();

        brandPojo.setName(brandName);
        brandPojo.setCategory(category);

        brandService.add(brandPojo);

        ProductPojo p = new ProductPojo();

        p.setBarcode(barcode);
        p.setBrandId(brandPojo.getId());
        p.setMrp(mrp);
        p.setName(name);



        productService.add(p);

        InventoryForm form = new InventoryForm();
        form.setBarcode(barcode);
        form.setQuantity(quantity);



        InventoryData data = inventoryDto.create(form);



        assertEquals(data.getBarcode(),form.getBarcode());
        assertEquals(data.getQuantity(),form.getQuantity());

    }

    @Test
    public void testAddWithInvalidBarcode() throws ApiException {
        String barcode = "efgh1234";
        int quantity = 15;
        String brandName = "boat";
        String category = "headphones";
        double mrp = 2100;
        String name = "boat rockerz";


        InventoryForm form = getInventoryForm(barcode,quantity,brandName,category,mrp,name);

        form.setBarcode("abcd1234");

        inventoryDto.normalizeInventory(form);

        exceptionRule.expect(ApiException.class);
        inventoryDto.create(form);
    }

    @Test
    public void testAddWithInvalidQuantity() throws ApiException {
        String barcode = "efgh1234";
        int quantity = -15;
        String brandName = "boat";
        String category = "headphones";
        double mrp = 2100;
        String name = "boat rockerz";
        InventoryForm form = getInventoryForm(barcode,quantity,brandName,category,mrp,name);


        exceptionRule.expect(ApiException.class);
        InventoryData data = inventoryDto.create(form);

    }

    @Test
    public void testGet() throws ApiException {
        String barcode = "efgh1234";
        int quantity = 15;
        String brandName = "boat";
        String category = "headphones";
        double mrp = 2100;
        String name = "boat rockerz";
        InventoryForm form = getInventoryForm(barcode,quantity,brandName,category,mrp,name);

        InventoryPojo pojo = inventoryService.create(form);

        InventoryData data = inventoryDto.get(form.getBarcode());

        assertEquals(data.getQuantity(),form.getQuantity());

    }

    @Test
    public void testGetAll() throws ApiException {
        String barcode = "efgh1234";
        int quantity = 15;
        String brandName = "boat";
        String category = "headphones";
        double mrp = 2100;
        String name = "boat rockerz";
        InventoryForm form = getInventoryForm(barcode,quantity,brandName,category,mrp,name);

        String barcode2 = "abcd1234";
        int quantity2 = 25;
        String brandName2 = "apple";
        String category2 = "headphones";
        double mrp2 = 2100;
        String name2 = "apple earbud";
        InventoryForm form2 = getInventoryForm(barcode2,quantity2,brandName2,category2,mrp2,name2);

        inventoryService.create(form);
        inventoryService.create(form2);

        List<InventoryData> list = inventoryDto.get();

        assertEquals(list.get(0).getBarcode(),barcode);
        assertEquals(list.get(0).getQuantity(),quantity);

        assertEquals(list.get(1).getBarcode(),barcode2);
        assertEquals(list.get(1).getQuantity(),quantity2);
    }

    @Test
    public void testGetByInventoryForm() throws ApiException {

        String barcode = "efgh1234";
        int quantity = 15;
        String brandName = "boat";
        String category = "headphones";
        double mrp = 2100;
        String name = "boat rockerz";
        InventoryForm form = getInventoryForm(barcode,quantity,brandName,category,mrp,name);

        String barcode2 = "abcd1234";
        int quantity2 = 25;
        String brandName2 = "apple";
        String category2 = "headphones";
        double mrp2 = 2100;
        String name2 = "apple earbud";
        InventoryForm form2 = getInventoryForm(barcode2,quantity2,brandName2,category2,mrp2,name2);

        inventoryService.create(form);
        inventoryService.create(form2);

        InventoryReportForm queryForm = new InventoryReportForm();

        queryForm.setBrand("boat");
        queryForm.setCategory("headphones");

        List<InventoryData> list = inventoryDto.get(queryForm);

        assertEquals(list.size(),1);
    }

    @Test
    public void testUpdate() throws ApiException {
        String barcode = "efgh1234";
        int quantity = 15;
        String brandName = "boat";
        String category = "headphones";
        double mrp = 2100;
        String name = "boat rockerz";
        InventoryForm form = getInventoryForm(barcode,quantity,brandName,category,mrp,name);

        inventoryService.create(form);

        InventoryForm updatedForm = new InventoryForm();
        int newQuantity = 20;

        updatedForm.setBarcode("efgh1234");
        updatedForm.setQuantity(newQuantity);

        InventoryData data = inventoryDto.update(updatedForm);

        assertEquals(data.getQuantity(),quantity + newQuantity);


    }










    public InventoryForm getInventoryForm(String barcode,int quantity,String brandName,String category,double mrp,String name) throws ApiException {




        BrandPojo brandPojo = new BrandPojo();

        brandPojo.setName(brandName);
        brandPojo.setCategory(category);

        brandService.add(brandPojo);

        ProductPojo p = new ProductPojo();

        p.setBarcode(barcode);
        p.setBrandId(brandPojo.getId());
        p.setMrp(mrp);
        p.setName(name);



        productService.add(p);

        InventoryForm form = new InventoryForm();
        form.setBarcode(barcode);
        form.setQuantity(quantity);

        return form;
    }





}
