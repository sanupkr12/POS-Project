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

import static com.google.common.collect.Lists.reverse;

@Api
@RestController
@RequestMapping(value="/api/inventory")
public class InventoryApiController {
    @Autowired
    private InventoryDto inventoryDto;


    @ApiOperation(value="Update New Inventory")
    @RequestMapping(path="",method = RequestMethod.POST)
    public void updateInventory(@RequestBody InventoryForm form) throws ApiException {
        inventoryDto.update(form);

    }



    @ApiOperation(value="Get Inventory by barcode")
    @RequestMapping(path="/{barcode}",method=RequestMethod.GET)
    public InventoryData get(@PathVariable String barcode) throws ApiException{
        return inventoryDto.get(barcode);
    }

    @ApiOperation(value="Get All Inventory")
    @RequestMapping(path="",method=RequestMethod.GET)
    public List<InventoryData> get() throws ApiException{
        return reverse(inventoryDto.get());
    }

    @ApiOperation(value="Update Inventory")
    @RequestMapping(path="",method=RequestMethod.PUT)
    public void update(@RequestBody InventoryForm form) throws ApiException{
        inventoryDto.replaceInventory(form);
    }





}
