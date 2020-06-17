package ru.miron.DAOsImpls;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.miron.DAOs.IUserDAO;
import ru.miron.Entities.Ban;
import ru.miron.Entities.DateInterval;
import ru.miron.Entities.FeedPost;
import ru.miron.Entities.IPAddress;
import ru.miron.RowMappers.BanMapperForCurrentUser;
import ru.miron.RowMappers.IPAddressMapper;
import ru.miron.RowMappers.UsersBannedTableDateIntervalMapper;
import ru.miron.Utils.CommonUtils;

import static ru.miron.Config.DBConstants.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class MySQLUserDAO implements IUserDAO {
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	public MySQLUserDAO(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public boolean existsUserByLogin(String login) throws DataAccessException, IllegalStateException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} = ?;", 
				usersTableName, 
				usersTableLoginFieldName);
		
		int accountsCount = jdbcTemplate.queryForObject(query, new Object[] {login}, Integer.class);
		if(accountsCount == 1) {
			return true;
		}
		if(accountsCount == 0) {
			return false;
		}
		throw new IllegalStateException("DB unique login error"); // don't need to be localized
	}
	
	@Override
	public boolean existsUserById(int id) throws DataAccessException, IllegalStateException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} = ?;", 
				usersTableName, 
				usersTableIDFieldName);
		
		int accountsCount = jdbcTemplate.queryForObject(query, new Object[] {id}, Integer.class);
		System.out.println(accountsCount);
		if(accountsCount == 1) {
			return true;
		}
		if(accountsCount == 0) {
			return false;
		}
		throw new IllegalStateException("DB unique id error"); // don't need to be localized
	}
	
	@Override
	public boolean existsIPAddressById(int id) throws DataAccessException, IllegalStateException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} = ?;", 
				usersIPAddressesTableName, 
				usersIPAddressesTableIDFieldName);
		
		int accountsCount = jdbcTemplate.queryForObject(query, new Object[] {id}, Integer.class);
		System.out.println(accountsCount);
		if(accountsCount == 1) {
			return true;
		}
		if(accountsCount == 0) {
			return false;
		}
		throw new IllegalStateException("DB unique id error"); // don't need to be localized
	}

	@Override
	public boolean isWrong(String login, String password) throws DataAccessException, IllegalStateException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} = ? AND {2} = ?;", 
				usersTableName, 
				usersTableLoginFieldName, 
				usersTablePasswordFieldName);
		
		int accountsCount = jdbcTemplate.queryForObject(query, new Object[] {login, password}, Integer.class);
		if(accountsCount == 1) {
			return false;
		}
		if(accountsCount == 0) {
			return true;
		}
		throw new IllegalStateException("DB unique login-password error"); // don't need to be localized
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public void insertUser(String login, String password) throws DataAccessException {
		String query = MessageFormat.format(
				"INSERT INTO {0} ({1}, {2}, {3}, {4}) VALUES (?, ?, curdate(), ?);", 
				usersTableName, 
				usersTableLoginFieldName, 
				usersTablePasswordFieldName,
				usersTableCreationDataFieldName,
				usersTableAccessLevelIDFieldName);
		int answerCode = jdbcTemplate.update(query, new Object[] {login, password, defaultUserAccessLevelID});
	}

	@Override
	public int getCountOfAccountsOfAddr(String addr) throws DataAccessException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM (SELECT DISTINCT {2} FROM {0} WHERE {1} = ?) as t;", 
				usersIPAddressesTableName, 
				usersIPAddressesTableAddressFieldName,
				usersIPAddressesTableUserIDFieldName);
		return jdbcTemplate.queryForObject(query, new Object[] {addr}, Integer.class);
	}

	@Override
	public boolean isBannedUser(String login) throws DataAccessException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {3} WHERE {4} = (SELECT {0} FROM {1} WHERE {2} = ?) AND {5} <= curdate() AND {6} >= curdate() AND {7} = false;",
				usersTableIDFieldName, 
				usersTableName,
				usersTableLoginFieldName,
				usersBannedTableName,
				usersBannedTableUserIDFieldName,
				usersBannedTableFromDateFieldName,
				usersBannedTableToDateFieldName,
				usersBannedTableRemovedFieldName);
		return  0 < jdbcTemplate.queryForObject(query, new Object[] {login}, Integer.class);
	}

	@Override
	public DateInterval getLastUserBanDateInterval(String login) throws DataAccessException {
		String query = MessageFormat.format(
				"SELECT {3}, {4} FROM {5} WHERE {4} = (SELECT MAX({4}) FROM {5} WHERE {6} = (SELECT {0} FROM {1} WHERE {2} = ?)) AND {3} <= curdate()",
				usersTableIDFieldName,
				usersTableName,
				usersTableLoginFieldName,
				usersBannedTableFromDateFieldName,
				usersBannedTableToDateFieldName,
				usersBannedTableName,
				usersBannedTableUserIDFieldName);
		System.out.println(query);
		List<DateInterval> lastDateIntervals;
		try {
			lastDateIntervals = jdbcTemplate.query(query, new Object[] {login}, new UsersBannedTableDateIntervalMapper());
		} catch(EmptyResultDataAccessException ex) {
			return null;
		}
		if(lastDateIntervals.isEmpty()) {
			return null;
		}
		return CommonUtils.getDateIntervalWithMinFromDate(lastDateIntervals);
	}

	@Override
	public boolean isAdmin(String login) throws DataAccessException { // should be executed after user check
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} = ? AND {2} = {3}",
				usersTableName,
				usersTableLoginFieldName,
				usersTableAccessLevelIDFieldName,
				adminAccessLevelID);
		return 1 == jdbcTemplate.queryForObject(query, new Object[] {login}, Integer.class);
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public int banUser(Date from, Date to, String login) throws DataAccessException {
		String query = MessageFormat.format(
				"INSERT INTO {0} ({1}, {2}, {3}) VALUES ((SELECT {4} FROM {5} WHERE {6} = ?), ?, ?)",
				usersBannedTableName,
				usersBannedTableUserIDFieldName,
				usersBannedTableFromDateFieldName,
				usersBannedTableToDateFieldName,
				usersTableIDFieldName,
				usersTableName,
				usersTableLoginFieldName);
		int countBanned = jdbcTemplate.update(query, new Object[] {login, dbDateFormater.format(from), dbDateFormater.format(to)});
		return countBanned;
	}
	
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public int banIPAddress(Date from, Date to, int id) throws DataAccessException {
		String query = MessageFormat.format(
				"INSERT INTO {0} ({1}, {2}, {3}) VALUES (?, ?, ?)",
				userIPAddressesBannedTableName,
				userIPAddressesBannedTableIPAdressIDFieldName,
				userIPAddressesBannedTableFromDateFieldName,
				userIPAddressesBannedTableToDateFieldName);
		int countBanned = jdbcTemplate.update(query, new Object[] {id, dbDateFormater.format(from), dbDateFormater.format(to)});
		return countBanned;
	}

	@Override
	public List<Ban> getUserBans(String login, int from, int to) throws DataAccessException{
		String query = MessageFormat.format(
				"SELECT {0}, {1}, {2}, {3}, {4} FROM {5} WHERE {6} = (SELECT {7} FROM {8} WHERE {9} = ?) ORDER BY {1} DESC, {0} DESC LIMIT ?, ?",
				usersBannedTableIDFieldName,
				usersBannedTableFromDateFieldName,
				usersBannedTableToDateFieldName,
				usersBannedTableRemovedFieldName,
				usersBannedTableRemovedDateFieldName,
				usersBannedTableName,
				usersBannedTableUserIDFieldName,
				usersTableIDFieldName,
				usersTableName,
				usersTableLoginFieldName);
		return jdbcTemplate.query(query, new Object[] {login, from, to} , new BanMapperForCurrentUser());
	}
	
	@Override
	public List<Ban> getIPAddressBans(int id, int from, int to) throws DataAccessException{
		String query = MessageFormat.format(
				"SELECT {0}, {1}, {2}, {3}, {4} FROM {5} WHERE {6} = ? ORDER BY {1} DESC, {0} DESC LIMIT ?, ?",
				userIPAddressesBannedTableIdFieldName,
				userIPAddressesBannedTableFromDateFieldName,
				userIPAddressesBannedTableToDateFieldName,
				userIPAddressesBannedTableRemovedFieldName,
				userIPAddressesBannedTableRemovedDateFieldName,
				userIPAddressesBannedTableName,
				userIPAddressesBannedTableIPAdressIDFieldName);
		return jdbcTemplate.query(query, new Object[] {id, from, to} , new BanMapperForCurrentUser());
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public int changeUserBanStateById(boolean isActive, int id) throws DataAccessException {
		String query = MessageFormat.format(
				"UPDATE {0} SET {1} = " + (isActive ? "true" : "false") + ", {2} = " + (isActive ? "curdate()" : "null") + " WHERE {3} = ?",
				usersBannedTableName,
				usersBannedTableRemovedFieldName,
				usersBannedTableRemovedDateFieldName,
				usersBannedTableIDFieldName);
		System.out.println(query + " id:" + id);
		return jdbcTemplate.update(query, new Object[] {id});
	}
	
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public int changeIPAddressBanStateById(boolean isActive, int id) throws DataAccessException {
		String query = MessageFormat.format(
				"UPDATE {0} SET {1} = " + (isActive ? "true" : "false") + ", {2} = " + (isActive ? "curdate()" : "null") + " WHERE {3} = ?",
				userIPAddressesBannedTableName,
				userIPAddressesBannedTableRemovedFieldName,
				userIPAddressesBannedTableRemovedDateFieldName,
				userIPAddressesBannedTableIdFieldName);
		System.out.println(query + " id:" + id);
		return jdbcTemplate.update(query, new Object[] {id});
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public boolean existsUserBanById(int id) throws DataAccessException, IllegalStateException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} = ?",
				usersBannedTableName,
				usersBannedTableIDFieldName);
		int accountsCount = jdbcTemplate.queryForObject(query, new Object[] {id}, Integer.class);
		if(accountsCount == 1) {
			return true;
		}
		if(accountsCount == 0) {
			return false;
		}
		throw new IllegalStateException("DB unique id error"); // don't need to be localized
	}
	
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public boolean existsIPAddressBanById(int id) throws DataAccessException, IllegalStateException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} = ?",
				userIPAddressesBannedTableName,
				userIPAddressesBannedTableIdFieldName);
		int accountsCount = jdbcTemplate.queryForObject(query, new Object[] {id}, Integer.class);
		if(accountsCount == 1) {
			return true;
		}
		if(accountsCount == 0) {
			return false;
		}
		throw new IllegalStateException("DB unique id error"); // don't need to be localized
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public void insertIPAddress(String login, String ipAddress) throws DataAccessException {
		String query = MessageFormat.format(
				"INSERT INTO {0} ({1}, {2}) VALUES (?, ?);", 
				usersIPAddressesTableName, 
				usersIPAddressesTableUserIDFieldName, 
				usersIPAddressesTableAddressFieldName);

		int answerCode = jdbcTemplate.update(query, new Object[] {getUserIDByLogin(login), ipAddress});
	}
	
	private int getUserIDByLogin(String login) {
		String query = MessageFormat.format(
				"SELECT {0} FROM {1} WHERE {2} = ?;", 
				usersTableIDFieldName,
				usersTableName, 
				usersTableLoginFieldName);
		try {
			return jdbcTemplate.queryForObject(query, new Object[] {login}, Integer.class);
		} catch(EmptyResultDataAccessException ex) {
			return -1;
		}
	}

	@Override
	public IPAddress getIPAddressByLogin(String login) throws DataAccessException  {
		String query = MessageFormat.format(
				"SELECT * FROM {0} WHERE {1} = ?",
				usersIPAddressesTableName,
				usersIPAddressesTableUserIDFieldName);
		try {
			return jdbcTemplate.queryForObject(query, new Object[] {getUserIDByLogin(login)}, new IPAddressMapper());
		} catch(EmptyResultDataAccessException ex) {
			return null;
		}
	}

	@Override
	public boolean isBannedUserIPAddressByID(int id) throws DataAccessException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} = ? AND {2} <= curdate() AND {3} >= curdate() AND {4} = false;", 
				userIPAddressesBannedTableName,
				userIPAddressesBannedTableIPAdressIDFieldName,
				userIPAddressesBannedTableFromDateFieldName,
				userIPAddressesBannedTableToDateFieldName,
				userIPAddressesBannedTableRemovedFieldName);
		return 0 < jdbcTemplate.queryForObject(query, new Object[] {id}, Integer.class);
	}
	
	@Override
	public boolean isBannedUserIPAddressByLogin(String login) throws DataAccessException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {3} WHERE {4} = (SELECT {0} FROM {1} WHERE {2} = ?) AND {5} <= curdate() AND {6} >= curdate() AND {7} = false;", 
				usersIPAddressesTableIDFieldName,
				usersIPAddressesTableName,
				usersIPAddressesTableUserIDFieldName,
				userIPAddressesBannedTableName,
				userIPAddressesBannedTableIPAdressIDFieldName,
				userIPAddressesBannedTableFromDateFieldName,
				userIPAddressesBannedTableToDateFieldName,
				userIPAddressesBannedTableRemovedFieldName);
		return 0 < jdbcTemplate.queryForObject(query, new Object[] {getUserIDByLogin(login)}, Integer.class);
	}

	@Override
	public DateInterval getLastUserIPAddressBanDateInterval(String login) throws DataAccessException {
		String query = MessageFormat.format(
				"SELECT {3}, {4} FROM {5} WHERE {4} = (SELECT MAX({4}) FROM {5} WHERE {6} = (SELECT {0} FROM {1} WHERE {2} = ?)) AND {3} <= curdate()",
				usersIPAddressesTableIDFieldName,
				usersIPAddressesTableName,
				usersIPAddressesTableUserIDFieldName,
				userIPAddressesBannedTableFromDateFieldName,
				userIPAddressesBannedTableToDateFieldName,
				userIPAddressesBannedTableName,
				userIPAddressesBannedTableIPAdressIDFieldName);
		System.out.println(query);
		List<DateInterval> lastDateIntervals = jdbcTemplate.query(query, new Object[] {getUserIDByLogin(login)}, new UsersBannedTableDateIntervalMapper());
		if(lastDateIntervals.isEmpty()) {
			return null;
		}
		return CommonUtils.getDateIntervalWithMinFromDate(lastDateIntervals);
	}

	@Override
	public int getUserBansCountBefore(int banId) throws DataAccessException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} > ?",
				usersBannedTableName,
				usersBannedTableIDFieldName);
		return jdbcTemplate.queryForObject(query, new Object[] {banId}, Integer.class);
	}

	@Override
	public int getIPAddressBansCountBefore(int banId) throws DataAccessException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} > ?",
				userIPAddressesBannedTableName,
				userIPAddressesBannedTableIdFieldName);
		return jdbcTemplate.queryForObject(query, new Object[] {banId}, Integer.class);
	}

}
