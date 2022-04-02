package it.polimi.ingsw.model;

import java.util.*;

/**
 * This class represents the school board. Every player has one of these, it represents the internal
 * state of each single player and his capabilities to "beat" or not other players during professors
 * and islands exchanges.
 */
public class SchoolBoard
{
    public static final int MAX_STUDENTS_PER_ROOM = 10;

    /**
     * Dependent on the number of players.
     */
    public final int MAX_STUDENTS_ENTRANCE;
    public final int MAX_TOWERS;

    /**
     * The entrance zone.
     */
    private List<Student> entrance;

    /**
     * The dining room zone.
     */
    private Map<SchoolColor, List<Student>> diningRoom;

    /**
     * The professor table zone.
     */
    private List<Professor> professorTable;

    /**
     * The towers list.
     */
    private List<Tower> towers;

    /**
     * The tower color that has to be followed for this SchoolBoard.
     */
    private TowerColor towerColor;

    /**
     * Constructor.
     * 
     * @param maxStudents max number of students in the entrance (7 or 9)
     * @throws IllegalArgumentException Thrown if maxStudents is neither 7 nor 9.
     * @throws NullPointerException Thrown if the color passed is null
     */
    public SchoolBoard(int maxStudents, TowerColor color)
            throws IllegalArgumentException, NullPointerException
    {
        if (maxStudents != 7 && maxStudents != 9)
            throw new IllegalArgumentException("[SchoolBoard] Invalid max students parameter");
        if (color == null)
            throw new NullPointerException("[SchoolBoard] Null tower color");

        this.MAX_STUDENTS_ENTRANCE = maxStudents;

        // The max number of towers depends on the number of players
        this.MAX_TOWERS = maxStudents == 7 ? 8 : 6;

        // Instantiate all the things
        entrance = new ArrayList<Student>();
        diningRoom = new HashMap<SchoolColor, List<Student>>();
        professorTable = new ArrayList<Professor>();
        towers = new ArrayList<Tower>();

        // Populate the hash map
        diningRoom.put(SchoolColor.BLUE, new ArrayList<Student>());
        diningRoom.put(SchoolColor.GREEN, new ArrayList<Student>());
        diningRoom.put(SchoolColor.PINK, new ArrayList<Student>());
        diningRoom.put(SchoolColor.RED, new ArrayList<Student>());
        diningRoom.put(SchoolColor.YELLOW, new ArrayList<Student>());
    }

    /**
     * Adds the professor to the professors table.
     * 
     * @param professor the professor that has to be added
     * @throws NullPointerException Thrown if the professor passed is null
     */
    public void addProfessor(Professor professor) throws NullPointerException
    {
        if (professor == null)
            throw new NullPointerException("[SchoolBoard] Null professor");

        // Check if it is not null and not already present
        if (professor != null && !professorTable.contains(professor))
        {
            professorTable.add(professor);
        }
    }

    /**
     * Removes the specified professor from the professors table.
     * 
     * @param professor the professor that has to be removed
     * @throws NullPointerException Thrown if the professor passed is null
     */
    public void removeProfessor(Professor professor) throws NullPointerException
    {
        if (professor == null)
            throw new NullPointerException("[SchoolBoard] Null professor");

        // Check if it is not null and present
        if (professorTable.contains(professor))
        {
            professorTable.remove(professor);
        }
    }

    /**
     * Tells if the board has that professor color
     *
     * @param color the color to be checked
     * @return boolean that represents whether there is the professor
     */
    public boolean hasProfessor(SchoolColor color)
    {
        return !professorTable.stream().filter(p -> p.getColor() == color).findFirst().isEmpty();
    }

    /**
     * Adds the tower to the list, with maximum of 6 or 8 towers depending on the number of players.
     * 
     * @param tower the tower that has to be added
     * @throws NullPointerException Thrown if the tower passed is null
     */
    public void addTower(Tower tower) throws NullPointerException
    {
        if (tower == null)
            throw new NullPointerException("[SchoolBoard] Null tower");

        // Check if the tower is not null, isn't already present
        // and the list doesn't exceed the limit
        if (!towers.contains(tower) && towers.size() < MAX_TOWERS && tower.getColor() == towerColor)
        {
            towers.add(tower);
        }
    }

    /**
     * Removes the tower from the list.
     * 
     * @param tower the tower that has to be removed
     */
    public void removeTower(Tower tower)
    {
        // Checks if the tower is not null and present
        if (tower != null && towers.contains(tower))
        {
            towers.remove(tower);
        }
    }

    /**
     * Adds the student to the entrance room
     * 
     * @param student the student that has to be added to the entrance room
     * @throws NullPointerException Thrown if the student passed is null
     */
    public void addStudentToEntrance(Student student) throws NullPointerException
    {
        if (student == null)
            throw new NullPointerException("[SchoolBoard] Null student");

        // Checks if the student is null or present already in the board
        if (entrance.contains(student) || entrance.size() >= MAX_STUDENTS_ENTRANCE)
        {
            return;
        }

        // Check if the student is not present in the dining room
        if (!diningRoom.get(student.getColor()).contains(student))
        {
            // Add the student to the entrance
            entrance.add(student);
        }
    }

    /**
     * Adds the student to the dining room.
     * 
     * @param student the student that has to be added to the dining room
     * @throws NullPointerException Thrown if the student passed is null
     */
    public void addStudentToDiningRoom(Student student) throws NullPointerException
    {
        if (student == null)
            throw new NullPointerException("[SchoolBoard] Null student");
        // Check if it is not already present and not null and if the dining room is not full
        if (!diningRoom.get(student.getColor()).contains(student)
                && diningRoom.get(student.getColor()).size() < MAX_STUDENTS_PER_ROOM)
        {
            // Add the student to the map
            diningRoom.get(student.getColor()).add(student);
        }
    }

    /**
     * Removes the student from the entrance.
     * 
     * @param student the student that has to be removed from the entrance
     */
    public void removeStudentFromEntrance(Student student)
    {
        // Checks if not null and present in entrance
        if (student != null && entrance.contains(student))
        {
            entrance.remove(student);
        }
    }

    /**
     * Removes the student from the entrance
     * @param color the student color to be removed
     * @return the removed student
     */
    public Optional<Student> removeStudentFromEntrance(SchoolColor color) throws NullPointerException
    {
        if(color == null)
            throw new NullPointerException("[SchoolBoard] Null color");

        //Find the student with that color
        int index = -1;
        for(int i = 0; i < entrance.size(); i++) { index = color == entrance.get(i).getColor() ? i : index; }

        //If I found it I return the object removed
        if(index != -1)
            return Optional.of(entrance.remove(index));
        else
            return Optional.empty();
    }

    /**
     * Removes the student from the dining room.
     *
     * @param color the color of the student that has to be removed from the dining room
     * @return the student removed
     */
    public Optional<Student> removeStudentFromDining(SchoolColor color)
    {
        // Checks if not null and there is at least one student of that color in dining
        if (color != null && diningRoom.get(color) != null && diningRoom.get(color).size() > 0)
        {
            return Optional.of(diningRoom.get(color).remove(diningRoom.get(color).size()-1));
        }
        else
        {
            return Optional.empty();
        }
    }

    /**
     * Moves the student to the dining room from the entrance.
     * 
     * @param student The student that has to be moved from entrance to dining room
     * @throws NullPointerException Thrown if the student passed is null
     */
    public void moveStudentToDining(Student student) throws NullPointerException
    {
        if (student == null)
            throw new NullPointerException("[SchoolBoard] Null student");

        // Check if the student is not null and present in the entrance
        if (entrance.contains(student))
        {
            removeStudentFromEntrance(student);
            addStudentToDiningRoom(student);
        }
    }

    /**
     * Same as the moveStudentToDining(Student student) but with the SchoolColor parameter
     * @param color the color of the student to be moved
     */
    public void moveStudentToDining(SchoolColor color) throws NullPointerException, NoSuchElementException
    {
        if (color == null)
            throw new NullPointerException("[SchoolBoard] Null color");

        //Save the student instance
        Student removed = removeStudentFromEntrance(color)
                .orElseThrow(() -> new NoSuchElementException("[SchoolBoard] No student in Entrance with the specified color"));

        addStudentToDiningRoom(removed);
    }

    public List<Professor> getProfessors()
    {
        return new ArrayList<>(professorTable);
    }

    public List<Student> getStudentsInEntrance()
    {
        return new ArrayList<>(entrance);
    }

    public List<Tower> getTowers()
    {
        return new ArrayList<>(towers);
    }

    public int getStudentsNumber(SchoolColor color)
    {
        return diningRoom.get(color).size();
    }

    public int getRemainingMovableStudentsInEntrance()
    {
        int movableStudents = MAX_STUDENTS_ENTRANCE == 7 ? 3 : 4;
        return movableStudents - (MAX_STUDENTS_ENTRANCE - entrance.size());
    }
}
