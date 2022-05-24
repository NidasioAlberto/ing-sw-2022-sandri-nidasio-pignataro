package it.polimi.ingsw.client.gui.objects.types;

import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.client.gui.objects.DrawableAssistantCard;
import it.polimi.ingsw.client.gui.objects.DrawableObject;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.List;

public class DrawableAssistantCollection extends DrawableObject
{
    // The initial amount of cards
    public static final int INITIAL_CARDS = 10;

    // The spacing for every card
    public static final double SPACING = 1;

    /**
     * Type of assistant cards
     */
    private final WizardType TYPE;

    /**
     * X dimension of all the cards
     */
    private final double X_DIMENSION;

    /**
     * Collection position (mean)
     */
    private Point3D position;

    /**
     * Collection of assistant cards
     */
    private List<DrawableAssistantCard> assistantCards;

    /**
     * Constructor
     * @param updater The animation handler that updates every card
     * @param x_dimension The x_dimension of all the cards
     * @param type The card type (not really useful if used with cards upwards)
     */
    public DrawableAssistantCollection(double x_dimension, WizardType type, AnimationHandler updater)
    {
        super(updater);

        if(type == null)
            throw new NullPointerException("[DrawableAssistantCollection] Null card type");
        if(x_dimension <= 0)
            throw new IllegalArgumentException("[DrawableAssistantCollection] X dimension less or equal to 0");

        // Assign the constants
        TYPE        = type;
        X_DIMENSION = x_dimension;

        // Set the position to 0
        position = new Point3D(0, 0, 0);

        // Create the card collection and populate them
        assistantCards = new ArrayList<>();

        for(int i = 1; i <= INITIAL_CARDS; i++)
            assistantCards.add(new DrawableAssistantCard(X_DIMENSION / INITIAL_CARDS - 2 * SPACING, i, TYPE, updater));

        // Translate them
        for(int i = 0; i < INITIAL_CARDS; i++)
        {
            assistantCards.get(i).translate(new Point3D(i * X_DIMENSION / INITIAL_CARDS - X_DIMENSION/2 + position.getX() + X_DIMENSION / (INITIAL_CARDS * 2),
                    position.getY(), position.getZ()));
        }
    }

    @Override
    public void addToGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableAssistantCollection] Null group");

        // If the group is not null i subscribe every card
        for(DrawableAssistantCard card : assistantCards)
            card.addToGroup(group);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableAssistantCollection] Null group");

        // If the group is not null i un subscribe every card
        for(DrawableAssistantCard card : assistantCards)
            card.removeFromGroup(group);
    }

    // This method does nothing, i want only ambient light
    @Override
    public void subscribeToPointLight(PointLight light) {}

    @Override
    public void subscribeToAmbientLight(AmbientLight light)
    {
        if(light == null)
            throw new NullPointerException("[DrawableAssistantCollection] Null ambient light");

        // If the light is not null i subscribe every card
        for(DrawableAssistantCard card : assistantCards)
            card.subscribeToAmbientLight(light);
    }

    // This method does nothing, i want only ambient light
    @Override
    public void unsubscribeFromPointLight(PointLight light) {}

    @Override
    public void unsubscribeFromAmbientLight(AmbientLight light)
    {
        if(light == null)
            throw new NullPointerException("[DrawableAssistantCollection] Null ambient light");

        // If the light is not null i subscribe every card
        for(DrawableAssistantCard card : assistantCards)
            card.unsubscribeFromAmbientLight(light);
    }

    @Override
    public void enableVisibility() {

    }

    @Override
    public void disableVisibility() {

    }

    @Override
    public void translate(Point3D point)
    {
        if(point == null)
            throw new NullPointerException("[DrawableAssistantCollection] Null point");

        position = point;

        // Translate them
        for(int i = 0; i < INITIAL_CARDS; i++)
        {
            assistantCards.get(i).translate(new Point3D(i * X_DIMENSION / INITIAL_CARDS - X_DIMENSION/2 + position.getX() + X_DIMENSION / (INITIAL_CARDS * 2),
                    position.getY(), position.getZ()));
        }
    }

    // This method does nothing, the collection doesn't rotate
    @Override
    public void addRotation(Rotate rotation) {}

    @Override
    public Point3D getPosition() { return position; }
}
