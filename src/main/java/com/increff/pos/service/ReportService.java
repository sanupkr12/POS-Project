package com.increff.pos.service;

import com.increff.pos.dao.DaySalesDao;
import com.increff.pos.pojo.DaySalesPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private DaySalesDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public void add(DaySalesPojo p) {
        dao.insert(p);
    }

    public List<DaySalesPojo> getDaySalesInfo() {
        List<DaySalesPojo> list = dao.get();
        return list;
    }
}
