package com.increff.pos.controller;

import java.util.ArrayList;
import java.util.List;

import com.increff.pos.model.*;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import com.increff.pos.service.ApiException;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class ProductApiController {

    @Autowired
    private ProductService service;

    @ApiOperation(value = "Adds a Product")
    @RequestMapping(path = "/api/product", method = RequestMethod.POST)
    public void add(@RequestBody ProductForm form) throws ApiException {

        if(form.getName().equals(""))
        {
            throw new ApiException("Product Name cannot be empty");
        }
        if(form.getBarcode().equals(""))
        {
            throw new ApiException("Barcode  cannot be empty");
        }

        if(form.getBrandName().equals(""))
        {
            throw new ApiException("Brand Name cannot be empty");
        }

        if(form.getMrp()<=0)
        {
            throw new ApiException("Price cannot be negative");
        }

        service.add(form);
    }


    @ApiOperation(value = "Deletes a Product")
    @RequestMapping(path = "/api/product/{id}", method = RequestMethod.DELETE)
    // /api/1
    public void delete(@PathVariable int id) throws ApiException {
        service.delete(id);
    }

    @ApiOperation(value = "Gets a Product by Id")
    @RequestMapping(path = "/api/product/{id}", method = RequestMethod.GET)
    public ProductData get(@PathVariable int id) throws ApiException {

        return service.get(id);
    }

    @ApiOperation(value = "Gets list of all Product")
    @RequestMapping(path = "/api/product", method = RequestMethod.GET)
    public List<ProductData> getAll() throws ApiException{

        return service.get();
    }

    @ApiOperation(value = "Updates an Product")
    @RequestMapping(path = "/api/product/{id}", method = RequestMethod.PUT)
    public void update(@RequestBody ProductForm form,@PathVariable int id) throws ApiException {
        if(form.getName().equals(""))
        {
            throw new ApiException("Product Name cannot be empty");
        }
        if(form.getBarcode().equals(""))
        {
            throw new ApiException("Barcode  cannot be empty");
        }

        if(form.getBrandName().equals(""))
        {
            throw new ApiException("Brand Name cannot be empty");
        }

        if(form.getMrp()<=0)
        {
            throw new ApiException("Price cannot be negative");
        }

        service.update(form,id);
    }


}
