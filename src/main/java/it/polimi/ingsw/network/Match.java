package it.polimi.ingsw.network;

import java.util.List;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.protocol.answers.Answer;
import it.polimi.ingsw.protocol.answers.EndMatchAnswer;
import it.polimi.ingsw.protocol.messages.ActionMessage;
import it.polimi.ingsw.protocol.updates.ModelUpdate;
import it.polimi.ingsw.model.GameMode;

public class Match implements Subscriber<ModelUpdate>
{
    private List<PlayerConnection> players;

    private Controller gameController;

    Match(int playersNumber, GameMode mode)
    {
        // TODO
    }

    public void addPlayer(PlayerConnection player)
    {
        // TODO
    }

    public void removePlayer(PlayerConnection player)
    {
        // TODO
    }

    public void sendErrorMessage(String player, String message)
    {
        // Create and ErrorAnswer
    }

    public void sendAllErrorMessage(String message)
    {
        // Create and ErrorAnswer
    }

    public void sendAllAnswer(Answer command)
    {
        // TODO
    }

    public void sendError(String player, String message)
    {
        // TODO
    }

    public void endMatch(String message)
    {
        sendAllAnswer(new EndMatchAnswer(message));
        // TODO: Remove all players from the match
    }

    public void applyAction(ActionMessage action, PlayerConnection player)
    {
        gameController.performAction(action, player.getPlayerName().get());
    }

    public int getPlayersNumber()
    {
        return players.size();
    }

    public Controller getController()
    {
        return gameController;
    }

    /**
     * This method is invoked when the model has been changed. Observer pattern
     * 
     * @param update The item that has been changed
     */
    @Override
    public void onNext(ModelUpdate update)
    {

    }

    /**
     * This method is called immediately when the subscription is done TODO MAYBE USEFUL, IF SO ADD
     * IT TO UML
     * 
     * @param subscription a new subscription
     */
    @Override
    public void onSubscribe(Subscription subscription)
    {}

    /**
     * This method is called by the model when an error occurs TODO MAYBE USEFUL, IF SO ADD IT TO
     * UML
     * 
     * @param throwable the exception
     */
    @Override
    public void onError(Throwable throwable)
    {}

    /**
     * This method is called when the model knows that nothing will change in the future anymore
     * TODO MAYBE USEFUL, IF SO ADD IT TO UML
     */
    @Override
    public void onComplete()
    {}
}
