package it.polimi.ingsw.client.gui.objects;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.effect.Light;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

import javax.swing.text.html.HTMLDocument;

/**
 * This class represents the drawable object of mother nature
 * aka 3 spheres one on top of another
 */
public class DrawableMotherNature extends DrawableObject
{

    /**
     * Number of the spheres that compose mother nature
     */
    private final int NUMBER_OF_SPHERES;

    /**
     * Initial radius
     */
    private final float STARTING_RADIUS;

    /**
     * Delta of radius between a sphere and the next one
     */
    private final float DELTA_RADIUS;

    /**
     * Set of spheres that compose mother nature
     */
    private Sphere spheres[];

    /**
     * Constructor
     */
    public DrawableMotherNature(int number_of_spheres, float starting_radius, float delta_radius)
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

    @Override
    public void addToGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableMotherNature] Null group scene");

        // Add all the spheres to the root
        for(Sphere sphere : spheres)
            group.getChildren().add(sphere);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableMotherNature] Null group scene");

        // Remove all the spheres
        for(Sphere sphere : spheres)
            group.getChildren().remove(sphere);
    }

    @Override
    public void subscribeToLight(PointLight light)
    {
        for(Sphere sphere : spheres)
            light.getScope().add(sphere);
    }

    @Override
    public void updateAnimation()
    {

    }

    /**
     * Position setters
     */
    @Override
    public void translate(Point3D point)
    {
        setX(point.getX());
        setY(point.getY());
        setZ(point.getZ());
    }

    public void setX(double x)
    {
        for(Sphere sphere : spheres)
            sphere.translateXProperty().set(x);
    }

    public void setY(double y)
    {
        for(int i = 0; i < spheres.length; i++)
            spheres[i].setTranslateY(y - (i * STARTING_RADIUS - i * DELTA_RADIUS));
    }

    public void setZ(double z)
    {
        for(Sphere sphere : spheres)
            sphere.translateZProperty().set(z);
    }

    @Override
    public Point3D getPosition() { return new Point3D(spheres[0].getTranslateX(), spheres[0].getTranslateY(), spheres[0].getTranslateZ()); }
}
