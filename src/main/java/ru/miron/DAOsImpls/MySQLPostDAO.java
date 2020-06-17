package ru.miron.DAOsImpls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import ru.miron.DAOs.IPostDAO;
import ru.miron.Entities.Ban;
import ru.miron.Entities.FeedPost;
import ru.miron.Entities.FeedPostWithBanInfo;
import ru.miron.Entities.PostCommentWithBanInfo;
import ru.miron.Exceptions.EmptyFileException;
import ru.miron.RowMappers.BanMapperForCurrentUser;
import ru.miron.RowMappers.BanMapperForPost;
import ru.miron.RowMappers.PostCommentWithBanInfoMapper;
import ru.miron.RowMappers.PostMapper;
import ru.miron.RowMappers.PostWithBanInfoMapper;
import ru.miron.Utils.CommonUtils;

import static ru.miron.Config.ConfigConstants.picturesDirectiory;
import static ru.miron.Config.DBConstants.*;
import static ru.miron.Config.ConfigConstants.MAX_FILES_WITH_SAME_NAME_COUNT;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;

@Component
public class MySQLPostDAO implements IPostDAO {

	JdbcTemplate jdbcTemplate;
	@Autowired
	MessageSource messageSource;
	
	@Autowired
	public MySQLPostDAO(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public String insertFile(CommonsMultipartFile file, Locale locale) throws IOException, IllegalStateException, EmptyFileException { // TODO: think about it. It doesn't have any relation to MySQL(!)PostDAO
		if(!file.isEmpty()) {
			File fileToUpload = null;
			String[] splittedByDots = file.getOriginalFilename().split("\\.");
			String fileName = CommonUtils.connectTo(splittedByDots, splittedByDots.length - 1);
			String fileExtension = "." + splittedByDots[splittedByDots.length - 1].toLowerCase(); // SHOULD BE SAVED IN LOWER CASE!
			boolean foundPlace = false;
			for(int i = 0; i < 100; i++) {
				if(!(fileToUpload = new File(picturesDirectiory + File.separatorChar + fileName + Integer.toString(i) + fileExtension)).exists()) {
					foundPlace = true;
					break;
				}
			}
			if(!foundPlace) {
				throw new IllegalStateException(messageSource.getMessage("TooManyFilesWithNameMsg", new Object[] {MAX_FILES_WITH_SAME_NAME_COUNT}, locale));
			}
			
			fileToUpload.createNewFile();
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileToUpload));
			out.write(file.getBytes());
			out.close();
			
			return fileToUpload.getName();
		}
		throw new EmptyFileException(messageSource.getMessage("EmptyFileMsg", new Object[] {file.getOriginalFilename()}, locale));
	}

	@Override
	public void insertRecord(String login, String title, String filePath) throws DataAccessException {
		String query = MessageFormat.format(
				"INSERT INTO {0} ({1}, {2}, {3}, {4}) VALUES (?, (SELECT {5} FROM {6} WHERE {7} = ?), curdate(), ?);", 
				postsTableName, 
				postsTableTitleFieldName, 
				postsTableUserIDFieldName,
				postsTablePublishDateFieldName,
				postsTablePictureNameFieldName,
				usersTableIDFieldName,
				usersTableName,
				usersTableLoginFieldName); // TODO: to remember method with names, not numbers
		jdbcTemplate.update(query.toString(), new Object[] {title, login, filePath});
	}

	@Override
	public List<FeedPost> getFreshPosts(int countGot, int countToGet) throws DataAccessException{
		String query = MessageFormat.format(
				"SELECT {1}, {2}, (SELECT {3} FROM {4} WHERE {5} = {0}.{6}), {7}, {8} FROM {0} ORDER BY {7} DESC, {1} DESC LIMIT ?, ?",
				postsTableName,
				postsTableIDFieldName,
				postsTableTitleFieldName,
				
				usersTableLoginFieldName,
				usersTableName,
				usersTableIDFieldName,
				postsTableUserIDFieldName,
				
				postsTablePublishDateFieldName,
				postsTablePictureNameFieldName);
		try {
			return jdbcTemplate.query(query, new Object[] {countGot, countToGet}, new PostMapper());
		} catch(EmptyResultDataAccessException ex) {
			return new ArrayList<FeedPost>();
		}
	}

	@Override
	public List<FeedPostWithBanInfo> getFreshPostsWithBiggestBanInfo(int countGot, int countToGet) throws DataAccessException {
		String query = MessageFormat.format(
				//TODO: bad getting 
				"SELECT {0}.{3}, {0}.{9}, (SELECT {11} FROM {12} WHERE {13} = {0}.{10}) as {11}, {0}.{1}, {14}, ifnull({2}.{5}, true) as {5}, {2}.{6}, {2}.{7} "
				+ "FROM (SELECT * FROM {0} ORDER BY {1} DESC, {3} DESC LIMIT ?, ?) as {0} "
				+ "LEFT JOIN (SELECT {5}, {6}, {7}, {4} FROM {2} as pb1 WHERE {8} = "
				+ "(SELECT {8} FROM {2} as pb2 WHERE {6} <= curdate() AND {7} >= curdate() AND pb1.{4} = pb2.{4} AND {7} = "
				+ "(SELECT MAX({7}) FROM {2} as pb3 WHERE {6} <= curdate() AND {7} >= curdate() AND pb3.{4} = pb2.{4}) LIMIT 1)) as {2} "
				+ "ON {2}.{4} = {0}.{3} ORDER BY {0}.{1} DESC, {0}.{3} DESC",
				postsTableName,
				postsTablePublishDateFieldName,
				postsBannedTableName,
				postsTableIDFieldName,
				postsBannedTablePostIDFieldName,
				postsBannedTableRemovedFieldName,
				postsBannedTableFromDateFieldName,
				postsBannedTableToDateFieldName,
				postsBannedTableIDFieldName,
				
				postsTableTitleFieldName,
				postsTableUserIDFieldName,
				usersTableLoginFieldName,
				usersTableName,
				usersTableIDFieldName,
				postsTablePictureNameFieldName);
		try {
			return jdbcTemplate.query(query, new Object[] {countGot, countToGet}, new PostWithBanInfoMapper());
		} catch(EmptyResultDataAccessException ex) {
			return new ArrayList<FeedPostWithBanInfo>();
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public int changePostBanState(boolean isActive, int id) throws DataAccessException {
		String query = MessageFormat.format(
				"UPDATE {0} SET {1} = " + (isActive ? "true" : "false") + ", {2} = " + (isActive ? "curdate()" : "null") + " WHERE {3} = ?",
				postsBannedTableName,
				postsBannedTableRemovedFieldName,
				postsBannedTableRemovedDateFieldName,
				postsBannedTableIDFieldName);
		return jdbcTemplate.update(query, new Object[] {id});
	}
	
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public int changeCommentBanState(boolean isActive, int id) throws DataAccessException {
		String query = MessageFormat.format(
				"UPDATE {0} SET {1} = " + (isActive ? "true" : "false") + ", {2} = " + (isActive ? "curdate()" : "null") + " WHERE {3} = ?",
				postCommentsBannedTableName,
				postCommentsBannedTableRemovedFieldName,
				postCommentsBannedTableRemovedDateFieldName,
				postCommentsBannedTableIDFieldName);
		return jdbcTemplate.update(query, new Object[] {id});
	}

	@Override
	public boolean existsPost(int id) throws DataAccessException, IllegalStateException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} = ?", 
				postsTableName, 
				postsTableIDFieldName);
		
		int accountsCount = jdbcTemplate.queryForObject(query, new Object[] {id}, Integer.class);
		if(accountsCount == 1) {
			return true;
		}
		if(accountsCount == 0) {
			return false;
		}
		throw new IllegalStateException("DB unique id error"); // don't need to be localized
	}
	
	@Override
	public boolean existsComment(int id) throws DataAccessException, IllegalStateException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} = ?", 
				postCommentsTableName, 
				postCommentsTableIDFieldName);
		
		int accountsCount = jdbcTemplate.queryForObject(query, new Object[] {id}, Integer.class);
		if(accountsCount == 1) {
			return true;
		}
		if(accountsCount == 0) {
			return false;
		}
		throw new IllegalStateException("DB unique id error"); // don't need to be localized
	}

	@Override
	public List<Ban> getPostBans(int id, int from, int to) throws DataAccessException {
		String query = MessageFormat.format(
				"SELECT {0}, {1}, {2}, {3}, {4} FROM {5} WHERE {6} = ? ORDER BY {1} DESC, {0} DESC LIMIT ?, ?",
				postsBannedTableIDFieldName,
				postsBannedTableFromDateFieldName,
				postsBannedTableToDateFieldName,
				postsBannedTableRemovedFieldName,
				postsBannedTableRemovedDateFieldName,
				postsBannedTableName,
				postsBannedTablePostIDFieldName);
		try {
			return jdbcTemplate.query(query, new Object[] {id, from, to} , new BanMapperForPost());
		} catch(EmptyResultDataAccessException ex) {
			return new ArrayList<Ban>();
		}
	}
	
	@Override
	public List<Ban> getCommentBans(int id, int from, int to) throws DataAccessException {
		String query = MessageFormat.format(
				"SELECT {0}, {1}, {2}, {3}, {4} FROM {5} WHERE {6} = ? ORDER BY {1} DESC, {0} DESC LIMIT ?, ?",
				postCommentsBannedTableIDFieldName,
				postCommentsBannedTableFromDateFieldName,
				postCommentsBannedTableToDateFieldName,
				postCommentsBannedTableRemovedFieldName,
				postCommentsBannedTableRemovedDateFieldName,
				postCommentsBannedTableName,
				postCommentsBannedTableCommentIDFieldName);
		try {
			return jdbcTemplate.query(query, new Object[] {id, from, to} , new BanMapperForPost());
		} catch(EmptyResultDataAccessException ex) {
			return new ArrayList<Ban>();
		}
	}



	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public int banPost(Date from, Date to, int id) throws DataAccessException {
		String query = MessageFormat.format(
				"INSERT INTO {0} ({1}, {2}, {3}) VALUES (?, ?, ?)",
				postsBannedTableName,
				postsBannedTablePostIDFieldName,
				postsBannedTableFromDateFieldName,
				postsBannedTableToDateFieldName);
		int countBanned = jdbcTemplate.update(query, new Object[] {id, dbDateFormater.format(from), dbDateFormater.format(to)});
		return countBanned;
	}
	
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public int banComment(Date from, Date to, int id) throws DataAccessException {
		String query = MessageFormat.format(
				"INSERT INTO {0} ({1}, {2}, {3}) VALUES (?, ?, ?)",
				postCommentsBannedTableName,
				postCommentsBannedTableCommentIDFieldName,
				postCommentsBannedTableFromDateFieldName,
				postCommentsBannedTableToDateFieldName);
		int countBanned = jdbcTemplate.update(query, new Object[] {id, dbDateFormater.format(from), dbDateFormater.format(to)});
		return countBanned;
	}

	@Override
	public boolean hasPostBan(int id) throws DataAccessException{
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} = ?",
				postsBannedTableName,
				postsBannedTableIDFieldName);
		int bansCount = jdbcTemplate.queryForObject(query, new Object[] {id}, Integer.class);
		if(bansCount > 0) {
			return true;
		}
		return false;
	}
	
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public boolean hasCommentBan(int id) throws DataAccessException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} = ?",
				postCommentsBannedTableName,
				postCommentsBannedTableIDFieldName);
		int bansCount = jdbcTemplate.queryForObject(query, new Object[] {id}, Integer.class);
		if(bansCount > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isPostBanned(int id) throws DataAccessException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} = ? AND {2} = false",
				postsBannedTableName,
				postsBannedTablePostIDFieldName,
				postsBannedTableRemovedFieldName);
		return 0 < jdbcTemplate.queryForObject(query, new Object[] {id}, Integer.class) ;
	}

	@Override
	public int countOfPostsToday(String login) throws DataAccessException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} = curdate() AND {2} = (SELECT {3} FROM {4} WHERE {5} = ?)",
				postsTableName,
				postsTablePublishDateFieldName,
				postsTableUserIDFieldName,
				usersTableIDFieldName,
				usersTableName,
				usersTableLoginFieldName);
		return jdbcTemplate.queryForObject(query, new Object[] {login}, Integer.class);
	}

	@Override
	public int getCountPostsBefore(int id) throws DataAccessException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} > ?", // id should indicate order of publish!
				postsTableName,
				postsBannedTableIDFieldName);
		return jdbcTemplate.queryForObject(query, new Object[] {id}, Integer.class);
	}

	@Override
	public int getPostBansCountBefore(int banId) throws DataAccessException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} > ?",
				postsBannedTableName,
				postsBannedTableIDFieldName);
		return jdbcTemplate.queryForObject(query, new Object[] {banId}, Integer.class);
	}

	@Override
	public int getCommentBansCountBefore(int banId) throws DataAccessException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} > ?",
				postCommentsBannedTableName,
				postCommentsBannedTableIDFieldName);
		return jdbcTemplate.queryForObject(query, new Object[] {banId}, Integer.class);
	}

	@Override
	public int getCommentsCountUnderPostBefore(int idOfComment) throws DataAccessException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} = (SELECT {1} FROM {0} WHERE {2} = ?) AND {1} > ?",
				postCommentsTableName,
				postCommentsTablePostIDFieldName,
				postCommentsTableIDFieldName);
		return jdbcTemplate.queryForObject(query, new Object[] {idOfComment, idOfComment}, Integer.class);
	}

	@Override
	public int getPostIDByCommentID(int id) throws DataAccessException {
		String query = MessageFormat.format(
				"SELECT {0} FROM {1} WHERE {2} = ?",
				postCommentsTablePostIDFieldName,
				postCommentsTableName,
				postCommentsTableIDFieldName);
		return jdbcTemplate.queryForObject(query, new Object[] {id}, Integer.class);
	}

	@Override
	public List<PostCommentWithBanInfo> getFreshCommentsWithBiggestBanInfo(int postID, int countGot, int countToGet) throws DataAccessException {
		String query = MessageFormat.format(
				//TODO: bad getting
				"SELECT pc1.{1}, pc1.{2}, (SELECT {8} FROM {6} WHERE {7} = pc1.{3}) as {8}, pc1.{4}, "
				+ "ifnull(cb1.{10}, true) as {10}, cb1.{11}, cb1.{12} "
				+ "FROM (SELECT {1}, {2}, {3}, {4} FROM {0} WHERE {5} = ? ORDER BY {4}, {1} LIMIT ?, ?) as pc1 "
				+ "LEFT JOIN (SELECT {10}, {11}, {12}, {13} FROM {9} as cb1 WHERE {11} <= curdate() AND {12} >= curdate() "
				+ "AND {12} = (SELECT MAX({12}) FROM {9} as cb2 WHERE {11} <= curdate() AND {12} >= curdate() AND cb1.{13} = cb2.{13}) LIMIT 1) as cb1 "
				+ "ON pc1.{1} = cb1.{13} ORDER BY pc1.{4}, pc1.{1}",
				postCommentsTableName, // 0
				postCommentsTableIDFieldName, // 1
				postCommentsTableTextFieldName, // 2
				postCommentsTableUserIDFieldName, // 3
				postCommentsTableDateFieldName, // 4
				postCommentsTablePostIDFieldName, // 5
				
				usersTableName, // 6
				usersTableIDFieldName, // 7
				usersTableLoginFieldName, // 8
				
				postCommentsBannedTableName, // 9
				postCommentsBannedTableRemovedFieldName, // 10
				postCommentsBannedTableFromDateFieldName, // 11
				postCommentsBannedTableToDateFieldName, // 12
				postCommentsBannedTableCommentIDFieldName // 13
				);
		
		try {
			return jdbcTemplate.query(query, new Object[] {postID, countGot, countToGet}, new PostCommentWithBanInfoMapper());
		} catch(EmptyResultDataAccessException ex) {
			return new ArrayList<PostCommentWithBanInfo>();
		}
	}

	@Override
	public int createComment(int postID, String login, String text) throws DataAccessException {
		String query = MessageFormat.format(
				"INSERT INTO {0} ({1}, {2}, {3}, {4}) VALUES (?, (SELECT {5} FROM {6} WHERE {7} = ?), ?, curdate())", 
				postCommentsTableName,
				postCommentsTablePostIDFieldName,
				postCommentsTableUserIDFieldName,
				postCommentsTableTextFieldName,
				postCommentsTableDateFieldName,
				
				usersTableIDFieldName,
				usersTableName,
				usersTableLoginFieldName);
		return jdbcTemplate.update(query, new Object[] {postID, login, text});
	}

	@Override
	public int getCommentsCountPerDay(String login) throws DataAccessException {
		String query = MessageFormat.format(
				"SELECT COUNT(*) FROM {0} WHERE {1} = curdate() AND {2} = (SELECT {3} FROM {4} WHERE {5} = ?)",
				postCommentsTableName,
				postCommentsTableDateFieldName,
				postCommentsTableUserIDFieldName,
				
				usersTableIDFieldName,
				usersTableName,
				usersTableLoginFieldName); 
		return  jdbcTemplate.queryForObject(query, new Object[] {login}, Integer.class);
	}

}
