package ru.miron.Entities;

import java.util.Date;

public class DateInterval {
	private Date fromDate;
	private Date toDate;
	
	public DateInterval(Date fromDate, Date toDate) {
		this.fromDate = fromDate;
		this.toDate = toDate;
	}
	
	public Date getFromDate() {
		return fromDate;
	}
	
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	
	public Date getToDate() {
		return toDate;
	}
	
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
}
