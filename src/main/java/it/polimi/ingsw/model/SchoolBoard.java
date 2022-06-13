package it.polimi.ingsw.model;

import it.polimi.ingsw.model.exceptions.EndGameException;
import it.polimi.ingsw.model.exceptions.NoSelectedStudentsException;
import it.polimi.ingsw.model.exceptions.NoSuchStudentInEntranceException;
import it.polimi.ingsw.model.exceptions.NotEnoughCoinsException;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * This class represents the school board. Every player has one and it represents the internal state of each single player and his capabilities to
 * "beat" or not other players during professors and islands exchanges.
 */
public class SchoolBoard implements Serializable
{
    @Serial
    private static final long serialVersionUID = -1601566713757416304L;

    /**
     * Absolute maximum number of students per room.
     */
    public static final int MAX_STUDENTS_PER_ROOM = 10;

    /**
     * Dependent on the number of players.
     */
    private Integer maxStudentsInEntrance = null;
    private Integer maxTowers = null;

    private List<Student> entrance;

    private Map<SchoolColor, List<Student>> diningRoom;

    private List<Professor> professorTable;

    private List<Tower> towers;

    /**
     * The tower color that has to be followed for this SchoolBoard.
     */
    private TowerColor towerColor;

    private GameMode mode;

    /**
     * The number of coins that player has in order to play character cards.
     */
    private int coins;

    /**
     * Creats a SchoolBoard object.
     * 
     * @param color Color for the board's towers.
     * @param mode Game mode.
     * @throws NullPointerException Thrown if the color or the mode are null.
     */
    public SchoolBoard(TowerColor color, GameMode mode) throws NullPointerException
    {
        if (color == null)
            throw new NullPointerException("[SchoolBoard] Null tower color");

        if (mode == null)
            throw new NullPointerException("[SchoolBoard] Game mode is null");

        // Assign the tower color
        towerColor = color;

        // Assign the mode
        this.mode = mode;

        // Set the initial coins
        coins = mode == GameMode.EXPERT ? 1 : 0;

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
     * Sets the players number.
     * 
     * @param playersNumber Number of players in the game..
     * @throws IllegalArgumentException Thrown if the given number of players is invalid.
     * @throws IllegalStateException Thrown if the maxStudentsInEntrance and maxTowers are set.
     */
    public void setPlayersNumber(Integer playersNumber) throws IllegalArgumentException, IllegalStateException
    {
        // The number of players must be 2 or 3
        if (playersNumber < 2 || playersNumber > 3)
            throw new IllegalArgumentException("[SchoolBoard] Invalid players number");

        // The number of players must not be modifiable
        if (maxStudentsInEntrance != null && maxTowers != null)
            throw new IllegalStateException("[SchoolBoard] The number of players can't be changed");

        // The max number of students and towers depends on the number of players
        if (playersNumber == 3)
        {
            maxStudentsInEntrance = 9;
            maxTowers = 6;
        } else
        {
            maxStudentsInEntrance = 7;
            maxTowers = 8;
        }
    }

    /**
     * Adds the professor to the professors table.
     * 
     * @param professor The professor that has to be added.
     * @throws NullPointerException Thrown if the professor passed is null.
     */
    public void addProfessor(Professor professor) throws NullPointerException
    {
        if (professor == null)
            throw new NullPointerException("[SchoolBoard] Null professor");

        // Check if it is not null and not already present
        if (professor != null && !professorTable.contains(professor)
                && professorTable.stream().filter(p -> p.getColor() == professor.getColor()).findFirst().isEmpty())
            professorTable.add(professor);
    }

    /**
     * Removes the specified professor from the professors table.
     * 
     * @param professor The professor that has to be removed.
     * @throws NullPointerException Thrown if the professor passed is null.
     * @throws NoSuchElementException Thrown if there is no such professor.
     */
    public void removeProfessor(Professor professor) throws NullPointerException, NoSuchElementException
    {
        if (professor == null)
            throw new NullPointerException("[SchoolBoard] Null professor");

        // Check if there is such professor
        if (!professorTable.remove(professor))
            throw new NoSuchElementException("[SchoolBoard] There is no such professor");
    }

    /**
     * Tells if the board has that professor color.
     *
     * @param color The color to be checked.
     * @return Boolean that represents whether there is the professor.
     */
    public boolean hasProfessor(SchoolColor color)
    {
        return !professorTable.stream().filter(p -> p.getColor() == color).findFirst().isEmpty();
    }

    /**
     * Adds the tower to the list, with maximum of 6 or 8 towers depending on the number of players.
     * 
     * @param tower The tower that has to be added.
     * @throws NullPointerException Thrown if the tower passed is null.
     */
    public void addTower(Tower tower) throws NullPointerException
    {
        if (tower == null)
            throw new NullPointerException("[SchoolBoard] Null tower");
        if (maxTowers == null)
            throw new NullPointerException("[SchoolBoard] Undefined max number of towers");

        // Check if the tower is not null, isn't already present
        // and the list doesn't exceed the limit
        if (!towers.contains(tower) && towers.size() < maxTowers && tower.getColor() == towerColor)
            towers.add(tower);
    }

    /**
     * Removes the tower from the list.
     * 
     * @param tower The tower that has to be removed.
     * @throws NullPointerException If the parameter is null.
     * @throws EndGameException If there are no more towers.
     */
    public void removeTower(Tower tower) throws NullPointerException, EndGameException
    {
        if (tower == null)
            throw new NullPointerException("[SchoolBoard] Null tower");

        // Checks if the tower is present
        if (towers.contains(tower))
        {
            towers.remove(tower);

            // Check if there are still towers
            if (towers.size() == 0)
                throw new EndGameException("[SchoolBoard] Towers finished");
        }
    }

    /**
     * Same as removeTower(Tower) but removes a tower of that color.
     * 
     * @param color The color of the removed tower.
     * @throws NullPointerException If the parameter is null.
     * @throws EndGameException If there are no more towers.
     */
    public void removeTower(TowerColor color)
    {
        if (color == null)
            throw new NullPointerException("[SchoolBoard] Null color");

        if (color == towerColor)
            removeTower(towers.stream().filter(t -> t.getColor() == color).findFirst()
                    .orElseThrow(() -> new EndGameException("[SchoolBoard] Towers finished")));
    }

    /**
     * Adds the student to the entrance room.
     * 
     * @param student The student that has to be added to the entrance room.
     * @throws NullPointerException Thrown if the student passed is null.
     */
    public void addStudentToEntrance(Student student) throws NullPointerException
    {
        if (student == null)
            throw new NullPointerException("[SchoolBoard] Null student");
        if (maxStudentsInEntrance == null)
            throw new NullPointerException("[SchoolBoard] Undefined max number of students");

        // Checks if the student is null or present already in the board
        if (entrance.contains(student) || entrance.size() >= maxStudentsInEntrance)
            return;

        // Check if the student is not present in the dining room
        if (!diningRoom.get(student.getColor()).contains(student))
            // Add the student to the entrance
            entrance.add(student);
    }

    /**
     * Adds the student to the dining room.
     * 
     * @param student The student that has to be added to the dining room.
     * @throws NullPointerException Thrown if the student passed is null.
     */
    public void addStudentToDiningRoom(Student student) throws NullPointerException
    {
        // Check if the student is null
        if (student == null)
            throw new NullPointerException("[SchoolBoard] Null student");

        // Check if it is not already present and if the dining room is not full
        if (!diningRoom.get(student.getColor()).contains(student) && diningRoom.get(student.getColor()).size() < MAX_STUDENTS_PER_ROOM)
        {
            // Add the student to the map
            diningRoom.get(student.getColor()).add(student);

            // In expert mode the player receives a coin when the 3rd, 6th, 9th students are
            // added
            if (mode == GameMode.EXPERT && diningRoom.get(student.getColor()).size() % 3 == 0)
                addCoins(1);
        }
    }

    /**
     * Removes the student from the entrance.
     * 
     * @param student The student that has to be removed from the entrance.
     */
    public void removeStudentFromEntrance(Student student)
    {
        if (student == null)
            throw new NullPointerException("[SchoolBoard] Null student");

        // Checks if not null and present in entrance
        if (entrance.contains(student))
            entrance.remove(student);
        else
            throw new NoSelectedStudentsException("[SchoolBoard]");
    }

    /**
     * Removes the student from the entrance.
     * 
     * @param color The student color to be removed.
     * @return The removed student.
     */
    public Optional<Student> removeStudentFromEntrance(SchoolColor color) throws NullPointerException
    {
        if (color == null)
            throw new NullPointerException("[SchoolBoard] Null color");

        // Find the student with that color
        int index = -1;
        for (int i = 0; i < entrance.size(); i++)
            index = color == entrance.get(i).getColor() ? i : index;

        // If I found it I return the object removed
        if (index != -1)
            return Optional.of(entrance.remove(index));
        else
            return Optional.empty();
    }

    /**
     * Method to add coins to the schoolBoard.
     *
     * @param coins The number of coins to be added.
     * @throws IllegalArgumentException Thrown if the parameter is negative.
     */
    public void addCoins(int coins) throws IllegalArgumentException
    {
        if (coins < 0)
            throw new IllegalArgumentException("[SchoolBoard] The number of coins must be positive");

        this.coins += coins;
    }

    /**
     * Method to remove coins from the schoolBoard.
     *
     * @param coins The number of coins to remove.
     * @throws IllegalArgumentException Thrown if the parameter is higher than the player's coins.
     */
    public void removeCoins(int coins) throws IllegalArgumentException
    {
        if (coins < 0)
            throw new IllegalArgumentException("[Player] You can't remove a negative number of coins");

        if (this.coins < coins)
            throw new NotEnoughCoinsException();

        this.coins -= coins;
    }

    /**
     * Removes the student from the dining room.
     *
     * @param color The color of the student that has to be removed from the dining room
     * @return The student removed
     */
    public Optional<Student> removeStudentFromDining(SchoolColor color)
    {
        if (color == null)
            throw new NullPointerException("[SchoolBoard] Null color");

        // Checks if not null and there is at least one student of that color in dining
        if (diningRoom.get(color) != null && diningRoom.get(color).size() > 0)
            return Optional.of(diningRoom.get(color).remove(diningRoom.get(color).size() - 1));
        else
            return Optional.empty();
    }

    /**
     * Moves the student to the dining room from the entrance.
     * 
     * @param student The student that has to be moved from entrance to dining room.
     * @throws NullPointerException Thrown if the student passed is null.
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
        } else
        {
            throw new NoSuchStudentInEntranceException("[SchoolBoard]");
        }
    }

    /**
     * Same as the moveStudentToDining(Student student) but with the SchoolColor parameter.
     * 
     * @param color the color of the student to be moved.
     */
    public void moveStudentToDining(SchoolColor color) throws NullPointerException, NoSuchElementException
    {
        if (color == null)
            throw new NullPointerException("[SchoolBoard] Null color");

        // Save the student instance
        Student removed = removeStudentFromEntrance(color).orElseThrow(() -> new NoSuchStudentInEntranceException("[SchoolBoard]"));

        addStudentToDiningRoom(removed);
    }

    /**
     * Returns the list of towers on the board.
     * 
     * @return Towers on the board.
     */
    public List<Tower> getTowers()
    {
        return new ArrayList<>(towers);
    }

    /**
     * Returns the list of professors on the board.
     * 
     * @return Professors on the board.
     */
    public List<Professor> getProfessors()
    {
        return new ArrayList<>(professorTable);
    }

    /**
     * Returns teh list of students in the entrance.
     * 
     * @return Students in the entrance.
     */
    public List<Student> getStudentsInEntrance()
    {
        return new ArrayList<>(entrance);
    }

    /**
     * Returns the number of students with the specified color inside the dining room.
     * 
     * @param color Color of the students.
     */
    public int getStudentsNumber(SchoolColor color)
    {
        return diningRoom.get(color).size();
    }

    /**
     * Returns the number of remaining students that can still be moved from the entrance.
     * 
     * @return Number of students still movable from the entrance.
     * @throws NullPointerException Thrown if the maxStudentsInEntrance parameter is null.
     */
    public int getRemainingMovableStudentsInEntrance() throws NullPointerException
    {
        if (maxStudentsInEntrance == null)
            throw new NullPointerException("[SchoolBoard] Undefined max number of students");

        int movableStudents = maxStudentsInEntrance == 7 ? 3 : 4;
        return movableStudents - (maxStudentsInEntrance - entrance.size());
    }

    /**
     * Returns the maximum number of students in the entrance.
     * 
     * @return Maximum number of students in the entrance.
     */
    public int getMaxStudentsInEntrance()
    {
        return maxStudentsInEntrance;
    }

    /**
     * Returns the maximum number of towers the board can hold.
     * 
     * @return Maximum number of towers the board can hold.
     */
    public int getMaxTowers()
    {
        return maxTowers;
    }

    /**
     * Returns the board's towers color.
     * 
     * @return Board's towers color.
     */
    public TowerColor getTowerColor()
    {
        return towerColor;
    }

    /**
     * Returns the number of coins available on the board.
     * 
     * @return Coins available on the board.
     */
    public int getCoins()
    {
        return coins;
    }

    /**
     * Returns the game mode for which the board is setup.
     * 
     * @return The board's game mode.
     */
    public GameMode getMode()
    {
        return mode;
    }
}
