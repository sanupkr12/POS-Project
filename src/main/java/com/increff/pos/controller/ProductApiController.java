package com.increff.pos.controller;

import java.util.ArrayList;
import java.util.List;

import com.increff.pos.dto.ProductDto;
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

import static com.google.common.collect.Lists.reverse;

@Api
@RestController
@RequestMapping(value="/api/product")
public class ProductApiController {
    @Autowired
    private ProductDto dto;

    //TODO change the name of functions
    @ApiOperation(value = "Adds a Product")
    @RequestMapping(path = "", method = RequestMethod.POST)
    public void createProduct(@RequestBody ProductForm form) throws ApiException {
        dto.addProduct(form);
    }

    @ApiOperation(value = "Deletes a Product")
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public void deleteProduct(@PathVariable int id) throws ApiException {
        dto.deleteProduct(id);
    }

    @ApiOperation(value = "Gets a Product by Id")
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ProductData getProductById(@PathVariable int id) throws ApiException {
        return dto.getProductById(id);
    }

    @ApiOperation(value = "Gets list of all Product")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<ProductData> getAllProduct() throws ApiException{
        return reverse(dto.getAllProduct());
    }

    @ApiOperation(value = "Updates an Product")
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public void updateProduct(@RequestBody ProductForm form,@PathVariable int id) throws ApiException {
        dto.updateProduct(form,id);
    }


}
