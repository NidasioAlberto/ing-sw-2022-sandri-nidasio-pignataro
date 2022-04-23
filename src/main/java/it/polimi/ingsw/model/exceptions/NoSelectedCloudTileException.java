package it.polimi.ingsw.model.exceptions;

import java.util.NoSuchElementException;

public class NoSelectedCloudTileException extends NoSuchElementException
{
    public NoSelectedCloudTileException(String str)
    {
        super(str + " No cloud tile selected");
    }
}
