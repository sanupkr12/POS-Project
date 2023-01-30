package com.increff.pos.service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.OrderForm;
import com.increff.pos.model.OrderItemData;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import org.dom4j.DocumentException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class InventoryServiceTest extends AbstractUnitTest{

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private InventoryDao inventoryDao;

    @Test
    public void testCreate() throws ApiException {
        BrandPojo brand = generateBrand("speakers","jabra");
        ProductPojo product = createProduct(brand,12000,"jab123","high end jabra speakers");



        InventoryForm form = new InventoryForm();
        form.setQuantity(20);
        form.setBarcode(product.getBarcode());

        expectedException.expect(ApiException.class);
        inventoryService.create(form);

    }

    @Test
    public void testGetByBarcode() throws ApiException {
        BrandPojo brand = generateBrand("speakers","jabra");
        ProductPojo product = createProduct(brand,12000,"jab123","high end jabra speakers");

        InventoryPojo inventoryPojo = inventoryService.get(product.getBarcode());

        assertEquals(inventoryPojo.getQuantity(),0);
    }

    @Test
    public void testGetAll() throws ApiException {
        BrandPojo brand = generateBrand("speakers","jabra");
        ProductPojo product1 = createProduct(brand,12000,"jab123","high end jabra speakers");
        ProductPojo product2 = createProduct(brand,8000,"jabra789","low end jabra speakers");



        List<InventoryPojo> products = inventoryService.get();


        assertEquals(products.get(0).getBarcode(),product1.getBarcode());
        assertEquals(products.get(1).getBarcode(),product2.getBarcode());

    }

    @Test
    public void testGetByBarcodeList() throws ApiException {
        BrandPojo brand = generateBrand("speakers","jabra");
        ProductPojo product1 = createProduct(brand,12000,"jab123","high end jabra speakers");
        ProductPojo product2 = createProduct(brand,8000,"jabra789","low end jabra speakers");

        BrandPojo brand2 = generateBrand("headphones","jbl");
        ProductPojo product3 = createProduct(brand2,11000,"jbl123","jbl premium headphones");
        ProductPojo product4 = createProduct(brand2,2400,"jbl234","jbl regular headphones");

        List<String> barcodeList = new ArrayList<>();

        barcodeList.add(product1.getBarcode());
        barcodeList.add(product3.getBarcode());

        List<InventoryPojo> inventoryList = inventoryService.get(barcodeList);


        assertEquals(inventoryList.get(0).getId(),product1.getId());




    }

    @Test
    public void testUpdate() throws ApiException {
        BrandPojo brand = generateBrand("speakers","jbl");
        ProductPojo product = createProduct(brand,4500,"jbl123","jbl regular speakers");

        InventoryForm form = new InventoryForm();
        form.setBarcode("jbl123");
        form.setQuantity(50);

        InventoryPojo inventoryPojo = inventoryService.update(form);

        assertEquals(inventoryPojo.getQuantity(),50);
    }

    @Test
    public void testReplaceInventory() throws ApiException {
        BrandPojo brand = generateBrand("speakers","jbl");
        ProductPojo product = createProduct(brand,4500,"jbl123","jbl regular speakers");

        InventoryForm form = new InventoryForm();
        form.setBarcode(product.getBarcode());
        form.setQuantity(50);


        InventoryPojo updatedInventory = inventoryService.replaceInventory(form);


        assertEquals(updatedInventory.getQuantity(),50);


    }

    @Test
    public void testUpdateWithOrder() throws ApiException, DocumentException, ParseException, IOException {
        BrandPojo brand = generateBrand("speakers","jbl");
        ProductPojo product = createProduct(brand,4500,"jbl123","jbl regular speakers");

        InventoryForm form = new InventoryForm();
        form.setQuantity(20);
        form.setBarcode(product.getBarcode());

        inventoryService.update(form);

        OrderForm orderForm = new OrderForm();
        orderForm.setBarcode(product.getBarcode());
        orderForm.setSellingPrice(4200);
        orderForm.setQuantity(5);

        List<OrderForm> itemList = new ArrayList<>();
        itemList.add(orderForm);

        List<OrderItemData> items = orderService.add(itemList);

        InventoryPojo pojo = inventoryDao.select(items.get(0).getBarcode());

        assertEquals(pojo.getQuantity(),15);



    }

    public ProductPojo createProduct(BrandPojo brand, double mrp, String barcode, String productName) throws ApiException {

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
