package it.polimi.ingsw.client.gui.objects;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

import java.util.Objects;

public class DrawableStudent extends DrawableObject
{

    /**
     * Radius of the cylinder
     */
    private final double RADIUS;

    /**
     * Height of the cylinder
     */
    private final double HEIGHT;

    /**
     * Student type (color)
     */
    private final StudentType TYPE;

    /**
     * Main cylinder that represents the student
     */
    private Cylinder cylinder;

    /**
     * Constructor
     * @param radius Radius of the cylinder
     * @param height Height of the cylinder
     */
    public DrawableStudent(double radius, double height, StudentType type)
    {
        if(radius <= 0)
            throw new IllegalArgumentException("[DrawableStudent] Less or equal to 0 cylinder radius");
        if(height <= 0)
            throw new IllegalArgumentException("[DrawableStudent] Less or equal to 0 cylinder height");
        if(type == null)
            throw new NullPointerException("[DrawableStudent] Null student type");

        // Assign the constant parameters
        RADIUS = radius;
        HEIGHT = height;
        TYPE = type;

        // Create the cylinder 3D object
        cylinder = new Cylinder(RADIUS, HEIGHT);

        // Create the correct texture material
        PhongMaterial material = new PhongMaterial();

        // Depending on the color i load a different texture
        material.setDiffuseMap(new Image(Objects.requireNonNull(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(TYPE.getFilename()))));

        // Apply the material
        cylinder.setMaterial(material);
    }

    @Override
    public void addToGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableStudent] Null group");

        // Add the cylinder to the group
        group.getChildren().add(cylinder);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableStudent] Null group");

        // Remove the cylinder from the group
        group.getChildren().remove(cylinder);
    }

    @Override
    public void subscribeToLight(PointLight light)
    {
        // The students should be under the light so i subscribe it
        light.getScope().add(cylinder);
    }

    @Override
    public void translate(Point3D point)
    {
        cylinder.translateXProperty().set(point.getX());
        cylinder.translateYProperty().set(point.getY());
        cylinder.translateZProperty().set(point.getZ());
    }

    @Override
    public Point3D getPosition()
    {
        return new Point3D(cylinder.getTranslateX(), cylinder.getTranslateY(), cylinder.getTranslateZ());
    }
}
