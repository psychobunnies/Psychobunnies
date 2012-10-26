package com.gravity.entity;

import com.gravity.entity.Player.Movement;
import com.gravity.root.GameSounds;

/**
 * Takes input events and converts them into commands for player to handle.
 * 
 * @author xiao
 * 
 */
public class PlayerKeyboardController {
    private Player player;
    
    private int left, right, jump, misc;
    private boolean lefting, righting;
    
    public PlayerKeyboardController(Player player) {
        this.player = player;
        lefting = false;
        righting = false;
    }
    
    public enum Control {
        LEFT, RIGHT, JUMP, MISC;
        
        public static int size() {
            // MISC is not used
            return 3;
        }
        
        public static Control getById(int id) {
            switch (id) {
                case 0:
                    return LEFT;
                case 1:
                    return RIGHT;
                case 2:
                    return JUMP;
                case 3:
                    return MISC;
                default:
                    throw new IllegalArgumentException("No Control id " + id);
            }
        }
    }
    
    public int getControl(Control ctrl) {
        switch (ctrl) {
            case JUMP:
                return getJump();
            case LEFT:
                return getLeft();
            case MISC:
                return getMisc();
            case RIGHT:
                return getRight();
            default:
                throw new RuntimeException("Unknown Control: " + ctrl.toString());
        }
    }
    
    public void setControl(Control ctrl, int key) {
        switch (ctrl) {
            case JUMP:
                setJump(key);
                break;
            case LEFT:
                setLeft(key);
                break;
            case MISC:
                setMisc(key);
                break;
            case RIGHT:
                setRight(key);
                break;
            default:
                throw new RuntimeException("Unknown Control: " + ctrl.toString());
        }
    }
    
    public int getLeft() {
        return left;
    }
    
    public int getRight() {
        return right;
    }
    
    public int getJump() {
        return jump;
    }
    
    public int getMisc() {
        return misc;
    }
    
    public PlayerKeyboardController setLeft(int key) {
        left = key;
        return this;
    }
    
    public PlayerKeyboardController setRight(int key) {
        right = key;
        return this;
    }
    
    public PlayerKeyboardController setJump(int key) {
        jump = key;
        return this;
    }
    
    public PlayerKeyboardController setMisc(int key) {
        misc = key;
        return this;
    }
    
    /**
     * Handle a key press event.
     * 
     * @return returns whether or not the keypress was handled.
     */
    public boolean handleKeyPress(int key) {
        if (key == left) {
            player.move(Movement.LEFT);
            player.setRequestedMovement(Movement.LEFT);
            lefting = true;
            return true;
        } else if (key == right) {
            player.move(Movement.RIGHT);
            player.setRequestedMovement(Movement.RIGHT);
            righting = true;
            return true;
        } else if (key == jump) {
            player.jump(true);
            GameSounds.playSickRabbitBeat();
            return true;
        }
        return false;
    }
    
    public boolean handleKeyRelease(int key) {
        if (key == left) {
            lefting = false;
            if (!lefting && !righting) {
                player.move(Movement.STOP);
                player.setRequestedMovement(Movement.STOP);
            }
            return true;
        } else if (key == right) {
            righting = false;
            if (!lefting && !righting) {
                player.move(Movement.STOP);
                player.setRequestedMovement(Movement.STOP);
            }
            return true;
        } else if (key == jump) {
            player.jump(false);
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PlayerKeyboardController [left=");
        builder.append(left);
        builder.append(", right=");
        builder.append(right);
        builder.append(", jump=");
        builder.append(jump);
        builder.append(", misc=");
        builder.append(misc);
        builder.append(", lefting=");
        builder.append(lefting);
        builder.append(", righting=");
        builder.append(righting);
        builder.append("]");
        return builder.toString();
    }
}
