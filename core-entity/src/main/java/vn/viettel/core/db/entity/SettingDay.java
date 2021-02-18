package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "shop_setting_days")
public class SettingDay extends BaseEntity {

	@Column(name = "type", nullable = false, length = 10)
	@NotNull
	private Integer type;

	@Column(name = "type_id", nullable = false)
	@NotNull
	private Long typeId;

	@Column(name = "status", nullable = false)
	@NotNull
	private Integer status;

	@Column(name = "weekdays", nullable = false)
	@NotNull
	private Integer weekdays;

	@Column(name = "opening_hour", nullable = false, length = 10)
	@NotNull
	private String openingHour;

	@Column(name = "closed_hour", nullable = false, length = 10)
	@NotNull
	private String closedHour;

	@Column(name = "breaktime_start", nullable = false, length = 10)
	@NotNull
	private String breaktimeStart;

	@Column(name = "breaktime_end", nullable = false, length = 10)
	@NotNull
	private String breaktimeEnd;

	@Column(name = "available_time", length = 96, nullable = false)
	private String availableTime;

	public SettingDay() {
		super();
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getWeekdays() {
		return weekdays;
	}

	public void setWeekdays(Integer weekdays) {
		this.weekdays = weekdays;
	}

	public String getOpeningHour() {
		return openingHour;
	}

	public void setOpeningHour(String openingHour) {
		this.openingHour = openingHour;
	}

	public String getClosedHour() {
		return closedHour;
	}

	public void setClosedHour(String closedHour) {
		this.closedHour = closedHour;
	}

	public String getBreaktimeStart() {
		return breaktimeStart;
	}

	public void setBreaktimeStart(String breaktimeStart) {
		this.breaktimeStart = breaktimeStart;
	}

	public String getBreaktimeEnd() {
		return breaktimeEnd;
	}

	public void setBreaktimeEnd(String breaktimeEnd) {
		this.breaktimeEnd = breaktimeEnd;
	}

	public String getAvailableTime() {
		return availableTime;
	}

	public void setAvailableTime(String availableTime) {
		this.availableTime = availableTime;
	}

}
