package it.polimi.ingsw.protocol.answers;

import it.polimi.ingsw.client.Visualizer;

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

    @Override
    public void handleAnswer(Visualizer handler)
    {
        handler.displayEndMatch(this);
    }
}
