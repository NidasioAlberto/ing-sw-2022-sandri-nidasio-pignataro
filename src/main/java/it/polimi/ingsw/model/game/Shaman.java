package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
    public boolean isValidAction(ExpertGameAction action)
    {
        // I don't have to intercept any action
        return action == ExpertGameAction.BASE_ACTION;
    }

    @Override
    public void applyAction()
    {
        //I have to check if mother nature has been moved.
        //If so i can disable the card
        if(!activated)
            return;

        if(instance.motherNatureMoved)
            this.deactivate();
    }

    @Override
    public void conquerProfessors() throws NoSuchElementException
    {
        //If the card is not activated I skip this method
        if(!activated)
        {
            instance.conquerProfessors();
            return;
        }

        // Check for every professor if there is a player who can have it
        for (SchoolColor color : SchoolColor.values())
        {
            // If the professor is still on the table, assign it to the player with the most
            // students, if there is any
            if (instance.professors.stream().filter(p -> p.getColor().equals(color)).count() > 0)
            {
                // Get the player with more students of the current color
                List<Player> sortedPlayers = instance.players.stream()
                        .sorted((p1, p2) -> p2.getBoard().getStudentsNumber(color)
                                - p1.getBoard().getStudentsNumber(color))
                        .collect(Collectors.toList());

                // The player gets the professor only if he has the majority
                if (sortedPlayers.get(0).getBoard().getStudentsNumber(color) > sortedPlayers.get(1)
                        .getBoard().getStudentsNumber(color))
                {
                    // Remove the professor from the table
                    Professor toMove = instance.professors.stream().filter(p -> p.getColor().equals(color))
                            .findFirst().orElse(null);
                    instance.professors.remove(toMove);

                    // Add him to the player's board
                    sortedPlayers.get(0).getBoard().addProfessor(toMove);
                }
            } else
            {
                // Look for the player that has that professor
                Player currentKing = instance.players.stream().filter(p -> p.getBoard().hasProfessor(color))
                        .findFirst().orElse(null);

                // Get the player with more students of the current color
                Player wannaBeKing =
                        instance.players.stream()
                                .sorted((p1, p2) -> p2.getBoard().getStudentsNumber(color)
                                        - p1.getBoard().getStudentsNumber(color))
                                .findFirst().orElse(null);

                // If they are the same player there's nothing to do
                if (currentKing != wannaBeKing)
                {
                    // Move the professor if the wanna be king is not the current king and he has
                    // more students
                    if (wannaBeKing.getBoard().getStudentsNumber(color) > currentKing.getBoard()
                            .getStudentsNumber(color))
                    {
                        // I take the instance of the professor to be moved
                        Professor professor = currentKing.getBoard().getProfessors().stream()
                                .filter(p -> p.getColor().equals(color)).findFirst().get();

                        // Remove the professor from the current king
                        currentKing.getBoard().removeProfessor(professor);

                        // Add the professor to the new king
                        wannaBeKing.getBoard().addProfessor(professor);
                    }
                }

                // MOVE THE PROFESSOR IF THE CURRENT KING ISN'T THE PLAYER THAT HAS PLAYED THIS CARD
                // AND THE PLAYER THAT HAS PLAYED THIS CARD HAS THE SAME STUDENTS NUMBER OF CURRENT KING
                if (currentKing != instance.getSelectedPlayer().orElseThrow(
                        () -> new NoSelectedPlayerException("[Shaman]"))
                        && instance.getSelectedPlayer().get().getBoard().getStudentsNumber(color) >=
                        currentKing.getBoard().getStudentsNumber(color))
                {
                    // I take the instance of the professor to be moved
                    Professor professor = currentKing.getBoard().getProfessors().stream()
                            .filter(p -> p.getColor().equals(color)).findFirst().get();

                    // Remove the professor from the current king
                    currentKing.getBoard().removeProfessor(professor);

                    // Add the professor to the new king
                    instance.getSelectedPlayer().get().getBoard().addProfessor(professor);
                }
            }
        }
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.SHAMAN;
    }
}
