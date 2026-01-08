package com.hms.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_txn", uniqueConstraints = @UniqueConstraint(columnNames = { "txnRef" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTxn {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long invoiceId;

	@Column(nullable = false, length = 30)
	private String provider; // VNPAY

	@Column(nullable = false, length = 80)
	private String txnRef;

	@Column(nullable = false, precision = 14, scale = 2)
	private BigDecimal amount;

	@Column(nullable = false, length = 30)
	private String status; // INITIATED/SUCCESS/FAILED

	private LocalDateTime createdAt;
	private LocalDateTime paidAt;

	@Column(length = 2000)
	private String rawCode;
}
