package ru.miron.CheckersImpls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import ru.miron.Checkers.IFileChecker;
import static ru.miron.Config.ConfigConstants.MAX_INPUT_FILENAME_LENGTH;
import static ru.miron.Config.ConfigConstants.MAX_FINAL_FILENAME_LENGTH;
import ru.miron.Config.ConfigConstants;
import ru.miron.Enums.PossibleIMGsSuffixes;

import static ru.miron.Utils.CommonUtils.*;

@Component
public class ImagesChecker implements IFileChecker {
	
	@Autowired
	MessageSource messageSource;

	@Override
	public List<String> getErrorsBeforeGetting(CommonsMultipartFile file, Locale locale) {
		List<String> errors = new ArrayList<String>();
		if(file.getSize() == 0) {
			errors.add(messageSource.getMessage("FileSizeNullMsg", new Object[] {file.getOriginalFilename()}, locale)); // TODO: put names into config - to expensive
		}
		if(file.getOriginalFilename().length() > MAX_INPUT_FILENAME_LENGTH) {
			errors.add(messageSource.getMessage(
					"FileSizeNullMsg", 
					new Object[] {file.getOriginalFilename().length(), MAX_INPUT_FILENAME_LENGTH},
					locale));
		}
		if(!file.getOriginalFilename().contains(".")) {
			errors.add(messageSource.getMessage("FileHasntExtensionMsg", new Object[] {file.getOriginalFilename()}, locale));
		} else {
			String[] splittedByDots = file.getOriginalFilename().split("\\.");
			String postfix = splittedByDots[splittedByDots.length - 1];
			if(!PossibleIMGsSuffixes.contains(postfix.toLowerCase())) {
				errors.add(messageSource.getMessage(
						"WrongFilePostfixMsg", 
						new Object[] {getEnumeration(PossibleIMGsSuffixes.getSuffixesList(), ", "), postfix.toLowerCase()}, 
						locale));
			}
		}
		return errors.isEmpty() ? new ArrayList<String>() : errors;
	}
	
	@Override
	public List<String> getErrorsAfterGetting(String fileName, Locale locale) {
		List<String> errors = new ArrayList<String>();
		if(fileName.length() > MAX_FINAL_FILENAME_LENGTH) {
			errors.add(messageSource.getMessage(
					"FileSizeNullMsg", 
					new Object[] {fileName.length(), MAX_FINAL_FILENAME_LENGTH},
					locale));
		}
		if(!fileName.contains(".")) {
			errors.add(messageSource.getMessage("FileHasntExtensionMsg", new Object[] {fileName}, locale));
		} else {
			String postfix = fileName.split("\\.")[1];
			if(!PossibleIMGsSuffixes.contains(postfix.toLowerCase())) {
				errors.add(messageSource.getMessage(
						"WrongFilePostfixMsg", 
						new Object[] {getEnumeration(PossibleIMGsSuffixes.getSuffixesList(), ", "), postfix.toLowerCase()}, 
						locale));
			}
		}
		return errors.isEmpty() ? new ArrayList<String>() : errors;
	}

}
