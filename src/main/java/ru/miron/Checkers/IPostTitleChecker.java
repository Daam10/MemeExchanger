package ru.miron.Checkers;

import java.util.List;
import java.util.Locale;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public interface IPostTitleChecker {
	List<String> getErrors(String title, Locale locale);
}
