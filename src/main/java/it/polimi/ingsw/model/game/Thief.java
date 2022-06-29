package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.ExpertGameAction;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.NoSelectedColorException;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;
import it.polimi.ingsw.protocol.updates.SchoolBoardUpdate;

import java.io.Serial;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

/**
 * Character card Thief. Effect: Choose a type of Student; every player (including yourself) must return 3 Students of that type from their Dining
 * Room to the bag. If any player has fewer than 3 Students of that type, return as many Students as they have.
 */
public class Thief extends CharacterCard
{

    @Serial
    private static final long serialVersionUID = 2057268773712564630L;

    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    Thief(Game game) throws NullPointerException
    {
        super(game);

        // Thief's cost
        this.cost = 3;
    }

    @Override
    public boolean isPlayable()
    {
        // This card can be played everytime
        return true;
    }

    @Override
    public boolean isValidAction(ExpertGameAction action)
    {
        // If it is activated I accept only the SELECT_COLOR action
        return action == ExpertGameAction.SELECT_COLOR;
    }

    /**
     * This method returns true only when the game is in the last round
     * 
     * @return Boolean that represents the game ending state
     */
    private boolean isGameEnding()
    {
        // If the student bag is empty i return immediately true
        if (instance.getStudentBag().size() == 0)
            return true;

        // I can return true if i find a player with no assistant cards available anymore
        for (Player player : instance.getPlayerTableList())
        {
            if (player.getCards().stream().filter(c -> !c.isUsed()).findFirst().isEmpty())
                return true;
        }

        return false;
    }

    /**
     * Move max 3 students, of the color selected by the current player, from each player's dining to the bag.
     *
     * @throws NoSuchElementException If there isn't a current player an exception is thrown.
     */
    @Override
    public void applyAction() throws NoSuchElementException
    {
        // If the card is not currently activated i do nothing
        if (!activated)
            return;

        if (isGameEnding())
        {
            instance.players.stream().forEach(p -> {
                // For each player remove max 3 students from the dining room
                IntStream.range(0, 3).forEach(i -> {
                    p.getBoard().removeStudentFromDining(instance.getSelectedPlayer().orElseThrow(() -> new NoSelectedPlayerException("[Thief]"))
                            // Of the color selected from the current player
                            .getSelectedColors().stream().findFirst().orElseThrow(() -> new NoSelectedColorException("[Thief]")));
                });
            });
        } else
        {
            // Get the player's list
            instance.players.stream().forEach(p -> {
                // For each player remove max 3 students from the dining room
                IntStream.range(0, 3).forEach(i -> {
                    p.getBoard().removeStudentFromDining(instance.getSelectedPlayer().orElseThrow(() -> new NoSelectedPlayerException("[Thief]"))
                            // Of the color selected from the current player
                            .getSelectedColors().stream().findFirst().orElseThrow(() -> new NoSelectedColorException("[Thief]")))
                            // The student removed is replaced in the bag
                            .ifPresent(s -> instance.addStudentToBag(s));
                });
            });
        }

        // I need to send the SchoolBoardUpdate because some students have been removed from the dining
        if (instance.subscriber.isPresent())
        {
            for (Player player : instance.players)
                instance.subscriber.get().onNext(new SchoolBoardUpdate(player.getBoard(), player.getNickname(), instance.players.indexOf(player)));

        }

        this.deactivate();
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.THIEF;
    }
}
