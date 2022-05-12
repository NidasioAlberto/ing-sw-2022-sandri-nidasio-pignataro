package it.polimi.ingsw.client.gui;

import java.io.File;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class is an immutable object that represents a model 3d object.
 * It parses all the vertices and all the faces of a 3D model.
 */
public class ObjectModelParser
{
    /**
     * Filename of the object
     */
    private final String filename;

    /**
     * Vertices
     */
    private List<Float> vertices;

    /**
     * Faces
     */
    private List<Integer> faces;

    /**
     * Normals
     */
    private List<Float> normals;

    /**
     * Texture coordinates
     */
    private List<Float> texture;

    /**
     * Constructor
     * @param filename The file to parse
     */
    public ObjectModelParser(String filename)
    {
        if(filename == null)
            throw new NullPointerException("[ObjectmodelParser] Null filename");

        // I assign the filename and parse it to vertices and faces
        this.filename = filename;

        // Create the arrays
        vertices    = new ArrayList<>();
        faces       = new ArrayList<>();
        normals     = new ArrayList<>();
        texture     = new ArrayList<>();

        // Parse the file
        parse();
    }

    /**
     * This method actually parses the object file
     */
    private void parse()
    {
        // Get the file as Input Stream
        InputStream fileStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);

        // Check if the file actually exists
        if(fileStream == null)
            throw new RuntimeException("[ObjectModelParser] No such file");

        // If the file exists i declare the scanner and parse the file
        Scanner scanner = new Scanner(fileStream);

        // I proceed until we reach the end of file
        while(scanner.hasNext())
        {
            // Take the current line
            String temp = scanner.nextLine();
            String splitString[] = temp.split(" ");

            // There could be black lines
            if(temp.equals(""))
                continue;

            // It could be different things depending on the first two letters
            if(splitString[0].equals("v"))
            {
                // It is a vertex
                vertices.add(Float.parseFloat(splitString[1]) * 10);
                vertices.add(Float.parseFloat(splitString[2]) * 10);
                vertices.add(Float.parseFloat(splitString[3]) * 10);
            }
            else if(splitString[0].equals("vn"))
            {
                // It is a normal
                normals.add(Float.parseFloat(splitString[1]));
                normals.add(Float.parseFloat(splitString[2]));
                normals.add(Float.parseFloat(splitString[3]));
            }
            else if(splitString[0].equals("vt"))
            {
                // It is a texture
                texture.add(Float.parseFloat(splitString[1]));
                texture.add(Float.parseFloat(splitString[2]));
                texture.add(Float.parseFloat(splitString[3]));
            }
            else if(splitString[0].equals("f"))
            {
                // It is a face. I have to parse normals and textures
                String[] tempSplit = splitString[1].split("/");
                faces.add(Integer.parseInt(tempSplit[0]) - 1);

                // If present i add the normal
                if(tempSplit.length >= 3 && !tempSplit[2].equals(""))
                    faces.add(Integer.parseInt(tempSplit[2]) - 1);
                else
                    faces.add(0);

                // If present i add the texture
                if(tempSplit.length >= 2 && !tempSplit[1].equals(""))
                    faces.add(Integer.parseInt(tempSplit[1]) - 1);
                else
                    faces.add(0);

                tempSplit = splitString[2].split("/");
                faces.add(Integer.parseInt(tempSplit[0]) - 1);

                // If present i add the normal
                if(tempSplit.length >= 3 && !tempSplit[2].equals(""))
                    faces.add(Integer.parseInt(tempSplit[2]) - 1);
                else
                    faces.add(0);

                // If present i add the texture
                if(tempSplit.length >= 2 && !tempSplit[1].equals(""))
                    faces.add(Integer.parseInt(tempSplit[1]) - 1);
                else
                    faces.add(0);

                tempSplit = splitString[3].split("/");
                faces.add(Integer.parseInt(tempSplit[0]) - 1);

                // If present i add the normal
                if(tempSplit.length >= 3 && !tempSplit[2].equals(""))
                    faces.add(Integer.parseInt(tempSplit[2]) - 1);
                else
                    faces.add(0);

                // If present i add the texture
                if(tempSplit.length >= 2 && !tempSplit[1].equals(""))
                    faces.add(Integer.parseInt(tempSplit[1]) - 1);
                else
                    faces.add(0);
            }
        }

        // At the end i close the file
        scanner.close();
    }

    /**
     * Get the parsed vertices
     * @return An array of vertices
     */
    public float[] getVertices()
    {
        // Create the result array
        float[] result = new float[vertices.size()];

        // Copy the array
        for(int i = 0; i < vertices.size(); i++)
            result[i] = vertices.get(i);

        // Return the result
        return result;
    }

    /**
     * Get the parsed faces
     * @return An array of faces
     */
    public int[] getFaces()
    {
        // Create the result array
        int[] result = new int[faces.size()];

        // Copy the array
        for(int i = 0; i < faces.size(); i++)
            result[i] = faces.get(i);

        // return the result
        return result;
    }

    /**
     * Get parsed normals
     * @return An array of normals
     */
    public float[] getNormals()
    {
        // Create the result array
        float[] result = new float[normals.size()];

        // Copy the array
        for(int i = 0; i < normals.size(); i++)
            result[i] = normals.get(i);

        // Return the result
        return result;
    }

    /**
     * Get parsed textures
     * @return An array of textures points
     */
    public float[] getTextures()
    {
        // Create the result array
        float[] result = new float[texture.size()];

        // If there are no texture coordinates i return a standard one
        if(result.length == 0)
            return new float[] {0, 0, 1, 0, 0, 1, 1, 1};

        // Copy the array
        for(int i = 0; i < texture.size(); i++)
            result[i] = texture.get(i);

        // Return the result
        return result;
    }
}
