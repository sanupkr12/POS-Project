package com.increff.pos.controller;

import com.increff.pos.model.*;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.List;

@Api
@RestController
public class OrderApiController {

    @Autowired
    private OrderService service;

    @ApiOperation(value="creating an order")
    @RequestMapping(path="/api/order",method = RequestMethod.POST)
    public void create(@RequestBody List<OrderForm> form) throws ParseException, ApiException, DocumentException, FileNotFoundException {
        service.add(form);
    }

    @ApiOperation(value="get orderItems by orderId")
    @RequestMapping(path="/api/order/{id}",method=RequestMethod.GET)
    public List<OrderItemData> get(@PathVariable int id) throws ApiException {
        return service.get(id);
    }


    @ApiOperation(value="get order")
    @RequestMapping(path="/api/order",method=RequestMethod.GET)
    public List<OrderData> get(){
        return service.getAll();
    }





    @ApiOperation(value="update Order")
    @RequestMapping(path="/api/order/item/{id}",method=RequestMethod.PUT)
    public void update(@RequestBody EditOrderForm form,@PathVariable int id) throws ApiException, ParseException {
        service.update(form,id);
    }

    @ApiOperation(value="get Order by itemId")
    @RequestMapping(path="/api/order/item/{id}",method=RequestMethod.GET)
    public OrderItemData getItem(@PathVariable int id) throws ApiException {
        return service.getByItemId(id);
    }

    @ApiOperation(value="generate invoice")
    @RequestMapping(path="/api/order/invoice/{id}",method=RequestMethod.PUT)
    public void invoice(@PathVariable int id) throws ApiException, FileNotFoundException, DocumentException {
        service.printInvoice(id);
    }

    @ApiOperation(value="get Invoice data")
    @RequestMapping(path="/api/order/invoice/{id}",method = RequestMethod.GET)
    public invoiceData getInvoice(@PathVariable int id){
        return service.getInvoice(id);
    }



}
