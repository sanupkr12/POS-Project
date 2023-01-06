package com.increff.pos.model;

public class invoiceData {
    int id;
    boolean invoiceGenerated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isInvoiceGenerated() {
        return invoiceGenerated;
    }

    public void setInvoiceGenerated(boolean invoiceGenerated) {
        this.invoiceGenerated = invoiceGenerated;
    }
}
