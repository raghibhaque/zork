# Flame of Prometheus

*A JavaFX Text Adventure Game*

## Overview

**Flame of Prometheus** is a narrative-driven Java text adventure
enhanced with a JavaFX GUI.\
The game blends classic room-based exploration with modern Java concepts
such as:

-   MVC architecture\
-   Object-oriented design\
-   Collections, enums, generics\
-   Serialization-based save/load\
-   NPC dialogue and puzzles\
-   Hidden paths and story progression

The world takes inspiration from mythic themes --- ash, fire, and
ancient punishment --- with original rooms, NPCs, puzzles, and items.

## How to Compile & Run

### Requirements

-   **Java 17 or higher**\
-   **JavaFX SDK** (matching your Java version)
-   **Maven or manual JavaFX linking**\
-   Project source code (from GitHub repo)

### Running Through IDE (Recommended)

1.  Install JavaFX.\
2.  Add JavaFX libraries to project setup.\
3.  Set VM options:

```{=html}
<!-- -->
```
    --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml

4.  Run `ZorkGUI.main()` or the generated JAR.

## Running the Executable JAR

In terminal:

    java --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml -jar FlameOfPrometheus.jar

## Gameplay Instructions

### Basic Commands

  Command            Description
  ------------------ ------------------------------------
  `go <direction>`   Move between rooms
  `take <item>`      Pick up an item
  `drop <item>`      Drop an item
  `talk <npc>`       Speak to an NPC
  `say <word>`       Speak a keyword (used for puzzles)
  `use <item>`       Use an item in the correct context
  `inventory`        Show all items
  `read <item>`      Read notes or inscriptions
  `save`             Save your game (serialization)
  `load`             Load previous progress

### GUI Features

-   Buttons for directional movement\
-   Inventory display panel\
-   TextArea output for story, dialogue, and descriptions\
-   Input field for typing commands\
-   CSS-styled interface

## Game Structure (MVC)

### Model

-   `Room`, `Item`, `NPC`, `Character`, `Command`, enums\
-   Inventory system\
-   Puzzle flags\
-   Save/load via serialization

### View

-   JavaFX GUI (`ZorkGUI`)\
-   CSS styling\
-   OutputArea + inventory panel

### Controller

-   `Parser` for command parsing\
-   `ZorkULGame` for game logic\
-   Annotated command methods

## Core Java Concepts Implemented

✔ OOP\
✔ Inheritance & Polymorphism\
✔ Collections API\
✔ Enums\
✔ Exception Handling\
✔ Generics\
✔ Parsing\
✔ Serialization\
✔ Annotations\
✔ JUnit\
✔ JavaFX GUI\
✔ MVC

## Features Implemented

### Rooms

Hall of Embers, Chamber of Echoes, Ashen Garden, Iron Spire, Vault of
Chains, Crucible

### Items

Torch, Ember Fragment, Flame Core, Gem, EtchedStone, Notes

### Puzzles

-   Echo puzzle (`say chain`)
-   Vault puzzle (`say prometheus`)
-   Altar ignition with torch
-   Final riddle (`say fire`)

### NPCs

-   Orpheon\
-   Acheron\
-   Sentinel

### Save / Load

Implemented with Java serialization.

## Extended Features

-   Hidden exits\
-   NPC dialogue system\
-   Custom artwork\
-   Thematic lore

# AI Usage Declaration (FULL)

I used AI (ChatGPT) for the following specific parts:

### AI Used For:

-   All pixel-art **artwork** used in the GUI\
-   Parts of narrative **dialogue**, **NPC lines**, and **story text**\
-   Puzzle ideas and atmospheric descriptions\
-   Assistance writing **CSS styling**\
-   Debugging support for puzzles, item usage, and command logic\
-   Help writing commit messages and documentation\
-   Guidance for MVC structure and architecture decisions

### AI Not Used For:

-   Core game logic\
-   Parsing, command handling, and inventory mechanics\
-   Generics, OOP structures, Collection implementations\
-   Serialization and IO logic\
-   GUI controller code\
-   JUnit tests\
-   Overall architectural design

I have verified, understood, and can fully explain all submitted code.

