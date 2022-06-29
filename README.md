# Eriantys

<img align="right" width="200" height="200" src="github/Eriantys box.png">

Final test of **Software Engineering**, course of "Computer Science Engineering" held at Politecnico di Milano (2021/22).

**Teacher** Gianpaolo Cugola

The project consists in the implementation of the Eriantys board game through the MVC (Model View Controller) architecture, consisting of a Server and several Clients that can connect to it via the network. Clients allow you to interact with the game via command line (CLI) or graphical interface (GUI).

This project is written in Java and is built using Maven. Unit test are written using the Junit framework and coverage reports are generated using the Jacoco plugin. ****

## Implemented features

| Feature                     | Implemented |
| --------------------------- | ----------- |
| Basic mode                  | ✔️           |
| Expert mode                 | ✔️           |
| CLI                         | ✔️           |
| GUI                         | ✔️           |
| 12 Character cards          | ✔️           |
| Multiple matches            | ✔️           |
| Resilience to disconnection | ✔️           |


## Test coverage

| Package    | Class        | Method        | Line            |
| ---------- | ------------ | ------------- | --------------- |
| Model      | 100% (54/54) | 98% (371/376) | 99% (1442/1455) |
| Controller | 100% (9/9)   | 100% (51/51)  | 92% (355/383)   |


## How to run
Once your computer meets the [requirements](#Requirements) you can type on a terminal the following commands to execute either the Server, CLI or GUI:  
 - Server:   
```
java -jar Eriantys_Server_softeng-GC9.jar [port number, default:2345]
```
 - CLI:   
 ```
 java -jar Eriantys_CLI_softeng-GC9.jar [ip] [port number]
 ```
 - GUI:   
 ```
 java -jar Eriantys_GUI_softeng-GC9.jar
 ```
 
## Requirements
 ### Linux and Mac Users
 The only requirement is to install a version of java jdk greater than or equal to 17. Both openjdk and java jdk are suitable for the job, feel free to run the following apt command:  
 ```
 sudo apt update && sudo apt install openjdk-17-jdk
 ```

 ### Windows users
 You need to install [java JDK](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html). You could need to set up the so called environment variables. To find out if you need to do this procedure you can type on your windows terminal the following command:  
 ```
 java -version
 ```

 if the answer is an error you most likely have to follow the environment variable procedure, else you're good to go.
 
 ### Windows users (Environment variables)
 To modify the environment variables you need to type in your `start` menu `Environment Variables` and click on `Edit the system environment variables` and again `Environment Variables`.

 Now in the user section click on `Path` and then on `Edit`, it should be possible now to create the variable selecting `New` and typing the full path to the installed java bin folder.

 ### System requirements
 Minimum RAM for GUI: 2GB free
 
## Others
If you are playing an expert match with the GUI, in order to activate a character card you just need to double click the card you want to activate. If a card's action requires to choose an island just double click it or if it requires a color selection just double click one of the student displayed above the cloud tiles. 

## The Team
* [Alberto Sandri](https://github.com/AlbertoSandri)
* [Alberto Nidasio](https://github.com/NidasioAlberto)
* [Matteo Pignataro](https://github.com/trainer400)
