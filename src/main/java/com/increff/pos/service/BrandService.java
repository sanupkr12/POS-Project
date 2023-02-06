package com.increff.pos.service;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import com.increff.pos.model.*;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import com.increff.pos.dao.BrandDao;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.util.StringUtil;

@Service
public class BrandService {
	@Autowired
	private BrandDao dao;

	@Transactional(rollbackOn = ApiException.class)
	public BrandPojo add(BrandPojo pojo) throws ApiException {
		if(dao.selectAny(pojo.getName(),pojo.getCategory()))
		{
			throw new ApiException("Brand already exist with given details");
		}
		if(StringUtil.isEmpty(pojo.getName())) {
			throw new ApiException("name cannot be empty");
		}
		dao.insert(pojo);
		return pojo;
	}

	@Transactional(rollbackOn = ApiException.class)
	public void delete(int id) {
		dao.delete(id);
	}

	public BrandPojo get(int id) throws ApiException {
		return getCheck(id);
	}

	public List<BrandPojo> getAll() {
		return dao.selectAll();
	}

	@Transactional(rollbackOn  = ApiException.class)
	public BrandPojo update(int id, BrandPojo brandPojo) throws ApiException {
		BrandPojo pojo = dao.select(id);
		if(pojo.getName().equals(brandPojo.getName()) && pojo.getCategory().equals(brandPojo.getCategory()))
		{
			throw new ApiException("No change detected");
		}
		if(dao.selectAny(brandPojo.getName(),brandPojo.getCategory()))
		{
			throw new ApiException("Brand already exist with given details");
		}
		BrandPojo ex = getCheck(id);
		ex.setCategory(brandPojo.getCategory());
		ex.setName(brandPojo.getName());
		return ex;
	}

	public BrandPojo get(String name,String category) throws ApiException{
		BrandPojo pojo = dao.getBrand(name,category);
		if(pojo==null)
		{
			throw new ApiException("Brand does not exist");
		}
		return pojo;
	}

	public List<String> getCategory(){
		return dao.getCategory();
	}

	public List<String> getBrandList(){
		return dao.getBrandList();
	}

	public BrandPojo getCheck(int id) throws ApiException {
		BrandPojo pojo = dao.select(id);
		if (pojo == null) {
			throw new ApiException("Brand with given ID does not exit, id: " + id);
		}
		return pojo;
	}

	public List<BrandPojo> getBrandByCategoryAndBrand(String name,String category)
	{
		List<BrandPojo> list = dao.selectBrand(category,name);
		return list;
	}

	public List<BrandPojo> getSales(SalesReportForm form) throws ApiException {
		List<BrandPojo> list = dao.selectBrand(form.getCategory(),form.getBrand());
		return list;
	}

	public List<BrandPojo> getBrandByCategory(String category){
		List<BrandPojo> brandList = dao.selectAll();
		return brandList;
	}

	public List<BrandPojo> getCategoryByBrand(String brand){
		List<BrandPojo> brandList = dao.selectAll();
		return brandList;
	}

	public List<BrandData> getBrand(BrandReportForm form) throws ApiException {
		List<BrandPojo> list = dao.selectBrand(form.getCategory(),form.getBrand());
		List<BrandData> data = new ArrayList<>();
		for(BrandPojo p:list)
		{
			data.add(convert(p));
		}
		return data;
	}

	private static BrandData convert(BrandPojo p) {
		BrandData d = new BrandData();
		d.setCategory(p.getCategory());
		d.setName(p.getName());
		d.setId(p.getId());
		return d;
	}
}
