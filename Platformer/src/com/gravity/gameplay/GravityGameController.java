package com.gravity.gameplay;

import com.gravity.player.Player;
import com.gravity.player.PlayerKeyboardController.Control;

/**
 * Interface which specifies any kind of logic that crosses between model, controller, and renderers.
 * 
 * @author xiao
 */
public interface GravityGameController {
    
    public void playerDies(Player player);
    
    public void playerHitSpikes(Player player);
    
    public void swapPlayerControls(Control ctrl);
    
    public void specialMoveSlingshot(Player slingshoter, float strength);
}
