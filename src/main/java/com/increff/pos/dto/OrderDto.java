package com.increff.pos.dto;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.model.*;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.FileUtil;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class OrderDto {
    @Autowired
    private ProductDto productDto;

    @Autowired
    private InventoryDto inventoryDto;

    @Autowired
    private OrderService service;

    @Autowired
    private RestTemplate restTemplate;


    @Transactional(rollbackOn = ApiException.class)
    public void add(List<OrderForm> orderList) throws ApiException, ParseException, DocumentException, FileNotFoundException {

        for(OrderForm form:orderList){


            ProductData data = productDto.get(form.getBarcode());


            if(inventoryDto.checkForInsufficientInventory(form))
            {
                throw new ApiException("Insufficient Inventory");
            }

        }


        service.add(orderList);

    }


    @Transactional(rollbackOn = ApiException.class)
    public void printInvoice(int orderId) throws ApiException {
        try {

            final String url = "http://localhost:8000/invoice/";

            OrderPojo p = service.getOrderById(orderId);

            List<OrderItemData> list = service.get(orderId);


            String base64Str = restTemplate.postForObject(url, list, String.class);


            File dir = FileUtil.createDirectory("invoiceFiles");
            File bill = new File("/home/sanupkumar/Downloads/invoiceFiles/bill" + String.valueOf(orderId) + ".pdf");

            byte[] decodedPdf = Base64.getDecoder().decode(base64Str);


            FileOutputStream outputStream = new FileOutputStream(bill);

            outputStream.write(decodedPdf);

            outputStream.flush();

            outputStream.getFD().sync();

            outputStream.close();

            p.setInvoiceGenerated(true);
        }
        catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }


    public List<OrderItemData> get(int id) throws ApiException {
        return service.get(id);

    }

    public List<OrderData> getAll(){

        List<OrderPojo> list = service.getAll();

        List<OrderData> data = new ArrayList<>();

        for(OrderPojo p:list){
            data.add(convert(p));
        }

        return data;


    }

    public OrderItemData getByItemId(int id) throws ApiException {
        return service.getByItemId(id);
    }


    public List<OrderItemData> getByProductId(int productId) throws ApiException {
        return service.getByProductId(productId);
    }


    @Transactional(rollbackOn = ApiException.class)
    public void update(EditOrderForm form, int id) throws ApiException, ParseException {
        service.update(form,id);
    }

    public invoiceData getInvoice(int id){

        OrderPojo p = service.getOrderById(id);
        invoiceData data = new invoiceData();
        data.setId(p.getId());
        data.setInvoiceGenerated(p.getInvoiceGenerated());

        return data;
    }


    public OrderData convert(OrderPojo p){
        List<OrderItemPojo> itemPojo = service.getOrderItemByOrderId(p.getId());

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
