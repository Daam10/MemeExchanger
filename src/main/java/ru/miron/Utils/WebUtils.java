package ru.miron.Utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.RedirectView;

import ru.miron.DAOs.IUserDAO;
import ru.miron.Entities.DateInterval;
import ru.miron.Entities.LoginAndPassword;
import ru.miron.Enums.LoginStatus;

import static ru.miron.Config.ConfigConstants.*;
import static ru.miron.Config.WebConstants.*;

import java.util.Arrays;
import java.util.Locale;

@Component
public class WebUtils {
	@Autowired
	IUserDAO userDAO;
	
	public LoginStatus isLoggedUsingCookies(HttpServletRequest request) {
		String login = null;
		String password = null;
		Cookie[] cookies = request.getCookies();
		if(cookies == null) {
			return LoginStatus.HASNT_COOKIES;
		}
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals(loginCookieName)) {
				login = cookie.getValue();
			} else if(cookie.getName().equals(passwordCookieName)) {
				password = cookie.getValue();
			}
		}
		if(login == null || password == null) {
			return LoginStatus.HASNT_COOKIES;
		}
		if(userDAO.isWrong(login, password)) {
			return LoginStatus.WRONG_COOKIES;
		}
		return LoginStatus.LOGGED;
	}

	public void putUserIntoCookies(HttpServletResponse response, String login, String password, int secondsToSaveCookie) {
		Cookie loginCookie = new Cookie(loginCookieName, login);
		Cookie passwordCookie = new Cookie(passwordCookieName, password);
		loginCookie.setMaxAge(secondsToSaveCookie);
		passwordCookie.setMaxAge(secondsToSaveCookie);
		response.addCookie(loginCookie);
		response.addCookie(passwordCookie);
	}

	public String getLoginFromCookies(HttpServletRequest request) {
		String login = null;
		Cookie[] cookies = request.getCookies();
		if(cookies == null) {
			return null;
		}
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals(loginCookieName)) {
				login = cookie.getValue();
			}
		}
		return login;
	}
	
	private LoginAndPassword getLoginAndPasswordFromCookies(HttpServletRequest request) {
		String login = null;
		String password = null;
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals(loginCookieName)) {
					login = cookie.getValue();
				} else if(cookie.getName().equals(passwordCookieName)) {
					password = cookie.getValue();
				}
			}
		}
		return new LoginAndPassword(login, password);
	}
	
	
	public RedirectView loggedCheck(HttpServletRequest request) {
		if(isLoggedUsingCookies(request) != LoginStatus.LOGGED) {
			return new RedirectView("/" + rootURL, true);
		}
		return null;
	}

	public RedirectView adminCheck(HttpServletRequest request, MessageSource messageSource, Locale locale) {
		if(!userDAO.isAdmin(getLoginFromCookies(request))) {
			request.getSession().setAttribute(failedPageErrorMessageAttrName, messageSource.getMessage("YouAreNotAdmin", null, locale));
			return new RedirectView("/" + failedURL, true);
		}
		return null;
	}

	public RedirectView userBanCheck(HttpServletRequest request, MessageSource messageSource, Locale locale) {
		return userBanCheck(getLoginFromCookies(request), request, messageSource, locale);
	}
	
	public RedirectView userBanCheck(String login, HttpServletRequest request, MessageSource messageSource, Locale locale) {
		if(userDAO.isBannedUser(login)) {
			DateInterval banDateInterval = userDAO.getLastUserBanDateInterval(login);
			if(banDateInterval == null) {
				banDateInterval = new DateInterval(null, null);
			}
			request.getSession().setAttribute(
					failedPageErrorMessageAttrName, 
					messageSource.getMessage(
							 "UserWithLoginBannedMsg", 
							 new Object[] {
									 login, 
									 banDateInterval.getFromDate(), 
									 banDateInterval.getToDate()},
							 locale));
			return new RedirectView("/" + failedURL, true);
		}
		return null;
	}
	
	public boolean getHTMLCheckboxValue(String htmlCheckboxValue) {
		return htmlCheckboxValue == null ? false : true;
	}

	public RedirectView ipAddressBanCheck(HttpServletRequest request, MessageSource messageSource, Locale locale) {
		return ipAddressBanCheck(getLoginFromCookies(request), request, messageSource, locale);
	}
	
	public RedirectView ipAddressBanCheck(String login, HttpServletRequest request, MessageSource messageSource, Locale locale) {
		if(userDAO.isBannedUserIPAddressByLogin(login)) {
			DateInterval banDateInterval = userDAO.getLastUserIPAddressBanDateInterval(login);
			if(banDateInterval == null) {
				banDateInterval = new DateInterval(null, null);
			}
			request.getSession().setAttribute(
					failedPageErrorMessageAttrName, 
					messageSource.getMessage(
							 "IPAddressBannedMsg", 
							 new Object[] {
									 login, 
									 banDateInterval.getFromDate(), 
									 banDateInterval.getToDate()},
							 locale));
			return new RedirectView("/" + failedURL, true);
		}
		return null;
	}

	public boolean getJSBooleanValue(String booleanValue) {
		if(booleanValue.equals("true")) {
			return true;
		} else if(booleanValue.equals("false")) {
			return false;
		} else {
			throw new IllegalArgumentException(booleanValue + " has bad value");
		}
	}

	public void deleteCookies(String[] names, HttpServletResponse response) {
		for(int i = 0; i < names.length; i++) {
			deleteCookie(names[i], response);
		}
	}
	public void deleteCookie(String name, HttpServletResponse response) {
		response.addCookie(new Cookie(name, null));
	}
}
