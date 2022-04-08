package it.polimi.ingsw.model;

import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class SchoolBoardTest
{
    SchoolBoard board;

    @BeforeEach
    public void init()
    {
        board = new SchoolBoard(TowerColor.BLACK);
        board.setPlayersNumber(2);
    }

    /**
     * Tests that if we put wrong parameters into the constructor, an error is thrown.
     */
    @Test
    public void wrongParametersTest()
    {
        // Null color parameter
        assertThrows(NullPointerException.class, () -> board = new SchoolBoard(null));

        // Invalid players number
        assertThrows(IllegalArgumentException.class, () -> board.setPlayersNumber(1));
        assertThrows(IllegalArgumentException.class, () -> board.setPlayersNumber(5));

        // The player number should not be modifiable
        assertThrows(IllegalStateException.class, () -> board.setPlayersNumber(3));
    }

    /**
     * Tests if with correct parameters the general parameter assignment is correct.
     */
    @Test
    public void correctParametersTest()
    {
        board = new SchoolBoard(TowerColor.BLACK);
        board.setPlayersNumber(2);

        // With 2 players the board has 7 students and 6 towers
        assertEquals(7, board.getMaxStudentsInEntrance());
        assertEquals(8, board.getMaxTowers());

        board = new SchoolBoard(TowerColor.GREY);
        board.setPlayersNumber(3);

        // With 3 players the board has 9 students and 8 towers
        assertEquals(9, board.getMaxStudentsInEntrance());
        assertEquals(6, board.getMaxTowers());

        board = new SchoolBoard(TowerColor.WHITE);
        board.setPlayersNumber(4);

        // With 4 players the board has 7 students and 6 towers
        assertEquals(7, board.getMaxStudentsInEntrance());
        assertEquals(8, board.getMaxTowers());
    }

    /**
     * Tests the correct initialization of all the lists.
     */
    @Test
    public void correctInitializationTest()
    {
        assertEquals(0, board.getProfessors().size());
        assertEquals(0, board.getTowers().size());
        assertEquals(0, board.getStudentsInEntrance().size());

        for (int i = 0; i < SchoolColor.values().length; i++)
            assertEquals(0, board.getStudentsNumber(SchoolColor.values()[i]));
    }

    /**
     * Tests the addProfessor method.
     */
    @Test
    public void addProfessorTest()
    {
        // Null professor
        assertThrows(NullPointerException.class, () -> board.addProfessor(null));
        assertEquals(0, board.getProfessors().size());

        // Create the professors to be added
        Professor firstProfessor = new Professor(SchoolColor.BLUE);
        Professor secondProfessor = new Professor(SchoolColor.RED);

        // Add legit professor
        board.addProfessor(firstProfessor);

        assertEquals(1, board.getProfessors().size());
        assertEquals(true, board.getProfessors().contains(firstProfessor));

        // Add the same professor
        board.addProfessor(firstProfessor);

        assertEquals(1, board.getProfessors().size());
        assertEquals(true, board.getProfessors().contains(firstProfessor));

        // Add the second professor
        board.addProfessor(secondProfessor);

        assertEquals(2, board.getProfessors().size());
        assertEquals(true, board.getProfessors().contains(secondProfessor));
        assertEquals(true, board.getProfessors().contains(firstProfessor));
    }

    /**
     * Tests the remove professor method.
     */
    @Test
    public void removeProfessorTest()
    {
        // Professors to be added and removed
        Professor firstProfessor = new Professor(SchoolColor.RED);
        Professor secondProfessor = new Professor(SchoolColor.BLUE);

        // Setup the test
        board.addProfessor(firstProfessor);
        board.addProfessor(secondProfessor);

        // Null remove
        assertThrows(NullPointerException.class, () -> board.removeProfessor(null));
        assertEquals(2, board.getProfessors().size());
        assertEquals(true, board.getProfessors().contains(firstProfessor));
        assertEquals(true, board.getProfessors().contains(secondProfessor));

        // Remove a different instance
        board.removeProfessor(new Professor(SchoolColor.RED));
        assertEquals(2, board.getProfessors().size());
        assertEquals(true, board.getProfessors().contains(firstProfessor));
        assertEquals(true, board.getProfessors().contains(secondProfessor));

        // Remove the actual first professor
        board.removeProfessor(firstProfessor);
        assertEquals(1, board.getProfessors().size());
        assertEquals(true, board.getProfessors().contains(secondProfessor));

        // Remove the actual second professor
        board.removeProfessor(secondProfessor);
        assertEquals(0, board.getProfessors().size());
    }

    /**
     * Tests the hasProfessor method.
     */
    @Test
    public void hasProfessorTest()
    {
        // Professors to be added and removed
        Professor firstProfessor = new Professor(SchoolColor.RED);
        Professor secondProfessor = new Professor(SchoolColor.BLUE);

        assertEquals(false, board.hasProfessor(firstProfessor.getColor()));
        assertEquals(false, board.hasProfessor(secondProfessor.getColor()));

        // Setup the test
        board.addProfessor(firstProfessor);
        board.addProfessor(secondProfessor);

        // Null remove
        assertThrows(NullPointerException.class, () -> board.removeProfessor(null));
        assertEquals(2, board.getProfessors().size());
        assertEquals(true, board.hasProfessor(firstProfessor.getColor()));
        assertEquals(true, board.hasProfessor(secondProfessor.getColor()));

        // Remove a different instance
        board.removeProfessor(new Professor(SchoolColor.RED));
        assertEquals(2, board.getProfessors().size());
        assertEquals(true, board.hasProfessor(firstProfessor.getColor()));
        assertEquals(true, board.hasProfessor(secondProfessor.getColor()));

        // Remove the actual first professor
        board.removeProfessor(firstProfessor);
        assertEquals(1, board.getProfessors().size());
        assertEquals(false, board.hasProfessor(firstProfessor.getColor()));
        assertEquals(true, board.hasProfessor(secondProfessor.getColor()));

        // Remove the actual second professor
        board.removeProfessor(secondProfessor);
        assertEquals(0, board.getProfessors().size());
        assertEquals(false, board.hasProfessor(firstProfessor.getColor()));
        assertEquals(false, board.hasProfessor(secondProfessor.getColor()));
    }

    @Test
    public void addTowerTest()
    {
        // Null add
        assertThrows(NullPointerException.class, () -> board.addTower(null));
        assertEquals(0, board.getTowers().size());

        // Towers to be added
        Tower firstTower = new Tower(TowerColor.BLACK);
        Tower secondTower = new Tower(TowerColor.BLACK);

        // Add different color tower
        board.addTower(new Tower(TowerColor.WHITE));
        assertEquals(0, board.getTowers().size());

        // Add legit towers
        board.addTower(firstTower);
        assertEquals(1, board.getTowers().size());
        assertEquals(true, board.getTowers().contains(firstTower));

        // Add the first tower again
        board.addTower(firstTower);
        assertEquals(1, board.getTowers().size());
        assertEquals(true, board.getTowers().contains(firstTower));

        // Add second legit tower
        board.addTower(secondTower);
        assertEquals(2, board.getTowers().size());
        assertEquals(true, board.getTowers().contains(firstTower));
        assertEquals(true, board.getTowers().contains(secondTower));

        // Add a lot of towers to see if the maximum number is 7
        for (int i = 0; i < 10; i++)
            board.addTower(new Tower(TowerColor.BLACK));

        assertEquals(8, board.getTowers().size());
    }

    @Test
    public void removeTowersTest()
    {
        // Setup the test
        Tower firstTower = new Tower(TowerColor.BLACK);
        Tower secondTower = new Tower(TowerColor.BLACK);
        board.addTower(firstTower);
        board.addTower(secondTower);

        // Remove null object
        assertThrows(NullPointerException.class, () -> board.removeTower((Tower) null));
        assertThrows(NullPointerException.class, () -> board.removeTower((TowerColor) null));

        // Remove object that doesn't exist
        assertThrows(NoSuchElementException.class, () -> board.removeTower(TowerColor.WHITE));
        assertEquals(2, board.getTowers().size());
        assertEquals(true, board.getTowers().contains(firstTower));
        assertEquals(true, board.getTowers().contains(secondTower));

        // Remove a different instance
        board.removeTower(new Tower(TowerColor.BLACK));
        assertEquals(2, board.getTowers().size());
        assertEquals(true, board.getTowers().contains(firstTower));
        assertEquals(true, board.getTowers().contains(secondTower));

        // Remove the actual tower
        board.removeTower(firstTower);
        assertEquals(1, board.getTowers().size());
        assertEquals(true, board.getTowers().contains(secondTower));
        assertEquals(false, board.getTowers().contains(firstTower));

        // Remove the second tower
        board.removeTower(secondTower.getColor());
        assertEquals(0, board.getTowers().size());
    }

    @Test
    public void addStudentToEntranceTest()
    {
        // Add the null element
        assertThrows(NullPointerException.class, () -> board.addStudentToEntrance(null));
        assertEquals(0, board.getStudentsInEntrance().size());

        // Students to be added
        Student firstStudent = new Student(SchoolColor.RED);
        Student secondStudent = new Student(SchoolColor.GREEN);

        // Add the first student
        board.addStudentToEntrance(firstStudent);
        assertEquals(1, board.getStudentsInEntrance().size());
        assertEquals(true, board.getStudentsInEntrance().contains(firstStudent));

        // Add the first student again
        board.addStudentToEntrance(firstStudent);
        assertEquals(1, board.getStudentsInEntrance().size());
        assertEquals(true, board.getStudentsInEntrance().contains(firstStudent));

        // Add the second student
        board.addStudentToEntrance(secondStudent);
        assertEquals(2, board.getStudentsInEntrance().size());
        assertEquals(true, board.getStudentsInEntrance().contains(firstStudent));
        assertEquals(true, board.getStudentsInEntrance().contains(firstStudent));
    }

    @Test
    public void addStudentToDiningTest()
    {
        // Add null object
        assertThrows(NullPointerException.class, () -> board.addStudentToDiningRoom(null));

        // for all the colors the hasStudent() must be 0
        for (SchoolColor color : SchoolColor.values())
            assertEquals(0, board.getStudentsNumber(color));

        // Create a list of colors to be added
        List<Student> students = new ArrayList<Student>();

        // Populate the list
        students.add(new Student(SchoolColor.GREEN));
        students.add(new Student(SchoolColor.RED));
        students.add(new Student(SchoolColor.BLUE));
        students.add(new Student(SchoolColor.PINK));
        students.add(new Student(SchoolColor.YELLOW));
        students.add(new Student(SchoolColor.GREEN));
        students.add(new Student(SchoolColor.GREEN));
        students.add(new Student(SchoolColor.RED));
        students.add(new Student(SchoolColor.BLUE));
        students.add(new Student(SchoolColor.YELLOW));

        // Memorize the number of students added
        int previousNumber[] = new int[]
        {0, 0, 0, 0, 0};

        for (int i = 0; i < students.size(); i++)
        {
            // I add the current student twice to see if avoids multiple equal instances
            board.addStudentToDiningRoom(students.get(i));
            board.addStudentToDiningRoom(students.get(i));

            // Check that all the number of students are right
            for (int color = 0; color < SchoolColor.values().length; color++)
            {
                if (SchoolColor.values()[color] == students.get(i).getColor())
                {
                    // Check if the actual increment is 1
                    assertEquals(previousNumber[color] + 1,
                            board.getStudentsNumber(SchoolColor.values()[color]));

                    // Increment the previous number
                    previousNumber[color]++;
                } else
                {
                    // Else check that the number isn't different
                    assertEquals(previousNumber[color],
                            board.getStudentsNumber(SchoolColor.values()[color]));
                }
            }
        }

        // Override one color to see if the maximum number of student is respected
        for (int i = 0; i < 20; i++)
        {
            board.addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        }

        // Check that the maximum number is 10
        assertEquals(10, board.getStudentsNumber(SchoolColor.GREEN));
    }

    @Test
    public void removeStudentFromEntranceTest()
    {
        // Prepare the test
        Student firstStudent = new Student(SchoolColor.YELLOW);
        Student secondStudent = new Student(SchoolColor.RED);
        board.addStudentToEntrance(firstStudent);
        board.addStudentToEntrance(secondStudent);

        // Remove null object
        assertThrows(NullPointerException.class,
                () -> board.removeStudentFromEntrance((Student) null));
        assertThrows(NullPointerException.class,
                () -> board.removeStudentFromEntrance((SchoolColor) null));

        // Remove a non present object
        board.removeStudentFromEntrance(new Student(SchoolColor.YELLOW));
        assertEquals(2, board.getStudentsInEntrance().size());
        assertEquals(true, board.getStudentsInEntrance().contains(firstStudent));
        assertEquals(true, board.getStudentsInEntrance().contains(secondStudent));

        // Remove the actual first object
        board.removeStudentFromEntrance(firstStudent);
        assertEquals(1, board.getStudentsInEntrance().size());
        assertEquals(false, board.getStudentsInEntrance().contains(firstStudent));
        assertEquals(true, board.getStudentsInEntrance().contains(secondStudent));

        // Remove the second object by color
        assertEquals(secondStudent, board.removeStudentFromEntrance(SchoolColor.RED).get());
        assertEquals(0, board.getStudentsInEntrance().size());
    }

    @Test
    public void removeStudentFromDining()
    {
        // Prepare the test
        List<Student> students = new ArrayList<Student>();
        int previousNumber[] = new int[]
        {0, 0, 0, 0, 0};

        // Populate the list
        students.add(new Student(SchoolColor.GREEN));
        students.add(new Student(SchoolColor.RED));
        students.add(new Student(SchoolColor.BLUE));
        students.add(new Student(SchoolColor.PINK));
        students.add(new Student(SchoolColor.GREEN));
        students.add(new Student(SchoolColor.GREEN));
        students.add(new Student(SchoolColor.RED));
        students.add(new Student(SchoolColor.BLUE));

        // Add all the students on the list to the dining room
        for (Student student : students)
            board.addStudentToDiningRoom(student);

        // Populate the number of colors
        for (int color = 0; color < SchoolColor.values().length; color++)
            previousNumber[color] = board.getStudentsNumber(SchoolColor.values()[color]);

        // Remove a null object
        assertThrows(NullPointerException.class, () -> board.removeStudentFromDining(null));

        // Remove a student that is not present
        assertEquals(Optional.empty(), board.removeStudentFromDining(SchoolColor.YELLOW));
        for (int color = 0; color < SchoolColor.values().length; color++)
            assertEquals(previousNumber[color],
                    board.getStudentsNumber(SchoolColor.values()[color]));

        // Remove all the students and compare them
        for (Student student : students)
        {
            Student removedStudent = board.removeStudentFromDining(student.getColor())
                    .orElseThrow(() -> new NoSuchElementException(
                            "[SchoolBoardTest] No student with that color"));

            // I expect the remove student to be contained into the list, if so i remove it
            assertEquals(true, students.contains(removedStudent));
        }
    }

    @Test
    public void moveStudentToDiningTest()
    {
        // Prepare the test
        List<Student> students = new ArrayList<Student>();

        // Populate the list
        students.add(new Student(SchoolColor.GREEN));
        students.add(new Student(SchoolColor.RED));
        students.add(new Student(SchoolColor.BLUE));
        students.add(new Student(SchoolColor.PINK));
        students.add(new Student(SchoolColor.GREEN));
        students.add(new Student(SchoolColor.GREEN));
        students.add(new Student(SchoolColor.RED));

        // Add the students to the entrance
        for (Student student : students)
            board.addStudentToEntrance(student);

        // Move a null student
        assertThrows(NullPointerException.class, () -> board.moveStudentToDining((Student) null));
        assertThrows(NullPointerException.class,
                () -> board.moveStudentToDining((SchoolColor) null));
        for (SchoolColor color : SchoolColor.values())
            assertEquals(0, board.getStudentsNumber(color));

        // Move a student that doesn't exist in entrance
        assertThrows(NoSuchElementException.class,
                () -> board.moveStudentToDining(new Student(SchoolColor.GREEN)));
        assertThrows(NoSuchElementException.class,
                () -> board.moveStudentToDining(SchoolColor.YELLOW));
        for (SchoolColor color : SchoolColor.values())
            assertEquals(0, board.getStudentsNumber(color));

        // Move all of them
        for (Student student : students)
            board.moveStudentToDining(student);

        // I check if the entrance is void
        assertEquals(0, board.getStudentsInEntrance().size());

        // At the end I should have the same number of colors
        for (SchoolColor color : SchoolColor.values())
        {
            assertEquals(board.getStudentsNumber(color),
                    students.stream().filter(s -> s.getColor() == color).count());
        }

        // Now the same but I use the color method (I RESET ALL FIRST)
        board = new SchoolBoard(TowerColor.BLACK);
        board.setPlayersNumber(2);

        // Add the students to the entrance
        for (Student student : students)
            board.addStudentToEntrance(student);

        for (Student student : students)
            board.moveStudentToDining(student.getColor());

        // I check if the entrance is void
        assertEquals(0, board.getStudentsInEntrance().size());

        // At the end I should have the same number of colors
        for (SchoolColor color : SchoolColor.values())
        {
            assertEquals(board.getStudentsNumber(color),
                    students.stream().filter(s -> s.getColor() == color).count());
        }
    }
}
