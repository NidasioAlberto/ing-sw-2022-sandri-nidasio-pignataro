package it.polimi.ingsw.protocol.answers;

public class EndMatchAnswer extends Answer
{
    String message;

    public EndMatchAnswer(String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return "[EndMatchAnswer] " + message;
    }
}
