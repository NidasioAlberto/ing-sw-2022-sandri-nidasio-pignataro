package it.polimi.ingsw.model.exceptions;

import java.util.NoSuchElementException;

public class NoMoreNoEntryTilesException extends NoSuchElementException
{
    public NoMoreNoEntryTilesException(String str)
    {
        super(str + " No more no entry tiles");
    }
}
