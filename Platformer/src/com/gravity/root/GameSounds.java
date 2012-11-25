package com.gravity.root;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public final class GameSounds {

    private GameSounds() {
    }

    @SuppressWarnings("unused")
    private static final Music gameMusic;
    private static final Sound sickRabbitBeat;
    private static final Music gameMusic3;

    static {
        try {
            gameMusic = new Music("./assets/Sound/Forminas.wav");
            sickRabbitBeat = new Sound("./assets/Sound/yippee.wav");
            gameMusic3 = new Music("./assets/Sound/Caketown 1.ogg");
        } catch (SlickException e) {
            throw new RuntimeException(e);
        }
    }

    public static void playBGM() {
        gameMusic3.loop();

    }

    public static void playSickRabbitBeat() {
        sickRabbitBeat.play();
    }
}
