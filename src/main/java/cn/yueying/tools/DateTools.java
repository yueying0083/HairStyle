package cn.yueying.tools;

public class DateTools {

	public java.sql.Date convertToSQLDate(java.util.Date date) {
		return new java.sql.Date(date.getTime());
	}
}
