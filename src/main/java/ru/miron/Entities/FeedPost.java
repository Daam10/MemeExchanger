package ru.miron.Entities;

import java.util.Date;

public class FeedPost {
	private int id;
	private String title;
	//private int userID;
	String login;
	private Date publishDate;
	private String pictureName;
	
	public FeedPost(int id, String login, Date publishDate, String pictureName) {
		this(id, login, publishDate, pictureName, null);
	}
	
	public FeedPost(int id, String login, Date publishDate, String pictureName, String title) {
		this.id = id;
		this.title = title;
		this.login = login;
		this.publishDate = publishDate;
		this.pictureName = pictureName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}
	
	public boolean isTitleExists() {
		return title != null;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public String getPictureName() {
		return pictureName;
	}

	public void setPictureName(String pictureName) {
		this.pictureName = pictureName;
	}
	
	
	
	
}
