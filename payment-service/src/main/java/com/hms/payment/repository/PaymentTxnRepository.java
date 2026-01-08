package com.hms.payment.repository;

import com.hms.payment.entity.PaymentTxn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentTxnRepository extends JpaRepository<PaymentTxn, Long> {
    Optional<PaymentTxn> findByTxnRef(String txnRef);
}
