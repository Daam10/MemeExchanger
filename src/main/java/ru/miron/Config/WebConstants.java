package ru.miron.Config;

import static ru.miron.Config.WebConstants.adminToolsMenuURL;
import static ru.miron.Config.WebConstants.adminToolsMenuUsersBanURL;
import ru.miron.Utils.CommonUtils;

import java.text.SimpleDateFormat;

public class WebConstants {
	public static final SimpleDateFormat htmlDateFormater = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final String servletPrefix = "/MemeExchanger/";
	
	// Cookies
	public static final String loginCookieName = "login";
	public static final String passwordCookieName = "password";
	public static final int secondsToSaveCookie = CommonUtils.getSecondsFromDays(31);
	
	// URIs
	public static final String rootURL = "";
	
	public static final String loginURL = "login";
	public static final String registrationURL = "registration";
	
	public static final String mainURL = "main";
	
	public static final String feedURL = "feed";
	public static final String createCommentURL = "createComment"; 
	public static final String getFeedURL = "getFeed";
	public static final String getCommentsURL = "getComments";
	public static final String imgsURL = "imgs";

	
	public static final String createPostURL = "createPost";
	
	public static final String logoutURL = "logout";
	
	public static final String adminToolsMenuURL = "adminToolsMenu";
	
	public static final String adminToolsMenuUsersBanURL = "usersBanManagement";
	public static final String adminToolsMenuPostsBanURL = "postsBanManagement";
	public static final String adminToolsMenuIPAddressesBanURL = "IPAddressesBanManagement";
	public static final String adminToolsMenuPostCommentsBanURL = "postCommentsBanManagement";
	
	public static final String banURL = "ban";
	public static final String getBansURL = "getBans";
	public static final String changeBanStateURL = adminToolsMenuURL + "/" + "changeBanState";
	public static final String getUsersBanURL = "getUserBans";
	
	public static final String getIPAddressByUserURL = "getIPAddressByUser";
	
	public static final String failedURL = "failed";
}
