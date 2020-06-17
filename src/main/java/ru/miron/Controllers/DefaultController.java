package ru.miron.Controllers;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.view.RedirectView;
import org.apache.commons.io.IOUtils;
import ru.miron.Checkers.IFileChecker;
import ru.miron.Checkers.IPostTitleChecker;
import ru.miron.DAOs.IPostDAO;
import ru.miron.DAOs.IUserDAO;
import ru.miron.Entities.Ban;
import ru.miron.Entities.DateInterval;
import ru.miron.Entities.FeedPost;
import ru.miron.Entities.FeedPostWithBanInfo;
import ru.miron.Entities.IPAddress;
import ru.miron.Entities.PostCommentWithBanInfo;
import ru.miron.Enums.LoginStatus;
import ru.miron.Enums.PossibleIMGsSuffixes;
import ru.miron.Utils.WebUtils;
import static ru.miron.Config.DBConstants.*;
import static ru.miron.Config.ConfigConstants.*;

import ru.miron.Config.ConfigConstants;
import ru.miron.Config.DBConstants;

import static ru.miron.Config.WebConstants.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
public class DefaultController {
	
	@Autowired
	WebUtils webUtils;
	@Autowired
	IUserDAO userDAO;
	@Autowired
	IPostDAO postDAO;
	@Autowired
	IFileChecker fileChecker;
	@Autowired
	IPostTitleChecker postTitleChecker;
	@Autowired
	MessageSource messageSource;
	
	@RequestMapping(value="/" + mainURL, method = RequestMethod.GET)
	public Object main(HttpServletRequest request, Locale locale) {
		RedirectView redirectTo;
		if((redirectTo = webUtils.loggedCheck(request)) != null ||
				(redirectTo = webUtils.userBanCheck(request, messageSource, locale)) != null ||
				(redirectTo = webUtils.ipAddressBanCheck(request, messageSource, locale)) != null) {
			return redirectTo;
		}
		request.setAttribute(isAdminAttrName, userDAO.isAdmin(webUtils.getLoginFromCookies(request)));
		return mainPageName;
	}
	
	@RequestMapping(value="/" + createPostURL, method = RequestMethod.GET)
	public Object createPost(HttpServletRequest request, Locale locale) {
		RedirectView redirectTo;
		if((redirectTo = webUtils.loggedCheck(request)) != null || 
				(redirectTo = webUtils.userBanCheck(request, messageSource, locale)) != null ||
				(redirectTo = webUtils.ipAddressBanCheck(request, messageSource, locale)) != null) {
			return redirectTo;
		}
		int countOfPostsToday = postDAO.countOfPostsToday(webUtils.getLoginFromCookies(request));
		if(countOfPostsToday >= MAX_POSTS_COUNT_PER_DAY) { 
			if(request.getSession().getAttribute(createPostPageTooManyPostsPerDayMsgAttrName) == null) { // if didn't redirected from createPost_postForm with this error
				request.getSession().setAttribute(
						createPostPageTooManyPostsPerDayMsgAttrName, 
						messageSource.getMessage(
								"TooManyPostsPerDayMsg", 
								new Object[] {webUtils.getLoginFromCookies(request), MAX_POSTS_COUNT_PER_DAY},
						locale));
			}
			
		}
		return createPostPageName;
	}
	
	@RequestMapping(value="/" + createPostURL, method = RequestMethod.POST)
	public Object createPost_postForm(
			HttpServletRequest request,
			@RequestParam(createPostPageTitleParamName) String title,
			@RequestParam(createPostPageFileParamName) CommonsMultipartFile file, 
			Locale locale) throws IOException {
		HttpSession session = request.getSession();
		RedirectView redirectTo;
		if((redirectTo = webUtils.loggedCheck(request)) != null ||
				(redirectTo = webUtils.userBanCheck(request, messageSource, locale)) != null ||
				(redirectTo = webUtils.ipAddressBanCheck(request, messageSource, locale)) != null) {
			return redirectTo;
		}
		List<String> errors;
		boolean hasErrors = false;
		if(!(errors = fileChecker.getErrorsBeforeGetting(file, locale)).isEmpty()) {
			hasErrors = true;
			errors.forEach(System.out::println);
			session.setAttribute(createPostPageFileBeforeInsertErrorsAttrName, errors);
		}
		if(!(errors = postTitleChecker.getErrors(title, locale)).isEmpty()) {
			hasErrors = true;
			errors.forEach(System.out::println);
			session.setAttribute(createPostPageTitleErrorsAttrName, errors);
		}
		int countOfPostsToday = postDAO.countOfPostsToday(webUtils.getLoginFromCookies(request));
		if(countOfPostsToday >= MAX_POSTS_COUNT_PER_DAY) {
			hasErrors = true;
			session.setAttribute(
					createPostPageTooManyPostsPerDayMsgAttrName, 
					messageSource.getMessage(
							"TooManyPostsPerDayMsg", 
							new Object[] {webUtils.getLoginFromCookies(request), MAX_POSTS_COUNT_PER_DAY},
					locale));
		}
		if(hasErrors) {
			return new RedirectView("/" + createPostURL, true);
		}
		String insertedFileName = postDAO.insertFile(file, locale);
		if(!(errors = fileChecker.getErrorsAfterGetting(insertedFileName, locale)).isEmpty()) {
			errors.forEach(System.out::println);
			session.setAttribute(createPostPageFileAfterInsertErrorsAttrName, errors);
			return new RedirectView("/" + createPostURL, true);
		}
		System.out.println(insertedFileName);
		postDAO.insertRecord(webUtils.getLoginFromCookies(request), title, insertedFileName);
		return new RedirectView("/" + createPostURL, true);
	}
	
	@RequestMapping(value="/" + feedURL, method = RequestMethod.GET)
	public Object feed(HttpServletRequest request) {
		RedirectView redirectTo;
		if((redirectTo = webUtils.loggedCheck(request)) != null) {
			return redirectTo;
		}
		return feedPageName;
	}
	
	@RequestMapping(value="/" + getFeedURL, method = RequestMethod.GET)
	public Object getFeed(HttpServletRequest request,
			@RequestParam(countFromParamName) int from,
			@RequestParam(countToParamName) int to,
			@RequestParam(postsFirstIDFromFirstSetParamName) Integer idOfFirst) throws IOException {
		if(webUtils.loggedCheck(request) != null) {
			return null; // TODO: write smth in answer?
		}
		boolean isFirstSet = idOfFirst == null;
		int countOfNewBeforeFirst = 0;
		if(!isFirstSet) {
			countOfNewBeforeFirst = postDAO.getCountPostsBefore(idOfFirst);
		}
		List<FeedPostWithBanInfo> feedPostsWithBanInfo = postDAO.getFreshPostsWithBiggestBanInfo(from + countOfNewBeforeFirst, to + countOfNewBeforeFirst);
		feedPostsWithBanInfo.forEach(System.out::println);
		request.setAttribute(feedPostsWithBanInfoAttrName, feedPostsWithBanInfo);
		request.setAttribute(isFirstSetAttrName, isFirstSet);
		String loginFromCookies = webUtils.getLoginFromCookies(request);
		request.setAttribute(getFeedPageCanLeaveCommentsAttrName, canLeaveComments(loginFromCookies));
		request.setAttribute(loginAttrName, loginFromCookies);
		return getFeedPageName;
	}
	
	@RequestMapping(value="/" + createCommentURL, method = RequestMethod.POST)
	@ResponseBody
	public Object createComment(
			HttpServletRequest request,
			@RequestParam(getFeedTextOfCommentParamName) String text,
			@RequestParam(postIDParamName) int postID) {
		String loginFromCookies = webUtils.getLoginFromCookies(request);
		if(webUtils.isLoggedUsingCookies(request) != LoginStatus.LOGGED ||
				userDAO.isBannedUser(loginFromCookies)	||
				userDAO.isBannedUserIPAddressByLogin(loginFromCookies)) {
			return "error - banned or not logged"; // TODO: to return smth?
		}
		
		if(text.length() > MAX_COMMENT_LENGTH || text.length() < MIN_COMMENT_LENGTH) {
			return "error - comment has illegal length - > " + MAX_COMMENT_LENGTH + " or < " + MIN_COMMENT_LENGTH;
		}
		if(!postDAO.existsPost(postID) || postDAO.isPostBanned(postID)) {
			return "error - post doesn't exist or banned";
		}
		if(postDAO.getCommentsCountPerDay(loginFromCookies) > MAX_COMMENTS_COUNT_PER_DAY) {
			return "too many comments per day";
		}
		
		int countCreated = postDAO.createComment(postID, loginFromCookies, text);
		if(countCreated == 0) {
			return "not created";
		}
		return "ok";
	}
	
	@RequestMapping(value="/" + imgsURL, method = RequestMethod.GET)
	public void getImg(@RequestParam(imgNameParamName) String name, HttpServletResponse response, HttpServletRequest request, Locale locale) throws IOException {
		if(webUtils.loggedCheck(request) != null ||
				webUtils.userBanCheck(request, messageSource, locale) != null) {
			return; // TODO: to return smth?
		}
		File file = new File(picturesDirectiory + File.separatorChar + name);
		if(!file.exists()) {
			System.out.println(name);
			response.sendError(404, messageSource.getMessage("ImgNotFoundMsg", new Object[] {name}, locale));
			return;
		}
		
		String postfix = name.split("\\.")[1];
		if(!PossibleIMGsSuffixes.contains(postfix.toLowerCase())) {
			response.sendError(500, messageSource.getMessage("InvalidFileTypeOnServerMsg", new Object[] {name}, locale));
		}
		response.setContentType("image/" + postfix);
		InputStream in = new FileInputStream(file);
		IOUtils.copy(in, response.getOutputStream());
	}
	
	@RequestMapping(value = "/" + getCommentsURL, method = RequestMethod.GET)
	public Object getComments(HttpServletRequest request,
			Locale locale,
			@RequestParam(countFromParamName) int from,
			@RequestParam(countToParamName) int to,
			@RequestParam(commentsFirstIDFromFirstSetParamName) Integer idOfFirstComment,
			@RequestParam(postIDParamName) int postID) {
		String loginFromCookies = webUtils.getLoginFromCookies(request);
		if(webUtils.isLoggedUsingCookies(request) != LoginStatus.LOGGED) {
			request.getSession().setAttribute(failedPageErrorMessageAttrName, messageSource.getMessage("NotLogonMsg", new Object[] {loginFromCookies}, locale));
			return failedPageName;
		}
		
		
		boolean isFirstSet = idOfFirstComment == null;
		
		int countOfNewBeforeFirst = 0;
		if(!isFirstSet) {
			countOfNewBeforeFirst = postDAO.getCommentsCountUnderPostBefore(idOfFirstComment);
		}
		List<PostCommentWithBanInfo> comments = postDAO.getFreshCommentsWithBiggestBanInfo(postID, from + countOfNewBeforeFirst, to + countOfNewBeforeFirst);
		request.setAttribute(getCommentsPageCommentsAttrName, comments);
		request.setAttribute(isFirstSetAttrName, isFirstSet);
		request.setAttribute(postIDAttrName, postID);
		System.out.println(from + " " + to + " " + countOfNewBeforeFirst);
		return getCommentsPageName;
	}
	
	@RequestMapping(value="/" + adminToolsMenuURL, method = RequestMethod.GET)
	public Object adminToolsMenu(HttpServletRequest request, Locale locale) {
		RedirectView redirectTo;
		if((redirectTo = webUtils.loggedCheck(request)) != null ||
				(redirectTo = webUtils.adminCheck(request, messageSource, locale)) != null ||
				(redirectTo = webUtils.userBanCheck(request, messageSource, locale)) != null ||
				(redirectTo = webUtils.ipAddressBanCheck(request, messageSource, locale)) != null) {
			return redirectTo;
		}
		return adminToolsMenuPageName;
	}
	
	@RequestMapping(value = "/" + failedURL)
	public Object failed(HttpServletRequest request) {
		return failedPageName;
	}
	
	boolean canLeaveComments(String login) {
		return !userDAO.isBannedUser(login) && !userDAO.isBannedUserIPAddressByLogin(login);
	}
}
