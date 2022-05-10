package it.polimi.ingsw.client.gui.objects;

import javafx.scene.Group;
import javafx.scene.PointLight;

public interface DrawableObject
{
    /**
     * Method to register the object to a group
     */
    void addToGroup(Group group);

    /**
     * Method to translate the object in X, Y and Z axes
     */
    void translate(float x, float y, float z);

    /**
     * Method to subscribe to an eventual point light if necessary
     */
    void subscribeToLight(PointLight light);

    /**
     * Method called to update every object animation if there is ibe
     */
    void updateAnimation();
}
