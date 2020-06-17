package ru.miron.CheckersImpls;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ru.miron.Checkers.IUserChecker;
import static ru.miron.Config.ConfigConstants.*;

@Component
public class UserChecker implements IUserChecker{
	
	@Autowired
	MessageSource messageSource;

	@Override
	public List<String> getLoginErrors(String login, Locale locale) {
		System.out.println(login + " " + login.length());
		List<String> errors = new ArrayList<String>();
		if(login.length() < MIN_LOGIN_LENGTH || login.length() > MAX_LOGIN_LENGTH) {
			errors.add(messageSource.getMessage(
					"IncorrectLoginLengthMsg",
					new Object[] {MIN_LOGIN_LENGTH, MAX_LOGIN_LENGTH, login.length()},
					locale));
		}
		if(!Pattern.matches("^[a-zA-Z][a-zA-Z0-9]*$", login)) {
			errors.add(messageSource.getMessage(
					"IncorrectLoginSymbols",
					new Object[] {login},
					locale));
		}
		return errors.isEmpty() ? new ArrayList<String>() : errors;
	}

	@Override
	public List<String> getPasswordErrors(String password, Locale locale) {
		System.out.println(password + " " + password.length());
		List<String> errors = new ArrayList<String>();
		if(password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
			errors.add(messageSource.getMessage(
					"IncorrectPasswordLengthMsg",
					new Object[] {MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH, password.length()},
					locale));
		}
		if(!Pattern.matches("^[a-zA-Z0-9]*$", password)) {
			errors.add(messageSource.getMessage(
					"IncorrectPasswordSymbols",
					new Object[] {password},
					locale));
		}
		return errors.isEmpty() ? new ArrayList<String>() : errors;
	}
	
}
