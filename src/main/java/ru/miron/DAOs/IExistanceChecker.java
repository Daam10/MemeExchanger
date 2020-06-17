package ru.miron.DAOs;

@FunctionalInterface
public interface IExistanceChecker {
	boolean exists(String identificator) throws IllegalArgumentException;
}
