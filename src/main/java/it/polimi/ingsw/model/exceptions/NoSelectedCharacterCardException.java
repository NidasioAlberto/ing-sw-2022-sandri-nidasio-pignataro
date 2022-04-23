package it.polimi.ingsw.model.exceptions;

import java.util.NoSuchElementException;

public class NoSelectedCharacterCardException extends NoSuchElementException
{
    public NoSelectedCharacterCardException(String str)
    {
        super(str + " No character card selected");
    }
}
