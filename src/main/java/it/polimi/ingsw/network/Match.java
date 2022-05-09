package it.polimi.ingsw.network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
import it.polimi.ingsw.protocol.answers.Answer;
import it.polimi.ingsw.protocol.answers.ErrorAnswer;
import it.polimi.ingsw.protocol.messages.ActionMessage;
import it.polimi.ingsw.protocol.updates.ModelUpdate;

public class Match implements Subscriber<ModelUpdate>
{
    private Server server;

    private List<PlayerConnection> players;

    private Controller gameController;

    Match(Server server, int playersNumber, GameMode mode)
    {
        this.server = server;
        players = new ArrayList<>();
        gameController = new Controller(this, playersNumber, mode);
    }

    /**
     * Adds a player to the match. If all the players are connected the game is setup.
     * 
     * @param nickname The player's nickname.
     * @throws NullPointerException If the nickname is null.
     * @throws IllegalArgumentException If already exists a player with such nickname.
     * @throws TooManyPlayersException If there are too many players.
     */
    public void addPlayer(PlayerConnection player)
            throws NullPointerException, IllegalArgumentException, TooManyPlayersException
    {
        gameController.addPlayer(player.getPlayerName().get());
        players.add(player);
    }

    public void removePlayer(PlayerConnection player)
    {
        players.remove(player);
    }

    public void sendAllAnswer(Answer answer)
    {
        for (PlayerConnection player : players)
            player.sendAnswer(answer);
    }

    public void sendError(String playerName, String message)
    {
        // Find the player with the given name
        players.forEach((player) -> {
            if (player.getPlayerName().isPresent())
                if (player.getPlayerName().get() == playerName)
                    player.sendAnswer(new ErrorAnswer(message));
        });
    }

    public void endMatch(String message)
    {
        server.removeMatch(this, message);
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

    public List<PlayerConnection> getPlayers()
    {
        return players;
    }

    /**
     * This method is invoked when the model has been changed. Observer pattern
     * 
     * @param update The item that has been changed
     */
    @Override
    public void onNext(ModelUpdate update)
    {
        for (PlayerConnection player : players)
            player.sendModelUpdate(update);
    }

    /**
     * This method is called immediately when the subscription is done.
     * 
     * TODO: Maybe usefule, if so add it to UML.
     */
    @Override
    public void onSubscribe(Subscription subscription)
    {}

    /**
     * This method is called by the model when an error occurs.
     * 
     * TODO: Maybe useful, if so add it to UML.
     */
    @Override
    public void onError(Throwable throwable)
    {}

    /**
     * This method is called when the model knows that nothing will change in the future anymore.
     * 
     * TODO: Maybe useful, if so add it to UML.
     */
    @Override
    public void onComplete()
    {}
}
