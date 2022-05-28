package it.polimi.ingsw.protocol.answers;

import it.polimi.ingsw.client.Visualizer;

import java.io.Serial;

public class SetNameAnswer extends Answer
{
    @Serial
    private static final long serialVersionUID = 1882584043643637670L;

    private String name;
    public SetNameAnswer(String name)
    {
        this.name = name;
    }

    @Override
    public void handleAnswer(Visualizer handler)
    {
        handler.displaySetName(this);
    }

    @Override
    public String toString()
    {
        return "[SetNameAnswer] Your nickname is " + name;
    }
}
