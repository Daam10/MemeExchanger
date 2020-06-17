package ru.miron.RowMappers;

import static ru.miron.Config.DBConstants.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import ru.miron.Entities.PostCommentWithBanInfo;

public class PostCommentWithBanInfoMapper implements RowMapper<PostCommentWithBanInfo> {

	@Override
	public PostCommentWithBanInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		int id = rs.getInt(postCommentsTableIDFieldName);
		String login = rs.getString(usersTableLoginFieldName);
		String text = rs.getString(postCommentsTableTextFieldName);
		Date date = rs.getDate(postCommentsTableDateFieldName);
		
		boolean isBanned = !rs.getBoolean(postCommentsBannedTableRemovedFieldName); // isBanned = !removed !!!
		Date fromBanned = rs.getDate(postCommentsBannedTableFromDateFieldName);
		Date toBanned = rs.getDate(postCommentsBannedTableToDateFieldName);
		
		return new PostCommentWithBanInfo(id, login, text, date, isBanned, fromBanned, toBanned);
	}

}
