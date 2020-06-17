package ru.miron.RowMappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import ru.miron.Entities.FeedPost;

import static ru.miron.Config.DBConstants.*;

public class PostMapper implements RowMapper<FeedPost>{

	@Override
	public FeedPost mapRow(ResultSet rs, int rowNum) throws SQLException {
		int id = rs.getInt(postsTableIDFieldName);
		String login = rs.getString(usersTableLoginFieldName);
		Date publishDate = rs.getDate(postsTablePublishDateFieldName);
		String pictureName = rs.getString(postsTablePictureNameFieldName);
		String title = rs.getString(postsTableTitleFieldName);
		return new FeedPost(id, login, publishDate, pictureName, title);
		
	}

}
