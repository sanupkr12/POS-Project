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

	private static String delete_id = "delete from BrandPojo p where id=:id";
	private static String select_id = "select p from BrandPojo p where id=:id";
	private static String select_all = "select p from BrandPojo p";
	private static String select_by_brand_and_category = "select p from BrandPojo p where name=:name and category=:category";
	private static String select_category = "select distinct p.category from BrandPojo p";
	private static String select_brand_list = "select distinct p.name from BrandPojo p";
	private static String select_brand = "select p from BrandPojo p where name=:name";
	private static String select_by_category = "select p from BrandPojo p where category=:category";
	@PersistenceContext
	private EntityManager em;

	@Transactional
	public void insert(BrandPojo p) {
		em.persist(p);
	}

	public int delete(int id) {
		Query query = em.createQuery(delete_id);
		query.setParameter("id", id);
		return query.executeUpdate();
	}

	public BrandPojo select(int id) {
		TypedQuery<BrandPojo> query = getQuery(select_id, BrandPojo.class);
		query.setParameter("id", id);
		return getSingle(query);
	}

	public Boolean selectAny(String name,String Category){
		TypedQuery<BrandPojo> query = getQuery(select_by_brand_and_category,BrandPojo.class);
		query.setParameter("name",name);
		query.setParameter("category",Category);

		if(query.getResultList().size() == 0)
		{
			return false;
		}

		return true;
	}
	public BrandPojo getBrand(String name,String Category){
		TypedQuery<BrandPojo> query = getQuery(select_by_brand_and_category,BrandPojo.class);
		query.setParameter("name",name);
		query.setParameter("category",Category);

		BrandPojo p = query.getSingleResult();


		return p;
	}

	public List<BrandPojo> selectAll() {
		TypedQuery<BrandPojo> query = getQuery(select_all, BrandPojo.class);
		return query.getResultList();
	}

	public void update(BrandPojo p) {
	}

	public List<String> getCategory(){
		TypedQuery<String> query =  getQuery(select_category, String.class);

		return query.getResultList();
	}

	public List<String> getBrandList(){
		TypedQuery<String> query = getQuery(select_brand_list,String.class);
		return query.getResultList();
	}

	public List<BrandPojo> selectBrand(String category,String brand)
	{
		if(category.length()==0 && brand.length()==0)
		{

			return this.selectAll();
		}
		if(category.equals("all") && brand.equals("all"))
		{
			return this.selectAll();
		}
		else if(category.equals("all"))
		{
			if(brand.length()==0)
			{
				return this.selectAll();
			}
			else{
				TypedQuery<BrandPojo> query = getQuery(select_brand,BrandPojo.class);
				query.setParameter("name",brand);
				return query.getResultList();
			}

		}
		else if(brand.equals("all"))
		{

			if(category.length()==0)
			{
				return this.selectAll();
			}

			TypedQuery<BrandPojo> query = getQuery(select_by_category,BrandPojo.class);
			query.setParameter("category",category);
			return query.getResultList();
		}
		else{
			TypedQuery<BrandPojo> query = getQuery(select_by_brand_and_category,BrandPojo.class);
			query.setParameter("category",category);
			query.setParameter("name",brand);
			return query.getResultList();
		}
	}

}
