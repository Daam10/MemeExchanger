package ru.miron.Entities;

import java.util.Date;

public class PostCommentWithBanInfo extends PostComment{

	private boolean banned;
	private Date bannedFrom;
	private Date bannedTo;

	public PostCommentWithBanInfo(
			Integer id, 
			String login,
			String text, 
			Date date,
			boolean banned,
			Date bannedFrom,
			Date bannedTo) {
		super(id, login, text, date);
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
}
