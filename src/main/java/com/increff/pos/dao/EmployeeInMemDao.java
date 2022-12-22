package com.increff.pos.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.BrandPojo;

@Repository
public class EmployeeInMemDao {

	private HashMap<Integer, BrandPojo> rows;
	private int lastId;

	@PostConstruct
	public void init() {
		rows = new HashMap<Integer, BrandPojo>();
	}
	
	public void insert(BrandPojo p) {
		lastId++;
		p.setId(lastId);
		rows.put(lastId, p);
	}

	public void delete(int id) {
		rows.remove(id);
	}

	public BrandPojo select(int id) {
		return rows.get(id);
	}
	
	public List<BrandPojo> selectAll() {
		ArrayList<BrandPojo> list = new ArrayList<BrandPojo>();
		list.addAll(rows.values());
		return list;
	}

	public void update(int id, BrandPojo p) {
		rows.put(id, p);
	}

}
