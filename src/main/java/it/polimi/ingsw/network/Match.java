package it.polimi.ingsw.network;

import java.util.List;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.Flow.Subscriber;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.protocol.messages.ActionMessage;
import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.protocol.updates.ModelUpdate;

public class Match implements Subscriber<ModelUpdate>
{
    private List<PlayerConnection> players;

    private Controller gameController;

    Match(int playersNumber, GameMode mode)
    {
        // ...
    }

    public void addPlayer(PlayerConnection player)
    {
        // ...
    }

    public void removePlayer(PlayerConnection player)
    {
        // ...
    }

    public void sendMessage(String player, String message)
    {
        // ...
    }

    public void sendAllMessage(String message)
    {
        // ...
    }

    public void actionCall(ActionMessage action, PlayerConnection player)
    {
        gameController.performAction(action, player.getPlayerName().get());
    }

    /**
     * This method is invoked when the model has been changed.
     * Observer pattern
     * @param update The item that has been changed
     */
    @Override
    public void onNext(ModelUpdate update)
    {

    }

    /**
     * This method is called immediately when the subscription is done
     * TODO MAYBE USEFUL, IF SO ADD IT TO UML
     * @param subscription a new subscription
     */
    @Override
    public void onSubscribe(Subscription subscription){}

    /**
     * This method is called by the model when an error occurs
     * TODO MAYBE USEFUL, IF SO ADD IT TO UML
     * @param throwable the exception
     */
    @Override
    public void onError(Throwable throwable) {}

    /**
     * This method is called when the model knows that nothing will change in the future anymore
     * TODO MAYBE USEFUL, IF SO ADD IT TO UML
     */
    @Override
    public void onComplete() {}
}
