package it.polimi.ingsw.client.gui.objects;

import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.LightBase;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;

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
     * Object mesh
     */
    private final TriangleMesh triangleMesh;
    private final MeshView studentMesh;

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

        // Create the mesh referring to the Object file
        triangleMesh = new TriangleMesh();

        // Set all the vertices, textures, faces and normals
        triangleMesh.setVertexFormat(VertexFormat.POINT_NORMAL_TEXCOORD);
        triangleMesh.getNormals().addAll(type.getParser().getNormals());
        triangleMesh.getFaces().addAll(type.getParser().getFaces());
        triangleMesh.getPoints().addAll(type.getParser().getVertices());
        triangleMesh.getTexCoords().addAll(type.getParser().getTextures());

        // Now i create the actual node with the mesh
        studentMesh = new MeshView(triangleMesh);

        // I create the material
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.RED);
        material.setSpecularColor(Color.WHITE);

        // Rotate the student of 180 degrees on the y axis
        studentMesh.getTransforms().add(new Rotate(180, new Point3D(0, 1, 0)));

        // Apply the material
        studentMesh.setMaterial(material);
    }

    @Override
    public void addToGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableStudent] Null group");

        // Add the cylinder to the group
        group.getChildren().add(studentMesh);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableStudent] Null group");

        // Remove the cylinder from the group
        group.getChildren().remove(studentMesh);
    }

    @Override
    public void subscribeToPointLight(PointLight light)
    {
        // The students should be under the light so i subscribe it
        light.getScope().add(studentMesh);
    }

    // This method does nothing because i don't want light from everywhere
    @Override
    public void subscribeToAmbientLight(AmbientLight light){}

    @Override
    public void translate(Point3D point)
    {
        studentMesh.translateXProperty().set(point.getX());
        studentMesh.translateYProperty().set(point.getY());
        studentMesh.translateZProperty().set(point.getZ());
    }

    @Override
    public Point3D getPosition()
    {
        return new Point3D(studentMesh.getTranslateX(), studentMesh.getTranslateY(), studentMesh.getTranslateZ());
    }
}
