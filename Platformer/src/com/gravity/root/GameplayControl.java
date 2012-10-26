package com.gravity.root;

import com.gravity.fauna.Player;
import com.gravity.fauna.PlayerKeyboardController.Control;

/**
 * Interface which specifies any kind of logic that crosses between model, controller, and renderers.
 * 
 * @author xiao
 */
public interface GameplayControl {

    public void playerDies(Player player);

    public void playerHitSpikes(Player player);

    public void swapPlayerControls(Control ctrl);

}
