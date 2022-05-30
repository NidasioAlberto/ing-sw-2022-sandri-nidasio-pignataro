package it.polimi.ingsw.protocol.answers;

import it.polimi.ingsw.client.Visualizable;

import java.io.Serial;

public class EndMatchAnswer extends Answer
{
    @Serial
    private static final long serialVersionUID = 4035211337580680792L;

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

    @Override
    public void handleAnswer(Visualizable handler)
    {
        handler.displayEndMatch(this);
    }
}
