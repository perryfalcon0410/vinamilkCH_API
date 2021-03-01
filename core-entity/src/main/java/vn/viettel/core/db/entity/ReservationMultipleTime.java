package vn.viettel.core.db.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "reservation_multiple_times")
public class ReservationMultipleTime extends BaseEntity {

	@Column(name = "item_id", nullable = false)
	private Long itemId;

	@Column(name = "reservation_date")
	private LocalDate reservationDate;

	@Column(name = "reservation_time")
	private String reservationTime;

	public ReservationMultipleTime() {
	}

	public Long getItemId() {
		return this.itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public LocalDate getReservationDate() {
		return this.reservationDate;
	}

	public void setReservationDate(LocalDate reservationDate) {
		this.reservationDate = reservationDate;
	}

	public String getReservationTime() {
		return this.reservationTime;
	}

	public void setReservationTime(String reservationTime) {
		this.reservationTime = reservationTime;
	}

}