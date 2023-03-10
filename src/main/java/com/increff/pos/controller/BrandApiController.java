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

import static com.google.common.collect.Lists.reverse;

@Api
@RestController
@RequestMapping(value = "/api/brand")
public class BrandApiController {
	@Autowired
	private BrandDto brandDto;

	@ApiOperation(value = "Adds an Brand")
	@RequestMapping(path = "", method = RequestMethod.POST)
	public void addBrand(@RequestBody BrandForm form) throws ApiException {
		brandDto.addBrand(form);
	}

	@ApiOperation(value = "Deletes and Brand")
	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
	// /api/1
	public void deleteBrand(@PathVariable int id) throws ApiException {
		brandDto.deleteBrand(id);
	}

	@ApiOperation(value = "Gets a Brand by ID")
	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	public BrandData getBrandById(@PathVariable int id) throws ApiException {
		return brandDto.getBrand(id);
	}

	@ApiOperation(value = "Gets list of all Brand")
	@RequestMapping(path = "", method = RequestMethod.GET)
	public List<BrandData> getAllBrand() {
		return reverse(brandDto.getAllBrand());
	}

	@ApiOperation(value = "Updates an Brand")
	@RequestMapping(path = "/{id}", method = RequestMethod.PUT)
	public void updateBrand(@PathVariable int id, @RequestBody BrandForm f) throws ApiException {
		brandDto.updateBrand(id, f);
	}

	@ApiOperation(value="get All Category")
	@RequestMapping(path="/category",method = RequestMethod.GET)
	public List<String> getCategoryList(){
		return brandDto.getCategory();
	}

	@ApiOperation(value="get All Category")
	@RequestMapping(path="/list",method = RequestMethod.GET)
	public List<String> getBrandList(){
		return brandDto.getBrandList();
	}

	@ApiOperation(value="get Brand By Category")
	@RequestMapping(path="/category/{category}",method = RequestMethod.GET)
	public List<String> getBrandByCategory(@PathVariable String category){
		return brandDto.getBrandByCategory(category);
	}

	@ApiOperation(value="get Category By Brand")
	@RequestMapping(path="/list/{brand}",method = RequestMethod.GET)
	public List<String> getCategoryByBrand(@PathVariable String brand){
		return brandDto.getCategoryByBrand(brand);
	}

}
