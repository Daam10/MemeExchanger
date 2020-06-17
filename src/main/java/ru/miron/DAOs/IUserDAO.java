package ru.miron.DAOs;

import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.miron.Entities.Ban;
import ru.miron.Entities.DateInterval;
import ru.miron.Entities.IPAddress;
import ru.miron.Entities.LoginAndPassword;

public interface IUserDAO{
	boolean existsUserByLogin(String login) throws DataAccessException, IllegalStateException;
	boolean existsUserById(int id) throws DataAccessException, IllegalStateException;
	boolean existsIPAddressById(int id) throws DataAccessException, IllegalStateException;
	boolean isWrong(String login, String password) throws DataAccessException, IllegalStateException;
	@Transactional(propagation = Propagation.SUPPORTS)
	void insertUser(String login, String password) throws DataAccessException ;
	@Transactional(propagation = Propagation.SUPPORTS)
	void insertIPAddress(String login, String ipAddress) throws DataAccessException ;
	int getCountOfAccountsOfAddr(String remoteAddr) throws DataAccessException ;
	boolean isBannedUser(String login) throws DataAccessException ;
	boolean isBannedUserIPAddressByID(int id) throws DataAccessException ;
	boolean isBannedUserIPAddressByLogin(String login) throws DataAccessException ;
	DateInterval getLastUserBanDateInterval(String login) throws DataAccessException ;
	DateInterval getLastUserIPAddressBanDateInterval(String login) throws DataAccessException ;
	boolean isAdmin(String login) throws DataAccessException;
	List<Ban> getUserBans(String login, int from, int to) throws DataAccessException;
	List<Ban> getIPAddressBans(int id, int from, int to) throws DataAccessException;
	@Transactional(propagation = Propagation.SUPPORTS)
	int changeUserBanStateById(boolean isActive, int id) throws DataAccessException;
	@Transactional(propagation = Propagation.SUPPORTS)
	int changeIPAddressBanStateById(boolean isActive, int id) throws DataAccessException;
	@Transactional(propagation = Propagation.SUPPORTS)
	int banUser(Date now, Date toForeverDate, String login) throws DataAccessException;
	@Transactional(propagation = Propagation.SUPPORTS)
	int banIPAddress(Date now, Date toForeverDate, int id) throws DataAccessException;
	boolean existsUserBanById(int id) throws DataAccessException, IllegalStateException;
	boolean existsIPAddressBanById(int id) throws DataAccessException, IllegalStateException;
	IPAddress getIPAddressByLogin(String login) throws DataAccessException ;
	int getUserBansCountBefore(int id) throws DataAccessException;
	int getIPAddressBansCountBefore(int id) throws DataAccessException;
}
