import java.awt.*;
import java.awt.event.*;
import java.security.Key;
import javax.swing.*;
import java.util.HashSet;
import java.util.Random;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U'; // U = up, D = down, L = left, R = right
        int velocityX = 0; // velocity in x direction
        int velocityY = 0; // velocity in y direction

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize / 4;
            } else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize / 4;
            } else if (this.direction == 'L') {
                this.velocityX = -tileSize / 4;
                this.velocityY = 0;
            } else if (this.direction == 'R') {
                this.velocityX = tileSize / 4;
                this.velocityY = 0;
            }
        }

        void reset() {
            this.x = startX;
            this.y = startY;
        }
    }

    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;

    private Image pacManUpImage;
    private Image pacManDownImage;
    private Image pacManLeftImage;
    private Image pacManRightImage;

    // X = wall, O = skip, P = pac man, ' ' = food
    // Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls;
    HashSet<Block> ghosts;
    HashSet<Block> foods;
    Block pacMan;

    Timer gameLoopTimer;
    char[] directions = { 'U', 'D', 'L', 'R' }; // up down left right
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;
    boolean gameStarted = false; // Add this to track if game has started
    boolean waitingForRestart = false; // Add this to track if we're waiting to restart after game over
    char nextDirection = 'R'; // Buffer for the next direction the player wants to go (start with right)
    long nextDirectionTime = 0; // Timestamp when the direction was buffered
    long directionBufferDuration = 300; // How long to keep the buffered direction (in milliseconds)

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this); // Add key listener to capture key events and PacMan() will be listening for key
                              // events according to the KeyListener interface functions implemented below
        setFocusable(true); // Set focusable to true so that the panel can receive key events

        // Load images
        wallImage = new ImageIcon(getClass().getResource("./images/wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./images/blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./images/orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./images/pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./images/redGhost.png")).getImage();
        pacManUpImage = new ImageIcon(getClass().getResource("./images/pacmanUp.png")).getImage();
        pacManDownImage = new ImageIcon(getClass().getResource("./images/pacmanDown.png")).getImage();
        pacManLeftImage = new ImageIcon(getClass().getResource("./images/pacmanLeft.png")).getImage();
        pacManRightImage = new ImageIcon(getClass().getResource("./images/pacmanRight.png")).getImage();

        loadMap();
        for (Block ghost : ghosts) {
            // Randomly assign a direction to each ghost
            char randomDirection = directions[random.nextInt(directions.length)];
            ghost.updateDirection(randomDirection);
        }

        // Create the timer but don't start it yet - wait for first arrow key press
        gameLoopTimer = new Timer(50, this); // 50 ms delay for game loop
        // Don't start the timer here - wait for user input

    }

    public void loadMap() {
        walls = new HashSet<Block>();
        ghosts = new HashSet<Block>();
        foods = new HashSet<Block>();

        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                String tileMapRow = tileMap[row];
                char tileMapChar = tileMapRow.charAt(col);

                // in order to draw, we need to know the x and y coordinates as well as the
                // width and height of each block (w and h are 32 px)
                // Remember 0,0 is the top left corner of the board, so to get the x and y
                // coordinates of each block, we multiply the row and column by the tile size
                // And to draw the block, we start at the top left corner of the block, which is
                // at (col * tileSize, row * tileSize)
                int x = col * tileSize;
                int y = row * tileSize;

                if (tileMapChar == 'X') { // block wall
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                } else if (tileMapChar == 'b') { // blue ghost
                    Block blueGhost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(blueGhost);
                } else if (tileMapChar == 'o') { // orange ghost
                    Block orangeGhost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(orangeGhost);
                } else if (tileMapChar == 'p') { // pink ghost
                    Block pinkGhost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(pinkGhost);
                } else if (tileMapChar == 'r') { // red ghost
                    Block redGhost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(redGhost);
                } else if (tileMapChar == 'P') { // Pacman
                    pacMan = new Block(pacManRightImage, x, y, tileSize, tileSize);
                    pacMan.updateDirection('R'); // Make sure Pac-Man starts moving right
                } else if (tileMapChar == ' ') { // food
                    Block food = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacMan.image, pacMan.x, pacMan.y, pacMan.width, pacMan.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }
        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }
        for (Block food : foods) {
            g.setColor(Color.MAGENTA);
            g.fillRect(food.x, food.y, food.width, food.height);

        }

        // score
        g.setFont(new Font("Arial", Font.BOLD, 18));
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            String gameOverMessage = "Game Over! Press any arrow key to continue.";
            FontMetrics fm = g.getFontMetrics();
            int messageWidth = fm.stringWidth(gameOverMessage);
            int x = (boardWidth - messageWidth) / 2;
            int y = boardHeight / 2;
            g.drawString(gameOverMessage, x, y);

            // Also show the final score below the game over message
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.setColor(Color.WHITE);
            String scoreMessage = "Final Score: " + String.valueOf(score);
            FontMetrics scoreFm = g.getFontMetrics();
            int scoreWidth = scoreFm.stringWidth(scoreMessage);
            int scoreX = (boardWidth - scoreWidth) / 2;
            int scoreY = y + 40; // Position below the game over message
            g.drawString(scoreMessage, scoreX, scoreY);
        } else if (waitingForRestart || !gameStarted) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            String startMessage = "Press any arrow key to START";
            FontMetrics fm = g.getFontMetrics();
            int messageWidth = fm.stringWidth(startMessage);
            int x = (boardWidth - messageWidth) / 2;
            int y = boardHeight / 2;
            g.drawString(startMessage, x, y);
        } else {
            g.setColor(Color.WHITE);
            g.drawString("Lives: " + String.valueOf(lives) + " | Score: " + String.valueOf(score), tileSize / 2,
                    tileSize / 2);
        }

    }

    // This method will be called when the player presses a key
    // We will update the direction of Pacman based on the key pressed
    // We will also update the image of Pacman based on the direction
    // We will also update the velocity of Pacman based on the direction
    public void move() {
        // Check if the buffered direction has expired
        long currentTime = System.currentTimeMillis();
        boolean directionStillValid = (currentTime - nextDirectionTime) <= directionBufferDuration;

        // Check if we can move in the intended direction (only if it hasn't expired)
        if (directionStillValid && nextDirection != pacMan.direction && canMoveInDirection(pacMan, nextDirection)) {
            pacMan.direction = nextDirection;
            pacMan.updateVelocity();

            // Update Pac-Man's image based on new direction
            if (pacMan.direction == 'U') {
                pacMan.image = pacManUpImage;
            } else if (pacMan.direction == 'D') {
                pacMan.image = pacManDownImage;
            } else if (pacMan.direction == 'L') {
                pacMan.image = pacManLeftImage;
            } else if (pacMan.direction == 'R') {
                pacMan.image = pacManRightImage;
            }

            // Clear the buffered direction since we used it
            nextDirection = pacMan.direction;
        }

        pacMan.x += pacMan.velocityX;
        pacMan.y += pacMan.velocityY;

        for (Block wall : walls) {
            if (collision(pacMan, wall)) {
                pacMan.x -= pacMan.velocityX;
                pacMan.y -= pacMan.velocityY;
                break;
            }
        }

        // Check for collisions with ghosts
        for (Block ghost : ghosts) {
            if (collision(ghost, pacMan)) {
                lives -= 1;
                if (lives == 0) {
                    gameOver = true;
                    return;
                }
                // Reset to start state when losing a life
                gameStarted = false;
                gameLoopTimer.stop();
                resetPositions();
                return; // Exit early to stop processing this frame
            }

            // Force ghosts to go up when in the middle horizontal tunnel area (like the example)
            if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }
            
            // Move the ghost
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            
            // Check for wall collisions or boundary collisions - much simpler approach
            for (Block wall : walls) {
                if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        // Check for collisions with food
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacMan, food)) {
                foodEaten = food;
                score += 10;
            }
        }

        foods.remove(foodEaten);

        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }
    // This method checks if two blocks are colliding
    // It checks if the bounding boxes of the two blocks intersect
    // If they do, it returns true, otherwise it returns false

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPositions() {
        // Reset Pacman's position
        pacMan.reset();
        pacMan.updateDirection('R'); // Reset direction to right
        nextDirection = 'R'; // Reset the buffered direction too
        nextDirectionTime = 0; // Clear the timestamp to invalidate any buffered direction
        // Don't manually set velocities to 0 - let updateDirection handle them

        // Reset each ghost's position
        for (Block ghost : ghosts) {
            ghost.reset();
            // Randomly assign a new direction to the ghost after resetting its position
            char randomDirection = directions[random.nextInt(directions.length)];
            ghost.updateDirection(randomDirection);
        }
    }

    // Add this helper method to check if movement in a direction is possible
    public boolean canMoveInDirection(Block block, char direction) {
        // Calculate test position based on direction
        int testVelocityX = 0;
        int testVelocityY = 0;

        if (direction == 'U') {
            testVelocityY = -tileSize / 4;
        } else if (direction == 'D') {
            testVelocityY = tileSize / 4;
        } else if (direction == 'L') {
            testVelocityX = -tileSize / 4;
        } else if (direction == 'R') {
            testVelocityX = tileSize / 4;
        }

        int testX = block.x + testVelocityX;
        int testY = block.y + testVelocityY;

        // Check wall collisions
        for (Block wall : walls) {
            if (testX < wall.x + wall.width &&
                    testX + block.width > wall.x &&
                    testY < wall.y + wall.height &&
                    testY + block.height > wall.y) {
                return false;
            }
        }

        // Check boundaries
        if (testX < 0 || testX + block.width > boardWidth ||
                testY < 0 || testY + block.height > boardHeight) {
            return false;
        }

        return true;
    }

    @Override
    // Every 50 milliseconds, this method will be called
    // This is where we will update the game state and repaint the screen
    public void actionPerformed(ActionEvent e) {
        if (gameStarted && !gameOver) {
            move(); // Only move if game has started and not over
        }
        repaint(); // Always repaint to show the start message or game state
        if (gameOver) {
            gameLoopTimer.stop(); // Stop the game loop timer if the game is over
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            // ANY key moves from game over to the start screen
            gameOver = false;
            waitingForRestart = true;
            gameStarted = false;
            return;
        }

        if (waitingForRestart) {
            // Only arrow keys work when waiting for restart
            boolean isArrowKey = (e.getKeyCode() == KeyEvent.VK_UP ||
                    e.getKeyCode() == KeyEvent.VK_DOWN ||
                    e.getKeyCode() == KeyEvent.VK_LEFT ||
                    e.getKeyCode() == KeyEvent.VK_RIGHT);

            if (isArrowKey) {
                // Arrow key pressed - reset the game and start
                loadMap();
                resetPositions();
                waitingForRestart = false;
                gameStarted = true;
                lives = 3;
                score = 0;
                gameLoopTimer.start();

                // Immediately update Pac-Man's direction and image
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    pacMan.updateDirection('U');
                    pacMan.image = pacManUpImage;
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    pacMan.updateDirection('D');
                    pacMan.image = pacManDownImage;
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    pacMan.updateDirection('L');
                    pacMan.image = pacManLeftImage;
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    pacMan.updateDirection('R');
                    pacMan.image = pacManRightImage;
                }
            }
            return; // Don't process any other keys when waiting for restart
        }

        // Check if this is an arrow key
        boolean isArrowKey = (e.getKeyCode() == KeyEvent.VK_UP ||
                e.getKeyCode() == KeyEvent.VK_DOWN ||
                e.getKeyCode() == KeyEvent.VK_LEFT ||
                e.getKeyCode() == KeyEvent.VK_RIGHT);

        if (!gameStarted && isArrowKey) {
            // First arrow key pressed - start the game
            gameStarted = true;
            gameLoopTimer.start();

            // Immediately update Pac-Man's direction and image
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                pacMan.updateDirection('U');
                pacMan.image = pacManUpImage;
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                pacMan.updateDirection('D');
                pacMan.image = pacManDownImage;
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                pacMan.updateDirection('L');
                pacMan.image = pacManLeftImage;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                pacMan.updateDirection('R');
                pacMan.image = pacManRightImage;
            }
        }

        if (isArrowKey && gameStarted) {
            // Store the intended direction and timestamp (only when game is running)
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                nextDirection = 'U';
                nextDirectionTime = System.currentTimeMillis();
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                nextDirection = 'D';
                nextDirectionTime = System.currentTimeMillis();
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                nextDirection = 'L';
                nextDirectionTime = System.currentTimeMillis();
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                nextDirection = 'R';
                nextDirectionTime = System.currentTimeMillis();
            }
        }
    }

}
