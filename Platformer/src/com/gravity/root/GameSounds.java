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

    private static final List<String> jumpSounds = Lists.newArrayList("jump_ha.ogg", "jump_ho.ogg", "jump_hup.ogg");
    private static final List<String> slingshotSounds = Lists.newArrayList("slingshot_1.ogg", "slingshot_2.ogg", "slingshot_3.ogg",
            "slingshot_4.ogg");
    private static final List<String> noSlingshotSounds = Lists.newArrayList("slingshot_fail.ogg");
    private static final List<String> wheeSounds = Lists.newArrayList("slingshot_whee.ogg", "jump_woo.ogg");
    private static final List<String> spikeSounds = Lists.newArrayList("death_spike.ogg", "death_crush3.ogg", "death_crush4.ogg", "death_crush5.ogg",
            "death_crush7.ogg");
    private static final List<String> crushSounds = Lists.newArrayList("death_crush1.ogg", "death_crush2.ogg", "death_crush6.ogg",
            "death_crush8.ogg", "death_crush5.ogg");
    private static final List<String> fellSounds = Lists.newArrayList("death_ugh.ogg");
    private static final List<String> bounceSounds = Lists.newArrayList(/*"bounce.ogg"*/);
    private static final List<String> bonkSounds = Lists.newArrayList("ceiling_bonk.ogg");
    private static final List<String> cageSlams = Lists.newArrayList("cage_shut.ogg");
    private static final List<String> endOfGame = Lists.newArrayList("city_endoflevel.ogg");
    private static final Music backgroundMusic;
    private static final Music menuMusic;
    private static final Random random;

    private static final String soundPath = "./assets/Sound/";

    static {

        try {
            backgroundMusic = new Music("./assets/Sound/Caketown 1.ogg", true);
            menuMusic = new Music("./assets/Sound/levelselect_theme.ogg", true);
        } catch (SlickException e) {
            throw new RuntimeException(e);
        }
        random = new Random();
    }

    public static enum Event {
        //@formatter:off
        JUMP         (jumpSounds),
        SLINGSHOT    (slingshotSounds),
        NO_SLING     (noSlingshotSounds),
        WHEE         (wheeSounds),
        SPIKED       (spikeSounds),
        CRUSHED      (crushSounds),
        WALL_OF_DEATH(spikeSounds),
        FELL_OFF_MAP (fellSounds),
        BOUNCE       (bounceSounds),
        BONK         (bonkSounds),
        CAGE_SLAM    (cageSlams),
        END_OF_GAME  (endOfGame);
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
        if (!backgroundMusic.playing()) {
            backgroundMusic.loop();
        }
    }

    public static void playMenuMusic() {
        if (!menuMusic.playing()) {
            menuMusic.loop();
        }
    }

    public static void playSoundFor(Event event) {
        event.playRandomSound();
    }
}
