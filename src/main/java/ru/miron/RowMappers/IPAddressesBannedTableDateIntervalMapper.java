package ru.miron.RowMappers;

import static ru.miron.Config.DBConstants.userIPAddressesBannedTableFromDateFieldName;
import static ru.miron.Config.DBConstants.userIPAddressesBannedTableToDateFieldName;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import ru.miron.Entities.DateInterval;

public class IPAddressesBannedTableDateIntervalMapper implements RowMapper<DateInterval>{

	@Override
	public DateInterval mapRow(ResultSet rs, int rowNum) throws SQLException {
		Date fromDate = rs.getDate(userIPAddressesBannedTableFromDateFieldName);
		Date toDate = rs.getDate(userIPAddressesBannedTableToDateFieldName);
		return new DateInterval(fromDate, toDate);
	}

}
