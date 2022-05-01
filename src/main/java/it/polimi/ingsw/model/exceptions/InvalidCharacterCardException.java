package it.polimi.ingsw.model.exceptions;

public class InvalidCharacterCardException extends RuntimeException
{
    public InvalidCharacterCardException(String str)
    {
        super(str);
    }
}
