package com.increff.pos.dao;

import java.sql.ResultSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.BrandPojo;

@Repository
public class BrandDao extends AbstractDao {
	private static final String DELETE_ID = "delete from BrandPojo p where id=:id";
	private static final String SELECT_ID = "select p from BrandPojo p where id=:id";
	private static final String SELECT_ALL = "select p from BrandPojo p";
	private static final String SELECT_BY_BRAND_AND_CATEGORY = "select p from BrandPojo p where name=:name and category=:category";
	private static final String SELECT_CATEGORY = "select distinct p.category from BrandPojo p";
	private static final String SELECT_BRAND_LIST = "select distinct p.name from BrandPojo p";
	private static final String SELECT_BRAND = "select p from BrandPojo p where name=:name";
	private static final String SELECT_BY_CATEGORY = "select p from BrandPojo p where category=:category";

	public void insert(BrandPojo p) {
		em.persist(p);
	}

	public int delete(int id) {
		Query query = em.createQuery(DELETE_ID);
		query.setParameter("id", id);
		return query.executeUpdate();
	}

	public BrandPojo select(int id) {
		TypedQuery<BrandPojo> query = getQuery(SELECT_ID, BrandPojo.class);
		query.setParameter("id", id);
		return getSingle(query);
	}

	public Boolean selectAny(String name,String Category){
		TypedQuery<BrandPojo> query = getQuery(SELECT_BY_BRAND_AND_CATEGORY,BrandPojo.class);
		query.setParameter("name",name);
		query.setParameter("category",Category);
		return !query.getResultList().isEmpty();
	}
	public BrandPojo getBrand(String name,String Category){
		TypedQuery<BrandPojo> query = getQuery(SELECT_BY_BRAND_AND_CATEGORY,BrandPojo.class);
		query.setParameter("name",name);
		query.setParameter("category",Category);
		return getSingle(query);
	}

	public List<BrandPojo> selectAll() {
		TypedQuery<BrandPojo> query = getQuery(SELECT_ALL, BrandPojo.class);
		return query.getResultList();
	}

	public void update(BrandPojo p) {
	}

	public List<String> getCategory(){
		TypedQuery<String> query =  getQuery(SELECT_CATEGORY, String.class);

		return query.getResultList();
	}

	public List<String> getBrandList(){
		TypedQuery<String> query = getQuery(SELECT_BRAND_LIST,String.class);
		return query.getResultList();
	}

	public List<BrandPojo> selectBrand(String category,String brand)
	{
		if(category.length()==0)
		{
			category = "all";
		}
		if(brand.length()==0)
		{
			brand = "all";
		}


		if(category.equals("all") && brand.equals("all"))
		{
			return this.selectAll();
		}
		else if(category.equals("all"))
		{

				TypedQuery<BrandPojo> query = getQuery(SELECT_BRAND,BrandPojo.class);
				query.setParameter("name",brand);
				return query.getResultList();

		}
		else if(brand.equals("all"))
		{


			TypedQuery<BrandPojo> query = getQuery(SELECT_BY_CATEGORY,BrandPojo.class);
			query.setParameter("category",category);
			return query.getResultList();
		}
		else{
			TypedQuery<BrandPojo> query = getQuery(SELECT_BY_BRAND_AND_CATEGORY,BrandPojo.class);
			query.setParameter("category",category);
			query.setParameter("name",brand);
			return query.getResultList();
		}
	}

}
