package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.objects.DrawableObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This singleton class is used by the components of the gui to inhibit
 * the mouse visibility of all the components or reactivate the visibility.
 * This is done because when the user is dragging a drawable object around
 * it is necessary that all the others are transparent to the mouse.
 * If not deactivated the visibility, the dragged object would go
 * around with spikes in other directions because of not deactivated visibility.
 */
public class MouseVisibilityHandler
{
    /**
     * Instance to be returned as singleton
     */
    private static MouseVisibilityHandler instance;

    /**
     * Collection of drawable objects to trigger
     */
    private List<DrawableObject> objects;

    /**
     * Private constructor
     */
    private MouseVisibilityHandler() { objects = new ArrayList<>(); }

    /**
     * Enable all the visibility
     */
    public void enable()
    {
        for(DrawableObject object : objects)
            object.enableVisibility();
    }

    /**
     * Disable all the visibility
     */
    public void disable()
    {
        for(DrawableObject object : objects)
            object.enableVisibility();
    }

    /**
     * Subscribe to the singleton
     */
    public void subscribe(DrawableObject object)
    {
        if(object == null)
            throw new NullPointerException("[MouseVisibilityHandler] Null object");
        if(objects.contains(object))
            throw new IllegalArgumentException("[MouseVisibilityHandler] Already present object");

        // If all goes well i add the object
        objects.add(object);
    }

    /**
     * Instance getter
     */
    public static MouseVisibilityHandler getInstance()
    {
        if(instance == null)
            instance = new MouseVisibilityHandler();
        return instance;
    }
}
