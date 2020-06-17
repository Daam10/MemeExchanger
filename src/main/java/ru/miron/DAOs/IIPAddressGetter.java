package ru.miron.DAOs;

import ru.miron.Entities.IPAddress;

public interface IIPAddressGetter {
	IPAddress get(String identificator) throws IllegalArgumentException;
}
