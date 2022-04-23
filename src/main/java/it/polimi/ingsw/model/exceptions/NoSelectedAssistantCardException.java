package it.polimi.ingsw.model.exceptions;

import java.util.NoSuchElementException;

public class NoSelectedAssistantCardException extends NoSuchElementException
{
    public NoSelectedAssistantCardException(String str)
    {
        super(str + " No assistant card selected");
    }
}
