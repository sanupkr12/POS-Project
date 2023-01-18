package com.increff.pos.controller;

import com.increff.pos.dto.InventoryDto;
import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
public class InventoryApiController {
    @Autowired
    private InventoryDto inventoryDto;


    @ApiOperation(value="Add New Inventory")
    @RequestMapping(path="/api/inventory",method = RequestMethod.POST)
    public void create(@RequestBody InventoryForm form) throws ApiException {
        inventoryDto.create(form);

    }



    @ApiOperation(value="Get Inventory by barcode")
    @RequestMapping(path="/api/inventory/{barcode}",method=RequestMethod.GET)
    public InventoryData get(@PathVariable String barcode) throws ApiException{
        return inventoryDto.get(barcode);
    }

    @ApiOperation(value="Get All Inventory")
    @RequestMapping(path="/api/inventory",method=RequestMethod.GET)
    public List<InventoryData> get() throws ApiException{
        return inventoryDto.get();
    }

    @ApiOperation(value="Update Inventory")
    @RequestMapping(path="/api/inventory",method=RequestMethod.PUT)
    public void update(@RequestBody InventoryForm form) throws ApiException{
        inventoryDto.update(form);
    }





}
