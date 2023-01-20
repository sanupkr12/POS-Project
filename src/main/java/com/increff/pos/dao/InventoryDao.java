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
    private String select_by_barcode = "select p from InventoryPojo p where barcode=:barcode";
    private String select_all = "select p from InventoryPojo p";


    @PersistenceContext
    private EntityManager em;

    public InventoryPojo select(String barcode){

        TypedQuery<InventoryPojo> query = getQuery(select_by_barcode,InventoryPojo.class);

        query.setParameter("barcode",barcode);

        InventoryPojo p =  getSingle(query);

        return p;
    }

    @Transactional
    public List<InventoryPojo> selectAll(){
        TypedQuery<InventoryPojo> query = getQuery(select_all,InventoryPojo.class);

        return query.getResultList();
    }

    public List<InventoryPojo> selectInventory(List<String> barcodeList){
        List<InventoryPojo> list = new ArrayList<>();

        for(String barcode:barcodeList)
        {

            TypedQuery<InventoryPojo> query = getQuery(select_by_barcode,InventoryPojo.class);


            query.setParameter("barcode",barcode);

            InventoryPojo p = getSingle(query);

            if(p!=null)
            {
                list.add(p);
            }


        }

        return list;

    }

    @Transactional
    public void insert(InventoryPojo p){

        em.persist(p);

    }

    @Transactional
    public void update(){

    }




}
