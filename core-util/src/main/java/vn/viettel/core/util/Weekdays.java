package vn.viettel.core.util;

public enum Weekdays {
	
	SUNDAY(0, "sunday"), MONDAY(1, "monday"), TUESDAY(2, "tuesday"), WEDNESDAY(3, "wednesday"), THURSDAY(4,
			"thurday"), FRIDAY(5, "friday"), SATURDAY(6, "saturday");

	private int weekdayNumber;
	private String weekdayValue;

	private Weekdays(int weekdayNumber, String weekdayValue) {
		this.weekdayNumber = weekdayNumber;
		this.weekdayValue = weekdayValue;
	}

	public int getWeekdayNumber() {
		return weekdayNumber;
	}

	public String getWeekdayValue() {
		return weekdayValue;
	}

	public static String valueOf(int weekdayNumber) {
		for (Weekdays weekday : Weekdays.values()) {
			if (weekday.weekdayNumber == weekdayNumber) {
				return weekday.weekdayValue;
			}
		}
		return null;
	}

}
