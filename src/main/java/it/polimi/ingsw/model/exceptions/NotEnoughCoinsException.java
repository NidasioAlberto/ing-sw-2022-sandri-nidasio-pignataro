package it.polimi.ingsw.model.exceptions;

public class NotEnoughCoinsException extends RuntimeException
{
    public NotEnoughCoinsException()
    {
        super("Not enough coins");
    }
}
