package it.polimi.ingsw.model.exceptions;

import java.util.NoSuchElementException;

public class NoSelectedColorException extends NoSuchElementException
{
    public NoSelectedColorException(String str)
    {
        super(str + " No color selected");
    }
}
