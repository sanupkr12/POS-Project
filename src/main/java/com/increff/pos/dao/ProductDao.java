package com.increff.pos.dao;

import com.increff.pos.pojo.ProductPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public class ProductDao extends AbstractDao{
    private static final String SELECT_PRODUCT = "select p from ProductPojo p where barcode=:barcode";
    private static final String SELECT_PRODUCT_BY_ID = "select p from ProductPojo p where id=:id";
    private static final String SELECT_ALL = "select p from ProductPojo p";
    private static final String DELETE_PRODUCT = "delete from ProductPojo p where barcode=:barcode";
    private static final String DELETE_PRODUCT_BY_ID = "delete from ProductPojo p where id=:id";
    private static final String CHECK_ANY = "select p from ProductPojo p where barcode=:barcode";
    private static final String CHECK_ANY_BY_ID = "select p from ProductPojo p where id=:id";
    private static final String SELECT_BY_BRAND_ID = "select p from ProductPojo p where brandId=:brandId";

    public void insert(ProductPojo p){
        em.persist(p);
    }

    public ProductPojo select(String barcode){
        TypedQuery<ProductPojo> query = getQuery(SELECT_PRODUCT,ProductPojo.class);
        query.setParameter("barcode",barcode);
        return getSingle(query);
    }

    public ProductPojo selectById(int id){
        TypedQuery<ProductPojo> query = getQuery(SELECT_PRODUCT_BY_ID,ProductPojo.class);
        query.setParameter("id",id);
        return query.getSingleResult();
    }

    public List<ProductPojo> selectAll(){
        TypedQuery<ProductPojo> query = getQuery(SELECT_ALL,ProductPojo.class);
        List<ProductPojo> list = query.getResultList();
        return list;
    }

    public void deleteProduct(int id){
        Query query = em.createQuery(DELETE_PRODUCT_BY_ID);
        query.setParameter("id",id);
        query.executeUpdate();
    }

    //TODO use correct names for function
    public boolean checkAny(String barcode)
    {
        TypedQuery<ProductPojo> query = getQuery(CHECK_ANY,ProductPojo.class);
        query.setParameter("barcode",barcode);

        return !query.getResultList().isEmpty();
    }

    public List<ProductPojo> selectByBrandId(int brandId){
        TypedQuery<ProductPojo> query = getQuery(SELECT_BY_BRAND_ID,ProductPojo.class);
        query.setParameter("brandId",brandId);
        return query.getResultList();
    }

    public boolean checkAny(int id)
    {
        TypedQuery<ProductPojo> query = getQuery(CHECK_ANY_BY_ID,ProductPojo.class);
        query.setParameter("id",id);
        return !query.getResultList().isEmpty();
    }
}
