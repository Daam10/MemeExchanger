package ru.miron.DAOs;

import java.util.Date;

import org.springframework.dao.DataAccessException;

@FunctionalInterface
public interface IBanner {
	int ban(Date now, Date toForeverDate, String identificator) throws DataAccessException, IllegalArgumentException;
}
