package vn.viettel.core.db.entity.status;

import vn.viettel.core.util.status.Validatable;

public enum SettingDayStatus implements Validatable {

	OPENING(1), CLOSED(0);

	private int status;

	SettingDayStatus(int status) {
		this.status = status;
	}

	public int status() {
		return this.status;
	}

	public String value() {
		return String.valueOf(this.status);
	}

	@Override
	public String validateValue() {
		return String.valueOf(this.status);
	}
}
