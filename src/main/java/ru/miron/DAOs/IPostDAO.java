package ru.miron.DAOs;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import ru.miron.Entities.*;
import ru.miron.Exceptions.EmptyFileException;

public interface IPostDAO {
	String insertFile(CommonsMultipartFile file, Locale locale) throws IOException, IllegalStateException, EmptyFileException ;
	void insertRecord(String login, String title, String filePath) throws DataAccessException;
	List<FeedPost> getFreshPosts(int countGot, int countToGet) throws DataAccessException;
	List<FeedPostWithBanInfo> getFreshPostsWithBiggestBanInfo(int countGot, int countToGet) throws DataAccessException;
	@Transactional(propagation = Propagation.SUPPORTS)
	int changePostBanState(boolean isActive, int id) throws DataAccessException;
	@Transactional(propagation = Propagation.SUPPORTS)
	int changeCommentBanState(boolean isActive, int id) throws DataAccessException;
	boolean existsPost(int id) throws DataAccessException;
	boolean existsComment(int id) throws DataAccessException;
	List<Ban> getPostBans(int id, int from, int to) throws DataAccessException;
	List<Ban> getCommentBans(int id, int from, int to) throws DataAccessException;
	@Transactional(propagation = Propagation.SUPPORTS)
	int banPost(Date now, Date toForeverDate, int id) throws DataAccessException;
	@Transactional(propagation = Propagation.SUPPORTS)
	int banComment(Date now, Date toForeverDate, int id) throws DataAccessException;
	boolean hasPostBan(int id) throws DataAccessException;
	boolean hasCommentBan(int id) throws DataAccessException;
	boolean isPostBanned(int id) throws DataAccessException;
	int countOfPostsToday(String login) throws DataAccessException;
	int getCountPostsBefore(int idOfFirst) throws DataAccessException;
	int getPostBansCountBefore(int id) throws DataAccessException;
	int getCommentBansCountBefore(int id) throws DataAccessException;
	int getCommentsCountUnderPostBefore(int idOfComment) throws DataAccessException;
	int getPostIDByCommentID(int idOfFirstComment) throws DataAccessException;
	List<PostCommentWithBanInfo> getFreshCommentsWithBiggestBanInfo(int postID, int from, int to) throws DataAccessException;
	int createComment(int postID, String login, String text) throws DataAccessException;
	int getCommentsCountPerDay(String login) throws DataAccessException;
}
