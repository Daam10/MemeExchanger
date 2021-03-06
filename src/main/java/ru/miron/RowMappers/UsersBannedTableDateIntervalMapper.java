package ru.miron.RowMappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import ru.miron.Entities.DateInterval;
import ru.miron.Entities.FeedPost;

import static ru.miron.Config.DBConstants.*;

public class UsersBannedTableDateIntervalMapper implements RowMapper<DateInterval>{

	@Override
	public DateInterval mapRow(ResultSet rs, int rowNum) throws SQLException {
		Date fromDate = rs.getDate(usersBannedTableFromDateFieldName);
		Date toDate = rs.getDate(usersBannedTableToDateFieldName);
		return new DateInterval(fromDate, toDate);
	}

}
