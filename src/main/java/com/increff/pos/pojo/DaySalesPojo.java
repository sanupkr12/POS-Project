package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@Entity
public class DaySalesPojo {

    @Id
    private Date date;
    private int orderCount;
    private int itemCount;
    private double revenue;

}
