# **Flames of Prometheus**
### **Enhanced Java Text Adventure Game (JavaFX Edition)**

## **Overview**
*Flames of Prometheus* is an expanded Java text-adventure game built to demonstrate strong Java programming principles, clean architecture, and JavaFX GUI design.  
The project follows a **Model–View–Controller (MVC)** pattern and includes navigation, inventory systems, puzzles, NPC interactions, saving/loading, and audio cues.

---

## **Features**

### **Core Java Concepts**
* Object-Oriented Programming structure (Rooms, Items, Characters, Commands, NPCs)  
* Inheritance, interfaces, and polymorphism  
* Collections Framework usage (`List`, `Map`, `Set`)  
* Enums for command words and movement directions  
* Exception handling for invalid commands and runtime issues  
* Generics for reusable container logic (inventory system)  
* String parsing, tokenization, and command interpretation  
* Serialization for saving and loading game state  
* Custom Annotations for command metadata  

### **JavaFX GUI**
* Directional movement buttons and WASD keyboard support  
* Input field for typed commands  
* Text area for room descriptions and game output  
* Room images rendered using pixel-art assets  
* Minimap display  
* Inventory popup window  
* Action buttons: **Take**, **Drop**, **Talk**, **Give**, **Save**, **Load**  
* Sound effects for item pickup and interactions  

### **Gameplay Mechanics**
* Multi-room environment with interconnected exits  
* Item pickup, usage, and dropping  
* NPC dialogue system  
* Puzzle mechanics requiring specific spoken answers  
* Hidden pathways and item-based unlocking  
* Dynamic GUI updates in response to game events  
* Save/Load functionality for persistent progress  

---

## **Project Structure**
src/
├── main/java/
│ ├── ZorkGUI.java
│ ├── ZorkULGame.java
│ ├── Room.java
│ ├── Item.java
│ ├── Character.java
│ ├── NPC.java
│ ├── Parser.java
│ ├── Command.java
│ ├── CommandWord.java
│ └── additional classes
│
└── main/resources/
├── images/
└── sfx/


---

## **Installation and Running**

### **Requirements**
* Java 17 or later  
* JavaFX SDK  
* IntelliJ IDEA or equivalent IDE  

### **Run in IDE**
1. Clone the repository  
2. Ensure JavaFX SDK is added to project dependencies  
3. Run `ZorkGUI`  

### **Compile and Run (Command Line)**
javac --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.media *.java
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.media ZorkGUI


### **Run the Executable JAR**
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.media -jar FlamesOfPrometheus.jar


---

## **Controls**

### **Keyboard**
* W — Move north  
* A — Move west  
* S — Move south  
* D — Move east  
* ESC — Exit the game  

### **GUI Buttons**
* Movement: North, South, East, West  
* Actions: Take, Drop, Inventory, Talk, Give  
* Save and Load controls  

---

## **Saving and Loading**
The game uses **Java Serialization** to save and restore full game state.  
A file named `savegame.ser` is created automatically or via GUI buttons.

---

## **Testing**
JUnit tests should be placed in:
src/test/java/



Recommended test coverage:
* Room navigation and connections  
* Inventory behaviour  
* Command parsing  
* Puzzle requirements  
* Serialization and deserialization  

---

## **Deliverables Checklist**
* Source code  
* JavaFX GUI implementation  
* JUnit test coverage  
* README documentation  
* Runnable JAR  
* Two-minute demo video  
* GitHub repository link  

---

## **Assessment Alignment**
* Demonstrates understanding of core Java programming  
* Correct implementation of specified requirements  
* Clean architecture and code clarity  
* Robustness through exception handling  
* Proper use of JavaFX, OOP, enums, collections, and serialization


## **AI Usage Declaration**

AI tools were used during the development of this project in the following specific areas:

* **GUI Development:**  
  AI assistance was used for guidance on JavaFX layout structure, button arrangement, CSS-style configuration for dark mode, and general interface organization.

* **Artwork Creation:**  
  All pixel-art room images, environmental graphics, and visual assets were generated with the support of AI-based image generation tools.

* **Dialogue and Story Content:**  
  AI assistance was used to help design, refine, and expand narrative elements, including NPC dialogue, puzzle text, and room descriptions.

All core implementation logic, Java code, class structure, system architecture, and gameplay functionality were authored and adapted independently.

