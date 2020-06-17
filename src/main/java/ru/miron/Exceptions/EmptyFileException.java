package ru.miron.Exceptions;

import java.io.IOException;
import java.io.Serial;

public class EmptyFileException extends IOException{
    @Serial
    static final long serialVersionUID = 78183758245725466L;
    
    public EmptyFileException() {
        super();
    }
    
    public EmptyFileException(String message) {
        super(message);
    }
    
    public EmptyFileException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public EmptyFileException(Throwable cause) {
        super(cause);
    }
}
