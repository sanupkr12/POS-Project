package com.increff.pos.controller;

import com.increff.pos.dto.OrderDto;
import com.increff.pos.model.*;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static com.google.common.collect.Lists.reverse;

@Api
@RestController
@RequestMapping(value="/api/order")
public class OrderApiController {

    @Autowired
    private OrderDto orderDto;

    @Autowired
    private OrderService service;

    @ApiOperation(value="creating an order")
    @RequestMapping(path="",method = RequestMethod.POST)
    public void create(@RequestBody List<OrderForm> form) throws ParseException, ApiException, DocumentException, IOException {
        orderDto.add(form);
    }

    @ApiOperation(value="get orderItems by orderId")
    @RequestMapping(path="/{id}",method=RequestMethod.GET)
    public List<OrderItemData> get(@PathVariable int id) throws ApiException {
        return orderDto.get(id);
    }


    @ApiOperation(value="get order")
    @RequestMapping(path="",method=RequestMethod.GET)
    public List<OrderData> get(){
        return reverse(orderDto.getAll());
    }


    @ApiOperation(value="update Order")
    @RequestMapping(path="/item/{id}",method=RequestMethod.PUT)
    public void update(@RequestBody EditOrderForm form,@PathVariable int id) throws ApiException, ParseException {
        orderDto.update(form,id);
    }

    @ApiOperation(value="get Order by itemId")
    @RequestMapping(path="/item/{id}",method=RequestMethod.GET)
    public OrderItemData getItem(@PathVariable int id) throws ApiException {
        return orderDto.getByItemId(id);
    }

    @ApiOperation(value="generate invoice")
    @RequestMapping(path="/invoice/{id}",method=RequestMethod.POST)
    public ResponseEntity<ByteArrayResource> invoice(@PathVariable int id) throws ApiException, FileNotFoundException, DocumentException {
        return orderDto.printInvoice(id);
    }

    @ApiOperation(value="get Invoice data")
    @RequestMapping(path="/invoice/{id}",method = RequestMethod.GET)
    public invoiceData getInvoice(@PathVariable int id){
        return orderDto.getInvoice(id);
    }



}
