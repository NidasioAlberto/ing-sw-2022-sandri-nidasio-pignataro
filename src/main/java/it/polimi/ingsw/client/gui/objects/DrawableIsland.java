package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.ActionTranslator;
import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.client.gui.objects.types.IslandType;
import it.polimi.ingsw.client.gui.objects.types.StudentType;
import it.polimi.ingsw.client.gui.objects.types.TowerType;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.SchoolColor;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

import java.util.*;
import java.util.List;

public class DrawableIsland extends DrawableObject
{
    // Constants
    public static final float FLOATING_AMPLITUDE = 5;
    public static final float FLOATING_ANGULAR_VELOCITY = 1.5f;

    // This defines how much an eventual island payload should stay off the island itself
    public static final float HEIGHT_SPAN = 1.0f;

    /**
     * Island square dimensions
     */
    private final int DIMENSION;

    /**
     * In game island number
     */
    private int NUMBER;

    /**
     * Positioning constants
     */
    private final double X_TOWER = -0.17;
    private final double Y_TOWER = 0.25;
    private final Point2D studentPositions[] =
    {new Point2D(0, 0.120), new Point2D(0.125, 0.030), new Point2D(0.0875, -0.130), new Point2D(-0.0875, -0.130), new Point2D(-0.125, 0.030)};
    private final double X_MOTHER = 0.17;
    private final double Y_MOTHER = 0.25;
    private final double X_NO_ENTRY = 0;
    private final double Y_NO_ENTRY = 0.280;

    /**
     * Island draw type
     */
    private final IslandType TYPE;

    /**
     * Box containing the island texture
     */
    private Box box;

    /**
     * Island payloads
     */
    private DrawableTower tower;
    private DrawableNoEntryTile noEntryTile;
    private int towerCount;
    private int noEntryCount;
    // List of all the drawn students
    private List<DrawableStudent> drawnStudents;
    // List of all the student types inside the island, for counting purposes
    private List<StudentType> students;
    // Eventual mother nature on the island
    private DrawableMotherNature motherNature;

    /**
     * Animation angle (to which apply the Math.sin and float the island)
     */
    private float floatingAngle;

    /**
     * Constructor
     * 
     * @param dimension The square dimensions
     * @param type The texture island type
     */
    public DrawableIsland(int dimension, IslandType type, AnimationHandler updater)
    {
        super(updater);

        if (dimension < 0)
            throw new IllegalArgumentException("[DrawableIsland] Negative island dimensions");
        if (type == null)
            throw new NullPointerException("[DrawableIsland] Null island type pointer");

        // Set the island constants
        DIMENSION = dimension;
        TYPE = type;

        // Create the collection of students
        drawnStudents = new ArrayList<>();
        students = new ArrayList<>();

        // Set the initial tower count to 0
        towerCount = 0;

        // Set the initial noEntry count to 0
        noEntryCount = 0;

        // Random set the first angle
        floatingAngle = new Random().nextFloat(360);

        // Setup the box as square
        box = new Box(dimension, dimension, 0);

        // Create and setup the material with the texture
        PhongMaterial material = new PhongMaterial();

        // Depending on the island type i can open the file
        material.setDiffuseMap(
                new Image(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(TYPE.getFilename()))));

        // Set the material with the texture
        box.setMaterial(material);

        // Rotate the island by 90 degrees in the x axis and 180 in the y axis
        box.getTransforms().add(new Rotate(90, new Point3D(1, 0, 0)));
        box.getTransforms().add(new Rotate(180, new Point3D(0, 0, 1)));

        // Set the node to mouse transparent
        box.setMouseTransparent(false);

        // Set a little light when the mouse drags over
        box.setOnMouseDragEntered((event) -> {
            material.setDiffuseColor(Color.color(1, 1, 1, 0.9));
        });

        box.setOnMouseDragExited((event) -> {
            material.setDiffuseColor(Color.color(1, 1, 1, 1));
        });

        box.setOnMouseDragReleased((event) -> {
            // System.out.println(event.getEventType());
            material.setDiffuseColor(Color.color(1, 1, 1, 1));

            // Set the dropped on item
            ActionTranslator.getInstance().setDroppedOnItem("Island");

            // Set the island number
            ActionTranslator.getInstance().selectIsland(NUMBER);

            // Send the message
            ActionTranslator.getInstance().execute();
        });
    }

    /**
     * Method to keep up the graphic version with the model island. Also it puts or removes the tower
     * 
     * @param island Model island passed
     * @param pointLight The eventual point light to which subscribe the new students
     * @param group The eventual group to which add the new students
     */
    public void update(Island island, Group group, PointLight pointLight)
    {
        if (island == null)
            throw new NullPointerException("[DrawableIsland] Null island update");

        // For every student color i keep up with the number of students
        for (SchoolColor color : SchoolColor.values())
        {
            long updateStudents = island.getStudents().stream().filter((s) -> s.getColor() == color).count();
            long islandStudents = students.stream().filter((s) -> SchoolColor.valueOf(s.name()) == color).count();
            // If the update has more students of that color than this island i add the difference
            if (updateStudents > islandStudents)
            {
                for (int i = 0; i < updateStudents - islandStudents; i++)
                    this.addStudent(StudentType.valueOf(color.name()), group, pointLight);
            }

            // If the update has less students of that color than this island i remove them
            if (updateStudents < islandStudents)
            {
                for (int i = 0; i < islandStudents - updateStudents; i++)
                    this.removeStudent(StudentType.valueOf(color.name()), group, pointLight);
            }
        }

        // Check if there are towers
        if (island.getTowers().size() > 0)
        {
            // I need to add the correct tower
            if (tower == null)
                addTower(TowerType.valueOf(island.getTowers().get(0).getColor().name()), group, pointLight);

            // In case of different colors
            if (!tower.getType().name().equals(island.getTowers().get(0).getColor().name()))
            {
                // Remove the tower and add the correct one
                removeTower(group, pointLight);
                addTower(TowerType.valueOf(island.getTowers().get(0).getColor().name()), group, pointLight);
            }

            // Parity the number of towers
            towerCount = island.getTowers().size();
        } else if (tower != null)
        {
            // Remove graphically the tower
            removeTower(group, pointLight);

            // Reset the counter
            towerCount = 0;
        }

        // Check if there are some no entry tiles
        if (island.getNoEntryTiles() > 0)
        {
            // I need to add the no entry
            if (noEntryTile == null)
                addNoEntry(group, pointLight);

            // Paritythe number of no entry tiles
            noEntryCount = island.getNoEntryTiles();
        } else if (noEntryTile != null)
        {
            // I remove the existing no entry
            removeNoEntry(group, pointLight);

            // Reset the counter
            noEntryCount = 0;
        }
    }

    /**
     * Clears the island
     * 
     * @param light The light to which unsubscribe tower and students
     * @param group The group to which unsubscribe tower and students
     */
    public void clear(PointLight light, Group group)
    {
        // For all the students unsubscribe them
        for (DrawableStudent student : drawnStudents)
        {
            student.unsubscribeFromPointLight(light);
            student.removeFromGroup(group);
        }

        // Remove the tower if existing
        if (tower != null)
            removeTower(group, light);

        // Remove the noEntry if existing
        if (noEntryTile != null)
            removeNoEntry(group, light);

        // Clear the lists
        drawnStudents.clear();
        students.clear();
        tower = null;
        noEntryTile = null;
        towerCount = 0;
        noEntryCount = 0;
    }

    /**
     * Sets the in-game island number
     * 
     * @param number The island number >= 0
     */
    public void setNumber(int number)
    {
        if (number < 0)
            throw new IllegalArgumentException("[DrawableIsland] Invalid island number");

        this.NUMBER = number;
    }

    /**
     * Method to add a no Entry tile graphically
     * 
     * @param group The group to which subscribe the no Entry
     * @param light The light to which subscribe the no Entry
     */
    public void addNoEntry(Group group, PointLight light)
    {
        if (noEntryTile != null)
            return;

        // Create the drawable noEntry
        noEntryTile = new DrawableNoEntryTile(updater);

        // Translate the no Entry
        noEntryTile.translate(new Point3D(X_NO_ENTRY * DIMENSION, -HEIGHT_SPAN - 10, Y_NO_ENTRY * DIMENSION).add(getPosition()));

        // Make the no Entry invisible to mouse (we don't want drag and drop)
        noEntryTile.disableVisibility();

        // Add the noEntry to the group and light
        noEntryTile.addToGroup(group);
        noEntryTile.subscribeToPointLight(light);

        // Set the counter to 1
        noEntryCount = 1;
    }

    /**
     * Method to remove the no entry tile graphically
     * 
     * @param group The group from which unsubscribe the tile
     * @param light The light from which unsubscribe the tile
     */
    public void removeNoEntry(Group group, PointLight light)
    {
        // If already not present i do nothing
        if (noEntryTile == null)
            return;

        // Remove the noEntryTile from group
        noEntryTile.removeFromGroup(group);

        // Remove the noEntryTile from the light
        noEntryTile.unsubscribeFromPointLight(light);

        // Remove the object itself
        noEntryTile = null;

        // Set the counter to 0
        noEntryCount = 0;
    }

    /**
     * Metho to add a tower to the floating island
     * 
     * @param type The type of tower
     * @param group The vision group where to add the tower
     * @param light The point light to which subscribe the new tower
     */
    public void addTower(TowerType type, Group group, PointLight light)
    {
        // I add the tower only if there isn't already one
        if (tower != null)
            return;

        // Create the drawable tower
        tower = new DrawableTower(type, updater);

        // Translate the tower
        tower.translate(new Point3D(X_TOWER * DIMENSION + getPosition().getX(), -HEIGHT_SPAN, Y_TOWER * DIMENSION + getPosition().getY()));

        // Make the tower invisible to mouse (we don't want drag and drop)
        tower.disableVisibility();

        // Add the tower to group and point light
        tower.addToGroup(group);
        tower.subscribeToPointLight(light);

        // Set the counter to 1
        towerCount = 1;
    }

    /**
     * Removes the only tower
     */
    public void removeTower(Group group, PointLight light)
    {
        // If there is no tower i remove nothing
        if (tower == null)
            return;

        // Remove the tower from group
        tower.removeFromGroup(group);

        // Remove the tower from the light
        tower.unsubscribeFromPointLight(light);

        // Remove the object itself
        tower = null;

        // Set the counter to 0
        towerCount = 0;
    }

    /**
     * Method to add a student to the island
     * 
     * @param type The type of student to be added
     * @param group The group to which add the student
     * @param light The point light to which subscribe the student
     */
    public void addStudent(StudentType type, Group group, PointLight light)
    {
        // I add the student only if it is not null
        if (type == null)
            throw new NullPointerException("[DrawableIsland] Null student type");

        // I add graphically the student only if it is not already present
        if (!students.contains(type))
        {
            // Create and add the student
            DrawableStudent student = new DrawableStudent(type, updater);

            // Translate the student where it should be
            student.translate(new Point3D(studentPositions[drawnStudents.size()].getX() * DIMENSION + getPosition().getX(), -HEIGHT_SPAN,
                    studentPositions[drawnStudents.size()].getY() * DIMENSION + getPosition().getZ()));

            // Set the student invisible to the mouse (we don't want drag and drop on islands)
            student.disableVisibility();

            // Add the student to the group and light
            student.addToGroup(group);
            student.subscribeToPointLight(light);

            // Add the student to the list
            drawnStudents.add(student);
        }

        // At the end i add anyway the student to the list
        // for counting purposes
        students.add(type);
    }

    /**
     * Method to remove a student from the island
     * 
     * @param type The student type
     * @param group The group to which in case remove the student
     * @param light The light to which in case unsubscribe the student
     */
    public void removeStudent(StudentType type, Group group, PointLight light)
    {
        if (type == null)
            throw new NullPointerException("[DrawableIsland] Null student type");

        // I remove graphically the student only if there is only one
        if (students.stream().filter((s) -> s.equals(type)).count() == 1)
        {
            // Take the student to be removed
            DrawableStudent student = drawnStudents.stream().filter((s) -> s.getType() == type).findFirst().get();

            // Remove the student from the group
            student.removeFromGroup(group);

            // Unsubscribe the student from the light
            student.unsubscribeFromPointLight(light);

            // Remove the student from the drawn list
            drawnStudents.remove(student);
        }

        // Remove the student anyway
        students.remove(type);
    }

    /**
     * Method to add mother nature to this island so that the floating action happens at the same time
     */
    public void addMotherNature(DrawableMotherNature motherNature)
    {
        if (motherNature == null)
            throw new NullPointerException("[DrawableIsland] Null mother nature");

        // Assign mother nature if not already present
        if (this.motherNature == null)
            this.motherNature = motherNature;

        // Translate mother nature
        motherNature.translate(new Point3D(X_MOTHER * DIMENSION + getPosition().getX(), getPosition().getY() - HEIGHT_SPAN,
                Y_MOTHER * DIMENSION + getPosition().getZ()));
    }

    /**
     * Method to remove mother nature
     */
    public void removeMotherNature()
    {
        this.motherNature = null;
    }

    @Override
    public void addToGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableIsland] Null group scene");

        // Add the box to the group
        group.getChildren().add(box);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableIsland] Null group scene");

        // Remove the box
        group.getChildren().remove(box);
    }

    // Does nothing because i don't want reflections
    @Override
    public void subscribeToPointLight(PointLight light)
    {}

    @Override
    public void subscribeToAmbientLight(AmbientLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableIsland] Null ambient light");

        // Subscribe the island to the light
        light.getScope().add(box);
    }

    // This method unsubscribes the components from the point light
    @Override
    public void unsubscribeFromPointLight(PointLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableIsland] Null point light");

        // Unsubscribe all the components
        if (tower != null)
            tower.unsubscribeFromPointLight(light);

        for (DrawableStudent student : drawnStudents)
            student.unsubscribeFromPointLight(light);
    }

    @Override
    public void unsubscribeFromAmbientLight(AmbientLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableIsland] Null ambient light");

        // Subscribe the island to the light
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
    public void updateAnimation()
    {
        // I update the floating angle and them sum the Y shift with the sin
        floatingAngle += FLOATING_ANGULAR_VELOCITY;
        floatingAngle = floatingAngle % 360;
        translate(new Point3D(getPosition().getX(), Math.sin(Math.toRadians(floatingAngle)) * FLOATING_AMPLITUDE, getPosition().getZ()));
    }

    /**
     * Position setters
     */
    @Override
    public void translate(Point3D point)
    {
        if (point == null)
            throw new NullPointerException("[DrawableIsland] Null point");

        // Set the box position
        box.translateXProperty().set(point.getX());
        box.translateYProperty().set(point.getY());
        box.translateZProperty().set(point.getZ());

        // Update all the components
        if (tower != null)
            tower.translate(new Point3D(X_TOWER * DIMENSION + point.getX(), point.getY() - HEIGHT_SPAN, Y_TOWER * DIMENSION + point.getZ()));

        // Update the students
        for (int i = 0; i < drawnStudents.size(); i++)
        {
            drawnStudents.get(i).translate(new Point3D(studentPositions[i].getX() * DIMENSION + point.getX(), point.getY() - HEIGHT_SPAN,
                    studentPositions[i].getY() * DIMENSION + point.getZ()));
        }

        // Update eventual mother nature
        if (motherNature != null && !motherNature.isDragging())
            motherNature.translate(new Point3D(X_MOTHER * DIMENSION + getPosition().getX(), getPosition().getY() - HEIGHT_SPAN,
                    Y_MOTHER * DIMENSION + getPosition().getZ()));

        // Update eventual no entry tile
        if (noEntryTile != null)
            noEntryTile.translate(new Point3D(X_NO_ENTRY * DIMENSION, -HEIGHT_SPAN - 10, Y_NO_ENTRY * DIMENSION).add(point));
    }

    @Override
    public void addRotation(Rotate rotation)
    {
        if (rotation == null)
            throw new NullPointerException("[DrawableIsland] Null rotation");

        // Rotate the box
        box.getTransforms().add(rotation);
    }

    public Optional<TowerType> getTowerType()
    {
        return tower == null ? Optional.empty() : Optional.of(tower.getType());
    }

    @Override
    public Point3D getPosition()
    {
        return new Point3D(box.getTranslateX(), box.getTranslateY(), box.getTranslateZ());
    }
}
