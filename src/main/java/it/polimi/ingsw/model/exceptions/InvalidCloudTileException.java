package it.polimi.ingsw.model.exceptions;

public class InvalidCloudTileException extends RuntimeException
{
    public InvalidCloudTileException(String str)
    {
        super(str + " this cloud tile is invalid: the index is out of bounds or the tile isn't filled");
    }
}
