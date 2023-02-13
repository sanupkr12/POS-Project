package com.increff.pos.dto;

import com.increff.pos.model.*;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.*;
import org.dom4j.DocumentException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OrderDtoTest extends AbstractUnitTest {

    @Rule
    public ExpectedException expectedException;

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private OrderDto orderDto;

    @Autowired
    private OrderService orderService;

    @Autowired
    private InventoryDto inventoryDto;

    @Test
    public void testAdd() throws ApiException, DocumentException, IOException, ParseException {
        List<OrderForm> form = new ArrayList<>();

        ProductPojo p1 = generateProduct("boat","headphones","boat rockerz 200","efgh1234",2400);
        ProductPojo p2 = generateProduct("apple","headphones","apple airpod","abcd1234",12000);

        OrderForm form1 = new OrderForm();
        OrderForm form2 = new OrderForm();

        form1.setBarcode(p1.getBarcode());
        form1.setQuantity(20);
        form1.setSellingPrice(2200);


        form2.setBarcode(p2.getBarcode());
        form2.setQuantity(15);
        form2.setSellingPrice(11000);


        form.add(form1);
        form.add(form2);


        List<OrderItemData> data = orderDto.add(form);

        assertEquals(data.get(0).getBarcode(),form1.getBarcode());
        assertEquals(data.get(1).getBarcode(),form2.getBarcode());

    }

    @Test
    public void testGet() throws ApiException, DocumentException, ParseException, IOException {
        ProductPojo p1 = generateProduct("boat","headphones","boat rockerz 200","efgh1234",2400);
        ProductPojo p2 = generateProduct("apple","headphones","apple airpod","abcd1234",12000);

        List<OrderForm> form = new ArrayList<>();


        OrderForm form1 = new OrderForm();
        form1.setBarcode(p1.getBarcode());
        form1.setQuantity(20);
        form1.setSellingPrice(2100);

        OrderForm form2 = new OrderForm();
        form2.setBarcode(p2.getBarcode());
        form2.setQuantity(10);
        form2.setSellingPrice(12000);

        form.add(form1);
        form.add(form2);


        List<OrderItemData> data = orderService.add(form);
        List<OrderItemData> list = orderDto.get(data.get(0).getId());



        for(int i=0;i<data.size();i++)
        {

            assertEquals(list.get(i).getProductName(),data.get(i).getProductName());
            assertEquals(list.get(i).getBarcode(),data.get(i).getBarcode());
        }



    }

    @Test
    public void testGetItemByItemId() throws ApiException, DocumentException, ParseException, IOException {
        ProductPojo p1 = generateProduct("boat","headphones","boat rockerz 200","efgh1234",2400);
        ProductPojo p2 = generateProduct("apple","headphones","apple airpod","abcd1234",12000);

        List<OrderForm> form = new ArrayList<>();


        OrderForm form1 = new OrderForm();
        form1.setBarcode(p1.getBarcode());
        form1.setQuantity(20);
        form1.setSellingPrice(2100);

        OrderForm form2 = new OrderForm();
        form2.setBarcode(p2.getBarcode());
        form2.setQuantity(10);
        form2.setSellingPrice(12000);

        form.add(form1);
        form.add(form2);


        List<OrderItemData> data = orderService.add(form);



        for(int i=0;i<data.size();i++)
        {
            OrderItemData item = orderDto.getByItemId(data.get(i).getItemId());

            assertEquals(item.getProductName(),data.get(i).getProductName());
            assertEquals(item.getBarcode(),data.get(i).getBarcode());
        }
    }

    @Test
    public void testUpdate() throws ApiException, DocumentException, ParseException, IOException {
        ProductPojo p1 = generateProduct("boat","headphones","boat rockerz 200","efgh1234",2400);
        ProductPojo p2 = generateProduct("apple","headphones","apple airpod","abcd1234",12000);

        List<OrderForm> form = new ArrayList<>();


        OrderForm form1 = new OrderForm();
        form1.setBarcode(p1.getBarcode());
        form1.setQuantity(20);
        form1.setSellingPrice(2100);

        OrderForm form2 = new OrderForm();
        form2.setBarcode(p2.getBarcode());
        form2.setQuantity(10);
        form2.setSellingPrice(12000);

        form.add(form1);
        form.add(form2);


        List<OrderItemData> data = orderService.add(form);

        int newQuantity = 30;

        EditOrderForm editForm = new EditOrderForm();
        editForm.setBarcode(data.get(0).getBarcode());
        editForm.setQuantity(newQuantity);
        editForm.setItemId(data.get(0).getItemId());

        OrderItemData updatedItem = orderDto.update(editForm,editForm.getItemId());

        List<OrderItemData> updatedData = orderService.get(data.get(0).getId());

        assertEquals(updatedData.get(0).getQuantity(),newQuantity);


    }

    @Test
    public void testGetAll() throws DocumentException, ParseException, IOException, ApiException {
        ProductPojo p1 = generateProduct("boat","headphones","boat rockerz 200","efgh1234",2400);
        ProductPojo p2 = generateProduct("apple","headphones","apple airpod","abcd1234",12000);




        OrderForm form1 = new OrderForm();
        form1.setBarcode(p1.getBarcode());
        form1.setQuantity(20);
        form1.setSellingPrice(2100);

        OrderForm form2 = new OrderForm();
        form2.setBarcode(p2.getBarcode());
        form2.setQuantity(10);
        form2.setSellingPrice(12000);

        List<OrderForm> form = new ArrayList<>();

        form.add(form1);
        form.add(form2);


        List<OrderItemData> data = orderService.add(form);


        List<OrderForm> orderForm = new ArrayList<>();




        orderForm.add(form1);


        List<OrderItemData> order2 = orderService.add(orderForm);

        List<OrderData> orderData = orderDto.getAll();

        assertEquals(orderData.get(0).getId(),data.get(0).getId());

    }

    @Test
    public void testGetInvoice() throws DocumentException, ParseException, IOException, ApiException {
        ProductPojo p1 = generateProduct("boat","headphones","boat rockerz 200","efgh1234",2400);
        ProductPojo p2 = generateProduct("apple","headphones","apple airpod","abcd1234",12000);




        OrderForm form1 = new OrderForm();
        form1.setBarcode(p1.getBarcode());
        form1.setQuantity(20);
        form1.setSellingPrice(2100);

        OrderForm form2 = new OrderForm();
        form2.setBarcode(p2.getBarcode());
        form2.setQuantity(10);
        form2.setSellingPrice(12000);

        List<OrderForm> form = new ArrayList<>();

        form.add(form1);
        form.add(form2);


        List<OrderItemData> data = orderService.add(form);


        invoiceData invoice =  orderDto.getInvoice(data.get(0).getId());

        assertEquals(invoice.isInvoiceGenerated(),false);



    }

    @Test
    public void testConvert() throws DocumentException, ParseException, IOException, ApiException {
        ProductPojo p1 = generateProduct("boat","headphones","boat rockerz 200","efgh1234",2400);
        ProductPojo p2 = generateProduct("apple","headphones","apple airpod","abcd1234",12000);




        OrderForm form1 = new OrderForm();
        form1.setBarcode(p1.getBarcode());
        form1.setQuantity(20);
        form1.setSellingPrice(2100);

        OrderForm form2 = new OrderForm();
        form2.setBarcode(p2.getBarcode());
        form2.setQuantity(10);
        form2.setSellingPrice(12000);

        List<OrderForm> form = new ArrayList<>();

        form.add(form1);
        form.add(form2);


        List<OrderItemData> data = orderService.add(form);

        OrderPojo pojo = new OrderPojo();
        pojo.setInvoiceGenerated(false);
        pojo.setDate(data.get(0).getDate());
        pojo.setId(data.get(0).getId());

        OrderData orderData = orderDto.convert(pojo);

        assertEquals(orderData.getId(),data.get(0).getId());

    }

    public ProductPojo generateProduct(String brandName, String category, String name, String barcode, double mrp) throws ApiException {

        BrandPojo brandPojo = new BrandPojo();

        brandPojo.setName(brandName);
        brandPojo.setCategory(category);

        BrandPojo pojo = brandService.add(brandPojo);

        ProductPojo p = new ProductPojo();

        p.setName(name);
        p.setBrandId(pojo.getId());
        p.setMrp(mrp);
        p.setBarcode(barcode);


        ProductPojo prod = productService.add(p);

        InventoryForm form = new InventoryForm();
        form.setBarcode(barcode);
        form.setQuantity(1000);



        InventoryData data = inventoryDto.create(form);

        return prod;
    }


}
