package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;

import java.util.NoSuchElementException;

/**
 * Character card Mushroom man. Effect: Choose a color of Student; during the influence calculation
 * this turn, that color adds no influence.
 */
public class MushroomMan extends CharacterCard
{
    /**
     * The selected color
     */
    private SchoolColor color;

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
        // This card is playable only before the movement of mother nature
        return !instance.motherNatureMoved;
    }

    @Override
    public boolean isValidAction(ExpertGameAction action)
    {
        // If it is activated I accept only the SELECT_COLOR action once and after only base actions
        return (action == ExpertGameAction.SELECT_COLOR && color == null) || (action == ExpertGameAction.ACTION_BASE && color != null);
    }

    @Override
    public void applyAction() throws NoSuchElementException
    {
        //I have to check if mother nature has been moved.
        //If so i can disable the card
        if(!activated)
            return;

        //If the color is not already selected, i take the player selection
        if(color == null)
        {
            //TODO IT COULD THROW A INDEXOUTOFBOUNDS EXCEPTION
            color = instance.getSelectedPlayer()
                    .orElseThrow(() -> new NoSuchElementException("[MushroomMan] No selected player"))
                    .getSelectedColors().get(0);
        }

        if(instance.motherNatureMoved)
            this.deactivate();
    }

    @Override
    public int computePlayerInfluence(Player player, int island) throws NoSuchElementException, IndexOutOfBoundsException, NullPointerException
    {
        //If the card is not active i ask the instance
        if(!activated)
            return instance.computePlayerInfluence(player, island);

        if(island < 0 || island > instance.islands.size())
            throw new IndexOutOfBoundsException("[MushroomMan] island index out of bounds");

        if(player == null)
            throw new NullPointerException("[MushroomMan] player null");

        if(color == null)
            throw new NoSuchElementException("[MushroomMan] No selected color");

        Island currentIsland = instance.islands.get(island);

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
    public void deactivate()
    {
        color = null;
        super.deactivate();
    }
}
