package com.gravity.levels;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import org.newdawn.slick.Graphics;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * A list of renderers with a loose ordering, such that all Renderers with a certain will be rendered before higher ordered Renderers, but after lower
 * ordered Renderers. Objects of the same order will be rendered in arbitrary order with respect to each other.
 * 
 * @author xiao
 */
public class RenderList implements Renderer {
    static public final Integer TERRA = 0;
    static public final Integer FLORA = 1;
    static public final Integer FAUNA = 2;

    private SortedMap<Integer, Collection<Renderer>> renderers;

    public RenderList() {
        renderers = Maps.newTreeMap();
    }

    /**
     * Add a renderer to the list with specified order.
     * 
     * Higher orders will be rendered later. (Have a higher z-index.)
     * 
     * @return if the renderer did not already exist in the list at the specified order
     */
    public boolean add(Renderer renderer, Integer order) {
        Preconditions.checkArgument(renderer != null, "Renderer cannot be null");
        Preconditions.checkArgument(order != null, "Order cannot be null");
        boolean retval = true;
        retval = remove(renderer);
        if (!renderers.containsKey(order)) {
            renderers.put(order, Sets.<Renderer> newLinkedHashSet());
        }
        renderers.get(order).add(renderer);
        return retval;
    }
    
    /**
     * Add a list of renderers to the RenderList.
     */
    public void addAll(List<Renderer> renderers, Integer order) {
        for (Renderer r : renderers) {
            add(r, order);
        }
    }

    /**
     * Remove the specified renderer from the list.
     * 
     * @return if the renderer was found in the list
     */
    public boolean remove(Renderer renderer) {
        Preconditions.checkArgument(renderer != null, "Renderer cannot be null");
        for (Integer i : renderers.keySet()) {
            Collection<Renderer> set = renderers.get(i);
            if (set.contains(renderer)) {
                set.remove(renderer);
                if (set.isEmpty()) {
                    renderers.remove(i);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Render the renderers contained by this list, respecting the request
     */
    @Override
    public void render(Graphics g, int offsetX, int offsetY) {
        for (Integer i : renderers.keySet()) {
            for (Renderer renderer : renderers.get(i)) {
                renderer.render(g, offsetX, offsetY);
            }
        }
    }

}
