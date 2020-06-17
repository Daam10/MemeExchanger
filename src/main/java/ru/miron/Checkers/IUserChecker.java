package ru.miron.Checkers;

import java.util.List;
import java.util.Locale;

public interface IUserChecker {
	List<String> getLoginErrors(String login, Locale locale);
	List<String> getPasswordErrors(String password, Locale locale);
}
