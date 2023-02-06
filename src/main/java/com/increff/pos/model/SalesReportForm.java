package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class SalesReportForm {
    private Date startDate;
    private Date endDate;
    private String category;
    private String brand;

}
