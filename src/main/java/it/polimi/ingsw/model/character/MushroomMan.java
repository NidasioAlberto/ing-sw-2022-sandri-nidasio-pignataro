package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.*;

import java.util.NoSuchElementException;

/**
 * Character card Mushroom man. Effect: Choose a color of Student; during the influence calculation
 * this turn, that color adds no influence.
 */
public class MushroomMan extends CharacterCard
{
    /**
     * The color selected by the player
     */
    SchoolColor color;

    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    MushroomMan(Game game) throws NullPointerException
    {
        super(game);

        // MushroomMan's cost
        this.cost = 3;

        color = null;
    }

    @Override
    public boolean isPlayable() throws NoSuchElementException
    {
        GameAction previousAction = instance.getGameAction().orElseThrow(
                () -> new NoSuchElementException("[MushroomMan] There is no previous action")
        );

        // This card must be played before the action MOVE_MOTHER_NATURE
        if(previousAction == GameAction.PLAY_ASSISTANT_CARD ||
                previousAction == GameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_DINING ||
                previousAction == GameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_ISLAND)
        {
            color = instance.getSelectedPlayer().orElseThrow(
                    () -> new NoSuchElementException("[MushroomMan] No selected player")
            ).getSelectedColors().stream().findFirst().orElseThrow(
                    () -> new NoSuchElementException("[MushroomMan] No selected color")
            );
            return true;
        }

        return false;
    }

    @Override
    public boolean isValidAction(GameAction action)
    {
        // If the card is not active I return the instance validation
        if (!activated)
        {
            return instance.isValidAction(action);
        }

        // If it is activated I accept only the SELECT_COLOR action
        return action == GameAction.SELECT_COLOR;
        //TODO se la isValidAction torna false, forse bisogna disattivare la carta
    }

    //TODO viene chiamata dopo ogni azione applyAction della carta se è attiva?
    //TODO problema perchè potrebbe essere giocata prima del calcolo dell'influenza
    @Override
    public void applyAction() throws NoSuchElementException
    {
        GameAction previousAction = instance.getGameAction().orElseThrow(
                () -> new NoSuchElementException("[MushroomMan] There is no previous action")
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
                .map(
                        (p) -> {
                            // Effect of the card: the selected color adds no influence
                            if(p.getColor() != color)
                                return currentIsland.getStudentsByColor(p.getColor());
                            else return 0;
                        }
                ).reduce(0, Integer::sum);

        // Add the influence from the towers
        influence += currentIsland.getTowers().stream()
                .filter(t -> t.getColor().equals(player.getColor())).count();

        return influence;
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.MUSHROOM_MAN;
    }

    @Override
    protected void deactivate()
    {
        color = null;
        super.deactivate();
    }
}
