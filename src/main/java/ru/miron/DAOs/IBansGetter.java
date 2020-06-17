package ru.miron.DAOs;

import java.util.List;

import org.springframework.dao.DataAccessException;

import ru.miron.Entities.Ban;

public interface IBansGetter {
	List<Ban> getBans(String identificator, int from, int to) throws IllegalArgumentException;
	int getBansCountBefore(int id) throws DataAccessException;
}
