// Main UI and gameplay area
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedList;
import java.util.Queue;
public class SnakePanel extends JPanel implements ActionListener {
    
    private GridLayout gridLayout;
    private Rectangle[][] squareGrid = new Rectangle[30][30];
    private Queue<Rectangle> snake = new LinkedList<Rectangle>();
    private Thread t1, t2, t3;
    private Timer timer;
    private boolean down, up, left, right;
    private boolean waitingForNextInterval;
    
    private final int SQUARE_INTERVAL = 20;
    private final int PANEL_SIDE_LENGTH = 600;
    private final int SQUARE_SIDE = 18;
    private final int EASY_DELAY = 125;
    private final int MED_DELAY = 100;
    private final int HARD_DELAY = 50;
    private Color snakeColor = Color.white;
    private Color bgColor = Color.black;
    private Color borderColor = Color.red;
    private int lastX, lastY;
    private Rectangle fruit;
    private boolean growingSnake;
    private int growthLeft;
    private boolean gameOver = false;
    //private JLabel fruitImage;
    //private ImageIcon apple;
    private JLabel score = new JLabel("SCORE:" + snake.size(), JLabel.LEFT);
    private boolean isPaused = false;
    
    public SnakePanel() {
        
        //setLayout(null);
        addKeyListener(new KeyLstnr());
        setFocusable(true);
        
        timer = new Timer(MED_DELAY, this);
        
        score.setFont(new Font("Courier New", Font.BOLD, 14));
        score.setForeground(Color.white);

    }
    
    // sets up the game arena and essential elements of the game
    public void initGameplay() {
        
        waitingForNextInterval = false;
        growingSnake = false;
        growthLeft = 3;
        
        up = false;
        down = true;
        left = false;
        right = false;
        
        if (!gameOver) {
            
            initGraphics();
            //fruitImage = new JLabel();
            //apple = new ImageIcon("images/apple.png");
            //fruitImage.setIcon(apple);
        }
        
        else {
            
            gameOver = false;
            initSnake();
            setFruitLocation();
        }
        
        add(score);

        
        timer.start();
    }
    
    // sets the grid location for the fruit
    private void setFruitLocation() {
        
        int x, y;
        
        do {
            x = (int)(Math.random() * 29);
            y = (int)(Math.random() * 29);
            
        } while (snake.contains(squareGrid[x][y]));
        
        fruit = squareGrid[x][y];
        
        paint(Color.green, fruit);
    }
    
    // initializes snake's location in the center of the arena
    private void initSnake() {
        
        addSegment(15, 12);
        paintGap();
        addSegment(15, 13);
        paintGap();
        addSegment(15, 14);
        paintGap();
    }
    
    // initializes the game's graphics
    private void initGraphics() {
        
        setBackground(bgColor);
        
        for (int x=0; x<30; x++) {
            for (int y=0; y < 30; y++) {
                
                squareGrid[x][y] = new Rectangle(x*20+2, y*20+2, SQUARE_SIDE, SQUARE_SIDE);
                
                paint(bgColor, squareGrid[x][y]);
                
            }
        }
        
        initSnake();
        setFruitLocation();
    }
    
    // colors in grid spaces
    private void paint(Color newColor, int i, int j) {
        
        checkCollision(i, j);
        
        if (!gameOver) {
            Graphics g = getGraphics();
            g.setColor(newColor);
            g.fillRect(((int)(squareGrid[i][j].getX())), (int)(squareGrid[i][j].getY()), SQUARE_SIDE, SQUARE_SIDE);    
        }
    }
    // overloaded method adapted to receive a Rectangle as a parameter
    private void paint(Color newColor, Rectangle rect) {
        
        Graphics g = getGraphics();
        g.setColor(newColor);
        g.fillRect(((int)(rect.getX())), (int)(rect.getY()), SQUARE_SIDE, SQUARE_SIDE);
    }
    
    // adds a new segment to the snake
    private void addSegment(int x, int y) {
        
        paint(snakeColor, x, y);
        
        if (!gameOver) {
            snake.add(squareGrid[x][y]);
            lastX = x;
            lastY = y;
        }
    }
    
    // decides which square the snake will inhabit next and paints it appropriately
    private void updateSnake() {
        
        move();
        checkForFruit();
    }
    
    // check to see if the fruit has been eaten and sets a new location if necessary
    private void checkForFruit() {
        
        if (squareGrid[lastX][lastY].equals(fruit)) {
            
            setFruitLocation();
            growingSnake = true;
        }
    }
    
    // check to see if the snake has run into a wall or into itself
    private void checkCollision(int x, int y) {
        
        if (x<0 || y<0 || x>29 || y>29 || snake.contains(squareGrid[x][y])) { 
            
            gameOver = true;
            timer.stop();
            snake.clear();
            wipeArena();
        }
    }
    
    // pushes the new head of the snake to the tail of the queue and paints it
    private void move() {
        
        paintGap();
        
        if (up)
            addSegment(lastX, lastY-1);
        
        else if (down)
            addSegment(lastX, lastY+1);
        
        else if (left)
            addSegment(lastX-1, lastY);
        
        else if (right)
            addSegment(lastX+1, lastY);
        
        if (!growingSnake && !gameOver) {
            
            wipeGap();
            paint(bgColor, snake.remove());
        }
    }
    
    // paints 2 pixel gap between snake segments to make snake body contiguous
    private void paintGap() {
        
        Graphics g = getGraphics();
        g.setColor(snakeColor);
        
        if (up && lastY-1 >= 0) {
            
            g.fillRect((int)squareGrid[lastX][lastY-1].getX(), (int)squareGrid[lastX][lastY-1].getY() + SQUARE_SIDE, SQUARE_SIDE, 2);
        }
        
        else if (down && lastY+1 <= 29) {
            
            g.fillRect((int)squareGrid[lastX][lastY+1].getX(), (int)squareGrid[lastX][lastY+1].getY() - 2, SQUARE_SIDE, 2);
        }
        
        else if (left && lastX-1 >= 0) {
            
            g.fillRect((int)squareGrid[lastX-1][lastY].getX() + SQUARE_SIDE, (int)squareGrid[lastX-1][lastY].getY(), 2, SQUARE_SIDE);
        }
        else if (right && lastX+1 <=29) {
            
            g.fillRect((int)squareGrid[lastX+1][lastY].getX() - 2, (int)squareGrid[lastX+1][lastY].getY(), 2, SQUARE_SIDE);
        }    
    }
    
    // undo what paintGap did
    private void wipeGap() {
        
        int x = (int)snake.peek().getX();
        int y = (int)snake.peek().getY();
        
        Graphics g = getGraphics();
        g.setColor(bgColor);
        
        g.fillRect(x, y - 2, SQUARE_SIDE, 2); // top gap
        g.fillRect(x, y + SQUARE_SIDE, SQUARE_SIDE, 2); // bottom gap
        g.fillRect(x - 2, y, 2, SQUARE_SIDE); // left gap
        g.fillRect(x + SQUARE_SIDE, y, 2, SQUARE_SIDE); // right gap
    }
    
    private void wipeArena() {
        
        Graphics g = getGraphics();
        g.setColor(bgColor);
        
        g.fillRect(0, 0, 602, 604);
    }
    
    // pauses code execution for a certain number of milliseconds
    private void sleep(int milliseconds) {
        
        try {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e) {
            System.exit(-1);
        }
    }
    
    // everything in this method is fired every *DELAY milliseconds
    public void actionPerformed(ActionEvent a) {
        
        waitingForNextInterval = false;
        
        if (growingSnake && growthLeft > 0)
            growthLeft--;
        
        updateSnake();   
        
        if (growthLeft == 0) {
            
            growingSnake = false;
            growthLeft = 3;
        }
        
        score.setText("SCORE:" + snake.size());
        repaint(score.getX(), score.getY(), score.getWidth(), score.getHeight());
        //getGraphics().setColor(Color.black);
        //getGraphics().fillRect(score.getX(), score.getY(), score.getWidth(), score.getHeight());
        //score.update(getGraphics());
    }
    
    // listener to receive directional input from the keyboard
    private class KeyLstnr extends KeyAdapter {
        
        public void keyPressed(KeyEvent e) {
            
            int key = e.getKeyCode();
            
            if (gameOver) {
                
                initGameplay();
                return;
            }
            
            else if (key == KeyEvent.VK_P) {
                
                if (!isPaused) {
                    
                    timer.stop();
                    isPaused = true;
                }
                
                else {
                    
                    timer.start();
                    isPaused = false;
                }
            }
            
            if (waitingForNextInterval) return;
            
            if (key == KeyEvent.VK_UP && !down) {
                
                up = true;
                left = false;
                right = false;
            }
            
            else if (key == KeyEvent.VK_DOWN && !up) {
                
                down = true;
                left = false;
                right = false;
            }
            
            else if (key == KeyEvent.VK_LEFT && !right) {
                
                left = true;
                up = false;
                down = false;
            }
            
            else if (key == KeyEvent.VK_RIGHT && !left) {
                
                right = true;
                up = false;
                down = false;
            }
            
            waitingForNextInterval = true;
        }
    }
}