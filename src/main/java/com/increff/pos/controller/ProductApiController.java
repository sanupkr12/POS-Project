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

    @ApiOperation(value = "Adds a Product")
    @RequestMapping(path = "", method = RequestMethod.POST)
    public void add(@RequestBody ProductForm form) throws ApiException {
        dto.add(form);
    }


    @ApiOperation(value = "Deletes a Product")
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    // /api/1
    public void delete(@PathVariable int id) throws ApiException {
        dto.delete(id);
    }

    @ApiOperation(value = "Gets a Product by Id")
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ProductData get(@PathVariable int id) throws ApiException {
        return dto.get(id);
    }

    @ApiOperation(value = "Gets list of all Product")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<ProductData> getAll() throws ApiException{
        return reverse(dto.get());
    }

    @ApiOperation(value = "Updates an Product")
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public void update(@RequestBody ProductForm form,@PathVariable int id) throws ApiException {
        dto.update(form,id);
    }


}
