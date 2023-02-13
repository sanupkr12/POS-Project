package com.increff.pos.dto;
import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.model.*;
import com.increff.pos.pojo.InventoryPojo;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import static com.increff.pos.util.NormalizeUtil.normalizeMrp;
@Service
public class OrderDto {
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderService service;
    @Autowired
    private ProductService productService;
    @Autowired
    private RestTemplate restTemplate;

    public List<OrderItemData> add(List<OrderForm> orderList) throws ApiException, ParseException, DocumentException, IOException {
        for (OrderForm form : orderList) {
            ProductData data = productService.get(form.getBarcode());
            InventoryPojo pojo = inventoryService.get(form.getBarcode());
            form.setSellingPrice(normalizeMrp(form.getSellingPrice()));
            if (form.getQuantity()>pojo.getQuantity()) {
                throw new ApiException("Insufficient Inventory for " + data.getName() + " only " + pojo.getQuantity() + " items left");
            }
        }
        return service.add(orderList);
    }

    @Transactional(rollbackOn = ApiException.class)
    public ResponseEntity<ByteArrayResource> printInvoice(int orderId) throws ApiException {
        try {
            OrderPojo p = service.getOrderById(orderId);
            File file = new File("/home/sanupkumar/Downloads/invoiceFiles/bill" + String.valueOf(orderId) + ".pdf");
            HttpHeaders header = getHttpHeaders("invoice.pdf");
            Path path = Paths.get(file.getAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
            p.setInvoiceGenerated(true);
            return ResponseEntity.ok()
                    .headers(header)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/pdf"))
                    .body(resource);
        }
        catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }

    public List<OrderItemData> get(int id) throws ApiException {
        return service.get(id);
    }

    public OrderItemData getByItemId(int id) throws ApiException {
        return service.getByItemId(id);
    }

    public OrderItemData update(EditOrderForm form, int id) throws ApiException, ParseException, IOException {
        return service.update(form,id);
    }

    public invoiceData getInvoice(int id){
        OrderPojo p = service.getOrderById(id);
        invoiceData data = new invoiceData();
        data.setId(p.getId());
        data.setInvoiceGenerated(p.isInvoiceGenerated());
        return data;
    }

    public List<OrderData> getAll(){
       return service.getAll();
    }

    protected OrderData convert(OrderPojo p){
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

    private HttpHeaders getHttpHeaders(String filename) {
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return header;
    }
}
