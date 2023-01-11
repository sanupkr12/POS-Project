package com.increff.pos.service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.model.*;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
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

    @Autowired
    private RestTemplate restTemplate;


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
        //generateInvoice(p.getId());
        printInvoice(p.getId());

    }

    public void printInvoice(int orderId) throws ApiException {
        try {

            final String url = "http://localhost:8000/invoice/";

            List<OrderItemData> list = get(orderId);


            String base64Str = restTemplate.postForObject(url, list, String.class);


            File dir = FileUtil.createDirectory("invoiceFiles");
            File bill = new File("/home/sanupkumar/Downloads/invoiceFiles/bill" + String.valueOf(orderId) + ".pdf");

            byte[] decodedPdf = Base64.getDecoder().decode(base64Str);


            FileOutputStream outputStream = new FileOutputStream(bill);

            outputStream.write(decodedPdf);

            outputStream.flush();

            outputStream.getFD().sync();

            outputStream.close();
        }
        catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }


    public List<OrderItemData> get(int id) throws ApiException {

        OrderPojo p = orderDao.get(id);


        List<OrderItemPojo> itemList = orderItemDao.getByOrderId(id);


        List<OrderItemData> data = new ArrayList<>();

        for (OrderItemPojo item:itemList){
            data.add(convert(item,p.getDate()));
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





    public OrderItemData getByItemId(int id) throws ApiException {
        OrderItemPojo p = orderItemDao.getByItemId(id);

        OrderPojo orderPojo = orderDao.get(p.getOrderId());

        return convert(p,orderPojo.getDate());

    }

    public OrderItemData convert(OrderItemPojo p , Date date) throws ApiException {
        OrderItemData data = new OrderItemData();
        data.setId(p.getOrderId());

        ProductData d = productService.get(p.getProductId());


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

    public invoiceData getInvoice(int id){

        OrderPojo p = orderDao.get(id);
        invoiceData data = new invoiceData();
        data.setId(p.getId());
        data.setInvoiceGenerated(p.getInvoiceGenerated());

        return data;
    }



    @Transactional(rollbackOn = ApiException.class)
    public void generateInvoice(int id) throws DocumentException, FileNotFoundException, ApiException {
        OrderPojo p = orderDao.get(id);


        String fileName = "invoices/invoice-order-id-" + p.getId() + " " + p.getDate() + " " + new Date().getTime() + ".pdf";



        OrderPojo orderPojo = orderDao.get(id);
        List<OrderItemPojo> list = orderItemDao.getByOrderId(id);


        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        pdfDocument.setDefaultPageSize(PageSize.A4);
        float col = 280f;
        float colWidth[] = {col,col};
        Table table = new Table(colWidth);
        table.setBackgroundColor(new DeviceRgb(1,32,77));
        table.setFontColor(Color.WHITE);
        table.setBorder(Border.NO_BORDER);
        table.addCell(new Cell(0,2).add("Increff")).setTextAlignment(TextAlignment.CENTER).setFontSize(35f).setVerticalAlignment(VerticalAlignment.MIDDLE);




        document.add(table);
        float titleWidth[] = {560f};
        Table titleTable = new Table(titleWidth);

        titleTable.setMarginTop(100f);

        titleTable.addCell(new Cell(0,6).add("Invoice").setBorder(Border.NO_BORDER)).setFontSize(20f).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);

        document.add(titleTable);


        float infoWidth[] = {40f,60f,150f,60f,100f,150f};



        Table infoTable = new Table(infoWidth);


        infoTable.addCell(new Cell().add("Order Id"));
        infoTable.addCell(new Cell().add("Barcode"));
        infoTable.addCell(new Cell().add("Product Name"));
        infoTable.addCell(new Cell().add("Quantity"));
        infoTable.addCell(new Cell().add("Cost"));
        infoTable.addCell(new Cell().add("Order Date"));





        float total = 0;
        for(OrderItemPojo p1:list){
            OrderItemData d = convert(p1,orderPojo.getDate());

            infoTable.addCell(new Cell().add(String.valueOf(d.getId())));
            infoTable.addCell(new Cell().add(d.getBarcode()));
            infoTable.addCell(new Cell().add(d.getProductName()));
            infoTable.addCell(new Cell().add(String.valueOf(d.getQuantity())));
            infoTable.addCell(new Cell().add(String.valueOf(d.getTotal())));
            infoTable.addCell(new Cell().add(String.valueOf(d.getDate())));

            total+=d.getTotal();
        }

        infoTable.addCell(new Cell(0,4).add("Total Cost")).setTextAlignment(TextAlignment.CENTER);
        infoTable.addCell(new Cell().add(String.valueOf(total)));

        document.add(infoTable);


        document.close();
        p.setInvoiceGenerated(true);

    }


    public OrderData convert(OrderPojo p){
        List<OrderItemPojo> itemPojo = orderItemDao.getByOrderId(p.getId());

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




}
