package it.polimi.ingsw.protocol.answers;

import it.polimi.ingsw.client.Visualizer;

public class SetNameAnswer extends Answer
{
    @Override
    public void handleAnswer(Visualizer handler)
    {
        handler.displaySetName(this);
    }
}
