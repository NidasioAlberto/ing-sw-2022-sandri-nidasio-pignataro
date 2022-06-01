package it.polimi.ingsw.client.gui;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class UpdatesHandler
{
    /**
     * Update period
     */
    private final int PERIOD;

    /**
     * Thread safe list of all the lambdas to update the client sent by the server
     */
    private List<Runnable> updates;

    /**
     * Updater
     */
    private Timeline updater;

    /**
     * Constructor
     * 
     * @param period Time in millis that the object waits before the new update
     */
    public UpdatesHandler(int period)
    {
        if (period <= 0)
            throw new IllegalArgumentException("[UpdatesHandler] Invalid time period");

        // Assign the period
        PERIOD = period;

        // Create the collection
        updates = new ArrayList<>();

        // Create the updater
        updater = new Timeline(new KeyFrame(Duration.millis(PERIOD), new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                // To handle the update i run the lambda
                if (updates.size() > 0)
                {
                    // Call the lambda
                    updates.get(0).run();

                    // Remove the update from the list
                    updates.remove(0);
                }
            }
        }));

        // Set the cycle count to infinite
        updater.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Method to subscribe the update function
     */
    public void subscribeUpdate(Runnable lambda)
    {
        if (lambda == null)
            throw new NullPointerException("[UpdatesHandler] Null lambda function");

        // Add the lambda to the list
        updates.add(lambda);
    }

    /**
     * Starts and stops the handler
     */
    public void start()
    {
        updater.play();
    }

    public void stop()
    {
        updater.stop();
    }
}
