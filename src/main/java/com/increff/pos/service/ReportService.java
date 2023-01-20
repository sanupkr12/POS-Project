package com.increff.pos.service;


import com.increff.pos.dao.DaySalesDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dto.BrandDto;
import com.increff.pos.dto.InventoryDto;
import com.increff.pos.dto.OrderDto;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.*;
import com.increff.pos.pojo.*;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class ReportService {

    @Autowired
    private DaySalesDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public void add(DaySalesPojo p){
        dao.insert(p);
    }

    public List<DaySalesPojo> getDaySalesInfo(){

        List<DaySalesPojo> list = dao.get();
        return list;

    }


}
