package it.polimi.ingsw.protocol.answers;

import it.polimi.ingsw.client.Visualizer;

import java.io.Serializable;

public abstract class Answer implements Serializable
{
    /**
     * This method is part of the command pattern.
     *
     * @param handler The handler class to modify client side
     */
    public abstract void handleAnswer(Visualizer handler);
}
