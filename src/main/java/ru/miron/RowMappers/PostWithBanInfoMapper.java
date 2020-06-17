package ru.miron.RowMappers;

import static ru.miron.Config.DBConstants.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import ru.miron.Entities.FeedPostWithBanInfo;

public class PostWithBanInfoMapper implements RowMapper<FeedPostWithBanInfo> {

	@Override
	public FeedPostWithBanInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		int id = rs.getInt(postsTableIDFieldName);
		String login = rs.getString(usersTableLoginFieldName);
		Date publishDate = rs.getDate(postsTablePublishDateFieldName);
		String pictureName = rs.getString(postsTablePictureNameFieldName);
		String title = rs.getString(postsTableTitleFieldName);
		
		boolean isBanned = !rs.getBoolean(postsBannedTableRemovedFieldName); // isBanned = !removed !!!
		Date fromBanned = rs.getDate(postsBannedTableFromDateFieldName);
		Date toBanned = rs.getDate(postsBannedTableToDateFieldName);
		
		return new FeedPostWithBanInfo(id, login, publishDate, pictureName, isBanned, fromBanned, toBanned, title);
	}

}
