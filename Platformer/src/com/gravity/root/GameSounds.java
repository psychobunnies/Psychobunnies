package com.gravity.root;

import java.util.List;
import java.util.Random;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import com.google.common.collect.Lists;

public final class GameSounds {

    private GameSounds() {
    }

    @SuppressWarnings("unused")
    private static final Music gameMusic;
    private static final List<Sound> sickRabbitBeats;
    private static final Music gameMusic3;
    private static final Random random;

    static {
        try {
            gameMusic = new Music("./assets/Sound/Forminas.wav");
            sickRabbitBeats = Lists.newArrayList(
                    new Sound("./assets/Sound/yippee_low.wav"),
                    new Sound("./assets/Sound/yippee.wav"),
                    new Sound("./assets/Sound/yippee.wav"));
            gameMusic3 = new Music("./assets/Sound/Caketown 1.ogg");
        } catch (SlickException e) {
            throw new RuntimeException(e);
        }
        random = new Random();
    }

    public static void playBGM() {
        gameMusic3.loop();

    }

    public static void playSickRabbitBeat() {
        sickRabbitBeats.get(random.nextInt(sickRabbitBeats.size())).play();
    }
}
