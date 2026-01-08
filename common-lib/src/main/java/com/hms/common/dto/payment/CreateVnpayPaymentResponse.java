package com.hms.common.dto.payment;

public class CreateVnpayPaymentResponse {
    private String paymentUrl;
    private String txnRef;

    public CreateVnpayPaymentResponse() {
    }

    public CreateVnpayPaymentResponse(String paymentUrl, String txnRef) {
        this.paymentUrl = paymentUrl;
        this.txnRef = txnRef;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }
}
