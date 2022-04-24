package it.polimi.ingsw.model.exceptions;

import com.sun.jdi.InvalidModuleException;

public class InvalidCharacterCardException extends InvalidModuleException
{
    public InvalidCharacterCardException(String str)
    {
        super(str);
    };
}
