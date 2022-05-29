package it.polimi.ingsw.model;

import it.polimi.ingsw.model.exceptions.EndGameException;
import it.polimi.ingsw.model.exceptions.NoSuchAssistantCardException;
import it.polimi.ingsw.protocol.updates.AssistantCardsUpdate;
import it.polimi.ingsw.protocol.updates.ModelUpdate;
import it.polimi.ingsw.protocol.updates.PlayedAssistantCardUpdate;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Publisher;

/**
 * This class represent one of the game's player.
 */
public class Player implements Publisher<ModelUpdate>
{
    /**
     * The nickname of the player.
     */
    private String nickname;

    /**
     * The school board of the player.
     */
    private SchoolBoard board;

    /**
     * The list of assistant card that the player can still play.
     */
    private List<AssistantCard> cards;

    /**
     * The player's color is associated with the color of the towers in his school board.
     */
    private TowerColor color;

    /**
     * The assistant card selected by the player during the planning phase.
     */
    private Optional<AssistantCard> selectedCard;

    /**
     * The island selected by the player during an action.
     */
    private Optional<Integer> selectedIsland;

    /**
     * The student colors selected by the player during an action.
     */
    private List<SchoolColor> selectedColors;

    /**
     * The cloud tile selected by the player at the end of the action phase.
     */
    private Optional<Integer> selectedCloudTile;

    /**
     * The character card selected by the player.
     */
    private Optional<Integer> selectedCharacterCard;

    /**
     * It is true if this player is currently connected to the game.
     */
    private boolean active;

    protected Optional<Subscriber<? super ModelUpdate>> subscriber;

    /**
     * Save the turn order of the last assistant card played.
     */
    private int lastAssistantCardUsed;

    /**
     * It is true if the player has played a card during the plan phase this round.
     */
    private boolean hasPlayedCard;

    public Player(String nickname, TowerColor color, GameMode mode)
    {
        this(nickname, new SchoolBoard(color, mode));
    }

    public Player(String nickname, SchoolBoard board) throws NullPointerException
    {
        if (nickname == null)
            throw new NullPointerException("[Player] The player's nickname can't be null");
        if (board == null)
            throw new NullPointerException("[Player] The player's board can't be null");

        this.nickname = nickname;
        this.color = board.getTowerColor();
        this.board = board;
        cards = new ArrayList<AssistantCard>();
        selectedCard = Optional.empty();
        selectedIsland = Optional.empty();
        selectedColors = new ArrayList<SchoolColor>();
        selectedCloudTile = Optional.empty();
        selectedCharacterCard = Optional.empty();
        subscriber = Optional.empty();
        active = false;
        hasPlayedCard = false;
    }

    /**
     * Method to select an AssistantCard during the planning phase.
     * 
     * @param turnOrder The number of turn of the card.
     * @throws NoSuchAssistantCardException if the player hasn't got the selected card.
     */
    public void selectCard(int turnOrder) throws NoSuchAssistantCardException
    {
        for (int i = 0; i < cards.size(); i++)
        {
            if (cards.get(i).getTurnOrder() == turnOrder && !cards.get(i).isUsed())
            {
                selectedCard = Optional.of(cards.get(i));

                // Toggle the selected card to be used
                cards.get(i).toggleUsed();
                hasPlayedCard = true;
                lastAssistantCardUsed = i;

                // I notify the subscriber because I have used a card
                notifySubscriber();
                return;
            }
        }

        throw new NoSuchAssistantCardException("[Player]");
    }

    public void notifySubscriber()
    {
        if (subscriber.isPresent())
        {
            subscriber.get().onNext(new AssistantCardsUpdate(nickname, new ArrayList<AssistantCard>(cards)));
            // If the player has played at least one card, I send the last assistant card played
            if (cards.stream().anyMatch(AssistantCard::isUsed))
            {
                subscriber.get().onNext(new PlayedAssistantCardUpdate(new AssistantCard(cards.get(lastAssistantCardUsed).getWizard(),
                        cards.get(lastAssistantCardUsed).getTurnOrder(), cards.get(lastAssistantCardUsed).getSteps()), nickname));
            }
        }
    }

    /**
     * Method to add an AssistantCard to the player.
     * 
     * @param card The card to be added.
     * @throws NullPointerException Thrown if the card is null.
     */
    public void addCard(AssistantCard card) throws NullPointerException
    {
        if (card == null)
            throw new NullPointerException("[Player] Null Assistant card");

        if (cards.isEmpty())
        {
            cards.add(card);
        } else if (cards.get(0).getWizard() == card.getWizard() && !cards.contains(card))
        {
            for (AssistantCard assistantCard : cards)
            {
                if (assistantCard.getTurnOrder() == card.getTurnOrder())
                    return;
            }
            cards.add(card);
        }
    }

    /**
     * Method to remove an AssistantCard from the player's list of cards.
     * 
     * @param turnOrder The number of turn of the card.
     * @throws NoSuchAssistantCardException if the player doesn't have the selected card.
     * @throws EndGameException if the last card is removed.
     */
    public void removeCard(int turnOrder) throws NoSuchAssistantCardException, EndGameException
    {
        for (int i = 0; i < cards.size(); i++)
        {
            if (cards.get(i).getTurnOrder() == turnOrder)
            {
                cards.remove(i);
                return;
            }
        }
        throw new NoSuchAssistantCardException("[Player]");
    }

    /**
     * Removes the currently selected card.
     */
    public void removeSelectedCard()
    {
        if (selectedCard.isPresent())
        {
            removeCard(selectedCard.get().getTurnOrder());

            selectedCard = Optional.empty();
        }
    }

    /**
     * Method to select an island.
     * 
     * @param islandIndex The index of the island.
     */
    public void selectIsland(int islandIndex)
    {
        selectedIsland = Optional.of(islandIndex);
    }

    /**
     * Method to select a color among SchoolColor.
     * 
     * @param color The color selected.
     * @throws NullPointerException Thrown if the specified color is invalid.
     */
    public void selectColor(SchoolColor color) throws NullPointerException
    {
        if (color == null)
            throw new NullPointerException("[Player] A null color was provided");

        selectedColors.add(color);
    }

    /**
     * Method to select a CloudTile during the last phase of the action phase.
     * 
     * @param tile The index of the CloudTile.
     */
    public void selectCloudTile(int tile)
    {
        selectedCloudTile = Optional.of(tile);
    }

    /**
     * Method to select a CharacterCard during the action phase.
     *
     * @param card The index of the CharacterCard.
     */
    public void selectCharacterCard(int card)
    {
        selectedCharacterCard = Optional.of(card);
    }

    /**
     * Method to call after every player's action to clear island and colors selected
     */
    public void clearSelections()
    {
        selectedIsland = Optional.empty();
        selectedColors.clear();
    }

    /**
     * Method to call at the end of the turn to clear all the selections
     */
    public void clearSelectionsEndTurn()
    {
        selectedIsland = Optional.empty();
        selectedColors.clear();
        selectedCloudTile = Optional.empty();
        selectedCharacterCard = Optional.empty();
    }

    /**
     * Method to call at the end of the round to clear the fact that
     * the player may have played a card.
     */
    public void clearSelectionEndRound()
    {
        hasPlayedCard = false;
    }

    public String getNickname()
    {
        return nickname;
    }

    public SchoolBoard getBoard()
    {
        return board;
    }

    public Optional<AssistantCard> getSelectedCard()
    {
        return selectedCard;
    }

    public Optional<Integer> getSelectedIsland()
    {
        return selectedIsland;
    }

    public List<SchoolColor> getSelectedColors()
    {
        return new ArrayList<>(selectedColors);
    }

    public Optional<Integer> getSelectedCloudTile()
    {
        return selectedCloudTile;
    }

    public Optional<Integer> getSelectedCharacterCard()
    {
        return selectedCharacterCard;
    }

    public List<AssistantCard> getCards()
    {
        return new ArrayList<AssistantCard>(cards);
    }

    public TowerColor getColor()
    {
        return color;
    }

    @Override
    public void subscribe(Subscriber<? super ModelUpdate> subscriber)
    {
        if (subscriber == null)
            throw new NullPointerException("[Player] Null subscriber");

        // I subscribe the observer to the observable only if doesn't already exist one
        if (this.subscriber.isEmpty())
            this.subscriber = Optional.of(subscriber);
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public boolean hasPlayedCard()
    {
        return hasPlayedCard;
    }
}
