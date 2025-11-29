/*
    PacMan Game
    @version 0.5
    Date: Nov 2025
    Description: A simple PacMan game implemented in Java using Swing, Jframe, and JPanel.
*/

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;


// Main PacMan game class
public class PacMan extends JPanel implements ActionListener, KeyListener {
    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U';
        int velocityX = 0;
        int velocityY = 0;

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
                this.velocityY = -tileSize/4;
            }
            else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize/4;
            }
            else if (this.direction == 'L') {
                this.velocityX = -tileSize/4;
                this.velocityY = 0;
            }
            else if (this.direction == 'R') {
                this.velocityX = tileSize/4;
                this.velocityY = 0;
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    // Variáveis de configuração do jogo
    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;
    private long lastEatSoundTime = 0;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    // Mapa do jogo representado como um array de strings
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
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;
    
    // Instanciando o SoundManager
    private SoundManager soundManager;


    // Construtor da classe PacMan
    PacMan() {


        /*
         * Dentro do construtor, configuramos o painel do jogo,
         * carregamos os recursos necessários, inicializamos o mapa
         * e iniciamos o loop do jogo.
        */

        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        soundManager = new SoundManager();

        soundManager.playBackgroundMusic("src/sounds/idle.wav");

        wallImage = new ImageIcon(getClass().getResource("./images/wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./images/blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./images/orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./images/pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./images/redGhost.png")).getImage();
        pacmanUpImage = new ImageIcon(getClass().getResource("./images/pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./images/pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./images/pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./images/pacmanRight.png")).getImage();
        
        loadMap();
        
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        
        gameLoop = new Timer(50, this);
        gameLoop.start();
    }


    // Carrega o mapa do jogo a partir do array de strings, criando os blocos correspondentes, como paredes, fantasmas, O Pacman e comidas
    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c*tileSize;
                int y = r*tileSize;

                if (tileMapChar == 'X') {
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                }
                else if (tileMapChar == 'b') {
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'o') {
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'p') {
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r') {
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'P') {
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                }
                else if (tileMapChar == ' ') {
                    Block food = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                }
            }
        }
    }


    // Método para desenhar os elementos do jogo na tela, estamos utilizando Graphics para o desenho dos elementos, o graphics nada mais é que uma ferramenta de desenho do java
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }


    // Desenhando os elementos do jogo na tela
    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }
        
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
        else {
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
    }

    // Lógica de movimentação do Pacman e dos fantasmas, detecção de colisões, pontuação e reinício do jogO
    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        for (Block ghost : ghosts) {
            if (collision(ghost, pacman)) {
                soundManager.playSound("src/sounds/death.wav");
                lives -= 1;
                if (lives == 0) {
                    gameOver = true;
                    soundManager.stopBackgroundMusic();
                    soundManager.playSound("src/sounds/gameover.wav");
                    return;
                }
                resetPositions();
            }

            if (ghost.y == tileSize*9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastEatSoundTime > 100) {
                    soundManager.playSound("src/sounds/eat.wav");
                    lastEatSoundTime = currentTime;
                }
            }
        }
        foods.remove(foodEaten);

        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    // Verifica colisão entre dois blocos
    public boolean collision(Block a, Block b) {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    // Reinicia as posições do Pacman e dos fantasmas depois de perder uma vida ou reiniciar o jogo
    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    // Método chamado a cada intervalo do timer para atualizar o estado do jogo
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    // Inútil, só para cumprir a interface do KeyListener
    @Override
    public void keyTyped(KeyEvent e) {}

    // Mesma coisa do acima
    @Override
    public void keyPressed(KeyEvent e) {}


    // Usamos o Override no método keyReleased para capturar as teclas liberadas e atualizar a direção do Pacman, usei switch somente para melhorar a legibilidade do código com boas práticas de programação
    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
            soundManager.playBackgroundMusic("src/sounds/idle.wav");
        }
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                pacman.updateDirection('U');
                break;
            case KeyEvent.VK_DOWN:
                pacman.updateDirection('D');
                break;
            case KeyEvent.VK_LEFT:
                pacman.updateDirection('L');
                break;
            case KeyEvent.VK_RIGHT:
                pacman.updateDirection('R');
                break;
        }

        switch (pacman.direction) {
            case 'U':
                pacman.image = pacmanUpImage;
                break;
            case 'D':
                pacman.image = pacmanDownImage;
                break;
            case 'L':
                pacman.image = pacmanLeftImage;
                break;
            case 'R':
                pacman.image = pacmanRightImage;
                break;
        }
    }
}
