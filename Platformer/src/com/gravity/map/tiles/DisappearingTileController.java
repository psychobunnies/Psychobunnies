package com.gravity.map.tiles;

import java.util.List;
import java.util.Random;

import org.newdawn.slick.tiled.Layer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.gravity.levels.UpdateCycling;

public final class DisappearingTileController implements UpdateCycling {

    private final float invisibleTime, visibleTime, normalVisibleTime, flickerTime;
    private final int flickerCount;
    private final Layer layer;

    private final List<Float> flickerIntervals = Lists.newArrayList();
    private final List<DisappearingTile> tiles = Lists.newArrayList();

    private static final float flickerLength = 200.0f;

    private float count = 0f;
    private boolean visible = true;

    public DisappearingTileController(float invisibleTime, float normalVisibleTime, float flickerTime, float geometricParameter, int totalFlickers,
            Layer layer) {
        this.invisibleTime = invisibleTime;
        this.flickerTime = flickerTime;
        this.normalVisibleTime = normalVisibleTime;
        this.flickerCount = totalFlickers;
        this.layer = layer;
        this.visibleTime = 2 * flickerTime + normalVisibleTime;

        float total = 0f, current = 1f;
        float[] offsets = new float[totalFlickers];
        float last = 1f/geometricParameter;
        Random rand = new Random();
        for (int i = 0; i < totalFlickers; i++) {
            // generate a random offset to each flicker
            if (i != 1) {
                // each offset is some fraction of the last spacing
                float newOffset = current * (10 + rand.nextInt(10)) / 20.0f;
                if (rand.nextInt(2) == 1) {
                    newOffset = -newOffset;
                }
                offsets[i] = newOffset;
            }
            
            // update the total
            total = total + offsets[i] + current;

            // update the current time between flickers
            last *= geometricParameter;
            current *= geometricParameter;
        }

        // store the flicker times
        float timeBetweenFlickers = flickerTime - flickerLength * totalFlickers;
        float base = timeBetweenFlickers / total;
        current = 1f;
        for (int i = 0; i < totalFlickers; i++) {
            flickerIntervals.add(base * (current + offsets[i]));
            current *= geometricParameter;
        }

        Preconditions.checkArgument(invisibleTime >= 0, "Invisible time must be non-negative.");
        Preconditions.checkArgument(normalVisibleTime >= 0, "Normal visible time must be non-negative.");
        Preconditions.checkArgument(flickerTime >= 0, "Flicker time must be non-negative.");
        Preconditions.checkArgument(flickerTime > totalFlickers * flickerLength,
                "Flicker time must be greater than number of flickers * flicker length");
        Preconditions.checkArgument(totalFlickers > 0, "Total number of flickers must be positive.");
        Preconditions.checkArgument(geometricParameter > 0, "The geometric parameter must be positive.");
        Preconditions.checkNotNull(layer, "Layer may not be null.");
    }

    @Override
    public void startUpdate(float millis) {
        count += millis;
        resolveState();
    }

    public void register(DisappearingTile tile) {
        tiles.add(tile);
    }

    private void resolveState() {
        if (visible) {
            if (count >= visibleTime) {
                count -= visibleTime;
                visible = false;
                resolveState();
            } else {
                float opacity = calculateDesiredOpacity(count);
                setLayerOpacity(opacity);
            }
        } else {
            if (count >= invisibleTime) {
                count -= invisibleTime;
                if (checkForExistingCollisions()) {
                    count = invisibleTime;
                } else {
                    visible = true;
                    resolveState();
                }
            } else {
                setLayerOpacity(0f);
            }
        }
    }

    private float opacityFlickerIn(float progress) {
        float angleProgress = progress * (float) Math.PI/2f;
        float cosineContrib = Math.abs((float) Math.cos(angleProgress));
        return cosineContrib;
    }

    private float opacityFlickerOut(float progress) {
        float sinContrib = Math.abs((float) Math.sin(progress * (float) Math.PI));
        return sinContrib;
    }

    private float calculateDesiredOpacity(float time) {
        if (time <= flickerTime) {
            if (checkForExistingCollisions()) {
                count = 0;
                return 0f;
            } else {
                for (int i = 0; i < flickerCount; i++) {
                    if (time <= flickerIntervals.get(i)) {
                        return 0f;
                    }
                    time -= flickerIntervals.get(i);
                    if (time <= flickerLength) {
                        return opacityFlickerIn(time / flickerLength);
                    }
                    time -= flickerLength;
                }
                return 1f;
            }
        }
        time -= flickerTime;

        if (time <= normalVisibleTime) {
            return 1f;
        }
        time -= normalVisibleTime;

        if (time <= flickerTime) {
            for (int i = 0; i < flickerCount; i++) {
                if (time <= flickerIntervals.get(i)) {
                    return 1f;
                }
                time -= flickerIntervals.get(i);
                if (time <= flickerLength) {
                    return opacityFlickerOut(time / flickerLength);
                }
                time -= flickerLength;
            }
            return 0f;
        }

        return 0f;
    }

    private void setLayerOpacity(float opacity) {
        Preconditions.checkArgument(opacity <= 1f, "Opacity must be at most 1, but was " + opacity);
        Preconditions.checkArgument(opacity >= 0f, "Opacity must be non-negative, but was " + opacity);
        layer.opacity = opacity;
    }

    @Override
    public void finishUpdate(float millis) {
        // no-op
    }

    private boolean checkForExistingCollisions() {
        for (DisappearingTile t : tiles) {
            if (t.isColliding()) {
                return true;
            }
        }
        return false;
    }

    public boolean collisionsEnabled() {
        return visible && count > flickerTime;
    }

    public List<DisappearingTile> getTiles() {
        return tiles;
    }

}
