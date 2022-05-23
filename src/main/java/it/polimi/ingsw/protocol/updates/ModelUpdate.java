package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.client.Visualizer;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

/**
 * This class represents the abstraction of a typical model update message from server to clients.
 * It is part of the command pattern, which is the communication core. The model is an observable
 * class, observed by the so called "virtual-view" (or in this context the communication Match).
 * When a change is applied the model updates the Match with one of these messages and eventually
 * the player to which send the message.
 */
public abstract class ModelUpdate implements Serializable
{
    @Serial
    private static final long serialVersionUID = 6601821834483061246L;

    /**
     * In case we want to define the single player that has to receive the message.
     */
    protected String playerDestination;

    /**
     * Constructor that allows the player destination.
     * 
     * @param playerDestination Name of the player that has to receive the message
     */
    protected ModelUpdate(String playerDestination)
    {
        if (playerDestination == null)
            throw new NullPointerException("[ModelUpdate] Null player destination");

        this.playerDestination = playerDestination;
    }

    /**
     * Constructor without a player destination
     */
    protected ModelUpdate()
    {
        this.playerDestination = null;
    }

    /**
     * This method is part of the command pattern.
     * 
     * @param handler The handler class to modify client side
     */
    public abstract void handleUpdate(Visualizer handler);

    public Optional<String> getPlayerDestination()
    {
        return playerDestination == null ? Optional.empty() : Optional.of(playerDestination);
    }
}
