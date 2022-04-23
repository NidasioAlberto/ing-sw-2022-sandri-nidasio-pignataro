package it.polimi.ingsw.model.exceptions;

public class IslandIndexOutOfBoundsException extends IndexOutOfBoundsException
{
    public IslandIndexOutOfBoundsException(String str)
    {
        super(str + " Island index out of bounds");
    }
}
