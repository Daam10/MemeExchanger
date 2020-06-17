package ru.miron.Entities;

import java.util.Date;

public class FeedPostWithBanInfo extends FeedPost{
	
	private boolean banned;
	private Date bannedFrom;
	private Date bannedTo;
	

	public FeedPostWithBanInfo(
			int id, 
			String login, 
			Date publishDate, 
			String pictureName, 
			boolean banned,
			Date bannedFrom,
			Date bannedTo) {
		this(id, login, publishDate, pictureName, banned, bannedFrom, bannedTo, null);
	}
	
	public FeedPostWithBanInfo(
			int id, 
			String login, 
			Date publishDate, 
			String pictureName, 
			boolean banned,
			Date bannedFrom,
			Date bannedTo,
			String title) {
		super(id, login, publishDate, pictureName, title);
		this.banned = banned;
		this.bannedFrom = bannedFrom;
		this.bannedTo = bannedTo;
	}

	public boolean isBanned() {
		return banned;
	}

	public void setBanned(boolean banned) {
		this.banned = banned;
	}

	public Date getBannedFrom() {
		return bannedFrom;
	}

	public void setBannedFrom(Date bannedFrom) {
		this.bannedFrom = bannedFrom;
	}

	public Date getBannedTo() {
		return bannedTo;
	}

	public void setBannedTo(Date bannedTo) {
		this.bannedTo = bannedTo;
	}

	@Override
	public String toString() {
		return "FeedPostWithBanInfo [isBanned()=" + isBanned() + ", getBannedFrom()=" + getBannedFrom()
				+ ", getBannedTo()=" + getBannedTo() + ", getId()=" + getId() + ", getTitle()=" + getTitle()
				+ ", isTitleExists()=" + isTitleExists() + ", getLogin()=" + getLogin() + ", getPublishDate()="
				+ getPublishDate() + ", getPictureName()=" + getPictureName() + "]";
	}
	
	
}
