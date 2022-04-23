package it.polimi.ingsw.model.exceptions;

import java.util.NoSuchElementException;

public class NoSuchStudentInDiningException extends NoSuchElementException
{
    public NoSuchStudentInDiningException(String str)
    {
        super(str + " No such student in dining");
    }
}
