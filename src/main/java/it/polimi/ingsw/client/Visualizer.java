package it.polimi.ingsw.client;

import it.polimi.ingsw.protocol.updates.*;
import it.polimi.ingsw.protocol.answers.*;

public abstract class Visualizer
{
    protected Client client;

    public Visualizer(Client client)
    {
        this.client = client;
    }

    public Client getClient()
    {
        return client;
    }

    // Updates

    public abstract void displayAssistantCards(AssistantCardsUpdate update);

    public abstract void displayCharacterCardPayload(CharacterCardPayloadUpdate update);

    public abstract void displayCharacterCards(CharacterCardsUpdate update);

    public abstract void displayCloudTiles(CloudTilesUpdate update);

    public abstract void displayIslands(IslandsUpdate update);

    public abstract void displayPlayedAssistantCard(PlayedAssistantCardUpdate update);

    public abstract void displaySchoolboard(SchoolBoardUpdate update);

    public abstract void setCurrentPlayer(CurrentPlayerUpdate update);

    // Answers

    public abstract void displayEndMatch(EndMatchAnswer answer);

    public abstract void displayError(ErrorAnswer answer);

    public abstract void displayJoinedMatch(JoinedMatchAnswer answer);

    public abstract void displayMatchesList(MatchesListAnswer answer);

    public abstract void displaySetName(SetNameAnswer answer);

    public abstract void displayStartMatch(StartMatchAnswer answer);
}
