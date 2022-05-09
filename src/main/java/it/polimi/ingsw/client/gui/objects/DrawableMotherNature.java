package it.polimi.ingsw.client.gui.objects;

import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

import javax.swing.text.html.HTMLDocument;

/**
 * This class represents the drawable object of mother nature
 * aka 3 spheres one on top of another
 */
public class DrawableMotherNature
{

    /**
     * Number of the spheres that compose mother nature
     */
    private final int NUMBER_OF_SPHERES;

    /**
     * Initial radius
     */
    private final int STARTING_RADIUS;

    /**
     * Delta of radius between a sphere and the next one
     */
    private final int DELTA_RADIUS;

    /**
     * Set of spheres that compose mother nature
     */
    private Sphere spheres[];

    /**
     * Constructor
     */
    public DrawableMotherNature(int number_of_spheres, int starting_radius, int delta_radius)
    {
        if(number_of_spheres < 0 || starting_radius < 0 || delta_radius < 0)
            throw new IllegalArgumentException("[DrawableMotherNature] Invalid mother nature dimensions");

        if(starting_radius < (number_of_spheres - 1) * delta_radius)
            throw new IllegalArgumentException("[DrawableMotherNature] The spheres disappear before the end");

        // Create the material which will color the spheres
        PhongMaterial material = new PhongMaterial();
        material.setSpecularColor(Color.WHITE);
        material.setDiffuseColor(Color.valueOf("f0722a"));

        // Set Mother Nature appearance
        NUMBER_OF_SPHERES = number_of_spheres;
        STARTING_RADIUS = starting_radius;
        DELTA_RADIUS = delta_radius;

        // Create the array of spheres
        spheres = new Sphere[NUMBER_OF_SPHERES];

        // Actually create the spheres and add them
        for(int i = 0; i < spheres.length; i++)
        {
            spheres[i] = new Sphere();
            spheres[i].setRadius(STARTING_RADIUS - i * DELTA_RADIUS);
            spheres[i].setMaterial(material);
            spheres[i].setTranslateY(-(i * STARTING_RADIUS - i * DELTA_RADIUS));
        }
    }

    /**
     * Adds all the mother nature spheres inside the group vision
     * @param group The root scene
     */
    public void addToGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableMotherNature] Null group scene");

        // Add all the spheres to the root
        for(Sphere sphere : spheres)
            group.getChildren().add(sphere);
    }

    /**
     * Position setters
     */

    public void setX(float x)
    {
        for(Sphere sphere : spheres)
            sphere.translateXProperty().set(x);
    }

    public void setY(float y)
    {
        for(int i = 0; i < spheres.length; i++)
            spheres[i].setTranslateY(y - (i * STARTING_RADIUS - i * DELTA_RADIUS));
    }

    public void setZ(float z)
    {
        for(Sphere sphere : spheres)
            sphere.translateZProperty().set(z);
    }
}
