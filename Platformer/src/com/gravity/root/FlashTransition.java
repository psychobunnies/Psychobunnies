package com.gravity.root;

import org.newdawn.slick.Color;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class FlashTransition extends SequenceTransition {

    public FlashTransition(Color color, int length, int flashes) {
        super();
        
        int eachLength = (int)(length / 2f / flashes);
        System.err.println("FlashTransition: " + flashes + " flashes, " + eachLength + "ms each");
        for (int i = 0; i < flashes; i++) {
            System.err.println("adding transitions " + i);
            if (i > 0) {
                addTransition(new FadeOutTransition(color, eachLength));
            }
            addTransition(new FadeInTransition(color, eachLength));
        }
    }
    
    public FlashTransition(int length, int flashes) {
        this(Color.black, length, flashes);
    }
    
    public FlashTransition(int length) {
        this(length, 3);
    }
    
}