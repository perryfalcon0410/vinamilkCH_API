package vn.viettel.sale.service.enums;

import lombok.Getter;

@Getter
public enum StatusType {
    	UNAPPROVE_STATUS(0),
    	APPROVE_STATUS(1),
    	CANCEL_STATUS(2);
    	 
    	private final Integer code;
    	
		StatusType(int code) {
			this.code = code;
		}
		
		public Integer code() {
			return code;
		}
}
