package com.increff.pos.dao;


import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class OrderDao extends AbstractDao{

    private static final String SELECT_ORDER = "select p from OrderPojo p where id=:id";
    private static final String SELECT_ALL = "select p from OrderPojo p";

    public void insert(OrderPojo p){
        em.persist(p);
    }

    public OrderPojo get(int id){
        TypedQuery<OrderPojo> query = getQuery(SELECT_ORDER,OrderPojo.class);
        query.setParameter("id",id);
        return query.getSingleResult();
    }

    public List<OrderPojo> get(){
        TypedQuery<OrderPojo> query = getQuery(SELECT_ALL,OrderPojo.class);
        return query.getResultList();
    }

}
