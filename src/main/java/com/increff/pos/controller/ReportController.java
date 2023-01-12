package com.increff.pos.controller;


import com.increff.pos.model.BrandReportForm;
import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryReportForm;
import com.increff.pos.model.SalesReportForm;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.ReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.List;

@Api
@RestController
public class ReportController {

    @Autowired
    private ReportService reportService;



    @ApiOperation(value="Generate Inventory Report")
    @RequestMapping(path = "/api/report/inventory",method = RequestMethod.POST)
    public void generateInventory(@RequestBody InventoryReportForm inventoryReportForm) throws ApiException, FileNotFoundException {

        reportService.getInventory(inventoryReportForm);
    }

    @ApiOperation(value="Generate Brand Report")
    @RequestMapping(path = "/api/report/brand",method = RequestMethod.POST)
    public void generateBrand(@RequestBody BrandReportForm brandReportForm) throws ApiException, FileNotFoundException {
        reportService.getBrand(brandReportForm);
    }


    @ApiOperation(value="Generate Sales Report")
    @RequestMapping(path = "/api/report/sales",method = RequestMethod.POST)
    public void generateSales(@RequestBody SalesReportForm salesReportForm) throws ApiException, FileNotFoundException {
        reportService.getSales(salesReportForm);
    }

    @ApiOperation(value="Generate Per Day Sales Report")
    @RequestMapping(path = "/api/report/daySales",method = RequestMethod.POST)
    public void generateDaySales() throws ApiException, FileNotFoundException, ParseException {
        reportService.getDaySales();
    }





}
