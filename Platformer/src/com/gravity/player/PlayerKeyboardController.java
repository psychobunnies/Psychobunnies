package com.gravity.player;

import com.gravity.player.Player.Movement;

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

	// TODO: create methods for setting keybindings

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
			lefting = true;
			return true;
		} else if (key == right) {
			player.move(Movement.RIGHT);
			righting = true;
			return true;
		} else if (key == jump) {
			player.jump(true);
			return true;

		}
		return false;
	}

	public boolean handleKeyRelease(int key) {
		if (key == left) {
			lefting = false;
			if (!lefting && !righting) {
				player.move(Movement.STOP);
			}
			return true;
		} else if (key == right) {
			righting = false;
			if (!lefting && !righting) {
				player.move(Movement.STOP);
			}
			return true;
		} else if (key == jump) {
			player.jump(false);
			return true;
		}
		return false;
	}
}