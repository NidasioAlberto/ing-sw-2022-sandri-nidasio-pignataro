package it.polimi.ingsw.model.exceptions;

import java.util.NoSuchElementException;

public class NoSelectedIslandException extends NoSuchElementException
{
    public NoSelectedIslandException(String str)
    {
        super(str + " No island selected");
    }
}
