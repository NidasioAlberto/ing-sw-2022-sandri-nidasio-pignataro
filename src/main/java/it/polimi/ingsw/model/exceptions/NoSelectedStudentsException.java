package it.polimi.ingsw.model.exceptions;

import java.util.NoSuchElementException;

public class NoSelectedStudentsException extends NoSuchElementException
{
    public NoSelectedStudentsException(String str)
    {
        super(str + " The number of students selected is not correct");
    }
}
