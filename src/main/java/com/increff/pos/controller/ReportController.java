package com.increff.pos.controller;


import com.increff.pos.dto.ReportDto;
import com.increff.pos.model.*;
import com.increff.pos.pojo.BrandPojo;
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
@RequestMapping(value="/api/report")
public class ReportController {
    @Autowired
    private ReportDto reportDto;

    @ApiOperation(value="Generate Inventory Report")
    @RequestMapping(path = "/inventory",method = RequestMethod.POST)
    public List<InventoryData> generateInventory(@RequestBody InventoryReportForm inventoryReportForm) throws ApiException, FileNotFoundException {

        return reportDto.getInventory(inventoryReportForm);
    }

    @ApiOperation(value="Generate Brand Report")
    @RequestMapping(path = "/brand",method = RequestMethod.POST)
    public List<BrandData> generateBrand(@RequestBody BrandReportForm brandReportForm) throws ApiException, FileNotFoundException {
        return reportDto.getBrand(brandReportForm);
    }

    @ApiOperation(value="Generate Sales Report")
    @RequestMapping(path = "/sales",method = RequestMethod.POST)
    public List<SalesReportData> generateSales(@RequestBody SalesReportForm salesReportForm) throws ApiException, FileNotFoundException {
        return reportDto.getSales(salesReportForm);
    }

    @ApiOperation(value="Generate Per Day Sales Report")
    @RequestMapping(path = "/daySales",method = RequestMethod.GET)
    public List<DaySalesData> generateDaySales() throws ApiException, FileNotFoundException, ParseException {
        return reportDto.getDaySalesInfo();
    }

}
