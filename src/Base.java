import javax.swing.JFrame;


// Nossa classe principal que inicia o jogo Pacman, criando a janela do jogo e adicionando o painel do jogo

public class Base {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Pacman");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        PacMan pacmanGame = new PacMan();
        frame.add(pacmanGame);
        frame.pack();
        frame.setVisible(true);
        pacmanGame.requestFocus();
    }
}
