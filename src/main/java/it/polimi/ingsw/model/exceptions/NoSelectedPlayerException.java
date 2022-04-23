package it.polimi.ingsw.model.exceptions;

import java.util.NoSuchElementException;

public class NoSelectedPlayerException extends NoSuchElementException
{
    public NoSelectedPlayerException(String str)
    {
        super(str + " No player selected");
    }
}
