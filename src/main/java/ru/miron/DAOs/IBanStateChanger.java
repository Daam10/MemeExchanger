package ru.miron.DAOs;

import org.springframework.dao.DataAccessException;

public interface IBanStateChanger {
	int changeBanState(boolean isActive, String identificator) throws DataAccessException, IllegalArgumentException;
}
