package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "shop_setting_days")
public class ShopSettingDay extends BaseEntity {

	@Column(name = "available_time")
	private String availableTime;

	@Column(name = "breaktime_end")
	private String breaktimeEnd;

	@Column(name = "breaktime_start")
	private String breaktimeStart;

	@Column(name = "closed_hour")
	private String closedHour;

	@Column(name = "opening_hour")
	private String openingHour;

	private int status;

	private String type;

	@Column(name = "type_id")
	private int typeId;

	private int weekdays;

	public ShopSettingDay() {
	}

	public String getAvailableTime() {
		return this.availableTime;
	}

	public void setAvailableTime(String availableTime) {
		this.availableTime = availableTime;
	}

	public String getBreaktimeEnd() {
		return this.breaktimeEnd;
	}

	public void setBreaktimeEnd(String breaktimeEnd) {
		this.breaktimeEnd = breaktimeEnd;
	}

	public String getBreaktimeStart() {
		return this.breaktimeStart;
	}

	public void setBreaktimeStart(String breaktimeStart) {
		this.breaktimeStart = breaktimeStart;
	}

	public String getClosedHour() {
		return this.closedHour;
	}

	public void setClosedHour(String closedHour) {
		this.closedHour = closedHour;
	}

	public String getOpeningHour() {
		return this.openingHour;
	}

	public void setOpeningHour(String openingHour) {
		this.openingHour = openingHour;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getTypeId() {
		return this.typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public int getWeekdays() {
		return this.weekdays;
	}

	public void setWeekdays(int weekdays) {
		this.weekdays = weekdays;
	}

}