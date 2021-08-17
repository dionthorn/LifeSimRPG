# LifeSimRPG
You can contact me on reddit /u/dionthorn if you have questions about the game. I help out on the /r/javahelp subreddit all the time!
Simple* JavaFX Controls based GUI for a Life Simulation game, inspired by classic older titles like Kudos and GameBiz

*Recently added FXML so its a little all over right now

Here is App.java the starting point
https://github.com/dionthorn/LifeSimRPG/blob/main/src/main/java/org/dionthorn/lifesimrpg/App.java

v0.0.1 Structure:

LifeSimRPG
+ App
+ Engine
+ GameState
+ FileOpUtil
+ NameDataUtil
      
LifeSimRPG.entites
+ AbstractEntity
+ + Job
+ + Course
+ + Map
+ +
+ + Place
+ + + Residence
+ +
+ + AbstractCharacter
+ + + PlayerCharacter
+ + + AICharacter
            
LifeSimRPG.controllers
+ AbstractScreenController
+ + CharacterCreationScreen
+ +
+ + AbstractStartScreenController
+ + + StartScreenController
+ + + GameOverScreenController
+ +
+ + AbstractGameScreenController
+ + + PlayerInfoScreenController
+ + + JobInfoScreenController
+ + + MapInfoScreenController
+ + + CoursesInfoScreenController

Kudos Series (makers of the Democracy series)
https://www.positech.co.uk/kudos2/
 
GameBiz series
http://www.veloci.dk/gamebiz/GB3.htm

CREDITS:

For first and last name data:
https://github.com/smashew/NameDatabases
