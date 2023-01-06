package com.increff.pos.dao;

import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.stereotype.Repository;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class OrderItemDao extends AbstractDao{
    private String select_by_order_id = "select p from OrderItemPojo p where orderId=:orderId";
    private String select_by_item_id = "select p from OrderItemPojo p where id=:id";
    private String select_by_product_id = "select p from OrderItemPojo p where productId=:productId";
    @PersistenceContext
    private EntityManager em;

    public void insert(OrderItemPojo p){
        em.persist(p);
    }

    public List<OrderItemPojo> getByOrderId(int orderId){
        TypedQuery<OrderItemPojo> query = getQuery(select_by_order_id,OrderItemPojo.class);
        query.setParameter("orderId",orderId);
        return query.getResultList();
    }

    public OrderItemPojo getByItemId(int id){

        TypedQuery<OrderItemPojo> query = getQuery(select_by_item_id,OrderItemPojo.class);
        query.setParameter("id",id);
        return query.getSingleResult();

    }

    public List<OrderItemPojo> getByProductId(int productId){
        TypedQuery<OrderItemPojo> query = getQuery(select_by_product_id,OrderItemPojo.class);
        query.setParameter("productId",productId);
        return query.getResultList();
    }
}
