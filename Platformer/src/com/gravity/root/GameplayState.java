package com.gravity.root;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMapPlus;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.gravity.camera.Camera;
import com.gravity.camera.PanningCamera;
import com.gravity.camera.PlayerStalkingCamera;
import com.gravity.fauna.Player;
import com.gravity.fauna.PlayerKeyboardController;
import com.gravity.fauna.PlayerKeyboardController.Control;
import com.gravity.fauna.PlayerRenderer;
import com.gravity.map.LevelFinishZone;
import com.gravity.map.TileWorld;
import com.gravity.map.TileWorldRenderer;
import com.gravity.physics.Collidable;
import com.gravity.physics.CollisionEngine;
import com.gravity.physics.GravityPhysics;
import com.gravity.physics.LayeredCollisionEngine;
import com.gravity.physics.PhysicalState;
import com.gravity.physics.PhysicsFactory;

public class GameplayState extends BasicGameState implements GameplayControl {

    final int ID;

    @Override
    public int getID() {
        return ID;
    }

    private TileWorld map;
    private Player playerA, playerB;
    private List<Renderer> renderers = new ArrayList<Renderer>();
    private PlayerKeyboardController controllerA, controllerB;
    private List<UpdateCycling> updaters;
    private CollisionEngine collider;
    private GameContainer container;
    private StateBasedGame game;
    private GravityPhysics gravityPhysics;
    private LevelFinishZone finish;
    private Player finishedPlayer;
    private final Random rand = new Random();
    private Camera camera;

    private boolean leftRemapped, rightRemapped, jumpRemapped;
    private Color lightPink = Color.pink.brighter();
    private Color lightYellow = new Color(1, 1, 0.5f);
    private Control remappedControl;
    private float remappedDecay;
    private Polygon controlArrow = new Polygon(new float[] { -50, 10, 20, 10, -10, 50, 10, 50, 50, 0, 10, -50, -10, -50, 20, -10, -50, -10 });

    public GameplayState(String levelName, String mapFile, int id) throws SlickException {
        ID = id;
        map = new TileWorld(levelName, new TiledMapPlus(mapFile), this);
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        this.container = container;
        this.game = game;
        map.initialize();
        reloadGame();
        GameSounds.playBGM();
    }

    public void reloadGame() {
        pauseRender();
        pauseUpdate();
        collider = new LayeredCollisionEngine();
        updaters = Lists.newLinkedList();
        gravityPhysics = PhysicsFactory.createDefaultGravityPhysics(collider);

        // Map initialization
        for (Collidable c : map.getTerrainEntitiesCallColls()) {
            collider.addCollidable(c, LayeredCollisionEngine.FLORA_LAYER);
        }
        for (Collidable c : map.getTerrainEntitiesNoCalls()) {
            collider.addCollidable(c, LayeredCollisionEngine.FLORA_LAYER);
        }
        finish = new LevelFinishZone(map.getFinishRect(), this);
        collider.addCollidable(finish, LayeredCollisionEngine.FLORA_LAYER);
        finishedPlayer = null;
        System.out.println("Got finish zone at: " + finish + " for map " + map);
        updaters.addAll(map.getTriggeredTexts());

        // Player initialization
        List<Vector2f> playerPositions = map.getPlayerStartPositions();
        Preconditions.checkArgument(playerPositions.size() == 2,
                "Invalid number of player start positions: expected 2, got " + playerPositions.size());
        playerA = new Player(this, gravityPhysics, "pink", playerPositions.get(0));
        playerB = new Player(this, gravityPhysics, "yellow", playerPositions.get(1));
        updaters.add(playerA);
        updaters.add(playerB);
        renderers.add(new TileWorldRenderer(map));
        renderers.add(new PlayerRenderer(playerA));
        renderers.add(new PlayerRenderer(playerB));
        collider.addCollidable(playerA, LayeredCollisionEngine.FAUNA_LAYER);
        collider.addCollidable(playerB, LayeredCollisionEngine.FAUNA_LAYER);
        //@formatter:off
        controllerA = new PlayerKeyboardController(playerA)
                .setLeft(Input.KEY_A).setRight(Input.KEY_D)
                .setJump(Input.KEY_W).setMisc(Input.KEY_SPACE);
        controllerB = new PlayerKeyboardController(playerB)
                .setLeft(Input.KEY_LEFT).setRight(Input.KEY_RIGHT)
                .setJump(Input.KEY_UP).setMisc(Input.KEY_ENTER);
        //@formatter:on
        leftRemapped = false;
        jumpRemapped = false;
        rightRemapped = false;

        // Camera initialization
        PanningCamera pancam = new PanningCamera(2000, new Vector2f(0, 0), new Vector2f(0.035f, 0), new Vector2f(map.getWidth()
                - container.getWidth(), 0), container.getWidth(), container.getHeight());
        camera = pancam;
        if (ID == 1002) {
            camera = new PlayerStalkingCamera(container.getWidth(), container.getHeight(), new Vector2f(0, 0), new Vector2f(map.getWidth(),
                    map.getHeight()), playerA, playerB);
        }
        updaters.add(pancam);

        unpauseRender();
        unpauseUpdate();
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        Vector2f offset = camera.getViewport().getPosition();
        for (Renderer r : renderers) {
            r.render(g, (int) offset.x, (int) offset.y);
        }

        // Draw slingshot indicator
        if (playerA.slingshot) {
            g.setColor(Color.pink);
            g.setLineWidth(playerA.slingshotStrength * 10);
            g.drawLine(playerA.getRect(0).getCenter().x + offset.x, playerA.getRect(0).getCenter().y + offset.y, playerB.getRect(0).getCenter().x
                    + offset.x, playerB.getRect(0).getCenter().y + offset.y);
        }
        if (playerB.slingshot) {
            g.setColor(Color.yellow);
            g.setLineWidth(playerB.slingshotStrength * 10);
            g.drawLine(playerB.getRect(0).getCenter().x + offset.x, playerB.getRect(0).getCenter().y + offset.y, playerA.getRect(0).getCenter().x
                    + offset.x, playerA.getRect(0).getCenter().y + offset.y);

        }
        g.setColor(Color.white);

        if (remappedDecay > 0) {
            g.pushTransform();
            g.translate(512, 384);
            g.scale(6 * remappedDecay, 6 * remappedDecay);
            switch (remappedControl) {
            case JUMP:
                g.rotate(0, 0, 270);
                if (jumpRemapped) {
                    g.setColor(Color.red);
                }
                break;
            case LEFT:
                if (leftRemapped) {
                    g.setColor(Color.red);
                }
                g.rotate(0, 0, 180);
                break;
            case RIGHT:
                if (rightRemapped) {
                    g.setColor(Color.red);
                }
                break;
            default:
                break;
            }

            g.fill(controlArrow);
            g.resetTransform();
            g.popTransform();
        }

        g.pushTransform();
        g.translate(32, 32);
        g.setColor(lightPink);
        g.fillRoundRect(0, 0, 320, 64, 10);
        renderControls(g, "Pink", controllerA);
        g.resetTransform();
        g.popTransform();

        g.pushTransform();
        g.translate(672, 32);
        g.setColor(lightYellow);
        g.fillRoundRect(0, 0, 320, 64, 10);
        renderControls(g, "Yellow", controllerB);
        g.resetTransform();
        g.popTransform();
    }

    public void renderControls(Graphics g, String playername, PlayerKeyboardController controller) {
        g.setColor(Color.red);
        if (jumpRemapped) {
            g.fillRoundRect(120, 12, 80, 16, 3);
        }
        if (leftRemapped) {
            g.fillRoundRect(50, 36, 80, 16, 3);
        }
        if (rightRemapped) {
            g.fillRoundRect(190, 36, 80, 12, 3);
        }
        g.setColor(Color.black);
        g.drawString(playername, 12, 12);
        g.drawString("Jump: " + Input.getKeyName(controller.getJump()), 120, 12);
        g.drawString("Left: " + Input.getKeyName(controller.getLeft()), 50, 36);
        g.drawString("Right: " + Input.getKeyName(controller.getRight()), 190, 36);
        g.setColor(Color.white);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        for (UpdateCycling uc : updaters) {
            uc.startUpdate(delta);
        }
        collider.update(delta);
        for (UpdateCycling uc : updaters) {
            uc.finishUpdate(delta);
        }

        // Tell player when to die if off the screen
        float xOffset = camera.getViewport().getX();
        checkDeath(playerA, xOffset);
        checkDeath(playerB, xOffset);

        // Prevent player from going off right side
        checkRightSide(playerA, xOffset);
        checkRightSide(playerB, xOffset);
        remappedDecay -= delta / 1000f;
    }

    private void checkDeath(Player player, float offsetX2) {
        Vector2f pos = player.getPosition(0f);
        if (pos.x + offsetX2 + 32 < 0) {
            playerDies(player);
        }
    }

    private void checkRightSide(Player player, float offsetX2) {
        PhysicalState state = player.getPhysicalState();
        player.setPhysicalState(new PhysicalState(state.getRectangle().setPosition(
                Math.min(state.getRectangle().getX(), -offsetX2 + container.getWidth() - 32), state.getRectangle().getY()), state.velX, state.velY,
                state.accX, state.accY));
    }

    @Override
    public void keyPressed(int key, char c) {
        if (!controllerA.handleKeyPress(key)) {
            controllerB.handleKeyPress(key);
        }
    }

    @Override
    public void keyReleased(int key, char c) {
        if (!controllerA.handleKeyRelease(key)) {
            controllerB.handleKeyRelease(key);
        }
    }

    @Override
    public void playerDies(Player player) {
        game.enterState(GameOverState.ID);
    }

    @Override
    public void swapPlayerControls(Control ctrl) {
        int akey, bkey;
        akey = controllerA.getControl(ctrl);
        bkey = controllerB.getControl(ctrl);
        controllerA.setControl(ctrl, bkey);
        controllerB.setControl(ctrl, akey);
        switch (ctrl) {
        case JUMP:
            jumpRemapped = !jumpRemapped;
            break;
        case LEFT:
            leftRemapped = !leftRemapped;
            break;
        case RIGHT:
            rightRemapped = !rightRemapped;
            break;
        default:
            break;
        }
        remappedControl = ctrl;
        remappedDecay = 1;
    }

    @Override
    public void playerHitSpikes(Player player) {
        // swapPlayerControls(Control.getById(rand.nextInt(Control.size())));
        playerDies(player);
        System.out.println("Player " + player.toString() + " hit spikes -- remapping controls.");
        System.out.println("ControllerA: " + controllerA.toString());
        System.out.println("ControllerB: " + controllerB.toString());
    }

    /**
     * 
     */
    @Override
    public void specialMoveSlingshot(Player slingshoter, float strength) {
        if (slingshoter == playerA) {
            playerB.slingshotMe(strength, playerA.getPosition(0).copy().sub(playerB.getPosition(0)));
        } else if (slingshoter == playerB) {
            playerA.slingshotMe(strength, playerB.getPosition(0).copy().sub(playerA.getPosition(0)));
        } else {
            throw new RuntimeException("Who the **** called this method?");
            // Now now, Kevin, we don't use that kind of language in these parts.
        }

    }

    @Override
    public void playerFinishes(Player player) {
        if (finishedPlayer == null) {
            finishedPlayer = player;
        } else if (finishedPlayer != player) {
            game.enterState(GameWinState.ID);
        }
    }
}
