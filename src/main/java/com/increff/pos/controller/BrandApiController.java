package com.increff.pos.controller;

import java.util.ArrayList;
import java.util.List;

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
	private BrandService service;

	@ApiOperation(value = "Adds an Brand")
	@RequestMapping(path = "/api/brand", method = RequestMethod.POST)
	public void add(@RequestBody BrandForm form) throws ApiException {

		if(form.getName().equals("") || form.getCategory().equals(""))
		{
			throw new ApiException("Name or Category cannot be empty");
		}


		BrandPojo p = convert(form);
		service.add(p);
	}

	
	@ApiOperation(value = "Deletes and Brand")
	@RequestMapping(path = "/api/brand/{id}", method = RequestMethod.DELETE)
	// /api/1
	public void delete(@PathVariable int id) {
		service.delete(id);
	}

	@ApiOperation(value = "Gets a Brand by ID")
	@RequestMapping(path = "/api/brand/{id}", method = RequestMethod.GET)
	public BrandData get(@PathVariable int id) throws ApiException {
		BrandPojo p = service.get(id);
		return convert(p);
	}

	@ApiOperation(value = "Gets list of all Brand")
	@RequestMapping(path = "/api/brand", method = RequestMethod.GET)
	public List<BrandData> getAll() {
		List<BrandPojo> list = service.getAll();
		List<BrandData> list2 = new ArrayList<BrandData>();
		for (BrandPojo p : list) {
			list2.add(convert(p));
		}
		return list2;
	}

	@ApiOperation(value = "Updates an Brand")
	@RequestMapping(path = "/api/brand/{id}", method = RequestMethod.PUT)
	public void update(@PathVariable int id, @RequestBody BrandForm f) throws ApiException {
		BrandPojo p = convert(f);
		service.update(id, p);
	}

	@ApiOperation(value="get All Category")
	@RequestMapping(path="/api/brand/category",method = RequestMethod.GET)
	public List<String> getCategoryList(){
		return service.getCategory();
	}

	@ApiOperation(value="get All Category")
	@RequestMapping(path="/api/brand/list",method = RequestMethod.GET)
	public List<String> getBrandList(){
		return service.getBrandList();
	}
	

	private static BrandData convert(BrandPojo p) {
		BrandData d = new BrandData();
		d.setCategory(p.getCategory());
		d.setName(p.getName());
		d.setId(p.getId());
		return d;
	}

	private static BrandPojo convert(BrandForm f) {
		BrandPojo p = new BrandPojo();
		p.setCategory(f.getCategory());
		p.setName(f.getName());
		return p;
	}



}
