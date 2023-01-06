package com.increff.pos.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.increff.pos.model.BrandReportForm;
import com.increff.pos.model.InventoryReportForm;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.SalesReportForm;
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

	@Autowired
	private ProductService productService;

	@Transactional(rollbackOn = ApiException.class)
	public void add(BrandPojo p) throws ApiException {
		normalize(p);

		if(dao.selectAny(p.getName(),p.getCategory()))
		{
			throw new ApiException("Brand already exist with given details");
		}

		if(StringUtil.isEmpty(p.getName())) {
			throw new ApiException("name cannot be empty");
		}


		dao.insert(p);
	}

	@Transactional
	public void delete(int id) {
		dao.delete(id);
	}

	@Transactional(rollbackOn = ApiException.class)
	public BrandPojo get(int id) throws ApiException {
		return getCheck(id);
	}

	@Transactional
	public List<BrandPojo> getAll() {
		return dao.selectAll();
	}

	@Transactional(rollbackOn  = ApiException.class)
	public void update(int id, BrandPojo p) throws ApiException {
		normalize(p);

		if(dao.selectAny(p.getName(),p.getCategory()))
		{
			throw new ApiException("Brand already exist with given details");
		}

		BrandPojo ex = getCheck(id);
		ex.setCategory(p.getCategory());
		ex.setName(p.getName());



//		dao.update(ex);
	}

	@Transactional(rollbackOn = ApiException.class)
	public BrandPojo get(String name,String category) throws ApiException{
		BrandPojo p = dao.getBrand(name,category);
		if(p==null)
		{
			throw new ApiException("Brand does not exist");
		}

		return p;
	}

	public List<String> getCategory(){
		return dao.getCategory();

	}
	public List<String> getBrandList(){
		return dao.getBrandList();
	}

	@Transactional
	public BrandPojo getCheck(int id) throws ApiException {
		BrandPojo p = dao.select(id);
		if (p == null) {
			throw new ApiException("Brand with given ID does not exit, id: " + id);
		}
		return p;
	}



	public List<String> get(InventoryReportForm form) throws ApiException {
		List<BrandPojo> list = dao.selectBrand(form.getCategory(),form.getBrand());
		List<String> barcodeList = new ArrayList<>();


		for(BrandPojo p:list)
		{


			List<ProductPojo> productList = productService.getByBrandId(p.getId());

			for(ProductPojo d:productList)
			{

				barcodeList.add(d.getBarcode());
			}
		}


		return barcodeList;
	}

	public List<BrandPojo> getBrand(BrandReportForm form) throws ApiException {
		List<BrandPojo> list = dao.selectBrand(form.getCategory(),form.getBrand());

		return list;
	}

	public List<BrandPojo> getSales(SalesReportForm form) throws ApiException {
		List<BrandPojo> list = dao.selectBrand(form.getCategory(),form.getBrand());

		return list;
	}




	protected static void normalize(BrandPojo p) {
		p.setCategory(p.getCategory().trim().toLowerCase());
		p.setName(p.getName().toLowerCase().trim());
	}
}
