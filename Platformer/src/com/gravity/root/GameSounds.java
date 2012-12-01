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

    private static final List<Sound> jumpSounds;
    private static final List<Sound> slingshotSounds;
    private static final Music backgroundMusic;
    private static final Random random;

    static {
        try {
            //@formatter:off
            jumpSounds = Lists.newArrayList(
                    new Sound("./assets/Sound/jump_ha.ogg"),
                    new Sound("./assets/Sound/jump_ho.ogg"),
                    new Sound("./assets/Sound/jump_woo.ogg"),
                    new Sound("./assets/Sound/jump_hup.ogg"),
                    new Sound("./assets/Sound/yippee.wav"));
            slingshotSounds = Lists.newArrayList(
                    new Sound("./assets/Sound/slingshot_1.ogg"),
                    new Sound("./assets/Sound/slingshot_2.ogg"),
                    new Sound("./assets/Sound/slingshot_3.ogg"),
                    new Sound("./assets/Sound/slingshot_4.ogg"),
                    new Sound("./assets/Sound/slingshot_5.ogg"),
                    new Sound("./assets/Sound/slingshot_whee.ogg"));
            backgroundMusic = new Music("./assets/Sound/Caketown 1.ogg");
            //@formatter:on
        } catch (SlickException e) {
            throw new RuntimeException(e);
        }
        random = new Random();
    }

    public static void playBGM() {
        backgroundMusic.loop();

    }

    private static void playRandomSound(List<Sound> sounds) {
        sounds.get(random.nextInt(sounds.size())).play();
    }

    public static void playJumpSound() {
        playRandomSound(jumpSounds);
    }

    public static void playSlingshotSound() {
        playRandomSound(slingshotSounds);
    }
}
