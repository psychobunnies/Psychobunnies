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

    private static final List<String> jumpSounds = Lists.newArrayList("jump_ha.ogg", "jump_ho.ogg", "jump_woo.ogg", "jump_hup.ogg");
    private static final List<String> slingshotSounds = Lists.newArrayList("slingshot_1.ogg", "slingshot_2.ogg", "slingshot_3.ogg",
            "slingshot_4.ogg", "slingshot_5.ogg", "slingshot_whee.ogg");
    private static final List<String> spikeSounds = Lists.newArrayList("death_spike.ogg", "death_crush3.ogg", "death_crush4.ogg", "death_crush5.ogg",
            "death_crush7.ogg");
    private static final List<String> crushSounds = Lists.newArrayList("death_crush1.ogg", "death_crush2.ogg", "death_crush6.ogg",
            "death_crush8.ogg", "death_crush5.ogg");
    private static final List<String> fellSounds = Lists.newArrayList("death_ugh.ogg");
    private static final List<String> bounceSounds = Lists.newArrayList();
    private static final Music backgroundMusic;
    private static final Random random;

    private static final String soundPath = "./assets/Sound/";

    static {

        try {
            backgroundMusic = new Music("./assets/Sound/Forminas.ogg");
        } catch (SlickException e) {
            throw new RuntimeException(e);
        }
        random = new Random();
    }

    public static enum Event {
        //@formatter:off
        JUMP         (jumpSounds),
        SLINGSHOT    (slingshotSounds),
        SPIKED       (spikeSounds),
        CRUSHED      (crushSounds),
        WALL_OF_DEATH(spikeSounds),
        FELL_OFF_MAP (fellSounds),
        BOUNCE       (bounceSounds);
        //@formatter:on

        private List<Sound> sounds;

        private Event(List<String> files) {
            sounds = Lists.newArrayList();
            for (String file : files) {
                try {
                    sounds.add(new Sound(soundPath + file));
                } catch (SlickException e) {
                    throw new RuntimeException("Could not load sound file: " + soundPath + file, e);
                }
            }
        }

        private void playRandomSound() {
            sounds.get(random.nextInt(sounds.size())).play();
        }
    }

    public static void playBGM() {
        backgroundMusic.loop();

    }

    public static void playSoundFor(Event event) {
        event.playRandomSound();
    }
}
