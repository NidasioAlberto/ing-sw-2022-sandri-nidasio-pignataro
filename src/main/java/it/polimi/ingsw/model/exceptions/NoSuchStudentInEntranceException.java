package it.polimi.ingsw.model.exceptions;

import java.util.NoSuchElementException;

public class NoSuchStudentInEntranceException extends NoSuchElementException
{
    public NoSuchStudentInEntranceException(String str)
    {
        super(str + " No such student in entrance");
    }
}
