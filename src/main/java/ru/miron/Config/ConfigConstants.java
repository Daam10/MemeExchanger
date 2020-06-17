package ru.miron.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigConstants {
	// business constants
	// spam security
	public static final int MAX_ACCOUNT_COUNT_PER_ADDR = 2;
	public static final int MAX_POSTS_COUNT_PER_DAY = 3;
	public static final int MAX_COMMENTS_COUNT_PER_DAY = 5;
	
	public static final int MAX_LOGIN_LENGTH = 30;
	public static final int MIN_LOGIN_LENGTH = 5;
	public static final int MAX_PASSWORD_LENGTH = 20;
	public static final int MIN_PASSWORD_LENGTH = 8;
	
	public static final int COMMENT_COLUMNS_COUNT = 50;
	
	// file managing
	public static final int MAX_INPUT_FILENAME_LENGTH = 45;
	public static final int MAX_FINAL_FILENAME_LENGTH = 50;
	public static final int MAX_FILES_WITH_SAME_NAME_COUNT = 100;
	public static final String picturesDirectiory = 
					"D:" + 
					File.separatorChar + 
					"serverPicturesDirectory";
	
	
	// page names
	public static final String rootPageName = "root";
	
	public static final String loginPageName = "login";
	//params
	public static final String loginPageLoginParamName = "login";
	public static final String loginPagePasswordParamName = "password";
	//attributes
	public static final String loginPageWrongLoginOrPasswordMsgAttrName = "wrong_login_or_password_error";
	


	public static final String registrationPageName = "registration";
	//params
	public static final String registrationPageLoginParamName = "login";
	//attributes
	public static final String registrationPagePasswordParamName = "password";
	public static final String registrationPageLoginAlreadyExistsMsgAttrName = "LoginAlreadyExistsMsg";
	public static final String registrationPagePasswordErrorsAttrName = "password_errors";
	public static final String registrationPageLoginErrorsAttrName = "login_errors";
	public static final String registrationPageTooManyAccountsMsgAttrName = "tooManyAccountsMsg";
	
	public static final String mainPageName = "main";
	
	public static final String createPostPageName = "createPost";
	//params
	public static final String createPostPageTitleParamName = "title";
	public static final String createPostPageFileParamName = "file";
	//attributes
	public static final String createPostPageFileBeforeInsertErrorsAttrName = "file_before_insert_errors";
	public static final String createPostPageTitleErrorsAttrName = "title_errors";
	public static final String createPostPageFileAfterInsertErrorsAttrName = "file_after_insert_errors";
	public static final String createPostPageTooManyPostsPerDayMsgAttrName = "tooManyPostsPerDayMsg";
	
	public static final String getFeedPageName = "getFeed";
	//params
	public static final String getFeedTextOfCommentParamName = "getFeedTextOfComment";
	//attributes
	public static final String feedPostsAttrName = "feedPosts";
	public static final String feedPostsWithBanInfoAttrName = "feedPostsWithBanInfo";
	public static final String getFeedPageCanLeaveCommentsAttrName = "getFeedPageCanLeaveComments";
	
	public static final String bansFirstIDFromFirstSetParamName = "bansFirstFromFirstSet";
	public static final String postsFirstIDFromFirstSetParamName = "postsFirstFromFirstSet";
	public static final String commentsFirstIDFromFirstSetParamName = "commentsFirstFromFirstSet";
	
	public static final String feedPageName = "feed";

	public static final String fatalErrorPageName = "fatalError";
	
	public static final String failedPageName = "failed";
	public static final String failedPageErrorMessagesAttrName = "errorMessages";
	public static final String failedPageErrorMessageAttrName = "errorMessage";
	
	public static final String adminToolsMenuPageName = "adminToolsMenu";
	public static final String adminToolsMenuPageIllegalEntityTypeMsgAttrName = "illegalEntityTypeMsg";
	public static final String adminToolsMenuPageIllegalIdentificatorTypeMsgAttrName = "illegalIdentificatorTypeMsg";
	
	public static final String entitiesBanManagementPageName = "adminToolsMenuEntitiesBanManagement";
	//params
	public static final String entitiesBanManagementPageToDateParam = "toDate";
	public static final String entitiesBanManagementPageForeverCheckboxParam = "forever";
	public static final String entitiesBanManagementPageTypeOfEntityParam = "typeOfEntity";
	//attributes
	public static final String entitiesBanManagementPageTitleAttrName = "entityPageTitle";
	public static final String entitiesBanManagementPageWrongToDateErrorMsgAttrName = "wrongToDateErrorMsg";
	public static final String entitiesBanManagementPageEntityDoesntExistMsgAttrName = "entityDoesntExistMsg";
	public static final String entitiesBanManagementPageMaybeDidntSetDateMsgAttrName = "maybeDidntSetDateMsg";
	public static final String entitiesBanManagementPageFromOrToDateParseExceptionMsgAttrName = "FromOrToDateParseExceptionMsg";
	public static final String entitiesBanManagementPageIdentificatorNameAttrName = "identificatorName";
	public static final String entitiesBanManagementPageIllegalIdentificatorMsgAttrName = "illegalIdentificatorMsg";
	
	public static final String getBansPage = "getBans";
	//params
	public static final String getBansPageDoActiveParamName = "doActive";
	public static final String getBansPageDoUnactiveParamName = "doUnactive";
	public static final String getBansPageFromDateParamName = "fromDate";
	public static final String getBansPageToDateParamName = "toDate";
	//attributes
	public static final String getBansPageBansAttrName = "bans";
	public static final String getBansPageWhenSwitchedURLAttrName = "whenSwitchedURL";
	
	public static final String getCommentsPageName = "getComments";
	public static final String getCommentsPageCommentsAttrName = "getCommentsPageComments";
	
	public static final String getIPAddressByUserPageUserParam = "user";
	
	public static final String getIPAddressPageName = "getIPAddressInfo";
	public static final String getIPAddressPageIPAddressAttrName = "ipAddress";
	
	
	
	//meta
	
	//meta attributes
	public static final String messageSourceAttrName = "messageSource";
	public static final String localeAttrName = "locale";
	public static final String isAdminAttrName = "isAdmin";
	public static final String isFirstSetAttrName = "isFirstSet";
	public static final String loginAttrName = "login";
	public static final String postIDAttrName = "postID";
	
	//meta params
	public static final String identificatorParamName = "identificator"; 
	
	public static final String typeOfIdentificatorParamName = "typeOfIdentificator";
	
	public static final String postIDParamName = "postID";
	public static final String loginParamName = "login";
	public static final String countFromParamName = "countFrom";
	public static final String countToParamName = "countTo";
	public static final String isFirstSetParamName = "isFirstSet";
	public static final String imgNameParamName = "name";
	
}
