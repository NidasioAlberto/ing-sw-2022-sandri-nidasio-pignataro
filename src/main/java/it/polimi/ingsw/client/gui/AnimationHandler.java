package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.objects.DrawableObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a collection of drawable objects called
 * every CONSTANT time in milliseconds to update the animation
 * status.
 */
public class AnimationHandler
{
    /**
     * Update period
     */
    private final int PERIOD;

    /**
     * Collection of drawableObjects
     */
    private List<DrawableObject> objects;

    /**
     * Updater
     */
    private Timeline updater;

    /**
     * Constructor
     * @param period Time in millis that the object waits before updating
     * the next animations
     */
    public AnimationHandler(int period)
    {
        if(period <= 0)
            throw new IllegalArgumentException("[AnimationHandler] Invalid period");

        // Assign the constant
        PERIOD = period;

        // Create the collection
        objects = new ArrayList<>();

        // Create the updater object
        updater = new Timeline(new KeyFrame(Duration.millis(PERIOD),
                new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                // To handle the animations update i call every drawable object
                for (DrawableObject object : objects)
                    object.updateAnimation();
            }
        }));

        // Set the cycle count to infinite
        updater.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Subscribe to the updater
     */
    public void subscribeObject(DrawableObject object)
    {
        if(object == null)
            throw new NullPointerException("[AnimationHandler] Null object");
        if(objects.contains(object))
            throw new IllegalArgumentException("[AnimationHandler] Already contained object");

        // If all goes correctly i add the object
        objects.add(object);
    }

    /**
     * Unsubscribe to the updater
     */
    public void unsubscribeObject(DrawableObject object)
    {
        if(object == null)
            throw new NullPointerException("[AnimationHandler] Null object");

        // IF all goes correctly i remove the object
        objects.remove(object);
    }

    /**
     * Starts and stops the updater
     */
    public void start() { updater.play(); }
    public void stop() { updater.stop(); }
}
