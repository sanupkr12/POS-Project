package com.increff.pos.dao;

import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.DaySalesPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class DaySalesDao extends AbstractDao {

    @PersistenceContext
    private EntityManager em;

    private String getDaySales = "select p from DaySalesPojo p";


    public void insert(DaySalesPojo p)
    {
        em.persist(p);
    }

    public List<DaySalesPojo> get(){
        TypedQuery<DaySalesPojo> query = getQuery(getDaySales, DaySalesPojo.class);
        return query.getResultList();
    }
}
