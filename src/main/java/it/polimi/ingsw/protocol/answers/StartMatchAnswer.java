package it.polimi.ingsw.protocol.answers;

import it.polimi.ingsw.client.Visualizer;

public class StartMatchAnswer extends Answer
{
    @Override
    public String toString()
    {
        return "[StartMatchAnswer] Match started";
    }

    @Override
    public void handleAnswer(Visualizer handler)
    {
        handler.displayStartMatch(this);
    }
}
