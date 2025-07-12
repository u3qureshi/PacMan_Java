# 🎮 Java Pac-Man Game

A fully functional Pac-Man game implementation built from scratch using Java Swing, demonstrating object-oriented programming principles, game development patterns, and advanced UI programming techniques.

## Features

### Core Gameplay
- **Classic Pac-Man mechanics** with smooth character movement
- **Intelligent ghost AI** with random movement patterns and collision detection
- **Dynamic food collection** system with real-time score tracking
- **Lives system** with game over and restart functionality
- **Level progression** - automatically loads new levels when all food is collected

### Advanced Input Handling
- **Input buffering system** - queues player input for smoother gameplay
- **Direction change optimization** - allows precise turns into narrow corridors
- **Responsive controls** with configurable buffer duration (300ms)

### User Experience
- **Multi-stage game flow**:
  - Start screen with clear instructions
  - Game over screen with final score display
  - Two-step restart process for better UX
- **Real-time UI updates** showing lives and score
- **Centered text rendering** using FontMetrics for professional presentation

### Technical Features
- **Collision detection system** for walls, ghosts, and food
- **Boundary checking** to prevent entities from moving off-screen
- **Timer-based game loop** running at 50ms intervals (20 FPS)
- **State management** with multiple game states (start, playing, game over, restart)

## 🛠️ Technical Implementation

### Architecture & Design Patterns
- **Object-Oriented Design**: Utilizes inner `Block` class for game entities
- **Component-based Architecture**: Separate concerns for rendering, input, and game logic
- **State Pattern**: Clean state management for different game phases
- **Observer Pattern**: KeyListener implementation for event-driven input handling

### Key Technical Concepts Demonstrated

#### 1. **Game Loop Architecture**
```java
Timer gameLoopTimer = new Timer(50, this); // 50ms interval
```
- Implements standard game loop pattern
- Separates update logic from rendering
- Consistent frame rate management

#### 2. **Collision Detection Algorithm**
```java
public boolean collision(Block a, Block b) {
    return a.x < b.x + b.width &&
           a.x + a.width > b.x &&
           a.y < b.y + b.height &&
           a.y + a.height > b.y;
}
```
- Axis-Aligned Bounding Box (AABB) collision detection
- Efficient O(1) collision checking

#### 3. **Input Buffering System**
- Implements sophisticated input queuing for responsive gameplay
- Time-based buffer expiration for balanced game feel
- Predictive movement validation

#### 4. **Dynamic Asset Management**
- Runtime image loading and management
- Efficient resource handling with ImageIcon
- Direction-based sprite switching

### Data Structures Used
- **HashSet<Block>**: For efficient entity management (walls, ghosts, food)
- **String Array**: For level map representation and parsing
- **Primitive Arrays**: For direction management and random selection

## 🎯 Skills Demonstrated

### Core Java Proficiencies
- **Object-Oriented Programming**: Inheritance, encapsulation, polymorphism
- **Collections Framework**: HashSet for efficient lookups and iterations
- **Exception Handling**: Resource loading and null safety
- **Java Swing**: Advanced GUI programming with custom components

### Software Engineering Practices
- **Clean Code**: Readable, well-documented methods and variables
- **Separation of Concerns**: Distinct responsibilities for each class/method
- **Performance Optimization**: Efficient algorithms and data structure choices
- **User Experience Design**: Intuitive controls and feedback systems

### Game Development Concepts
- **Real-time Systems**: Frame-rate independent game logic
- **State Management**: Complex state transitions and validation
- **Physics Simulation**: Movement, collision, and boundary detection
- **AI Implementation**: Random but constrained ghost movement patterns

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Any Java IDE (VS Code, IntelliJ IDEA, Eclipse)

### Installation & Setup
1. **Clone the repository**
   ```bash
   git clone [repository-url]
   cd Pacman
   ```

2. **Compile the project**
   ```bash
   javac -d bin src/*.java
   ```

3. **Run the game**
   ```bash
   java -cp bin App
   ```

### Project Structure
```
Pacman/
├── src/
│   ├── App.java          # Main application entry point
│   ├── PacMan.java       # Core game logic and rendering
│   └── images/           # Game sprites and assets
├── bin/                  # Compiled class files
└── README.md            # Project documentation
```

## 🎮 How to Play

1. **Start the Game**: Press any arrow key to begin
2. **Movement**: Use arrow keys to navigate Pac-Man
3. **Objective**: Collect all food while avoiding ghosts
4. **Lives**: You have 3 lives - contact with ghosts reduces lives
5. **Game Over**: When all lives are lost, press any key to restart
6. **Restart**: After game over, press any arrow key to play again

## 🔧 Technical Specifications

- **Language**: Java 8+
- **Framework**: Java Swing
- **Architecture**: Event-driven, component-based
- **Rendering**: Custom paintComponent implementation
- **Input**: KeyListener with buffered input system
- **Performance**: 20 FPS game loop with efficient collision detection

## 🎨 Code Quality Features

- **Comprehensive commenting** for complex algorithms
- **Consistent naming conventions** following Java standards
- **Modular design** with reusable components
- **Error handling** for resource loading and game state management
- **Performance considerations** in collision detection and rendering

## 🚀 Future Enhancements

- **Multiple Levels**: Additional maze layouts and difficulty progression
- **Power Pellets**: Temporary ghost vulnerability mechanic
- **High Score System**: Persistent score tracking with file I/O
- **Sound Effects**: Audio integration for enhanced user experience
- **Multiplayer Support**: Network programming for competitive play

## 📈 Learning Outcomes

This project demonstrates proficiency in:
- **Advanced Java Programming**: Complex OOP design and implementation
- **Game Development**: Real-time systems and user interaction
- **Algorithm Design**: Efficient collision detection and pathfinding
- **UI/UX Development**: Creating engaging and responsive interfaces
- **Software Architecture**: Designing maintainable and extensible code

---

*This project showcases practical application of Java programming concepts in a real-world scenario, demonstrating both technical skills and creative problem-solving abilities essential for professional software development.*
