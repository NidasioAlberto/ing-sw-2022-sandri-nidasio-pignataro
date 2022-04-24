package it.polimi.ingsw.model.exceptions;

import java.security.InvalidParameterException;

public class InvalidMovementException extends InvalidParameterException
{
    public InvalidMovementException(String str)
    {
        super(str);
    }
}
