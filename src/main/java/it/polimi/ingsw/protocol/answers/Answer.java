package it.polimi.ingsw.protocol.answers;

import it.polimi.ingsw.client.Visualizable;

import java.io.Serial;
import java.io.Serializable;

public abstract class Answer implements Serializable
{
    @Serial
    private static final long serialVersionUID = 3075004929990451300L;

    /**
     * This method is part of the command pattern.
     *
     * @param handler The handler class to modify client side
     */
    public abstract void handleAnswer(Visualizable handler);
}
