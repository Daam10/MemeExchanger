package ru.miron.Checkers;

import java.util.List;
import java.util.Locale;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public interface IFileChecker {
	List<String> getErrorsBeforeGetting(CommonsMultipartFile file, Locale locale);
	List<String> getErrorsAfterGetting(String fileName, Locale locale);
}
