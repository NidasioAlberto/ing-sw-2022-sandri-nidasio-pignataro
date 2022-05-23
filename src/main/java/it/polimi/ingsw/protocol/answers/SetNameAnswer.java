package it.polimi.ingsw.protocol.answers;

import it.polimi.ingsw.client.Visualizer;

import java.io.Serial;

public class SetNameAnswer extends Answer
{
    @Serial
    private static final long serialVersionUID = 1882584043643637670L;

    @Override
    public void handleAnswer(Visualizer handler)
    {
        handler.displaySetName(this);
    }
}
