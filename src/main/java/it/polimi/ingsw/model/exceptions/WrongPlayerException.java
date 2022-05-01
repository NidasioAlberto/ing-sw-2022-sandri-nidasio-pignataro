package it.polimi.ingsw.model.exceptions;

public class WrongPlayerException extends RuntimeException
{
    public WrongPlayerException()
    {
        super("Wrong player");
    }
}
