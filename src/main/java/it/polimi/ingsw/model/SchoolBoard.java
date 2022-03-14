package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the school board. Every player has one of these, it
 * represents the internal state of each single player and his capabilities to
 * "beat" or not other players during professors and islands exchanges.
 */
public class SchoolBoard
{
    public static final int MAX_TOWERS              = 8;

    public static final int MAX_STUDENTS_PER_ROOM   = 10;

    /**
     * Dependent on the number of players
     */
    public final int MAX_STUDENTS_ENTRANCE;

    /**
     * The entrance zone
     */
    private List<Student> entrance;

    /**
     * The dining room zone
     */
    private Map<SchoolColor, List<Student>> diningRoom;

    /**
     * The professor table zone
     */
    private List<Professor> professorTable;

    /**
     * The towers list
     */
    private List<Tower> towers;

    /**
     * Constructor
     */
    public SchoolBoard(int maxStudents)
    {
        this.MAX_STUDENTS_ENTRANCE = maxStudents > 0 ? maxStudents : 7;
        //Instantiate all the things
        entrance        = new ArrayList<Student>();
        diningRoom      = new HashMap<SchoolColor, List<Student>>();
        professorTable  = new ArrayList<Professor>();
        towers          = new ArrayList<Tower>();

        //Populate the hash map
        diningRoom.put(SchoolColor.BLUE, new ArrayList<Student>());
        diningRoom.put(SchoolColor.GREEN, new ArrayList<Student>());
        diningRoom.put(SchoolColor.PINK, new ArrayList<Student>());
        diningRoom.put(SchoolColor.RED, new ArrayList<Student>());
        diningRoom.put(SchoolColor.YELLOW, new ArrayList<Student>());
    }

    /**
     * Adds the professor to the professors table
     * @param professor The professor to be added
     */
    public void addProfessor(Professor professor)
    {
        //Check if it is not null and not already present
        if(professor != null && !professorTable.contains(professor))
        {
            professorTable.add(professor);
        }
    }

    /**
     * Removes the specified professor
     * @param professor The professor to be removed
     */
    public void removeProfessor(Professor professor)
    {
        //Check if it is not null and present
        if(professor != null && professorTable.contains(professor))
        {
            professorTable.remove(professor);
        }
    }

    /**
     * Adds the tower to the list (with maximum of 8 towers)
     * @param tower The tower to be added
     */
    public void addTower(Tower tower)
    {
        //Check if the tower is not null, isn't already present
        //and the list doesn't exceed the limit
        if(tower != null && !towers.contains(tower) && towers.size() < MAX_TOWERS)
        {
            towers.add(tower);
        }
    }

    /**
     * Removes the tower from the list
     * @param tower The tower to be removed
     */
    public void removeTower(Tower tower)
    {
        //Checks if the tower is not null and present
        if(tower != null && towers.contains(tower))
        {
            towers.remove(tower);
        }
    }

    /**
     * Adds the student in the entrance
     * TODO ADD THIS METHOD TO UML
     * @param student The student to be added
     */
    public void addStudentToEntrance(Student student)
    {
        //Checks if the student is null or present already in the board
        if(student == null || entrance.contains(student) || entrance.size() >= MAX_STUDENTS_ENTRANCE)
        {
            return;
        }

        //Check if the student is not present in the dining room
        if(!diningRoom.get(student.getColor()).contains(student))
        {
            //Add the student to the entrance
            entrance.add(student);
        }
    }

    /**
     * Adds the student to the dining room
     * @param student The student to be added
     */
    private void addStudentToDiningRoom(Student student)
    {
        //Check if it is not already present and not null and if the dining room is not full
        if(student != null && !diningRoom.get(student.getColor()).contains(student) &&
           diningRoom.get(student.getColor()).size() < MAX_STUDENTS_PER_ROOM)
        {
            //Add the student to the map
            diningRoom.get(student.getColor()).add(student);
        }
    }

    /**
     * Removes the student from the entrance
     * @param student The student to be removed
     */
    public void removeStudentFromEntrance(Student student)
    {
        //Checks if not null and present in entrance
        if(student != null && entrance.contains(student))
        {
            entrance.remove(student);
        }
    }

    /**
     * Moves the student to the dining room from the entrance
     * @param student The student to be moved
     */
    public void moveStudentToDining(Student student)
    {
        //Check if the student is not null and present in the entrance
        if(student != null && entrance.contains(student))
        {
            removeStudentFromEntrance(student);
            addStudentToDiningRoom(student);
        }
    }

    /**
     * Getters
     */
    public Professor[] getProfessors()
    {
        //Create the result array
        Professor[] result = new Professor[professorTable.size()];

        //Fill the result array
        professorTable.toArray(result);

        return result;
    }

    //TODO ADD THIS METHOD IN UML
    public Student[] getStudentsInEntrance()
    {
        //Create the result array
        Student[] result = new Student[entrance.size()];

        //Fill the result array
        entrance.toArray(result);

        return result;
    }

    //TODO MODIFY THE UML
    public Tower[] getTowers()
    {
        //Create the result array
        Tower[] result = new Tower[towers.size()];

        //Fill the result array
        towers.toArray(result);

        return result;
    }

    public int getMaxStudentsNumber(SchoolColor color)
    {
        return diningRoom.get(color).size();
    }
}
