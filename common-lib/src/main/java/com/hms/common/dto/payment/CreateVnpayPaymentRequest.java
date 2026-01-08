package com.hms.common.dto.payment;

public class CreateVnpayPaymentRequest {
    private Long invoiceId;

    public CreateVnpayPaymentRequest() {
    }

    public CreateVnpayPaymentRequest(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }
}
