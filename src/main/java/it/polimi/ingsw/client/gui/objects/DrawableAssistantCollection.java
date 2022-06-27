package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.client.gui.objects.types.WizardType;
import it.polimi.ingsw.model.AssistantCard;
import it.polimi.ingsw.protocol.updates.AssistantCardsUpdate;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;

import java.util.ArrayList;
import java.util.List;

public class DrawableAssistantCollection extends DrawableCollection
{
    /**
     * X dimension of all the cards
     */
    private final double DIMENSION;

    /**
     * Collection of assistant cards
     */
    private List<DrawableAssistantCard> assistantCards;

    /**
     * Constructor
     * 
     * @param updater The animation handler that updates every card
     * @param dimension The x_dimension of all the cards
     */
    public DrawableAssistantCollection(double dimension, PointLight pointLight, AmbientLight ambientLight, Group group, AnimationHandler updater)
    {
        super(pointLight, ambientLight, group, updater);

        if (dimension <= 0)
            throw new IllegalArgumentException("[DrawableAssistantCollection] X dimension less or equal to 0");

        // Assign the constants
        DIMENSION = dimension;

        // Set the position to 0
        position = new Point3D(0, 0, 0);

        // Create the card collection and populate them
        assistantCards = new ArrayList<>();
    }

    /**
     * Method to position all the cards inside the collection correctly
     */
    private void updatePosition()
    {
        for (int i = 0; i < assistantCards.size(); i++)
            assistantCards.get(i).translate(new Point3D(position.getX() - DIMENSION * assistantCards.size() / 2.0 + DIMENSION * i + DIMENSION / 2,
                    position.getY(), position.getZ()));
    }

    /**
     * Method to display the assistant cards update
     */
    public void displayUpdate(AssistantCardsUpdate update)
    {
        // Remove all the flagged assistant cards
        for (int i = 0; i < update.getCards().size(); i++)
        {
            if (update.getCards().get(i).isUsed())
                update.getCards().remove(i--);
        }

        // Case when we have to create the cards
        if (assistantCards.size() == 0)
        {
            // Create the assistant cards instances
            for (AssistantCard card : update.getCards())
            {
                assistantCards.add(new DrawableAssistantCard(DIMENSION, card.getTurnOrder(), WizardType.valueOf(card.getWizard().name()), updater));
            }

            // Subscribe all the cards to the light
            for (DrawableAssistantCard card : assistantCards)
            {
                card.subscribeToAmbientLight(ambientLight);
                card.subscribeToPointLight(pointLight);
            }

            // Add all the cards to the group
            this.addToGroup();
        } else
        {
            // The number is not changed so i don't update
            if (assistantCards.size() == update.getCards().size())
                return;

            // Number changed, i delete the missing one
            for (int i = 0; i < update.getCards().size(); i++)
            {
                if (assistantCards.get(i).getTurnOrder() != update.getCards().get(i).getTurnOrder())
                {
                    // Remove the card
                    assistantCards.get(i).removeFromGroup(group);
                    assistantCards.get(i).unsubscribeFromPointLight(pointLight);
                    assistantCards.get(i).unsubscribeFromAmbientLight(ambientLight);
                    updater.unsubscribeObject(assistantCards.get(i));
                    assistantCards.remove(i--);
                }
            }
            // Left cards are deleted
            for (int i = update.getCards().size(); i < assistantCards.size(); i++)
            {
                // Remove the left ones
                assistantCards.get(i).removeFromGroup(group);
                assistantCards.get(i).unsubscribeFromPointLight(pointLight);
                assistantCards.get(i).unsubscribeFromAmbientLight(ambientLight);
                updater.unsubscribeObject(assistantCards.get(i));
                assistantCards.remove(i--);
            }
        }

        // At the end i need to update the positioning
        updatePosition();
    }

    @Override
    public void clearAll()
    {
        for (DrawableAssistantCard card : assistantCards)
        {
            card.removeFromGroup(group);
            card.unsubscribeFromAmbientLight(ambientLight);
            card.unsubscribeFromPointLight(pointLight);
            updater.unsubscribeObject(card);
        }

        // Delete al the cards from the arraylist
        assistantCards.clear();
    }

    @Override
    public void addToGroup()
    {
        // I subscribe every card
        for (DrawableAssistantCard card : assistantCards)
            card.addToGroup(group);
    }

    @Override
    public void removeFromGroup()
    {
        // I un subscribe every card
        for (DrawableAssistantCard card : assistantCards)
            card.removeFromGroup(group);
    }

    @Override
    public void translate(Point3D point)
    {
        if (point == null)
            throw new NullPointerException("[DrawableAssistantCollection] Null point");

        // Update the current position
        position = point;

        // Update the cards positioning
        updatePosition();
    }
}
