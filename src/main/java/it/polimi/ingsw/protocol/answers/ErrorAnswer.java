package it.polimi.ingsw.protocol.answers;

import it.polimi.ingsw.client.Visualizer;

import java.io.Serial;

public class ErrorAnswer extends Answer
{
    @Serial
    private static final long serialVersionUID = -4240143038569233287L;

    private String errorMessage;

    public ErrorAnswer(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    @Override
    public String toString()
    {
        return "[ErrorAnswer] Error: " + errorMessage;
    }

    @Override
    public void handleAnswer(Visualizer handler)
    {
        handler.displayError(this);
    }
}
