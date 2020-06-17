package ru.miron.RowMappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ru.miron.Config.DBConstants;
import ru.miron.Entities.IPAddress;

public class IPAddressMapper  implements RowMapper<IPAddress>{

	@Override
	public IPAddress mapRow(ResultSet rs, int rowNum) throws SQLException {
		int id = rs.getInt(DBConstants.usersIPAddressesTableIDFieldName);
		int userId = rs.getInt(DBConstants.usersIPAddressesTableUserIDFieldName);
		String name = rs.getString(DBConstants.usersIPAddressesTableAddressFieldName);
		return new IPAddress(id, userId, name);
	}

}
