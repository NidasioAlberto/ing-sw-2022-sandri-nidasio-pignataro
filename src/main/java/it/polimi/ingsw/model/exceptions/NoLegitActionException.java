package it.polimi.ingsw.model.exceptions;

public class NoLegitActionException extends RuntimeException
{
    public NoLegitActionException()
    {
        super("No legit action");
    }
}
