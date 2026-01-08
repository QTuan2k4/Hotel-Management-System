package com.hms.billing.entity;

import com.hms.common.dto.billing.InvoiceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "bookingId" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long bookingId;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private Long roomId;

	@Column(nullable = false)
	private long nights;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal pricePerNight;

	@Column(nullable = false, precision = 14, scale = 2)
	private BigDecimal total;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private InvoiceStatus status;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	private LocalDateTime paidAt;
}
