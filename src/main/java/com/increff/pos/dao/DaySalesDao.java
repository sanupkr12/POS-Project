package com.increff.pos.dao;

import com.increff.pos.pojo.DaySalesPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class DaySalesDao {

    @PersistenceContext
    private EntityManager em;


    public void insert(DaySalesPojo p)
    {
        em.persist(p);
    }
}
