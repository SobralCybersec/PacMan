/*
* SoundManager foi criado para gerenciar a reprodução de sons no jogo PacMan, 
* ele funciona carregando arquivos de áudio e reproduzindo através de métodos específicos, 
* em C nós podemos utilizar winmm.lib ou outras bibliotecas de áudio.
*/

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/*
* Classe principal para gerenciar sons no jogo PacMan
*/

public class SoundManager {
    private Clip backgroundMusic;
    
    public void playSound(String soundFile) {
        try {
            File file = new File(soundFile);
            if (file.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Erro ao reproduzir som: " + soundFile);
        }
    }
    
    public void playBackgroundMusic(String musicFile) {
        try {
            File file = new File(musicFile);
            if (file.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(audioStream);
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Erro ao reproduzir música de fundo: " + musicFile);
        }
    }
    
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }
}
