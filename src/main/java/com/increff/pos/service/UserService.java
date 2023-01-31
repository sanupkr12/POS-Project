package com.increff.pos.service;

import java.util.List;

import javax.transaction.Transactional;

import com.increff.pos.model.InfoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.UserDao;
import com.increff.pos.pojo.UserPojo;

@Service
public class UserService {

	@Autowired
	private UserDao dao;

	@Autowired
	private InfoData info;

	@Transactional
	public void add(UserPojo p) throws ApiException {

		UserPojo existing = dao.select(p.getEmail());
		if (existing != null) {
			throw new ApiException("User with given email already exists");
		}
		dao.insert(p);
	}

	public UserPojo getById(int id){
		UserPojo pojo = dao.getById(id);

		return pojo;
	}

	@Transactional(rollbackOn = ApiException.class)
	public UserPojo get(String email) throws ApiException {
		return dao.select(email);
	}

	@Transactional(rollbackOn = ApiException.class)
	public void update(int id,String email,String role) throws ApiException {
		UserPojo userPojo;

		if(get(email)==null)
		{
			throw new ApiException("No User Exist With Current Email");
		}


		if(info.getRole().equals("supervisor"))
		{
			userPojo = dao.getById(id);
			userPojo.setRole(role);
		}
		else{
			throw new ApiException("Access denied");
		}

	}

	@Transactional
	public List<UserPojo> getAll() {
		return dao.selectAll();
	}

	@Transactional
	public void delete(int id) throws ApiException {
		UserPojo user = getById(id);

		if(user.getRole().equals("supervisor"))
		{
			throw new ApiException("Not Permitted");
		}
		dao.delete(id);
	}

}
