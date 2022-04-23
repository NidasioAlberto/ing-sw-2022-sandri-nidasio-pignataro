package it.polimi.ingsw.model.exceptions;

import java.util.NoSuchElementException;

public class NoSuchAssistantCardException extends NoSuchElementException
{
    public NoSuchAssistantCardException(String str)
    {
        super(str + " No assistant card with such turn order");
    }
}
