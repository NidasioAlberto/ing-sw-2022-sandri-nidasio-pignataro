package it.polimi.ingsw.client;

import it.polimi.ingsw.protocol.updates.*;
import it.polimi.ingsw.protocol.answers.*;

public interface Visualizable
{
    // Updates

    void displayAssistantCards(AssistantCardsUpdate update);

    void displayCharacterCardPayload(CharacterCardPayloadUpdate update);

    void displayCharacterCards(CharacterCardsUpdate update);

    void displayCloudTiles(CloudTilesUpdate update);

    void displayIslands(IslandsUpdate update);

    void displayPlayedAssistantCard(PlayedAssistantCardUpdate update);

    void displaySchoolboard(SchoolBoardUpdate update);

    void setCurrentPlayer(CurrentPlayerUpdate update);

    // Answers

    void displayEndMatch(EndMatchAnswer answer);

    void displayError(ErrorAnswer answer);

    void displayJoinedMatch(JoinedMatchAnswer answer);

    void displayMatchesList(MatchesListAnswer answer);

    void displaySetName(SetNameAnswer answer);

    void displayStartMatch(StartMatchAnswer answer);
}
