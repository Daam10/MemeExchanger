package ru.miron.Config;

import static ru.miron.Config.DBConstants.postsBannedTableFromDateFieldName;

import java.text.SimpleDateFormat;

public class DBConstants {
	// public final static String  = ""; -- template
	public static final SimpleDateFormat dbDateFormater = new SimpleDateFormat("yyyy-MM-dd");
	public static final int MAX_COMMENT_LENGTH = 500;
	public static final int MIN_COMMENT_LENGTH = 10;
	public static final int MAX_POST_TITLE_LENGTH = 100;
	
	// users table
	public final static String usersTableName = "users";
	public final static String usersTableIDFieldName = "id";
	public final static String usersTableLoginFieldName = "name";
	public final static String usersTablePasswordFieldName = "password";
	public final static String usersTableCreationDataFieldName = "creation_date";
	public final static String usersTableAccessLevelIDFieldName = "access_level_id";
	//access_levels
	public final static String accessLevelsTableName = "access_levels";
	public final static String accessLevelsTableIDFieldName = "id";
	public final static String accessLevelsTableNameFieldName = "name";
	public final static String defaultUserAccessLevelID = "1";
	public final static String adminAccessLevelID = "2";
	// users_banned table
	public final static String usersBannedTableName = "users_banned";
	public final static String usersBannedTableIDFieldName = "id";
	public final static String usersBannedTableUserIDFieldName = "user_id";
	public final static String usersBannedTableFromDateFieldName = "_from";
	public final static String usersBannedTableToDateFieldName = "_to";
	public final static String usersBannedTableRemovedFieldName = "removed";
	public final static String usersBannedTableRemovedDateFieldName = "removed_date";
	// user_ip_addresses table
	public final static String usersIPAddressesTableName = "user_ip_adresses";
	public final static String usersIPAddressesTableIDFieldName = "id";
	public final static String usersIPAddressesTableAddressFieldName = "name";
	public final static String usersIPAddressesTableUserIDFieldName = "user_id";
	// user_id_adresses_banned table
	public final static String userIPAddressesBannedTableName = "user_id_adresses_banned";
	public final static String userIPAddressesBannedTableIdFieldName = "id";
	public final static String userIPAddressesBannedTableIPAdressIDFieldName = "ip_adress_id";
	public final static String userIPAddressesBannedTableFromDateFieldName = "_from";
	public final static String userIPAddressesBannedTableToDateFieldName = "_to";
	public final static String userIPAddressesBannedTableRemovedFieldName = "removed";
	public final static String userIPAddressesBannedTableRemovedDateFieldName = "removed_date";
	// posts table
	public final static String postsTableName = "posts";
	public final static String postsTableIDFieldName = "id";
	public final static String postsTableTitleFieldName = "title";
	public final static String postsTableUserIDFieldName = "user_id";
	public final static String postsTablePublishDateFieldName = "publish_date";
	public final static String postsTablePictureNameFieldName = "picture_name";
	// posts_banned table
	public final static String postsBannedTableName = "posts_banned";
	public final static String postsBannedTableIDFieldName = "id";
	public final static String postsBannedTablePostIDFieldName = "post_id";
	public final static String postsBannedTableFromDateFieldName = "_from";
	public final static String postsBannedTableToDateFieldName = "_to";
	public final static String postsBannedTableRemovedFieldName = "removed";
	public final static String postsBannedTableRemovedDateFieldName = "removed_date";
	// post_likes table
	public final static String postLikesTableName = "post_likes";
	public final static String postLikesTableIDFieldName = "id";
	public final static String postLikesTablePostIDFieldName = "post_id";
	public final static String postLikesTableUserIDFieldName = "user_id";
	public final static String postLikesTableDateFieldName = "date";
	// post_comments table
	public final static String postCommentsTableName = "post_comments";
	public final static String postCommentsTableIDFieldName = "id";
	public final static String postCommentsTablePostIDFieldName = "post_id";
	public final static String postCommentsTableUserIDFieldName = "user_id";
	public final static String postCommentsTableAnswerToUserIDFieldName = "answer_to";
	public final static String postCommentsTableTextFieldName = "text";
	public final static String postCommentsTableDateFieldName = "date";
	// post_comments_banned
	public final static String postCommentsBannedTableName = "post_comments_banned";
	public final static String postCommentsBannedTableIDFieldName = "id";
	public final static String postCommentsBannedTableCommentIDFieldName = "comment_id";
	public final static String postCommentsBannedTableFromDateFieldName = "_from";
	public final static String postCommentsBannedTableToDateFieldName = "_to";
	public final static String postCommentsBannedTableRemovedFieldName = "removed";
	public final static String postCommentsBannedTableRemovedDateFieldName = "removed_date";
}
