package ru.miron.CheckersImpls;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ru.miron.Checkers.IPostTitleChecker;
import static ru.miron.Config.DBConstants.*;

@Component
public class PostTitleChecker implements IPostTitleChecker{
	
	@Autowired
	MessageSource messageSource;

	@Override
	public List<String> getErrors(String title, Locale locale) {
		List<String> errors = new ArrayList<String>();
		if(title.length() > MAX_POST_TITLE_LENGTH) {
			errors.add(messageSource.getMessage(
					"LongTitleMsg", 
					new Object[] {title.length(), MAX_POST_TITLE_LENGTH}, 
					locale));
		}
		return errors.isEmpty() ? new ArrayList<String>() : errors;
	}
	
}
