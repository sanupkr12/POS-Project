package com.increff.pos.dto;

import com.increff.pos.model.*;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.*;
import org.dom4j.DocumentException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReportDtoTest extends AbstractUnitTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private BrandService brandService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductDto productDto;

    @Autowired
    private ReportDto reportDto;

    @Test
    public void testGetInventory() throws ApiException, DocumentException, ParseException, IOException {

        ProductData prod1 = generateProduct("jbl","speakers","jbl regular speakers","jbl1234",4500);

        OrderForm form1 = new OrderForm();
        form1.setBarcode(prod1.getBarcode());
        form1.setQuantity(20);
        form1.setSellingPrice(4200);

        ProductData prod2 = generateProduct("jabra","speakers","jabra premium speakers","jabra1234",14500);


        OrderForm form2 = new OrderForm();
        form2.setBarcode(prod2.getBarcode());
        form2.setQuantity(10);
        form2.setSellingPrice(13000);

        List<OrderForm> form = new ArrayList<>();

        form.add(form1);
        form.add(form2);


        List<OrderItemData> data = orderService.add(form);

        InventoryReportForm inventoryReportForm = new InventoryReportForm();
        inventoryReportForm.setBrand("jabra");
        inventoryReportForm.setCategory("speakers");

        List<InventoryData> inventoryList = reportDto.getInventory(inventoryReportForm);

        assertEquals(inventoryList.get(0).getQuantity(),990);

    }

    @Test
    public void testGetSales() throws ApiException, DocumentException, ParseException, IOException {
        ProductData prod1 = generateProduct("jbl","speakers","jbl regular speakers","jbl1234",4500);
        OrderForm form1 = new OrderForm();
        form1.setBarcode(prod1.getBarcode());
        form1.setQuantity(20);
        form1.setSellingPrice(4200);

        ProductData prod2 = generateProduct("jabra","speakers","jabra premium speakers","jabra1234",14500);

        OrderForm form2 = new OrderForm();
        form2.setBarcode(prod2.getBarcode());
        form2.setQuantity(10);
        form2.setSellingPrice(13000);

        List<OrderForm> order = new ArrayList<>();
        order.add(form1);
        order.add(form2);

        List<OrderItemData> data = orderService.add(order);

        SalesReportForm salesReportForm = new SalesReportForm();
        Date StartDate = new Date();
        Date EndDate = new Date();

        salesReportForm.setStartDate(StartDate);
        salesReportForm.setEndDate(EndDate);
        salesReportForm.setCategory("speakers");
        salesReportForm.setBrand("jbl");


        List<SalesReportData> salesData = reportDto.getSales(salesReportForm);

        assertEquals(salesData.size(),1);
    }


    @Test
    public void testGetDaySales() throws ApiException, DocumentException, ParseException, IOException {
        ProductData prod1 = generateProduct("jbl","speakers","jbl regular speakers","jbl1234",4500);
        OrderForm form1 = new OrderForm();
        form1.setBarcode(prod1.getBarcode());
        form1.setQuantity(20);
        form1.setSellingPrice(4200);

        ProductData prod2 = generateProduct("jabra","speakers","jabra premium speakers","jabra1234",14500);

        OrderForm form2 = new OrderForm();
        form2.setBarcode(prod2.getBarcode());
        form2.setQuantity(10);
        form2.setSellingPrice(13000);

        List<OrderForm> order = new ArrayList<>();
        order.add(form1);
        order.add(form2);

        List<OrderItemData> data = orderService.add(order);

        reportDto.getDaySales();

        List<DaySalesData> daySalesData = reportDto.getDaySalesInfo();

        assertEquals(daySalesData.get(0).getItemCount(),2);

    }

    @Test
    public void testGetBrand() throws ApiException, DocumentException, ParseException, IOException {
        ProductData prod1 = generateProduct("jbl","speakers","jbl regular speakers","jbl1234",4500);


        BrandReportForm form = new BrandReportForm();
        form.setCategory("speakers");
        form.setBrand("jbl");

        List<BrandData> data = reportDto.getBrand(form);

        assertEquals(data.get(0).getName(),"jbl");

    }
    public ProductData generateProduct(String brandName, String category, String name, String barcode, double mrp) throws ApiException {

        BrandPojo brandPojo = new BrandPojo();

        brandPojo.setName(brandName);
        brandPojo.setCategory(category);

        BrandPojo pojo = brandService.add(brandPojo);

        ProductForm p = new ProductForm();

        p.setName(name);
        p.setBrandCategory(brandPojo.getCategory());
        p.setBrandName(brandPojo.getName());
        p.setMrp(mrp);
        p.setBarcode(barcode);


        ProductData prod = productDto.add(p);

        InventoryForm form = new InventoryForm();
        form.setBarcode(prod.getBarcode());
        form.setQuantity(1000);



        inventoryService.update(form);

        return prod;
    }
}
