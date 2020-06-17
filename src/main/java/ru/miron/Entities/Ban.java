package ru.miron.Entities;

import java.util.Date;

public class Ban {
	private Integer id;
	private Date from;
	private Date to;
	private Boolean removed;
	private Date removeDate;
	
	public Ban(int id, Date from, Date to, Boolean removed, Date removeDate) {
		this.id = id;
		this.from = from;
		this.to = to;
		this.removed = removed;
		this.removeDate = removeDate;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

	public Boolean getRemoved() {
		return removed;
	}

	public void setRemoved(Boolean removed) {
		this.removed = removed;
	}

	public Date getRemoveDate() {
		return removeDate;
	}

	public void setRemoveDate(Date removeDate) {
		this.removeDate = removeDate;
	}
	
	
}
