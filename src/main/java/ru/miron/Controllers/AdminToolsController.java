package ru.miron.Controllers;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.view.RedirectView;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.miron.DAOs.*;
import ru.miron.Entities.Ban;
import ru.miron.Entities.DateInterval;
import ru.miron.Entities.IPAddress;
import ru.miron.Enums.*;
import ru.miron.Utils.*;
import static ru.miron.Config.WebConstants.*;
import static ru.miron.Config.ConfigConstants.*;
import static ru.miron.Config.DBConstants.dbDateFormater;
//import static ru.miron.Config.DBConstants.*;

@Controller
public class AdminToolsController {
	
	@SuppressWarnings("deprecation")
	Date toForeverDate = new Date(3000, 1, 1); 
	
	@Autowired
	WebUtils webUtils;
	@Autowired
	IUserDAO userDAO;
	@Autowired
	IPostDAO postDAO;
	@Autowired
	MessageSource messageSource;
	
	@RequestMapping(value = {
			"/" + adminToolsMenuURL + "/" + adminToolsMenuUsersBanURL, 
			"/" + adminToolsMenuURL + "/" + adminToolsMenuPostsBanURL,
			"/" + adminToolsMenuURL + "/" + adminToolsMenuPostCommentsBanURL,
			"/" + adminToolsMenuURL + "/" + adminToolsMenuIPAddressesBanURL}, 
			method = RequestMethod.GET)
	public Object entitiesBanManagement(
			HttpServletRequest request,
			Locale locale) {
		RedirectView redirectTo;
		if((redirectTo = webUtils.loggedCheck(request)) != null	||
		    (redirectTo = webUtils.adminCheck(request, messageSource, locale)) != null ||
		    (redirectTo = webUtils.userBanCheck(request, messageSource, locale)) != null ||
		    (redirectTo = webUtils.ipAddressBanCheck(request, messageSource, locale)) != null) {
			return redirectTo;
		}		
		
		
		if(request.getServletPath().endsWith(adminToolsMenuUsersBanURL)) {
			request.setAttribute(entitiesBanManagementPageTypeOfEntityParam, TypesOfEntity.USERS);
			request.setAttribute(typeOfIdentificatorParamName, TypesOfIdentificator.LOGIN);
			request.setAttribute(entitiesBanManagementPageTitleAttrName, messageSource.getMessage("UsersBanManagement", null, locale));
			request.setAttribute(entitiesBanManagementPageIdentificatorNameAttrName, messageSource.getMessage("Login", null, locale));
		} else if(request.getServletPath().endsWith(adminToolsMenuPostsBanURL)) {
			request.setAttribute(entitiesBanManagementPageTypeOfEntityParam, TypesOfEntity.POSTS);
			request.setAttribute(typeOfIdentificatorParamName, TypesOfIdentificator.ID);
			request.setAttribute(entitiesBanManagementPageTitleAttrName, messageSource.getMessage("PostsBanManagement", null, locale));
			request.setAttribute(entitiesBanManagementPageIdentificatorNameAttrName, messageSource.getMessage("PostID", null, locale));
		} else if(request.getServletPath().endsWith(adminToolsMenuPostCommentsBanURL)) {
			request.setAttribute(entitiesBanManagementPageTypeOfEntityParam, TypesOfEntity.POST_COMMENTS);
			request.setAttribute(typeOfIdentificatorParamName, TypesOfIdentificator.ID);
			request.setAttribute(entitiesBanManagementPageTitleAttrName, messageSource.getMessage("PostCommentsBanManagement", null, locale));
			request.setAttribute(entitiesBanManagementPageIdentificatorNameAttrName, messageSource.getMessage("CommentID", null, locale));
		} else if(request.getServletPath().endsWith(adminToolsMenuIPAddressesBanURL)){
			request.setAttribute(entitiesBanManagementPageTypeOfEntityParam, TypesOfEntity.IP_ADDRESSES);
			request.setAttribute(typeOfIdentificatorParamName, TypesOfIdentificator.ID);
			request.setAttribute(entitiesBanManagementPageTitleAttrName, messageSource.getMessage("IPAddressesBanManagement", null, locale));
			request.setAttribute(entitiesBanManagementPageIdentificatorNameAttrName, messageSource.getMessage("IPAddrID", null, locale)); // TODO: show user ip addresses before
		} else {
			throw new IllegalStateException("Wrong URL");
		}
		return entitiesBanManagementPageName;
	}
	
	@RequestMapping(value = "/" + adminToolsMenuURL + "/" + banURL, method = RequestMethod.POST)
	public Object ban(
			HttpServletRequest request,
			Locale locale,
			@RequestParam(identificatorParamName) String identificator,
			@RequestParam(typeOfIdentificatorParamName) String typeOfIdentificator,
			@RequestParam(entitiesBanManagementPageToDateParam) String toDateString,
			@RequestParam(value = entitiesBanManagementPageForeverCheckboxParam, required = false) String foreverCheckbox,
			@RequestParam(entitiesBanManagementPageTypeOfEntityParam) String typeOfEntity) throws ParseException {
		RedirectView redirectTo;
		if((redirectTo = webUtils.loggedCheck(request)) != null	||
				(redirectTo = webUtils.adminCheck(request, messageSource, locale)) != null ||
				(redirectTo = webUtils.userBanCheck(request, messageSource, locale)) != null ||
				(redirectTo = webUtils.ipAddressBanCheck(request, messageSource, locale)) != null) {
			return redirectTo;
		}
		
		String banMenuURL = null;
		IBanner banner = null;
		IExistanceChecker existanceChecker = null;
		TypesOfEntity enumTypeOfEnity = null;
		TypesOfIdentificator enumTypeOfIdentificator = null;
		if(TypesOfEntity.contains(typeOfEntity)) {
			enumTypeOfEnity = TypesOfEntity.getByString(typeOfEntity);
			banner = chooseBanner(enumTypeOfEnity);
			banMenuURL = chooseBanMenuURL(enumTypeOfEnity);
		} else {
			return illegalEntityTypeRedirect(typeOfEntity, request, locale);
		}
		if(TypesOfIdentificator.contains(typeOfIdentificator)) {
			enumTypeOfIdentificator = TypesOfIdentificator.getByString(typeOfIdentificator);
			existanceChecker = chooseEntityExistanceChecker(enumTypeOfEnity, enumTypeOfIdentificator);
		} else {
			return illegalIdentificatorTypeRedirect(typeOfIdentificator, request, locale);
		}
		try {
			if(!existanceChecker.exists(identificator)) {
				return entityDoesntExistRedirect(banMenuURL, identificator, request, locale);
			}
			
			Date now = new Date();
			boolean forever = webUtils.getHTMLCheckboxValue(foreverCheckbox);
			if(forever) {
				banner.ban(now, toForeverDate, identificator);
			} else {
				try {
					Date toDate = htmlDateFormater.parse(toDateString);
					if(now.after(toDate)) {
						return wrongToDateRedirect(banMenuURL, now, toDate, request, locale); // TODO: not generic message?
					}
					banner.ban(now, toDate, identificator);
				} catch(ParseException ex) {
					return maybeDidntSetDateRedirect(banMenuURL, identificator, request, locale);
				}
			}
			return new RedirectView("/" + adminToolsMenuURL + "/" + banMenuURL, true);
		} catch(IllegalArgumentException ex) {
			return illegalIdentificatorRedirect(banMenuURL, identificator, request, locale);
		}
		
		
	}

	@RequestMapping(value =  "/" + adminToolsMenuURL + "/" + getBansURL, method = RequestMethod.GET)
	public Object getBansUsingJSGet(
			HttpServletRequest request,
			Locale locale,
			@RequestParam(countFromParamName) int from,
			@RequestParam(countToParamName) int to,
			@RequestParam(bansFirstIDFromFirstSetParamName) Integer idOfFirstBan,
			@RequestParam(identificatorParamName) String identificator,
			@RequestParam(typeOfIdentificatorParamName) String typeOfIdentificator,
			@RequestParam(entitiesBanManagementPageTypeOfEntityParam) String typeOfEntity) {
		String loginFromCookies = webUtils.getLoginFromCookies(request);
		if(webUtils.isLoggedUsingCookies(request) != LoginStatus.LOGGED) {
			request.getSession().setAttribute(failedPageErrorMessageAttrName, messageSource.getMessage("NotLogonMsg", new Object[] {loginFromCookies}, locale));
			return failedPageName;
		}
		if(!userDAO.isAdmin(loginFromCookies)) {
			request.getSession().setAttribute(failedPageErrorMessageAttrName, messageSource.getMessage("YouAreNotAdmin", null, locale));
			return failedPageName;
		}
		if(userDAO.isBannedUser(loginFromCookies)) {
			DateInterval banDateInterval = userDAO.getLastUserBanDateInterval(loginFromCookies);
			Date fromDate = null, toDate = null;
			if(banDateInterval != null) {
				fromDate = banDateInterval.getFromDate();
				toDate = banDateInterval.getToDate();
			}
			request.getSession().setAttribute(failedPageErrorMessageAttrName, messageSource.getMessage("UserWithLoginBannedMsg", new Object[] {loginFromCookies, fromDate, toDate},  locale));
			return failedPageName;
		}
		if(userDAO.isBannedUserIPAddressByLogin(webUtils.getLoginFromCookies(request))) {
			DateInterval banDateInterval = userDAO.getLastUserIPAddressBanDateInterval(loginFromCookies);
			IPAddress ipAddress = userDAO.getIPAddressByLogin(loginFromCookies);
			Date fromDate = null, toDate = null;
			if(banDateInterval != null) {
				fromDate = banDateInterval.getFromDate();
				toDate = banDateInterval.getToDate();
			}
			request.getSession().setAttribute(failedPageErrorMessageAttrName, messageSource.getMessage("IPAddressBannedMsg", new Object[] {ipAddress == null ? null : ipAddress.getName(), fromDate, toDate},  locale));
			return failedPageName;
		}
		
		
		IBansGetter bansGetter = null;
		IExistanceChecker banExistanceChecker = null;
		TypesOfEntity enumTypeOfEnity = null;
		TypesOfIdentificator enumTypeOfIdentificator = null;
		if(TypesOfEntity.contains(typeOfEntity)) {
			enumTypeOfEnity = TypesOfEntity.getByString(typeOfEntity);
			bansGetter = chooseBansGetter(enumTypeOfEnity);
		} else {
			return illegalEntityTypeRedirect(typeOfEntity, request, locale);
		}
		if(TypesOfIdentificator.contains(typeOfIdentificator)) {
			enumTypeOfIdentificator = TypesOfIdentificator.getByString(typeOfIdentificator);
			banExistanceChecker = chooseEntityExistanceChecker(enumTypeOfEnity, enumTypeOfIdentificator);
		} else {
			return illegalIdentificatorTypeRedirect(typeOfIdentificator, request, locale);
		}
		
		try {
			if(!banExistanceChecker.exists(identificator)) {
				request.getSession().setAttribute(
						failedPageErrorMessageAttrName, 
						messageSource.getMessage("EntityDoesntExistsMsg", new Object[] {identificator}, locale)); 
				return failedPageName;
			}
			
			boolean isFirstSet = idOfFirstBan == null;
			int countOfNewBeforeFirst = 0;
			if(!isFirstSet) {
				countOfNewBeforeFirst = bansGetter.getBansCountBefore(idOfFirstBan);
			}
			System.out.println(identificator + " " + from + " " + to + " " + countOfNewBeforeFirst);
			List<Ban> bans = bansGetter.getBans(identificator, from + countOfNewBeforeFirst, to + countOfNewBeforeFirst);
			request.setAttribute(getBansPageBansAttrName, bans);
			request.setAttribute(getBansPageWhenSwitchedURLAttrName, changeBanStateURL);
			request.setAttribute(entitiesBanManagementPageTypeOfEntityParam, enumTypeOfEnity);
			request.setAttribute(isFirstSetAttrName, isFirstSet);
			return getBansPage;
		} catch(IllegalArgumentException ex) {
			request.getSession().setAttribute(
					failedPageErrorMessageAttrName,
					messageSource.getMessage(
							"IllegalIdentificatorMsg", 
							new Object[] {identificator}, 
							locale));
			return failedPageName;
		}
	}

	@RequestMapping(value = "/" + changeBanStateURL, method=RequestMethod.POST)
	public Object changeBanState(
			HttpServletRequest request,
			Locale locale,
			@RequestParam(value=getBansPageDoActiveParamName,required=false) String doActive,
			@RequestParam(identificatorParamName) String banIdentificator,
			@RequestParam(entitiesBanManagementPageTypeOfEntityParam) String typeOfEntity) {
		System.out.println(banIdentificator);
		RedirectView redirectTo;
		if((redirectTo = webUtils.loggedCheck(request)) != null	||
				(redirectTo = webUtils.adminCheck(request, messageSource, locale)) != null ||
				(redirectTo = webUtils.userBanCheck(request, messageSource, locale)) != null ||
				(redirectTo = webUtils.ipAddressBanCheck(request, messageSource, locale)) != null) {
			return redirectTo;
		}
		
		String banMenuURL = null;
		IBanStateChanger banStateChanger = null;
		IExistanceChecker banExistanceChecker = null;
		if(TypesOfEntity.contains(typeOfEntity)) {
			TypesOfEntity enumTypeOfEnity = TypesOfEntity.getByString(typeOfEntity);
			banMenuURL = chooseBanMenuURL(enumTypeOfEnity);
			banExistanceChecker = chooseEntityBanExistanceChecker(enumTypeOfEnity, TypesOfIdentificator.ID);
			banStateChanger = chooseBanStateChanger(enumTypeOfEnity, TypesOfIdentificator.ID);
		} else {
			return illegalEntityTypeRedirect(typeOfEntity, request, locale);
		}
		
		try {
			if(!banExistanceChecker.exists(banIdentificator)) {
				return entityDoesntExistRedirect(banMenuURL, banIdentificator, request, locale);
			}
			boolean isActive = doActive == null ? true : false;
			int changedCount = banStateChanger.changeBanState(isActive, banIdentificator);
			if(changedCount != 1) {
				System.out.println("Changed " + changedCount + " bans instead of 1");
			}
			return new RedirectView("/" + adminToolsMenuURL + "/" + banMenuURL, true);
		} catch(IllegalArgumentException ex) {
			return illegalIdentificatorRedirect(banMenuURL, banIdentificator, request, locale);
		}
	}
	
	@RequestMapping(value = "/" + adminToolsMenuURL + "/" + getIPAddressByUserURL, method=RequestMethod.POST)
	public Object getIPAddress(
			HttpServletRequest request,
			Locale locale,
			@RequestParam(typeOfIdentificatorParamName) String typeOfIdentificator,
			@RequestParam(identificatorParamName) String identificator) {
		String loginFromCookies = webUtils.getLoginFromCookies(request);
		if(webUtils.isLoggedUsingCookies(request) != LoginStatus.LOGGED) {
			request.getSession().setAttribute(failedPageErrorMessageAttrName, messageSource.getMessage("NotLogonMsg", new Object[] {loginFromCookies}, locale));
			return failedPageName;
		}
		if(!userDAO.isAdmin(loginFromCookies)) {
			request.getSession().setAttribute(failedPageErrorMessageAttrName, messageSource.getMessage("YouAreNotAdmin", null, locale));
			return failedPageName;
		}
		if(userDAO.isBannedUser(loginFromCookies)) {
			DateInterval banDateInterval = userDAO.getLastUserBanDateInterval(loginFromCookies);
			Date from = null, to = null;
			if(banDateInterval != null) {
				from = banDateInterval.getFromDate();
				to = banDateInterval.getToDate();
			}
			request.getSession().setAttribute(failedPageErrorMessageAttrName, messageSource.getMessage("UserWithLoginBannedMsg", new Object[] {loginFromCookies, from, to},  locale));
			return failedPageName;
		}
		if(userDAO.isBannedUserIPAddressByLogin(webUtils.getLoginFromCookies(request))) {
			DateInterval banDateInterval = userDAO.getLastUserIPAddressBanDateInterval(loginFromCookies);
			IPAddress ipAddress = userDAO.getIPAddressByLogin(loginFromCookies);
			Date from = null, to = null;
			if(banDateInterval != null) {
				from = banDateInterval.getFromDate();
				to = banDateInterval.getToDate();
			}
			request.getSession().setAttribute(failedPageErrorMessageAttrName, messageSource.getMessage("IPAddressBannedMsg", new Object[] {ipAddress == null ? null : ipAddress.getName(), from, to},  locale));
			return failedPageName;
		}
		TypesOfIdentificator enumTypeOfIdentificator = null;
		IIPAddressGetter ipAddressGetter = null;
		if(TypesOfIdentificator.contains(typeOfIdentificator)) {
			enumTypeOfIdentificator = TypesOfIdentificator.getByString(typeOfIdentificator);
			ipAddressGetter = chooseByUserIPAddressGetter(enumTypeOfIdentificator);
		} else {
			request.getSession().setAttribute(
					failedPageErrorMessageAttrName,
					messageSource.getMessage(
							"IllegalIdentificatorTypeMsg", 
							new Object[] {
									typeOfIdentificator, 
									CommonUtils.getEnumeration(TypesOfIdentificator.getTypesOfIdentificatorList(), ", ")}, 
							locale));
			return failedPageName;
		}
		
		try {
			request.setAttribute(getIPAddressPageIPAddressAttrName, ipAddressGetter.get(identificator));
			return getIPAddressPageName;
		} catch(IllegalArgumentException ex) {
			request.getSession().setAttribute(
					failedPageErrorMessageAttrName,
					messageSource.getMessage(
							"IllegalIdentificatorMsg", 
							new Object[] {identificator}, 
							locale));
			return failedPageName;
		}
		
	}
	
	private IIPAddressGetter chooseByUserIPAddressGetter(TypesOfIdentificator enumTypeOfIdentificator) {
		switch(enumTypeOfIdentificator) {
		case LOGIN:
			return userDAO::getIPAddressByLogin;
		default:
			throw new IllegalStateException();
		}
		
	}

	private IExistanceChecker chooseEntityExistanceChecker(TypesOfEntity typeOfEntity, TypesOfIdentificator typeOfIdentificator) {
		switch(typeOfEntity) {
		case USERS:
			switch(typeOfIdentificator) {
			case LOGIN:
				return userDAO::existsUserByLogin;
			case ID:
				return (String identificator) -> userDAO.existsUserById(Integer.parseInt(identificator));
			default:
				throw new IllegalStateException("Illegal type of entity to ban");
			}
			
		case POSTS:
			return (String identificator) -> postDAO.existsPost(Integer.parseInt(identificator));
		case POST_COMMENTS:
			return (String identificator) -> postDAO.existsComment(Integer.parseInt(identificator));
		case IP_ADDRESSES:
			return (String identificator) -> userDAO.existsIPAddressById(Integer.parseInt(identificator));
		default:
			throw new IllegalStateException("illegal type of entity to ban");
		}
	}

	private String chooseBanMenuURL(TypesOfEntity typeOfEntity) {
		switch(typeOfEntity) {
		case USERS:
			return adminToolsMenuUsersBanURL;
		case POSTS:
			return adminToolsMenuPostsBanURL;
		case POST_COMMENTS:
			return adminToolsMenuPostCommentsBanURL;
		case IP_ADDRESSES:
			return adminToolsMenuIPAddressesBanURL;
		default:
			throw new IllegalStateException("illegal type of entity to ban");
		}
	}
	
	private IBansGetter chooseBansGetter(TypesOfEntity typeOfEntity) {
		switch(typeOfEntity) {
		case USERS:
			return new IBansGetter() {
				@Override
				public List<Ban> getBans(String identificator, int from, int to) {
					return userDAO.getUserBans(identificator, from, to);
				}
				@Override
				public int getBansCountBefore(int id) throws DataAccessException {
					return userDAO.getUserBansCountBefore(id);
				}
			};
		case POSTS:
			return new IBansGetter() {
				@Override
				public List<Ban> getBans(String identificator, int from, int to) {
					return postDAO.getPostBans(Integer.parseInt(identificator), from, to);
				}
				@Override
				public int getBansCountBefore(int id) throws DataAccessException {
					return postDAO.getPostBansCountBefore(id);
				}
			};
		case POST_COMMENTS:
			return new IBansGetter() {
				@Override
				public List<Ban> getBans(String identificator, int from, int to) {
					return postDAO.getCommentBans(Integer.parseInt(identificator), from, to);
				}
				@Override
				public int getBansCountBefore(int id) throws DataAccessException {
					return postDAO.getCommentBansCountBefore(id);
				}
			};
		case IP_ADDRESSES:
			return new IBansGetter() {
				@Override
				public List<Ban> getBans(String identificator, int from, int to) {
					return userDAO.getIPAddressBans(Integer.parseInt(identificator), from, to);
				}
				@Override
				public int getBansCountBefore(int id) throws DataAccessException {
					return userDAO.getIPAddressBansCountBefore(id);
				}
			};
		default:
			throw new IllegalStateException("Illegal type of entity to ban");
		}
	}

	private IBanner chooseBanner(TypesOfEntity typeOfEntity) throws ParseException {
		switch(typeOfEntity) {
		case USERS:
			return userDAO::banUser;
		case POSTS:
			return (Date from, Date to, String identificator) -> postDAO.banPost(from, to, Integer.parseInt(identificator));
		case POST_COMMENTS:
			return (Date from, Date to, String identificator) -> postDAO.banComment(from, to, Integer.parseInt(identificator));
		case IP_ADDRESSES:
			return (Date from, Date to, String identificator) -> userDAO.banIPAddress(from, to, Integer.parseInt(identificator));
		default:
			throw new IllegalStateException("illegal type of entity to ban");
		}
	}
	
	private IExistanceChecker chooseEntityBanExistanceChecker(TypesOfEntity typeOfEntity, TypesOfIdentificator typeOfIdentificator) {
		switch(typeOfEntity) {
		case USERS:
			return (String identificator) -> userDAO.existsUserBanById(Integer.parseInt(identificator));
		case POSTS:
			return (String identificator) -> postDAO.hasPostBan(Integer.parseInt(identificator));
		case POST_COMMENTS:
			return (String identificator) -> postDAO.hasCommentBan(Integer.parseInt(identificator));
		case IP_ADDRESSES:
			return (String identificator) -> userDAO.existsIPAddressBanById(Integer.parseInt(identificator));
		default:
			throw new IllegalStateException("illegal type of entity to ban");
		}
	}
	
	private IBanStateChanger chooseBanStateChanger(TypesOfEntity typeOfEntity, TypesOfIdentificator typeOfIdentificator) {
		switch(typeOfEntity) {
		case USERS:
			return (boolean isActive, String identificator) -> userDAO.changeUserBanStateById(isActive, Integer.parseInt(identificator));
		case POSTS:
			return (boolean isActive, String identificator) -> postDAO.changePostBanState(isActive, Integer.parseInt(identificator));
		case POST_COMMENTS:
			return (boolean isActive, String identificator) -> postDAO.changeCommentBanState(isActive, Integer.parseInt(identificator));
		case IP_ADDRESSES:
			return (boolean isActive, String identificator) -> userDAO.changeIPAddressBanStateById(isActive, Integer.parseInt(identificator));
		default:
			throw new IllegalStateException("illegal type of entity to ban");
		}
	}

	private RedirectView maybeDidntSetDateRedirect(
			String adminToolURL,
			String login,
			HttpServletRequest request,
			Locale locale) {
		request.getSession().setAttribute(entitiesBanManagementPageMaybeDidntSetDateMsgAttrName, messageSource.getMessage("MaybeDidntSetDateMsg", null, locale));
		return new RedirectView("/" + adminToolsMenuURL + "/" + adminToolURL, true);
	}

	private RedirectView wrongToDateRedirect(
			String adminToolURL,
			Date now,
			Date toDate,
			HttpServletRequest request,
			Locale locale) {
		request.getSession().setAttribute(entitiesBanManagementPageWrongToDateErrorMsgAttrName, messageSource.getMessage("WrongToDateErrorMsg", new Object[] {now, dbDateFormater.format(toDate)}, locale));
		return new RedirectView("/" + adminToolsMenuURL + "/" + adminToolURL, true);
	}

	private RedirectView entityDoesntExistRedirect(
			String toolURL, 
			String identificator,
			HttpServletRequest request, 
			Locale locale) {
		request.getSession().setAttribute(entitiesBanManagementPageEntityDoesntExistMsgAttrName, messageSource.getMessage("EntityDoesntExistsMsg", new Object[] {identificator}, locale)); 
		return new RedirectView("/" + adminToolsMenuURL + "/" + toolURL, true);
	}
	
	private RedirectView illegalIdentificatorRedirect(
			String toolURL,
			String identificator,
			HttpServletRequest request, 
			Locale locale) {
		request.getSession().setAttribute(
				adminToolsMenuPageIllegalEntityTypeMsgAttrName, // TODO: think about creating name-spaces - EntitiesBanManagement.* class f.e.
				messageSource.getMessage(
						"IllegalIdentificatorMsg", 
						new Object[] {identificator}, 
						locale));
		return new RedirectView("/" + adminToolsMenuURL + "/" + toolURL, true);
	}
	
	private RedirectView illegalIdentificatorTypeRedirect(
			String typeOfIdentificator, 
			HttpServletRequest request, 
			Locale locale) {
		request.getSession().setAttribute(
				adminToolsMenuPageIllegalIdentificatorTypeMsgAttrName,
				messageSource.getMessage(
						"IllegalIdentificatorTypeMsg", 
						new Object[] {
								typeOfIdentificator, 
								CommonUtils.getEnumeration(TypesOfIdentificator.getTypesOfIdentificatorList(), ", ")}, 
						locale));
		return new RedirectView("/" + adminToolsMenuURL, true);
	}
	
	private RedirectView illegalEntityTypeRedirect(
			String typeOfEntity,
			HttpServletRequest request, 
			Locale locale) {
		request.getSession().setAttribute(
				adminToolsMenuPageIllegalEntityTypeMsgAttrName,
				messageSource.getMessage(
						"IllegalEntityTypeMsg", 
						new Object[] {
								typeOfEntity, 
								CommonUtils.getEnumeration(TypesOfEntity.getTypesOfEntityList(), ", ")}, 
						locale));
		return new RedirectView("/" + adminToolsMenuURL, true);
	}
}
