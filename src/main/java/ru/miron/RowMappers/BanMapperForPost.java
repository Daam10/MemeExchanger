package ru.miron.RowMappers;

import static ru.miron.Config.DBConstants.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import ru.miron.Entities.Ban;

public class BanMapperForPost implements RowMapper<Ban> {

	@Override
	public Ban mapRow(ResultSet rs, int rowNum) throws SQLException {
		int id = rs.getInt(postsBannedTableIDFieldName);
		Date from = rs.getDate(postsBannedTableFromDateFieldName);
		Date to = rs.getDate(postsBannedTableToDateFieldName);
		boolean removed = rs.getBoolean(postsBannedTableRemovedFieldName);
		Date removeDate = rs.getDate(postsBannedTableRemovedDateFieldName);
		return new Ban(id, from, to, removed, removeDate);
	}

}
