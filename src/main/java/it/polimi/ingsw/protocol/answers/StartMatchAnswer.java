package it.polimi.ingsw.protocol.answers;

import it.polimi.ingsw.client.Visualizer;

import java.io.Serial;

public class StartMatchAnswer extends Answer
{
    @Serial
    private static final long serialVersionUID = 2455878640788529559L;

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
