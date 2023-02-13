package com.increff.pos.dao;

import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.stereotype.Repository;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class OrderItemDao extends AbstractDao{
    private static String SELECT_BY_ORDER_ID = "select p from OrderItemPojo p where orderId=:orderId";
    private static String SELECT_BY_ITEM_ID = "select p from OrderItemPojo p where id=:id";
    private static String SELECT_BY_PRODUCT_ID = "select p from OrderItemPojo p where productId=:productId";


    public void insert(OrderItemPojo p){
        em.persist(p);
    }

    public List<OrderItemPojo> getByOrderId(int orderId){
        TypedQuery<OrderItemPojo> query = getQuery(SELECT_BY_ORDER_ID,OrderItemPojo.class);
        query.setParameter("orderId",orderId);
        return query.getResultList();
    }

    public OrderItemPojo getByItemId(int id){
        TypedQuery<OrderItemPojo> query = getQuery(SELECT_BY_ITEM_ID,OrderItemPojo.class);
        query.setParameter("id",id);
        return query.getSingleResult();
    }

    public List<OrderItemPojo> getByProductId(int productId){
        TypedQuery<OrderItemPojo> query = getQuery(SELECT_BY_PRODUCT_ID,OrderItemPojo.class);
        query.setParameter("productId",productId);
        return query.getResultList();
    }
}
