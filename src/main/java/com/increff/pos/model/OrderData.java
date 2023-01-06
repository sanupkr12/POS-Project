package com.increff.pos.model;

import java.util.Date;

public class OrderData {
    int id;
    double total;
    Date date;
    boolean invoiceGenerated;

    public boolean isInvoiceGenerated() {
        return invoiceGenerated;
    }

    public void setInvoiceGenerated(boolean invoiceGenerated) {
        this.invoiceGenerated = invoiceGenerated;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
