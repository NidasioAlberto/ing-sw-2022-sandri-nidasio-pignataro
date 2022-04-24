package it.polimi.ingsw.model.exceptions;

public class WrongPlayerException extends RuntimeException
{
    String player;

    public WrongPlayerException(String player)
    {
        super("Wrong player");

        this.player = player;
    }

    public String getPlayer()
    {
        return player;
    }
}
