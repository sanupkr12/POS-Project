package com.increff.pos.service;


import com.increff.pos.dao.DaySalesDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.model.*;
import com.increff.pos.pojo.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class ReportService {
    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DaySalesDao dao;

    public void getInventory(InventoryReportForm inventoryReportForm) throws ApiException, FileNotFoundException {
        List<InventoryData> data =  inventoryService.get(inventoryReportForm);
        String fileName = "inventory-report" + new Date().getTime() + ".pdf";

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        pdfDocument.setDefaultPageSize(PageSize.A4);

        float []titleTableWidth = {560f};
        Table table = new Table(titleTableWidth);

        table.addCell(new Cell().add("Inventory Report").setBorder(Border.NO_BORDER)).setBackgroundColor(new DeviceRgb(1,32,77)).setFontSize(30f).setTextAlignment(TextAlignment.CENTER).setFontColor(Color.WHITE);

        document.add(table);

        float []infoTableWidth = {80f,200f,200f,80f};
        Table infoTable = new Table(infoTableWidth);
        infoTable.setMarginTop(50f);

        infoTable.addCell(new Cell().add("ID").setBackgroundColor(Color.BLACK).setFontColor(Color.WHITE));
        infoTable.addCell(new Cell().add("Barcode").setBackgroundColor(Color.BLACK).setFontColor(Color.WHITE));
        infoTable.addCell(new Cell().add("Product Name").setBackgroundColor(Color.BLACK).setFontColor(Color.WHITE));
        infoTable.addCell(new Cell().add("Quantity").setBackgroundColor(Color.BLACK).setFontColor(Color.WHITE));



        for (InventoryData d:data){
            infoTable.addCell(new Cell().add(String.valueOf(d.getId())));
            infoTable.addCell(new Cell().add(d.getBarcode()));
            infoTable.addCell(new Cell().add(d.getName()));
            infoTable.addCell(new Cell().add(String.valueOf(d.getQuantity())));
        }

        document.add(infoTable);

        document.close();

    }

    public void getSales(SalesReportForm salesReportForm) throws ApiException, FileNotFoundException {
        List<BrandPojo> data =  brandService.getSales(salesReportForm);
        List<OrderItemData> list = new ArrayList<>();
        for(BrandPojo p:data)
        {
            List<ProductPojo> d = productService.getByBrandId(p.getId());

            for(ProductPojo productPojo:d)
            {
                List<OrderItemData> itemList = orderService.getByProductId(productPojo.getId());

                for(OrderItemData item:itemList)
                {
                    list.add(item);
                }
            }
        }

        String fileName = "Sales-report" + new Date().getTime() + ".pdf";

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        pdfDocument.setDefaultPageSize(PageSize.A4);

        float []titleTableWidth = {560f};
        Table table = new Table(titleTableWidth);

        table.addCell(new Cell().add("Sales Report").setBorder(Border.NO_BORDER)).setBackgroundColor(new DeviceRgb(1,32,77)).setFontSize(30f).setTextAlignment(TextAlignment.CENTER).setFontColor(Color.WHITE);

        document.add(table);

        float []dateTableWidth = {100f,180f,100f,180f};
        Table dateTable = new Table(dateTableWidth);

        dateTable.setMarginTop(50f);

        dateTable.addCell(new Cell().add("Start Date: "));
        dateTable.addCell(new Cell().add(salesReportForm.getStartDate().toString()));
        dateTable.addCell(new Cell().add("End Date: "));
        dateTable.addCell(new Cell().add(salesReportForm.getEndDate().toString()));

        document.add(dateTable);


        float []infoTableWidth = {40f,200f,60f,180f,80f};
        Table infoTable = new Table(infoTableWidth);

        infoTable.setMarginTop(50f);

        infoTable.addCell(new Cell().add("ID").setBackgroundColor(Color.BLACK).setFontColor(Color.WHITE));
        infoTable.addCell(new Cell().add("Product Name").setBackgroundColor(Color.BLACK).setFontColor(Color.WHITE));
        infoTable.addCell(new Cell().add("Quantity").setBackgroundColor(Color.BLACK).setFontColor(Color.WHITE));
        infoTable.addCell(new Cell().add("Date").setBackgroundColor(Color.BLACK).setFontColor(Color.WHITE));
        infoTable.addCell(new Cell().add("Total").setBackgroundColor(Color.BLACK).setFontColor(Color.WHITE));


        double totalCost = 0;
        for (OrderItemData d:list){
            infoTable.addCell(new Cell().add(String.valueOf(d.getId())));
            infoTable.addCell(new Cell().add(d.getProductName()));
            infoTable.addCell(new Cell().add(String.valueOf(d.getQuantity())));
            infoTable.addCell(new Cell().add(String.valueOf(d.getDate())));
            infoTable.addCell(new Cell().add(String.valueOf(d.getTotal())));
            totalCost+=d.getTotal();
        }

        infoTable.addCell(new Cell(0,4).add("Total Sales").setTextAlignment(TextAlignment.CENTER));
        infoTable.addCell(new Cell().add(String.valueOf(totalCost)));
        document.add(infoTable);

        document.close();



    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(rollbackOn = ApiException.class)
    public void getDaySales() throws ApiException, ParseException {
        List<OrderData> orderList = orderService.getAll();
        LocalDate currentDate = LocalDate.now(ZoneOffset.UTC);

        int orderCount = 0;
        int orderItemCount = 0;
        double revenue = 0;

        for(OrderData data:orderList)
        {
            LocalDate orderDate = data.getDate().toInstant().atOffset(ZoneOffset.UTC).toLocalDate();
            if(orderDate.equals(currentDate))
            {
                orderCount+=1;


                List<OrderItemData> itemList =  orderService.get(data.getId());

               for(OrderItemData item:itemList)
               {
                   orderItemCount+=1;
                   revenue += item.getTotal();
               }
            }
        }

        System.out.println(orderItemCount);
        DaySalesPojo p = new DaySalesPojo();

        p.setDate(new Date());
        p.setOrderCount(orderCount);
        p.setItemCount(orderItemCount);
        p.setRevenue(revenue);

        dao.insert(p);

    }

    public void getBrand(BrandReportForm brandReportForm) throws FileNotFoundException, ApiException {
        List<BrandPojo> data =  brandService.getBrand(brandReportForm);


        String fileName = "Brand-report" + new Date().getTime() + ".pdf";

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        pdfDocument.setDefaultPageSize(PageSize.A4);

        float []titleTableWidth = {560f};
        Table table = new Table(titleTableWidth);

        table.addCell(new Cell().add("Brand Report").setBorder(Border.NO_BORDER)).setBackgroundColor(new DeviceRgb(1,32,77)).setFontSize(30f).setTextAlignment(TextAlignment.CENTER).setFontColor(Color.WHITE);

        document.add(table);

        float []infoTableWidth = {100f,230f,230f};
        Table infoTable = new Table(infoTableWidth);

        infoTable.setMarginTop(50f);

        infoTable.addCell(new Cell().add("ID").setBackgroundColor(Color.BLACK).setFontColor(Color.WHITE));
        infoTable.addCell(new Cell().add("Brand").setBackgroundColor(Color.BLACK).setFontColor(Color.WHITE));
        infoTable.addCell(new Cell().add("Category").setBackgroundColor(Color.BLACK).setFontColor(Color.WHITE));




        for (BrandPojo d:data){
            infoTable.addCell(new Cell().add(String.valueOf(d.getId())));
            infoTable.addCell(new Cell().add(d.getName()));
            infoTable.addCell(new Cell().add(String.valueOf(d.getCategory())));
        }

        document.add(infoTable);

        document.close();
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
