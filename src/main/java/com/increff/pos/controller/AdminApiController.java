package com.increff.pos.controller;

import java.util.ArrayList;
import java.util.List;

import com.increff.pos.dto.UserDto;
import com.increff.pos.model.editUserForm;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.pos.model.UserData;
import com.increff.pos.model.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class AdminApiController {
	@Autowired
	private UserDto dto;

	@ApiOperation(value = "Adds a user")
	@RequestMapping(path = "/api/supervisor/user", method = RequestMethod.POST)
	public void addUser(@RequestBody UserForm form) throws ApiException {
		dto.add(form);
	}

	@ApiOperation(value = "Get a user by id")
	@RequestMapping(path = "/api/supervisor/user/{id}", method = RequestMethod.GET)
	public UserData getUserById(@PathVariable int id) throws ApiException {
		return dto.getUserById(id);
	}



	@ApiOperation(value = "Edit user")
	@RequestMapping(path = "/api/supervisor/user/{id}", method = RequestMethod.POST)
	public void editUser(@RequestBody editUserForm form,@PathVariable int id) throws ApiException {
		dto.update(id,form);
	}



	@ApiOperation(value = "Deletes a user")
	@RequestMapping(path = "/api/supervisor/user/{id}", method = RequestMethod.DELETE)
	public void deleteUser(@PathVariable int id) {
		dto.delete(id);
	}

	@ApiOperation(value = "Gets list of all users")
	@RequestMapping(path = "/api/supervisor/user", method = RequestMethod.GET)
	public List<UserData> getAllUser() {

		return dto.getAll();
	}



}
