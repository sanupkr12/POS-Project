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

    private String selectOrder = "select p from OrderPojo p where id=:id";
    private String select_all = "select p from OrderPojo p";
    private String update_invoice = "update OrderPojo p set p.invoiceGenerated = true where p.id=:id";

    @PersistenceContext
    private EntityManager em;

    public void insert(OrderPojo p){
        em.persist(p);
    }

    public OrderPojo get(int id){
        TypedQuery<OrderPojo> query = getQuery(selectOrder,OrderPojo.class);
        query.setParameter("id",id);
        return query.getSingleResult();
    }

    public List<OrderPojo> get(){
        TypedQuery<OrderPojo> query = getQuery(select_all,OrderPojo.class);
        return query.getResultList();
    }
    public void updateInvoice(int id){
        Query query = em.createQuery(update_invoice,OrderPojo.class);
        query.setParameter("id",id);
        query.executeUpdate();
    }




}
