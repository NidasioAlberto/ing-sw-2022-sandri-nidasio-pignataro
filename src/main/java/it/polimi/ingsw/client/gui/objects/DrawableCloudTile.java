package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.ActionTranslator;
import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.client.gui.objects.types.CloudType;
import it.polimi.ingsw.client.gui.objects.types.StudentType;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DrawableCloudTile extends DrawableObject
{
    /**
     * The square box dimension
     */
    private final double DIMENSION;

    /**
     * Cloud tile in-game number
     */
    private int NUMBER;

    /**
     * Type of cloud tile
     */
    private final CloudType TYPE;

    /**
     * Box containing the cloud texture
     */
    private final Box box;

    /**
     * List of rotations applied
     */
    private List<Rotate> rotations;

    /**
     * Collection of students
     */
    private List<DrawableStudent> students;

    /**
     * Constructor
     * 
     * @param updater The animation updater object
     */
    public DrawableCloudTile(double dimension, CloudType type, AnimationHandler updater)
    {
        super(updater);

        if (dimension <= 0)
            throw new IllegalArgumentException("[DrawableCloudTile] Invalid box dimension");
        if (type == null)
            throw new NullPointerException("[DrawableCloudTile] Null cloud type");

        // Assign all the constants
        DIMENSION = dimension;
        TYPE = type;

        // Create the rotations collection
        rotations = new ArrayList<>();

        // Create the students collection
        students = new ArrayList<>();

        // Create the box with the corresponding texture
        box = new Box(DIMENSION, DIMENSION, 0);

        // Create the texture material
        PhongMaterial material = new PhongMaterial();

        // Open the correct file
        material.setDiffuseMap(
                new Image(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(type.getFilename()))));

        // Set the material with the texture
        box.setMaterial(material);

        // Rotate the island by 90 degrees in the x axis and 180 in the y axis
        box.getTransforms().add(new Rotate(90, new Point3D(1, 0, 0)));
        box.getTransforms().add(new Rotate(180, new Point3D(0, 0, 1)));

        // Set the tile mouse transparent
        box.setMouseTransparent(false);

        // Set the click event
        box.setOnMouseClicked((event) -> {
            // Ensure that the click happened only one time
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 1)
            {
                // Set the selected card and call the action
                ActionTranslator.getInstance().setDraggedItem("CloudTile");

                // Set the cloud number
                ActionTranslator.getInstance().selectCloudTile(NUMBER);

                // Execute the action
                ActionTranslator.getInstance().execute();
            }
        });

        // Set also the dragged over event because of reset purposes in case of a wrong action
        box.setOnMouseDragReleased((event) -> {
            // Set the droppedOn target
            ActionTranslator.getInstance().setDroppedOnItem("CloudTile");

            // Execute the reset
            ActionTranslator.getInstance().execute();
        });
    }

    /**
     * Sets the in-game cloud tile number >= 0
     * 
     * @param number The number to be set
     */
    public void setNumber(int number)
    {
        if (number < 0)
            throw new IllegalArgumentException("[DrawableCloudTile] Illegal in game number");
        this.NUMBER = number;
    }

    /**
     * Method to add a student to the cloud tile
     * 
     * @param type The type of student to be added
     */
    public void addStudent(StudentType type, Group group, PointLight light)
    {
        // If there is no space left i don't put the student
        if (students.size() >= TYPE.getPositionsNumber())
            return;

        // Create the new student
        DrawableStudent student = new DrawableStudent(type, updater);

        // Create the coordinates without rotations
        Point2D position = TYPE.getPositionAt(students.size());
        Point3D coordinates = new Point3D(position.getX() * DIMENSION, -1, position.getY() * DIMENSION);

        // For all the rotations apply them
        for (Rotate rotation : rotations)
            coordinates = rotation.transform(coordinates);

        // Translate the student
        student.translate(coordinates.add(new Point3D(box.getTranslateX(), box.getTranslateY(), box.getTranslateZ())));

        // Make the student invisible to mouse (We don't want drag and drop)
        student.disableVisibility();

        // Add the student to the model
        students.add(student);

        // Add the student to the group and to the lights
        student.addToGroup(group);
        student.subscribeToPointLight(light);
    }

    /**
     * Method to clear the cloud tile
     * 
     * @param group The group to which unsubscribe all the students
     * @param light The light to which unsubscribe all the students
     */
    public void clear(Group group, PointLight light)
    {
        for (DrawableStudent student : students)
        {
            student.unsubscribeFromPointLight(light);
            student.removeFromGroup(group);
            // Unsubscribe from updater
            updater.unsubscribeObject(student);
        }
        // At the end i clear the drawable list
        students.clear();
    }

    @Override
    public void addToGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableCloudTile] Null group");

        // If all is correct i subscribe to the group
        group.getChildren().add(box);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableCloudTile] Null group");

        // If all is correct i unsubscribe from the group
        group.getChildren().remove(box);
    }

    // This method does nothing, i want to subscribe to the ambient light
    @Override
    public void subscribeToPointLight(PointLight light)
    {}

    @Override
    public void subscribeToAmbientLight(AmbientLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableCloudTile] Null ambient light");

        // If all is correct i subscribe the box to the light
        light.getScope().add(box);
    }

    // Unsubscribe all the potential students
    @Override
    public void unsubscribeFromPointLight(PointLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableCloudTile] Null point light");

        for (DrawableStudent student : students)
            student.unsubscribeFromPointLight(light);
    }

    @Override
    public void unsubscribeFromAmbientLight(AmbientLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableCloudTile] Null light");

        light.getScope().remove(box);
    }

    @Override
    public void enableVisibility()
    {

    }

    @Override
    public void disableVisibility()
    {

    }

    @Override
    public void translate(Point3D point)
    {
        if (point == null)
            throw new NullPointerException("[DrawableCloudTile] Null 3D point");

        // If all is correct i translate the box
        box.translateXProperty().set(point.getX());
        box.translateYProperty().set(point.getY());
        box.translateZProperty().set(point.getZ());

        // Translate all the students
        for (int i = 0; i < students.size(); i++)
        {
            // Calculate the translation position
            Point2D position = TYPE.getPositionAt(i);
            Point3D coordinates = new Point3D(position.getX() * DIMENSION, -1, position.getY() * DIMENSION);

            // Actually translate the student
            students.get(i).translate(coordinates.add(point));
        }
    }

    @Override
    public void addRotation(Rotate rotation)
    {
        if (rotation == null)
            throw new NullPointerException("[DrawableCloudTile] Null rotation");

        // If all is correct i add the rotation to the box
        rotations.add(rotation);
        box.getTransforms().add(rotation);
    }

    @Override
    public Point3D getPosition()
    {
        return new Point3D(box.getTranslateX(), box.getTranslateY(), box.getTranslateZ());
    }
}
