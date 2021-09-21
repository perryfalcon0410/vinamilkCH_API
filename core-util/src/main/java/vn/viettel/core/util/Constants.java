package vn.viettel.core.util;

import java.util.Arrays;
import java.util.List;

public class Constants {

	public final static String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

	// Time
	public final static List<String> TIME = Arrays.asList("00:00", "00:15", "00:30", "00:45", "01:00", "01:15", "01:30",
			"01:45", "02:00", "02:15", "02:30", "02:45", "03:00", "03:15", "03:30", "03:45", "04:00", "04:15", "04:30",
			"04:45", "05:00", "05:15", "05:30", "05:45", "06:00", "06:15", "06:30", "06:45", "07:00", "07:15", "07:30",
			"07:45", "08:00", "08:15", "08:30", "08:45", "09:00", "09:15", "09:30", "09:45", "10:00", "10:15", "10:30",
			"10:45", "11:00", "11:15", "11:30", "11:45", "12:00", "12:15", "12:30", "12:45", "13:00", "13:15", "13:30",
			"13:45", "14:00", "14:15", "14:30", "14:45", "15:00", "15:15", "15:30", "15:45", "16:00", "16:15", "16:30",
			"16:45", "17:00", "17:15", "17:30", "17:45", "18:00", "18:15", "18:30", "18:45", "19:00", "19:15", "19:30",
			"19:45", "20:00", "20:15", "20:30", "20:45", "21:00", "21:15", "21:30", "21:45", "22:00", "22:15", "22:30",
			"22:45", "23:00", "23:15", "23:30", "23:45");

	public final static String SERVICE_ALIVE = "UP";

	public final static String REQUEST_FROM = "request-from";
	public final static String CLIENT_REQUEST = "client-request";
	public final static String SERVICE_REQUEST = "service-request";
	public final static String CURRENT_SHOP_ID = "current-shop-id";
	public final static String CURRENT_USER_ID = "current-user-id";
	public final static String CURRENT_USERNAME = "current-username";

	public final static String REQUEST_SECRET_MARK_AS_OUTSIDE_REQUEST = "request_secret_mark_as_outside_request";

	public final static String REQUEST_SECRET_MARK_AS_CLAIM_OUTSIDE_REQUEST = "request_secret_mark_as_claim_outside_request";

	public final static String REQUEST_SECRET_MARK_AS_OLD_TOKEN = "request_secret_mark_as_old_token";

	public final static String REQUEST_SECRET_MARK_AS_LOGIN_USER_ID = "request_secret_mark_as_login_user_id";

	//Sort vietnamese
	public final static String rules = "<0<1<2<3<4<5<6<7<8<9<@"+
			"<a,A<á<à<ả<ã<ạ<ă<ắ<ằ<ẳ<ẵ<ặ<â<ấ<ầ<ẩ<ẫ<ậ"+
			"<b<c<d,D<đ,Đ<e<é<è<ẻ<ẽ<ẹ<ê<ế<ề<ể<ễ<ệ<f<g<h"+
			"<i<í<ì<ỉ<ĩ<ị<j<k<l<m<n<o<ó<ò<ỏ<õ<ọ<ô<ố<ồ<ỗ"+
			"<ộ<ơ<ớ<ờ<ở<ỡ<ợ<p<q<r<s<t"+
			"<u<ú<ù<ủ<ũ<ụ<ư<ứ<ừ<ử<ữ"+
			"<v<w<x<y<ý<ỳ<ỷ<ỹ<ỵ<z";
}
