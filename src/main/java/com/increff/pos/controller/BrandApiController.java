package com.increff.pos.controller;

import java.util.ArrayList;
import java.util.List;

import com.increff.pos.dto.BrandDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class BrandApiController {
	@Autowired
	private BrandDto brandDto;
	@Autowired
	private BrandService service;

	@ApiOperation(value = "Adds an Brand")
	@RequestMapping(path = "/api/brand", method = RequestMethod.POST)
	public void add(@RequestBody BrandForm form) throws ApiException {
		brandDto.add(form);
	}

	
	@ApiOperation(value = "Deletes and Brand")
	@RequestMapping(path = "/api/brand/{id}", method = RequestMethod.DELETE)
	// /api/1
	public void delete(@PathVariable int id) throws ApiException {
		brandDto.delete(id);
	}

	@ApiOperation(value = "Gets a Brand by ID")
	@RequestMapping(path = "/api/brand/{id}", method = RequestMethod.GET)
	public BrandData get(@PathVariable int id) throws ApiException {
		return brandDto.get(id);
	}

	@ApiOperation(value = "Gets list of all Brand")
	@RequestMapping(path = "/api/brand", method = RequestMethod.GET)
	public List<BrandData> getAll() {
		return brandDto.getAll();
	}

	@ApiOperation(value = "Updates an Brand")
	@RequestMapping(path = "/api/brand/{id}", method = RequestMethod.PUT)
	public void update(@PathVariable int id, @RequestBody BrandForm f) throws ApiException {
		brandDto.update(id, f);
	}

	@ApiOperation(value="get All Category")
	@RequestMapping(path="/api/brand/category",method = RequestMethod.GET)
	public List<String> getCategoryList(){
		return brandDto.getCategory();
	}

	@ApiOperation(value="get All Category")
	@RequestMapping(path="/api/brand/list",method = RequestMethod.GET)
	public List<String> getBrandList(){
		return brandDto.getBrandList();
	}

	@ApiOperation(value="get Brand By Category")
	@RequestMapping(path="/api/brand/category/{category}",method = RequestMethod.GET)
	public List<String> getBrandByCategory(@PathVariable String category){
		return brandDto.getBrandByCategory(category);
	}

	@ApiOperation(value="get Category By Brand")
	@RequestMapping(path="/api/brand/list/{brand}",method = RequestMethod.GET)
	public List<String> getCategoryByBrand(@PathVariable String brand){
		return brandDto.getCategoryByBrand(brand);
	}



}
