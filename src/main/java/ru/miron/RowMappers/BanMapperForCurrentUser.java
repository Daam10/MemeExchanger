package ru.miron.RowMappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import ru.miron.Entities.Ban;
import ru.miron.Entities.FeedPost;
import static ru.miron.Config.DBConstants.*;

public class BanMapperForCurrentUser implements RowMapper<Ban>{

	@Override
	public Ban mapRow(ResultSet rs, int rowNum) throws SQLException {
		int id = rs.getInt(usersBannedTableIDFieldName);
		Date from = rs.getDate(usersBannedTableFromDateFieldName);
		Date to = rs.getDate(usersBannedTableToDateFieldName);
		boolean removed = rs.getBoolean(usersBannedTableRemovedFieldName);
		Date removeDate = rs.getDate(usersBannedTableRemovedDateFieldName);
		return new Ban(id, from, to, removed, removeDate);
	}
	
}
