package com.increff.pos.service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.model.*;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import org.dom4j.DocumentException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.criteria.Order;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OrderServiceTest extends AbstractUnitTest{

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private OrderService orderService;

    private int productId;



    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testAdd() throws ApiException, DocumentException, ParseException, IOException {


        BrandPojo brandPojo = generateBrand("smartphone","one plus");
        ProductPojo productPojo = createProduct(brandPojo,24000,"op1234","one plus nord 2");
        addInventory(productPojo.getBarcode(),100);


        BrandPojo brandPojo1 = generateBrand("speakers","jbl");
        ProductPojo productPojo1 = createProduct(brandPojo1,4500,"jbl1234","jbl regular speaker");
        addInventory(productPojo1.getBarcode(),200);

        OrderForm form = new OrderForm();
        form.setBarcode(productPojo.getBarcode());
        form.setSellingPrice(23500);
        form.setQuantity(10);

        OrderForm form1 = new OrderForm();
        form1.setBarcode(productPojo1.getBarcode());
        form1.setSellingPrice(3800);
        form1.setQuantity(20);

        List<OrderForm> orderList = new ArrayList<>();
        orderList.add(form);
        orderList.add(form1);


        List<OrderItemData> orderItems = orderService.add(orderList);


        assertEquals(orderItems.get(0).getProductName(),productPojo.getName());
        assertEquals(orderItems.get(0).getQuantity(),form.getQuantity());



    }


    @Test
    public void testGetByOrderId() throws DocumentException, ParseException, IOException, ApiException {
        List<OrderItemData> orderItems = placeOrder();

        List<OrderItemData> itemList = orderService.get(orderItems.get(0).getId());

        for(int i=0;i<itemList.size();i++)
        {
            assertEquals(itemList.get(i).getBarcode(),orderItems.get(i).getBarcode());
        }
    }

    @Test
    public void testGetItemByItemId() throws DocumentException, ParseException, IOException, ApiException {
        List<OrderItemData> orderItems = placeOrder();

        for(int i=0;i<orderItems.size();i++)
        {
            OrderItemData data = orderService.getByItemId(orderItems.get(i).getItemId());

            assertEquals(orderItems.get(i).getProductName(),data.getProductName());
        }


    }

    @Test
    public void testGetByProductId() throws DocumentException, ParseException, IOException, ApiException {
        List<OrderItemData> items = placeOrder();

        List<OrderItemData> data = orderService.getByProductId(productId);

        assertEquals(data.get(0).getProductName(),items.get(0).getProductName());

    }


    @Test
    public void testGetAll() throws DocumentException, ParseException, IOException, ApiException {
        List<OrderItemData> items = placeOrder();

        List<OrderData> data = orderService.getAll();


        assertEquals(items.get(0).getId(),data.get(0).getId());



    }

    @Test
    public void testUpdate() throws DocumentException, ParseException, IOException, ApiException {
        List<OrderItemData> items = placeOrder();

        EditOrderForm form = new EditOrderForm();
        form.setItemId(items.get(0).getItemId());
        form.setBarcode(items.get(0).getBarcode());
        form.setQuantity(15);

        OrderItemData updatedData = orderService.update(form,items.get(0).getItemId());


        assertEquals(updatedData.getQuantity(),15);

    }

    @Test
    public void testGetOrderByOrderId() throws DocumentException, ParseException, IOException, ApiException {
        List<OrderItemData> items = placeOrder();

        OrderPojo pojo = orderService.getOrderById(items.get(0).getId());

        assertEquals(pojo.getId(),items.get(0).getId());

    }

    @Test
    public void testGetOrderItemByOrderId() throws DocumentException, ParseException, IOException, ApiException {
        List<OrderItemData> items = placeOrder();

        List<OrderItemPojo> itemList = orderService.getOrderItemByOrderId(items.get(0).getId());

        assertEquals(itemList.size(),2);
    }

    public List<OrderItemData> placeOrder() throws ApiException, DocumentException, ParseException, IOException {


        BrandPojo brandPojo = generateBrand("smartphone","one plus");
        ProductPojo productPojo = createProduct(brandPojo,24000,"op1234","one plus nord 2");
        addInventory(productPojo.getBarcode(),100);

        productId = productPojo.getId();


        BrandPojo brandPojo1 = generateBrand("speakers","jbl");
        ProductPojo productPojo1 = createProduct(brandPojo1,4500,"jbl1234","jbl regular speaker");
        addInventory(productPojo1.getBarcode(),200);

        OrderForm form = new OrderForm();
        form.setBarcode(productPojo.getBarcode());
        form.setSellingPrice(23500);
        form.setQuantity(10);

        OrderForm form1 = new OrderForm();
        form1.setBarcode(productPojo1.getBarcode());
        form1.setSellingPrice(3800);
        form1.setQuantity(20);

        List<OrderForm> orderList = new ArrayList<>();
        orderList.add(form);
        orderList.add(form1);


        List<OrderItemData> orderItems = orderService.add(orderList);


        return orderItems;



    }




    public void addInventory(String barcode,int quantity) throws ApiException {
        InventoryForm form = new InventoryForm();
        form.setBarcode(barcode);
        form.setQuantity(quantity);

        inventoryService.update(form);
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
