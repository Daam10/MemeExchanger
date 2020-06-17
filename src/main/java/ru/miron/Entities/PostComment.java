package ru.miron.Entities;

import java.util.Date;

public class PostComment {
	private Integer id;
	private String login;
	private String text;
	private Date date;
	
	
	public PostComment(Integer id, String login, String text, Date date) {
		this.id = id;
		this.text = text;
		this.date = date;
		this.login = login;
	}

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
