package com.increff.pos.dto;
import com.increff.pos.dao.DaySalesDao;
import com.increff.pos.model.*;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import static com.increff.pos.util.ConvertUtil.convert;

@Service
public class ReportDto {
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ReportService reportService;

    public List<InventoryData> getInventory(InventoryReportForm inventoryReportForm) throws ApiException, FileNotFoundException {
        List<InventoryData> data =  inventoryService.get(inventoryReportForm);
        return data;
    }

    public List<SalesReportData> getSales(SalesReportForm salesReportForm) throws ApiException, FileNotFoundException {
        if(salesReportForm.getStartDate()==null)
        {
            Date date = new Date();
            date.setTime(1000);
            salesReportForm.setStartDate(date);
        }
        if(salesReportForm.getEndDate()==null)
        {
            Date date = new Date();
            salesReportForm.setEndDate(new Date(date.getTime() + 1000 * 60 * 60 * 24));
        }
        LocalDate startDate = salesReportForm.getStartDate().toInstant().atOffset(ZoneOffset.UTC).toLocalDate();
        LocalDate endDate = salesReportForm.getEndDate().toInstant().atOffset(ZoneOffset.UTC).toLocalDate();
        List<BrandPojo> data =  brandService.getSales(salesReportForm);
        List<OrderItemData> list = new ArrayList<>();
        HashMap<Integer,Double> mapTotal = new HashMap<>();
        HashMap<Integer,Integer> mapQuantity = new HashMap<>();
        for(BrandPojo p:data)
        {
            List<ProductPojo> d = productService.getByBrandId(p.getId());
            for(ProductPojo productPojo:d)
            {
                List<OrderItemData> itemList = orderService.getByProductId(productPojo.getId());
                for(OrderItemData item:itemList)
                {
                    LocalDate date = item.getDate().toInstant().atOffset(ZoneOffset.UTC).toLocalDate();
                    if(date.compareTo(startDate)>=0 && date.compareTo(endDate)<=0)
                    {
                        if(mapTotal.containsKey(p.getId()))
                        {
                            double prevTotal = mapTotal.get(p.getId());
                            mapTotal.put(p.getId(),prevTotal + item.getTotal());
                            int prevQuantity = mapQuantity.get(p.getId());
                            mapQuantity.put(p.getId(),prevQuantity + item.getQuantity());
                        }
                        else{
                            mapTotal.put(p.getId(),item.getTotal());
                            mapQuantity.put(p.getId(),item.getQuantity());
                        }
                        list.add(item);
                    }
                }
            }
        }
       List<SalesReportData> salesList = new ArrayList<>();
       for(BrandPojo p:data)
       {
           if(mapTotal.containsKey(p.getId()))
           {
               SalesReportData item = new SalesReportData();
               item.setCategory(p.getCategory());
               item.setBrand(p.getName());
               item.setTotal(mapTotal.get(p.getId()));
               item.setQuantity(mapQuantity.get(p.getId()));
               salesList.add(item);
           }
       }
        return salesList;
    }

    public List<DaySalesData> getDaySalesInfo(){
        List<DaySalesPojo> list = reportService.getDaySalesInfo();
        List<DaySalesData> data = new ArrayList<>();
        for (DaySalesPojo p:list)
        {
            data.add(convert(p));
        }
        return data;
    }

    public List<BrandData> getBrand(BrandReportForm brandReportForm) throws FileNotFoundException, ApiException {
        List<BrandData> data =  brandService.getBrand(brandReportForm);
        return data;
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
        DaySalesPojo p = new DaySalesPojo();
        Date date = new Date(System.currentTimeMillis() - 5 * 3600 * 1000 - 1800 * 1000);
        p.setDate(date);
        p.setOrderCount(orderCount);
        p.setItemCount(orderItemCount);
        p.setRevenue(revenue);
        reportService.add(p);
    }
}
