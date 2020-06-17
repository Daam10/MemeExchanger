package ru.miron.GlobalExceptionsResolvers;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.view.RedirectView;
import static ru.miron.Config.ConfigConstants.*;

@Component
public class CommonExceptionResolver {
	private static final Logger logger = Logger.getLogger(CommonExceptionResolver.class);
	
	@ExceptionHandler
	public RedirectView toHandleEverything(Throwable th) {
		logger.error(th);
		return new RedirectView(fatalErrorPageName);
	}
}
