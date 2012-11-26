package com.gravity.root;

import java.util.Collection;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.Layer;
import org.newdawn.slick.tiled.Tile;
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
import com.gravity.geom.Rect;
import com.gravity.map.LevelFinishZone;
import com.gravity.map.TileType;
import com.gravity.map.TileWorld;
import com.gravity.map.TileWorldRenderer;
import com.gravity.map.tiles.DisappearingTileController;
import com.gravity.map.tiles.FallingTile;
import com.gravity.map.tiles.MovingCollidable;
import com.gravity.map.tiles.PlayerKeyedTile;
import com.gravity.map.tiles.TileRendererDelegate;
import com.gravity.physics.Collidable;
import com.gravity.physics.CollisionEngine;
import com.gravity.physics.GravityPhysics;
import com.gravity.physics.LayeredCollisionEngine;
import com.gravity.physics.PhysicalState;
import com.gravity.physics.PhysicsFactory;

public class GameplayState extends BasicGameState implements GameplayControl, Resetable {

    final int ID;

    public static final String PANNING_CAMERA = "panning";
    public static final String STALKING_CAMERA = "stalking";

    @Override
    public int getID() {
        return ID;
    }

    protected TileWorld map;
    protected Player playerA, playerB;
    protected RenderList renderers;
    protected PlayerKeyboardController controllerA, controllerB;
    protected List<UpdateCycling> updaters;
    protected CollisionEngine collider;
    protected GameContainer container;
    protected StateBasedGame game;
    protected GravityPhysics gravityPhysics;
    protected LevelFinishZone finish;
    protected Player finishedPlayer;
    protected Camera camera;

    private boolean leftRemapped, rightRemapped, jumpRemapped;
    private Control remappedControl;
    private float remappedDecay;
    private Polygon controlArrow = new Polygon(new float[] { -50, 10, 20, 10, -10, 50, 10, 50, 50, 0, 10, -50, -10, -50, 20, -10, -50, -10 });

    protected final List<Resetable> resetableTiles = Lists.newArrayList();

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

    public void reloadGame() throws SlickException {
        pauseRender();
        pauseUpdate();

        collider = new LayeredCollisionEngine();
        updaters = Lists.newLinkedList();
        renderers = new RenderList();
        resetableTiles.clear();

        for (DisappearingTileController controller : map.reinitializeDisappearingLayers(collider)) {
            updaters.add(controller);
        }

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
        Collection<List<MovingCollidable>> movingColls = map.getMovingCollMap().values();
        for (List<MovingCollidable> l : movingColls) {
            updaters.addAll(l);
        }

        // Player initialization
        List<Vector2f> playerPositions = map.getPlayerStartPositions();
        Preconditions.checkArgument(playerPositions.size() == 2,
                "Invalid number of player start positions: expected 2, got " + playerPositions.size());
        playerA = new Player(this, gravityPhysics, "pink", playerPositions.get(0));
        playerB = new Player(this, gravityPhysics, "yellow", playerPositions.get(1));
        updaters.add(playerA);
        updaters.add(playerB);
        renderers.add(new TileWorldRenderer(map), RenderList.TERRA);
        renderers.add(new PlayerRenderer(playerA), RenderList.FAUNA);
        renderers.add(new PlayerRenderer(playerB), RenderList.FAUNA);
        collider.addCollidable(playerA, LayeredCollisionEngine.FAUNA_LAYER);
        collider.addCollidable(playerB, LayeredCollisionEngine.FAUNA_LAYER);
        //@formatter:off
        controllerA = new PlayerKeyboardController(playerA)
                .setLeft(Input.KEY_A).setRight(Input.KEY_D)
                .setJump(Input.KEY_W).setMisc(Input.KEY_TAB);
        controllerB = new PlayerKeyboardController(playerB)
                .setLeft(Input.KEY_LEFT).setRight(Input.KEY_RIGHT)
                .setJump(Input.KEY_UP).setMisc(Input.KEY_SPACE);
        //@formatter:on
        leftRemapped = false;
        jumpRemapped = false;
        rightRemapped = false;

        // Map-tile construction
        TiledMapPlus tiledMap = map.map;
        Layer pkLayer = tiledMap.getLayer(TileWorld.PLAYERKEYED_LAYER_NAME);
        if (pkLayer != null) {
            pkLayer.visible = false;
            TileRendererDelegate rendererDelegate = new TileRendererDelegate(tiledMap, TileType.PLAYER_KEYED_UNSET);
            TileRendererDelegate rendererDelegateYellow = new TileRendererDelegate(tiledMap, TileType.PLAYER_KEYED_YELLOW);
            TileRendererDelegate rendererDelegatePink = new TileRendererDelegate(tiledMap, TileType.PLAYER_KEYED_PINK);
            try {
                for (Tile tile : pkLayer.getTiles()) {
                    PlayerKeyedTile pkTile = new PlayerKeyedTile(new Rect(tile.x * 32, tile.y * 32, 32, 32), collider, rendererDelegate,
                            rendererDelegateYellow, rendererDelegatePink, pkLayer, tile.x, tile.y);
                    resetableTiles.add(pkTile);
                    updaters.add(pkTile);
                    collider.addCollidable(pkTile, LayeredCollisionEngine.FLORA_LAYER);
                    renderers.add(pkTile, RenderList.TERRA);
                }
            } catch (SlickException e) {
                throw new RuntimeException("Unable to make keyedplayertile", e);
            }
        }

        Layer fallSpike = tiledMap.getLayer(TileWorld.FALLING_SPIKE_LAYER_NAME);
        if (fallSpike != null) {
            fallSpike.visible = false;
            TileRendererDelegate rd = new TileRendererDelegate(tiledMap, TileType.SPIKE);
            try {
                for (Tile tile : fallSpike.getTiles()) {
                    FallingTile fsTile = new FallingTile(this, new Rect(tile.x * 32, tile.y * 32, 32, 32), collider, rd, fallSpike, tile.x, tile.y);
                    updaters.add(fsTile);
                    collider.addCollidable(fsTile, LayeredCollisionEngine.FALLING_LAYER);
                    renderers.add(fsTile, RenderList.TERRA);
                }
            } catch (SlickException e) {
                throw new RuntimeException("Unable to make keyedplayertile", e);
            }
        }

        // Camera initialization
        PanningCamera pancam = new PanningCamera(2000, new Vector2f(0, 0), new Vector2f(0.035f, 0), new Vector2f(map.getWidth()
                - container.getWidth(), 0), container.getWidth(), container.getHeight());
        camera = pancam;
        if (map.map.getMapProperty("camera", PANNING_CAMERA).equals(STALKING_CAMERA)) {
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
        renderers.render(g, (int) offset.x, (int) offset.y);

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
        checkDeath(playerA);
        checkDeath(playerB);

        float xOffset = camera.getViewport().getX();
        // Prevent player from going off right side
        checkRightSide(playerA, xOffset);
        checkRightSide(playerB, xOffset);
        remappedDecay -= delta / 1000f;

        // if both bunnies did not collide with win box this turn, reset
        finishedPlayer = null;
    }

    private void checkDeath(Player player) {
        Vector2f pos = player.getPosition(0f);
        Rect r = camera.getViewport();
        if (pos.x + r.getX() + 32 < 0 || pos.y + r.getY() > r.getHeight() + 32) {
            // if (pos.x + offsetX2 + 32 < 0) {
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
        reset();
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
            reset();
            game.enterState(GameWinState.ID);
        }
    }

    @Override
    public void newStartPositions(List<Vector2f> startPositions) {
        Preconditions.checkArgument(startPositions.size() == 2);
        map.setStartPositions(startPositions);
    }

    @Override
    public void reset() {
        for (Resetable r : resetableTiles) {
            r.reset();
        }
    }
}
