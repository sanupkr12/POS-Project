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

    @PersistenceContext
    private EntityManager em;



    private String select_product = "select p from ProductPojo p where barcode=:barcode";
    private String select_product_by_id = "select p from ProductPojo p where id=:id";
    private String select_all = "select p from ProductPojo p";
    private String delete_product = "delete from ProductPojo p where barcode=:barcode";
    private String delete_product_by_id = "delete from ProductPojo p where id=:id";
    private String check_any = "select p from ProductPojo p where barcode=:barcode";
    private String check_any_by_id = "select p from ProductPojo p where id=:id";
    private String select_by_brandId = "select p from ProductPojo p where brandId=:brandId";

    @Transactional
    public void insert(ProductPojo p){
        em.persist(p);

    }

    public ProductPojo select(String barcode){
        TypedQuery<ProductPojo> query = getQuery(select_product,ProductPojo.class);
        query.setParameter("barcode",barcode);
        ProductPojo p = getSingle(query);
        return p;
    }

    public ProductPojo selectById(int id){
        TypedQuery<ProductPojo> query = getQuery(select_product_by_id,ProductPojo.class);
        query.setParameter("id",id);
        return query.getSingleResult();
    }

    public List<ProductPojo> selectAll(){
        TypedQuery<ProductPojo> query = getQuery(select_all,ProductPojo.class);
        List<ProductPojo> list = query.getResultList();
        return list;
    }

    @Transactional
    public void deleteProduct(String barcode){
        Query query = em.createQuery(delete_product);
        query.setParameter("barcode",barcode);
        query.executeUpdate();
    }

    @Transactional
    public void deleteProduct(int id){
        Query query = em.createQuery(delete_product_by_id);
        query.setParameter("id",id);
        query.executeUpdate();
    }

    public void updateProduct(ProductPojo p){

    }

    public boolean checkAny(String barcode)
    {
        TypedQuery<ProductPojo> query = getQuery(check_any,ProductPojo.class);
        query.setParameter("barcode",barcode);

        if(query.getResultList().size() == 0)
        {
            return false;
        }

        return true;
    }

    public List<ProductPojo> selectByBrandId(int brandId){
        TypedQuery<ProductPojo> query = getQuery(select_by_brandId,ProductPojo.class);
        query.setParameter("brandId",brandId);
        return query.getResultList();
    }

    public boolean checkAny(int id)
    {
        TypedQuery<ProductPojo> query = getQuery(check_any_by_id,ProductPojo.class);
        query.setParameter("id",id);

        if(query.getResultList().size() == 0)
        {
            return false;
        }

        return true;
    }

}
