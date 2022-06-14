package it.polimi.ingsw.client.gui.objects;

import java.util.ArrayList;
import java.util.List;
import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.protocol.updates.CharacterCardPayloadUpdate;
import it.polimi.ingsw.protocol.updates.CharacterCardsUpdate;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;

public class DrawableCharacterCardCollection extends DrawableCollection
{
    /**
     * Spacing between each card
     */
    public static final double SPACING = 15;

    /**
     * Dimension of a single Character card
     */
    private final double DIMENSION;

    /**
     * All the character cards
     */
    private List<DrawableCharacterCard> cards;

    /**
     * Constructor
     * 
     * @param dimension Single character card dimension
     */
    public DrawableCharacterCardCollection(double dimension, PointLight pointLight, AmbientLight ambientLight, Group group, AnimationHandler updater)
    {
        super(pointLight, ambientLight, group, updater);

        if (dimension < 0)
            throw new IllegalArgumentException("[DrawableCharacterCardCollection] Invalid character card dimension");

        // Assign all the variables
        this.DIMENSION = dimension;

        // Initialize the character card list
        cards = new ArrayList<>();
    }

    /**
     * Method to update the internal position of the character cards
     */
    public void updatePosition()
    {
        for (int i = 0; i < cards.size(); i++)
            cards.get(i)
                    .translate(new Point3D(
                            position.getX() - (DIMENSION + SPACING / 2.0) * cards.size() / 2 + (DIMENSION + SPACING / 2.0) * (i + 1.0 / 2.0),
                            position.getY(), position.getZ()));
    }

    /**
     * Method to display the character cards update
     * 
     * @param update Update message to visualize
     */
    public void displayUpdate(CharacterCardsUpdate update)
    {
        // In case we have to create the cards
        if (cards.size() == 0)
        {
            for (int i = 0; i < update.getCards().size(); i++)
                cards.add(new DrawableCharacterCard(DIMENSION, i, update.getCards().get(i).getCardType(), updater));

            // Subscribe all the cards to the lights
            for (DrawableCharacterCard card : cards)
            {
                card.subscribeToAmbientLight(ambientLight);
                card.subscribeToPointLight(pointLight);
            }

            // Add the cards to the group
            this.addToGroup();

            // Update their positionings
            updatePosition();
        }
    }

    /**
     * Method to display the character cards payload update
     */
    public void displayUpdate(CharacterCardPayloadUpdate update)
    {
        // Verify that it could be done
        if (cards.size() == 0)
            return;

        cards.get(update.getIndex()).update(update, group, pointLight);
    }

    @Override
    public void addToGroup()
    {
        for (DrawableCharacterCard card : cards)
            card.addToGroup(group);
    }

    @Override
    public void removeFromGroup()
    {
        for (DrawableCharacterCard card : cards)
            card.removeFromGroup(group);
    }

    @Override
    public void translate(Point3D point)
    {
        if (point == null)
            throw new NullPointerException("[DrawableCharacterCardCollection] Null point");

        // Assign the new position
        this.position = point;

        // Translate all the cards
        updatePosition();
    }

}
