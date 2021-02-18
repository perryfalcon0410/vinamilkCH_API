package vn.viettel.core.db.entity.status;

public enum PublishStatus implements Validatable {

	PUBLISHED(1), NOT_PUBLISHED(0);

	private int status;

	PublishStatus(int status) {
		this.status = status;
	}

	public int status() {
		return this.status;
	}

	@Override
	public String validateValue() {
		return String.valueOf(this.status);
	}

}
