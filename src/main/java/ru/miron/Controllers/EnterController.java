package ru.miron.Controllers;

import static ru.miron.Config.ConfigConstants.*;
import static ru.miron.Config.WebConstants.*;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import ru.miron.Checkers.IUserChecker;
import ru.miron.Config.WebConstants;
import ru.miron.DAOs.IUserDAO;
import ru.miron.Enums.LoginStatus;
import ru.miron.Utils.WebUtils;

@Controller
@EnableTransactionManagement
public class EnterController {
	
	@Autowired
	WebUtils webUtils;
	@Autowired
	IUserDAO userDAO;
	@Autowired
	IUserChecker userChecker;
	@Autowired
	MessageSource messageSource;
	
	@RequestMapping(value="/" + rootURL, method = RequestMethod.GET)
	public Object root(HttpServletRequest request) {
		if(webUtils.isLoggedUsingCookies(request) != LoginStatus.LOGGED) { // to do ban checks
			return rootPageName;
		}
		return new RedirectView(mainURL, true);
	}
	
	@RequestMapping(value="/" + loginURL, method = RequestMethod.GET)
	public Object login(HttpServletRequest request) {
		return loginPageName;
	}
	
	@RequestMapping(value="/" + loginURL, method = RequestMethod.POST)
	public Object login_loginForm(
			HttpServletRequest request,
			HttpSession model,
			@RequestParam(loginPageLoginParamName) String login, 
			@RequestParam(loginPagePasswordParamName) String password, 
			HttpServletResponse response,
			Locale locale) {
		if(userDAO.isWrong(login, password)) {
			model.setAttribute(
					loginPageWrongLoginOrPasswordMsgAttrName, 
					messageSource.getMessage("WrongLoginOrPasswordMsg", null, locale));
			return loginPageName;
		}
		RedirectView redirectTo;
		if((redirectTo = webUtils.userBanCheck(login, request, messageSource, locale)) != null || 
				(redirectTo = webUtils.ipAddressBanCheck(login, request, messageSource, locale)) != null) {
			return redirectTo;
		}
		webUtils.putUserIntoCookies(response, login, password, secondsToSaveCookie);
		return new RedirectView(mainURL, true);
	}
	
	@RequestMapping(value="/" + registrationURL, method = RequestMethod.GET)
	public Object registration(HttpServletRequest request) {
		return registrationPageName;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
	@RequestMapping(value="/" + registrationURL, method = RequestMethod.POST)
	public Object registration_registationForm(
			@RequestParam(registrationPageLoginParamName) String login, 
			@RequestParam(registrationPagePasswordParamName) String password, 
			HttpServletResponse response,
			HttpServletRequest request,
			Locale locale) {
		if(userDAO.existsUserByLogin(login)) {
			request.setAttribute(
					registrationPageLoginAlreadyExistsMsgAttrName, 
					messageSource.getMessage("LoginAlreadyExistsMsg", null, locale));
			return registrationPageName;
		}
		List<String> errors;
		if(!(errors = userChecker.getLoginErrors(login, locale)).isEmpty()) {
			errors.forEach(System.out::println);
			request.setAttribute(registrationPageLoginErrorsAttrName, errors);
			return registrationPageName;
		}
		if(!(errors = userChecker.getPasswordErrors(password, locale)).isEmpty()) {
			errors.forEach(System.out::println);
			request.setAttribute(registrationPagePasswordErrorsAttrName, errors);
			return registrationPageName;
		}
		String ipAddress = request.getRemoteAddr();
		if(userDAO.getCountOfAccountsOfAddr(ipAddress) > MAX_ACCOUNT_COUNT_PER_ADDR) {
			request.setAttribute(
					registrationPageTooManyAccountsMsgAttrName, 
					messageSource.getMessage("TooManyAccountsMsg", new Object[] {MAX_ACCOUNT_COUNT_PER_ADDR}, locale));
			return registrationPageName;
		}
		
		webUtils.putUserIntoCookies(response, login, password, secondsToSaveCookie);
		
		userDAO.insertUser(login, password);
		userDAO.insertIPAddress(login, ipAddress);
		return new RedirectView(mainURL, true);
	}
	
	@RequestMapping(value="/" + logoutURL, method = RequestMethod.GET)
	public RedirectView logout(HttpServletResponse response) {
		webUtils.deleteCookies(new String[] {loginCookieName, passwordCookieName}, response);
		return new RedirectView(rootURL, true);
	}
}
