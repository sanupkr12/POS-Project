package com.increff.pos.service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.dto.InventoryDto;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.*;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.spring.RestConfig;
import com.increff.pos.util.FileUtil;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import javax.persistence.criteria.Order;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Stream;
import java.util.Base64;

@Service
public class OrderService {

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;




    @Transactional(rollbackOn = ApiException.class)
    public void add(List<OrderForm> orderList) throws ApiException, ParseException, DocumentException, FileNotFoundException {

        for(OrderForm form:orderList){


            ProductData data = productService.get(form.getBarcode());


            if(inventoryService.checkForInsufficientInventory(form))
            {
                throw new ApiException("Insufficient Inventory");
            }

        }

        OrderPojo p = new OrderPojo();

        p.setDate(getCurrentUtcTime());

        orderDao.insert(p);

        List<OrderItemPojo> itemList = new ArrayList<>();

        for(OrderForm form:orderList){

            if(form.getBarcode().equals(""))
            {
                throw new ApiException("Barcode cannot be empty");
            }


            OrderItemPojo itemPojo = new OrderItemPojo();
            ProductData data = productService.get(form.getBarcode());
            itemPojo.setOrderId(p.getId());

            if(form.getQuantity()<=0)
            {
                throw new ApiException("Quantity cannot be negative");
            }

            itemPojo.setQuantity(form.getQuantity());
            itemPojo.setProductId(data.getId());

            if(data.getMrp()<form.getSellingPrice())
            {
                throw new ApiException("Selling price cannot be more than mrp");
            }

            itemPojo.setSellingPrice(form.getSellingPrice());



            orderItemDao.insert(itemPojo);

            inventoryService.updateWithOrder(form);
        }


    }

    @Transactional(rollbackOn = ApiException.class)
    public List<OrderItemData> get(int id) throws ApiException {

        OrderPojo p = orderDao.get(id);

        List<OrderItemPojo> itemList = orderItemDao.getByOrderId(id);

        List<OrderItemData> list = new ArrayList<>();

        for(OrderItemPojo item:itemList)
        {
            list.add(convert(item,p.getDate()));
        }

        return list;

    }




    @Transactional(rollbackOn = ApiException.class)
    public OrderItemData getByItemId(int id) throws ApiException {
        OrderItemPojo p = orderItemDao.getByItemId(id);

        OrderPojo orderPojo = orderDao.get(p.getOrderId());

        return convert(p,orderPojo.getDate());

    }


    @Transactional(rollbackOn = ApiException.class)
    public List<OrderItemData> getByProductId(int productId) throws ApiException {
        List<OrderItemPojo> list = orderItemDao.getByProductId(productId);

        List<OrderItemData> data = new ArrayList<>();

        for(OrderItemPojo p:list)
        {
            OrderPojo orderPojo = orderDao.get(p.getOrderId());

            data.add(convert(p,orderPojo.getDate()));
        }

        return data;
    }

    public List<OrderData> getAll(){

        List<OrderPojo> list = orderDao.get();

        List<OrderData> data = new ArrayList<>();

        for(OrderPojo p:list){
            data.add(convert(p));
        }

        return data;


    }


    @Transactional(rollbackOn = ApiException.class)
    public void update(EditOrderForm form, int id) throws ApiException, ParseException {

        OrderItemPojo p = orderItemDao.getByItemId(id);

        OrderPojo orderPojo = orderDao.get(p.getOrderId());


        if(orderPojo.getInvoiceGenerated())
        {
            throw new ApiException("Order Cannot be modified since invoice has already been generated");
        }


        String barcode = form.getBarcode();
        int oldQuantity = p.getQuantity();

        InventoryForm inventoryForm = new InventoryForm();

        inventoryForm.setBarcode(barcode);
        inventoryForm.setQuantity(oldQuantity);




        inventoryService.update(inventoryForm);


        int newQuantity = form.getQuantity();

        OrderForm orderForm = new OrderForm();

        orderForm.setQuantity(newQuantity);
        orderForm.setBarcode(barcode);

        if(inventoryService.checkForInsufficientInventory(orderForm))
        {
            throw new ApiException("Insufficient Inventory");
        }


        p.setQuantity(newQuantity);
        inventoryService.updateWithOrder(orderForm);

        orderPojo.setDate(getCurrentUtcTime());





    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo getOrderById(int id)
    {
        return orderDao.get(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<OrderItemPojo> getOrderItemByOrderId(int orderId)
    {
        return orderItemDao.getByOrderId(orderId);
    }


    public OrderItemData convert(OrderItemPojo p , Date date) throws ApiException {
        OrderItemData data = new OrderItemData();
        data.setId(p.getOrderId());

        ProductPojo d = productService.get(p.getProductId());


        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); // Or whatever IST is supposed to be

        data.setItemId(p.getId());
        data.setBarcode(d.getBarcode());
        data.setProductName(d.getName());
        data.setDate(date);
        data.setQuantity(p.getQuantity());
        data.setTotal(p.getSellingPrice() * p.getQuantity());

        return data;
    }

    public static Date getCurrentUtcTime() throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");

        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat ldf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");

        Date d1 = null;
        try {

            d1 = ldf.parse( sdf.format(new Date()) );
        }
        catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return d1;
    }

    public OrderData convert(OrderPojo p){
        List<OrderItemPojo> itemPojo = getOrderItemByOrderId(p.getId());

        double totalCost = 0;

        for(OrderItemPojo item:itemPojo){
            totalCost+=(item.getSellingPrice()*item.getQuantity());
        }

        OrderData data = new OrderData();
        data.setId(p.getId());
        data.setDate(p.getDate());
        data.setTotal(totalCost);

        return data;

    }




}
