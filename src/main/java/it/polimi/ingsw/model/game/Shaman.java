package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;

import java.util.NoSuchElementException;

/**
 * Character card Shaman. Effect: During this turn, you take control of any number of Professors
 * even if you have the same number of Students as the player who currently controls them
 */
public class Shaman extends CharacterCard
{
    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    Shaman(Game game) throws NullPointerException
    {
        super(game);

        // Shaman's cost
        this.cost = 2;
    }

    @Override
    public boolean isPlayable()
    {
        // This card is playable only before the movement of mother nature
        return !instance.motherNatureMoved;
    }

    @Override
    public boolean isValidAction(GameAction action)
    {
        // If the card is not activated the action isn't valid
        if(!activated)
        {
            return false;
        }

        // I don't have to intercept any action
        return true;
    }

    @Override
    public void applyAction()
    {}

    @Override
    public void conquerProfessors()
    {
        //If the card is not activated I skip this method
        if(!activated)
        {
            instance.conquerProfessors();
            return;
        }

        //Current selected player
        Player currentPlayer = instance.getSelectedPlayer().orElseThrow(() -> new NoSuchElementException("[Shaman] No player selected"));

        //Check for every professor if the selected player has at least a student of the same
        //color. If the professor is still in this instance it means that no one has a student
        //of the expected color except from that player
        for(int i = 0; i < instance.professors.size(); i++)
        {
            //If the player has at least one student of the same color i can assign
            //the professor to that player
            if(currentPlayer.getBoard().getStudentsNumber(instance.professors.get(i).getColor()) > 0)
            {
                currentPlayer.getBoard().addProfessor(instance.removeProfessor(i));
            }
        }

        //Now I can check who is the player with the most students for every color and the assign the professor
        for(int i = 0; i < SchoolColor.values().length; i++)
        {
            int finalI = i;
            //Look for the player that has that professor
            Player currentKing = instance.players.stream().filter(p -> p.getBoard().hasProfessor(SchoolColor.values()[finalI])).findFirst().get();

            //If the players differ and don't have the same number of students I move the professor
            //THE ONLY DIFFERENCE IS THE >= SIGN
            if(currentKing != currentPlayer && currentPlayer.getBoard().getStudentsNumber(SchoolColor.values()[finalI]) >=
                    currentKing.getBoard().getStudentsNumber(SchoolColor.values()[finalI]))
            {
                Professor prof;
                //I take the instance of the professor to be moved
                prof = currentKing.getBoard().getProfessors().stream().filter(p -> p.getColor() == SchoolColor.values()[finalI]).findFirst().get();

                //Remove the professor from the king
                currentKing.getBoard().removeProfessor(prof);

                //Add the professor to the new king
                currentPlayer.getBoard().addProfessor(prof);
            }
        }
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.SHAMAN;
    }
}
