package it.polimi.ingsw.model.exceptions;

import java.util.NoSuchElementException;

public class NoSuchStudentOnCardException extends NoSuchElementException
{
    public NoSuchStudentOnCardException(String str)
    {
        super(str + " No such student on card");
    }
}
