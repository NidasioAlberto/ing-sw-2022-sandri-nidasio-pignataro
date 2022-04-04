package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.GameAction;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Player;

import java.util.NoSuchElementException;

/**
 * Character card Knight. Effect: During the influence calculation this turn, you count as having 2
 * more influence.
 */
public class Knight extends CharacterCard
{
    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    Knight(Game game) throws NullPointerException
    {
        super(game);

        // Knight's cost
        this.cost = 2;
    }

    @Override
    public boolean isPlayable() throws NoSuchElementException
    {
        GameAction previousAction = instance.getGameAction().orElseThrow(
                () -> new NoSuchElementException("[Knight] There is no previous action")
        );

        // This card must be played before the action MOVE_MOTHER_NATURE
        return previousAction == GameAction.PLAY_ASSISTANT_CARD ||
                previousAction == GameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_DINING ||
                previousAction == GameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_ISLAND;
    }

    @Override
    public boolean isValidAction(GameAction action)
    {
        // This card doesn't have a connected action
        return instance.isValidAction(action);
    }

    //TODO problema perchè potrebbe essere giocata prima del calcolo dell'influenza
    @Override
    public void applyAction()
    {
        GameAction previousAction = instance.getGameAction().orElseThrow(
                () -> new NoSuchElementException("[Knight] There is no previous action")
        );

        if(previousAction != GameAction.MOVE_MOTHER_NATURE)
            return;

        //TODO non so se funziona correttamente chiamando così
        instance.computeInfluence();

        if (!firstUsed)
        {
            cost += 1;
            firstUsed = true;
        }

        this.deactivate();
    }

    //TODO problema perchè le varie chiamate non fanno riferimento a instance
    @Override
    public int computePlayerInfluence(Player player, int island) throws NoSuchElementException, IndexOutOfBoundsException, NullPointerException
    {
        if(island < 0 || island > instance.getIslands().size())
            throw new IndexOutOfBoundsException("[Game] island index out of bounds");

        if(player == null)
            throw new NullPointerException("[Game] player null");

        Island currentIsland = instance.getIslands().get(island);

        // Compute the influence of this player from students
        int influence = player.getBoard().getProfessors().stream()
                .map(p -> currentIsland.getStudentsByColor(p.getColor())).reduce(0, Integer::sum);

        // Add the influence from the towers
        influence += currentIsland.getTowers().stream()
                .filter(t -> t.getColor().equals(player.getColor())).count();

        Player currentPlayer = instance.getSelectedPlayer().orElseThrow(
                () -> new NoSuchElementException("[Knight] No selected player")
        );

        // Effect of the card: the current player gets 2 more influence points
        if(player == currentPlayer)
            influence += 2;

        return influence;
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.KNIGHT;
    }
}
