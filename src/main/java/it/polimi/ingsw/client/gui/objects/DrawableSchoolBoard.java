package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.ActionTranslator;
import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.client.gui.objects.types.ProfessorType;
import it.polimi.ingsw.client.gui.objects.types.StudentType;
import it.polimi.ingsw.client.gui.objects.types.TowerType;
import it.polimi.ingsw.model.SchoolBoard;
import it.polimi.ingsw.model.SchoolColor;
import it.polimi.ingsw.model.TowerColor;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

import java.util.*;

public class DrawableSchoolBoard extends DrawableObject
{
    // Scaling factor between x and y dimensions to maintain the proportions
    public static final float SCALE_FACTOR = 2.30f;

    /**
     * Positioning constants
     */
    private static final double DINING_X_STEP = 0.04763;
    private static final double DINING_Y_STEP = 0.07143;
    private static final double ENTRANCE_X_STEP = 0.05857;
    private static final double TOWER_X_STEP = 0.07714;
    private static final double TOWER_Y_STEP = 0.07143;
    private static final double FIRST_X_DINING = -0.29286;
    private static final double FIRST_X_PROFESSOR = 0.22857;
    private static final double FIRST_X_ENTRANCE = -0.44429;
    private static final double FIRST_Y_ENTRANCE = 0.14286;
    private static final double FIRST_X_TOWER = 0.33857;
    private static final double FIRST_Y_TOWER = 0.10714;
    private static final Map<SchoolColor, Double> HEIGHTS = new HashMap<>();

    /**
     * Reference Schoolboard arrays
     */
    private List<DrawableStudent> entrance;
    private List<DrawableTower> towers;
    private List<DrawableProfessor> professors;
    private Map<SchoolColor, List<DrawableStudent>> dining;

    /**
     * X Dimension constant
     */
    private final double X_DIMENSION;

    /**
     * Y Dimension constant
     */
    private final double Y_DIMENSION;

    /**
     * Box containing the SchoolBoard texture
     */
    private final Box box;

    /**
     * Collection of rotations to perform on the drawable objects
     */
    private List<Rotate> rotations;

    /**
     * SchoolBoard player name
     */
    private String playerName;

    /**
     * Constructor
     * 
     * @param x_dimension The x dimension of the board
     */
    public DrawableSchoolBoard(double x_dimension, String playerName, AnimationHandler updater)
    {
        super(updater);

        if (x_dimension < 0)
            throw new IllegalArgumentException("[DrawableSchoolBoard] X dimension less than 0");
        if (playerName == null)
            throw new NullPointerException("[DrawableSchoolBoard] Null player name");

        // Assign the parameters
        X_DIMENSION = x_dimension;
        Y_DIMENSION = x_dimension / SCALE_FACTOR;
        this.playerName = playerName;

        // Initialize all the reference model
        entrance = new ArrayList<>();
        professors = new ArrayList<>();
        towers = new ArrayList<>();
        dining = new HashMap<>();

        for (SchoolColor color : SchoolColor.values())
            dining.put(color, new ArrayList<>());

        // Create the rotation collection
        rotations = new ArrayList<>();

        // Create the map with reference heights
        HEIGHTS.put(SchoolColor.GREEN, 0.14286);
        HEIGHTS.put(SchoolColor.RED, 0.07143);
        HEIGHTS.put(SchoolColor.YELLOW, 0.0);
        HEIGHTS.put(SchoolColor.PINK, -0.07143);
        HEIGHTS.put(SchoolColor.BLUE, -0.14286);

        // Create the box
        this.box = new Box(X_DIMENSION, Y_DIMENSION, 0);

        // Create the correct texture
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(
                new Image(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("schoolboard.png"))));

        // Assign the texture
        box.setMaterial(material);

        // Rotate the box correctly
        box.getTransforms().add(new Rotate(90, new Point3D(1, 0, 0)));
        box.getTransforms().add(new Rotate(180, new Point3D(0, 0, 1)));

        // Set the node to mouse visible
        box.setMouseTransparent(false);

        // Set the mouse drag released checking if the area is dining or entrance
        box.setOnMouseDragReleased((event) -> {
            if (event.getX() >= box.getTranslateX() + X_DIMENSION * 3.5 / 10)
                ActionTranslator.getInstance().setDroppedOnItem("Entrance");
            else
                ActionTranslator.getInstance().setDroppedOnItem("Dining");

            // Send the message
            ActionTranslator.getInstance().execute();
        });

        // At the end if the updater != null i add the box to it
        if (this.updater != null)
            this.updater.subscribeObject(this);
    }

    @Override
    public void addToGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableSchoolBoard] Null group");

        // Add the box to the group
        group.getChildren().add(box);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableSchoolBoard] Null group");

        // Remove the box from the group
        group.getChildren().remove(box);
    }

    // This method does nothing because i don't want reflections
    @Override
    public void subscribeToPointLight(PointLight light)
    {}

    @Override
    public void subscribeToAmbientLight(AmbientLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableSchoolBoard] Null ambient light");

        // Add the box to the light
        light.getScope().add(box);
    }

    // Unsubscribe all the payloads
    @Override
    public void unsubscribeFromPointLight(PointLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableSchoolBoard] Null point light");

        // Unsubscribe all the components
        for (DrawableStudent student : entrance)
            student.unsubscribeFromPointLight(light);

        for (SchoolColor color : dining.keySet())
            for (DrawableStudent student : dining.get(color))
                student.unsubscribeFromPointLight(light);

        for (DrawableTower tower : towers)
            tower.unsubscribeFromPointLight(light);
    }

    @Override
    public void unsubscribeFromAmbientLight(AmbientLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableSchoolBoard] Null ambient light");

        // Remove the box from the light
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

    /**
     * Method to update this board to match the passed one
     * 
     * @param board The goal board
     */
    public void update(SchoolBoard board, Group group, PointLight light)
    {
        // Entrance
        for (SchoolColor color : SchoolColor.values())
        {
            // Calculate the actual number of students
            long numBoard = board.getStudentsInEntrance().stream().filter((s) -> s.getColor() == color).count();
            long num = entrance.stream().filter((s) -> s.getType().name().equals(color.name())).count();

            // Remove or add the difference
            if (num > numBoard)
            {
                for (long i = numBoard; i < num; i++)
                    this.removeStudentFromEntrance(color, group, light);
            } else if (numBoard > num)
            {
                for (long i = num; i < numBoard; i++)
                    this.addStudentToEntrance(color, group, light);
            }
        }

        // Dining
        for (SchoolColor color : SchoolColor.values())
        {
            long numBoard = board.getStudentsNumber(color);
            long num = dining.get(color).size();

            // Remove or add the difference
            if (num > numBoard)
            {
                for (long i = numBoard; i < num; i++)
                    this.removeStudentFromDining(color, group, light);
            } else if (numBoard > num)
            {
                for (long i = num; i < numBoard; i++)
                    this.addStudentToDining(color, group, light);
            }
        }

        // Towers
        {
            long numBoard = board.getTowers().size();
            long num = towers.size();

            // Remove or add the difference
            if (num > numBoard)
            {
                for (long i = numBoard; i < num; i++)
                    this.removeTower(group, light);
            } else if (numBoard > num)
            {
                for (long i = num; i < numBoard; i++)
                    this.addTower(board.getTowerColor(), group, light);
            }
        }
    }

    /**
     * Adds a student to the dining room
     * 
     * @param color The student color to be added
     * @param group The group to which add the student
     * @param light The point light to which subscribe the new student
     */
    public void addStudentToDining(SchoolColor color, Group group, PointLight light)
    {
        // Check that the student can be there
        if (dining.get(color).size() >= 10)
            return;

        // Create the new student of the same color
        DrawableStudent student = new DrawableStudent(StudentType.valueOf(color.name()), updater);

        // The coordinates without rotations
        Point3D coordinates = new Point3D(FIRST_X_DINING * X_DIMENSION + dining.get(color).size() * DINING_X_STEP * X_DIMENSION, 0,
                HEIGHTS.get(color) * X_DIMENSION);

        // For all the present rotations i rotate the student
        for (Rotate rotation : rotations)
        {
            coordinates = rotation.transform(coordinates);

            // Rotate also the component with a little correction in axis
            student.addRotation(
                    new Rotate(rotation.getAngle(), new Point3D(-rotation.getAxis().getX(), rotation.getAxis().getY(), -rotation.getAxis().getZ())));
        }

        // Translate the student in the correct position
        student.translate(new Point3D(coordinates.getX() + box.getTranslateX(), coordinates.getY() + box.getTranslateY(),
                coordinates.getZ() + box.getTranslateZ()));

        // Set the student to not draggable
        student.setDraggable(false);

        // Add the student to the model
        dining.get(color).add(student);

        // Add the student to the group and to the lights
        student.addToGroup(group);
        student.subscribeToPointLight(light);
    }

    /**
     * Removes a student from the dining room
     * 
     * @param color The color to be removed
     * @param group The group from which remove the student
     * @param light The light from which unsubscribe the student
     */
    public void removeStudentFromDining(SchoolColor color, Group group, PointLight light)
    {
        // I do nothing if the size of students of that color is 0
        if (dining.get(color).size() == 0)
            return;

        // Take the student instance
        DrawableStudent student = dining.get(color).get(dining.get(color).size() - 1);

        // Remove the student from the group
        student.removeFromGroup(group);

        // Unsubscribe the student from the light
        student.unsubscribeFromPointLight(light);

        // Remove the instance from the list
        dining.get(color).remove(student);
    }

    /**
     * Adds a professor to the schoolboard
     * 
     * @param color The professor color
     * @param group The group to which add the professor
     * @param light The point light to which add the professor
     */
    public void addProfessor(SchoolColor color, Group group, PointLight light)
    {
        // Check if that professor is already inside the schoolboard
        for (DrawableProfessor professor : professors)
        {
            // If there is a copy i don't add the professor
            if (professor.getType().name().equals(color.name()))
                return;
        }

        // Create the new professor
        DrawableProfessor professor = new DrawableProfessor(ProfessorType.valueOf(color.name()), updater);

        // The coordinates without rotations
        Point3D coordinates = new Point3D(FIRST_X_PROFESSOR * X_DIMENSION, 0, HEIGHTS.get(color) * X_DIMENSION);

        // For all the present rotations i rotate the professor
        for (Rotate rotation : rotations)
        {
            coordinates = rotation.transform(coordinates);

            // Rotate also the component with a little correction
            professor.addRotation(
                    new Rotate(rotation.getAngle(), new Point3D(rotation.getAxis().getZ(), rotation.getAxis().getY(), -rotation.getAxis().getX())));
        }

        // Translate the professor where it should be
        professor.translate(new Point3D(coordinates.getX() + box.getTranslateX(), coordinates.getY() + box.getTranslateY(),
                coordinates.getZ() + box.getTranslateZ()));

        // Add the professor to the model
        professors.add(professor);

        // Add the Professor to the group and to the light
        professor.addToGroup(group);
        professor.subscribeToPointLight(light);
    }

    /**
     * Removes a professor from the schoolboard
     * 
     * @param color The color to be removed
     * @param group The group from which remove the professor
     * @param light The light from which unsubscribe the professor
     */
    public void removeProfessor(SchoolColor color, Group group, PointLight light)
    {
        // I do nothing if there is no professor of that color
        if (professors.stream().filter((p) -> p.getType().name().equals(color.name())).count() == 0)
            return;

        // Take the professor instance
        DrawableProfessor professor = professors.stream().filter((p) -> p.getType().name().equals(color.name())).findFirst().get();

        // Remove the professor from the group
        professor.removeFromGroup(group);

        // Unsubscribe the professor from the light
        professor.unsubscribeFromPointLight(light);

        // Remove the professor from the collection
        professors.remove(professor);
    }

    /**
     * Adds a student to the entrance
     * 
     * @param color The student color
     * @param group The group to which add the student
     * @param light The point light to which add the student
     */
    public void addStudentToEntrance(SchoolColor color, Group group, PointLight light)
    {
        // Check if it is actually possible
        if (entrance.size() >= 9)
            return;

        // Create the new student
        DrawableStudent student = new DrawableStudent(StudentType.valueOf(color.name()), updater);

        // The coordinates without rotations
        Point3D coordinates = new Point3D(FIRST_X_ENTRANCE * X_DIMENSION + ((entrance.size() + 1) % 2) * ENTRANCE_X_STEP * X_DIMENSION, 0,
                FIRST_Y_ENTRANCE * X_DIMENSION - (int) ((entrance.size() + 1) / 2) * DINING_Y_STEP * X_DIMENSION);

        // For all the present rotations i rotate the student
        for (Rotate rotation : rotations)
        {
            coordinates = rotation.transform(coordinates);

            // Rotate also the component with a little correction in axis
            student.addRotation(
                    new Rotate(rotation.getAngle(), new Point3D(-rotation.getAxis().getX(), rotation.getAxis().getY(), -rotation.getAxis().getZ())));
        }

        // Translate the student to the correct position
        student.translate(new Point3D(coordinates.getX() + box.getTranslateX(), coordinates.getY() + box.getTranslateY(),
                coordinates.getZ() + box.getTranslateZ()));

        // Add the student to the model
        entrance.add(student);

        // Add the student to the group and the light
        student.addToGroup(group);
        student.subscribeToPointLight(light);
    }

    /**
     * Removes a student from the entrance
     * 
     * @param color The student color
     * @param group The group to which remove the student
     * @param light The light to which unsubscribe the student
     */
    public void removeStudentFromEntrance(SchoolColor color, Group group, PointLight light)
    {
        // If the entrance doesn't have any student of that color i do nothing
        if (entrance.stream().filter((s) -> s.getType().name().equals(color.name())).count() == 0)
            return;

        // Take the student to remove
        DrawableStudent student = entrance.stream().filter((s) -> s.getType().name().equals(color.name())).findFirst().get();

        // Remove the student from the group
        student.removeFromGroup(group);

        // Remove the student from the light
        student.unsubscribeFromPointLight(light);

        // Remove the student from the collection
        entrance.remove(student);

        // Update positionings
        for (int i = 0; i < entrance.size(); i++)
        {
            // Create the corresponding coordinates
            Point3D coordinates = new Point3D(FIRST_X_ENTRANCE * X_DIMENSION + ((i + 1) % 2) * ENTRANCE_X_STEP * X_DIMENSION, 0,
                    FIRST_Y_ENTRANCE * X_DIMENSION - (int) ((i + 1) / 2) * DINING_Y_STEP * X_DIMENSION);

            // Rotate the coordinates
            for (Rotate rotation : rotations)
                coordinates = rotation.transform(coordinates);

            // Translate the student to the correct position
            entrance.get(i).translate(new Point3D(coordinates.getX() + box.getTranslateX(), coordinates.getY() + box.getTranslateY(),
                    coordinates.getZ() + box.getTranslateZ()));
        }
    }

    /**
     * Adds the tower to the schoolboard
     * 
     * @param color The color of the tower
     * @param group The group to which add the tower
     * @param light The point light to which add the tower
     */
    public void addTower(TowerColor color, Group group, PointLight light)
    {
        // Verify that it could actually be done
        if (towers.size() >= 8)
            return;

        // Create the new tower
        DrawableTower tower = new DrawableTower(TowerType.valueOf(color.name()), updater);

        // The coordinates without rotations
        Point3D coordinates = new Point3D(FIRST_X_TOWER * X_DIMENSION + (towers.size() % 2) * TOWER_X_STEP * X_DIMENSION, 0,
                FIRST_Y_TOWER * X_DIMENSION - (int) (towers.size() / 2) * TOWER_Y_STEP * X_DIMENSION);

        // For all the present rotations i rotate the tower
        for (Rotate rotation : rotations)
        {
            coordinates = rotation.transform(coordinates);

            // Rotate also the component
            tower.addRotation(rotation);
        }

        // Translate the tower to the correct position
        tower.translate(new Point3D(coordinates.getX() + box.getTranslateX(), coordinates.getY() + box.getTranslateY(),
                coordinates.getZ() + box.getTranslateZ()));

        // Add the tower to the model
        towers.add(tower);

        // Add the tower to the group and to the light
        tower.addToGroup(group);
        tower.subscribeToPointLight(light);
    }

    /**
     * Method to remove a tower
     * 
     * @param group The group to which remove the tower
     * @param light The light to which unsubscribe the tower
     */
    public void removeTower(Group group, PointLight light)
    {
        // If no tower can be removed i don't do that
        if (towers.size() == 0)
            return;

        // Take the tower to be removed
        DrawableTower tower = towers.get(towers.size() - 1);

        // Unsubscribe from grup
        tower.removeFromGroup(group);

        // Unsubscribe from light
        tower.unsubscribeFromPointLight(light);

        // Remove the tower from the collection
        towers.remove(towers.size() - 1);
    }

    /**
     * Position setters need to be synchronized
     */
    @Override
    public synchronized void translate(Point3D point)
    {
        if (point == null)
            throw new NullPointerException("[DrawableSchoolBoard] Null point");

        // Set the box position
        box.translateXProperty().set(point.getX());
        box.translateYProperty().set(point.getY());
        box.translateZProperty().set(point.getZ());
    }

    @Override
    public void addRotation(Rotate rotation)
    {
        // The idea is to add the rotation to the box and memorize it
        // so that students, towers and professors added after are affected
        // TODO consider an update for the present objects

        if (rotation == null)
            throw new NullPointerException("[DrawableSchoolBoard] Null rotation");

        // Add the transformation to the box (little axis swap because of box as plane)
        box.getTransforms()
                .add(new Rotate(-rotation.getAngle(), new Point3D(rotation.getAxis().getX(), rotation.getAxis().getZ(), rotation.getAxis().getY())));

        // Also i add the rotation to the list
        rotations.add(rotation);
    }

    @Override
    public Point3D getPosition()
    {
        return new Point3D(box.getTranslateX(), box.getTranslateY(), box.getTranslateZ());
    }

    public String getPlayerName()
    {
        return playerName;
    }
}
