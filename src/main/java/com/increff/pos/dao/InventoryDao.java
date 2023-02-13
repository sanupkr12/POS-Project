package com.increff.pos.dao;

import com.increff.pos.pojo.InventoryPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Repository
public class InventoryDao extends AbstractDao{
    private static final String SELECT_BY_BARCODE = "select p from InventoryPojo p where barcode=:barcode";
    private static final String SELECT_ALL = "select p from InventoryPojo p";


    public InventoryPojo select(String barcode){
        TypedQuery<InventoryPojo> query = getQuery(SELECT_BY_BARCODE,InventoryPojo.class);
        query.setParameter("barcode",barcode);
        return getSingle(query);
    }

    public List<InventoryPojo> selectAll(){
        TypedQuery<InventoryPojo> query = getQuery(SELECT_ALL,InventoryPojo.class);

        return query.getResultList();
    }

    public List<InventoryPojo> selectInventory(List<String> barcodeList){
        List<InventoryPojo> list = new ArrayList<>();

        for(String barcode:barcodeList)
        {
            TypedQuery<InventoryPojo> query = getQuery(SELECT_BY_BARCODE,InventoryPojo.class);
            query.setParameter("barcode",barcode);
            InventoryPojo p = getSingle(query);
            if(p!=null)
            {
                list.add(p);
            }
        }
        return list;
    }

    public void insert(InventoryPojo p){
        em.persist(p);
    }


}
