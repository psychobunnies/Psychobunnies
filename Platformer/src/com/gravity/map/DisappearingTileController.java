package com.gravity.map;

import java.util.List;

import org.newdawn.slick.tiled.Layer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.gravity.root.UpdateCycling;

public final class DisappearingTileController implements UpdateCycling {

    private final float invisibleTime, visibleTime, normalVisibleTime, flickerTime, minFlickerOpacity;
    private final int totalFlickers;
    private final Layer layer;

    private final List<Float> flickerIntervals = Lists.newArrayList();

    private static final float flickerLength = 200.0f;

    private float count = 0f;
    private boolean visible = true;

    public DisappearingTileController(float invisibleTime, float normalVisibleTime, float flickerTime, float minFlickerOpacity,
            float geometricParameter, int totalFlickers, Layer layer) {
        this.invisibleTime = invisibleTime;
        this.flickerTime = flickerTime;
        this.normalVisibleTime = normalVisibleTime;
        this.minFlickerOpacity = minFlickerOpacity;
        this.totalFlickers = totalFlickers;
        this.layer = layer;
        this.visibleTime = 2 * flickerTime + normalVisibleTime;

        float total = 0f, current = 1f;
        for (int i = 0; i < totalFlickers; i++) {
            total += current;
            current *= geometricParameter;
        }
        float timeBetweenFlickers = flickerTime - flickerLength * totalFlickers;
        float base = timeBetweenFlickers / total;
        current = 1f;
        for (int i = 0; i < totalFlickers; i++) {
            flickerIntervals.add(base * current);
            current *= geometricParameter;
        }

        Preconditions.checkArgument(invisibleTime >= 0, "Invisible time must be non-negative.");
        Preconditions.checkArgument(normalVisibleTime >= 0, "Normal visible time must be non-negative.");
        Preconditions.checkArgument(flickerTime >= 0, "Flicker time must be non-negative.");
        Preconditions.checkArgument(flickerTime > totalFlickers * flickerLength,
                "Flicker time must be greater than number of flickers * flicker length");
        Preconditions.checkArgument(minFlickerOpacity >= 0 && minFlickerOpacity <= 1, "Minimum flicker opacity must be between 0 and 1, but was "
                + minFlickerOpacity);
        Preconditions.checkArgument(totalFlickers > 0, "Total number of flickers must be positive.");
        Preconditions.checkArgument(geometricParameter > 0, "The geometric parameter must be positive.");
        Preconditions.checkNotNull(layer, "Layer may not be null.");
    }

    @Override
    public void startUpdate(float millis) {
        // no-op
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
                // TODO check if layer is allowed to become visible
                visible = true;
                resolveState();
            } else {
                setLayerOpacity(0f);
            }
        }
    }

    private float opacityFlickerIn(float progress) {
        float cosineContrib = Math.abs((float) Math.cos(progress * (float) Math.PI));
        return cosineContrib;
    }

    private float opacityFlickerOut(float progress) {
        float sinContrib = Math.abs((float) Math.sin(progress * (float) Math.PI));
        return sinContrib * (1 - minFlickerOpacity) + minFlickerOpacity;
    }

    private float calculateDesiredOpacity(float time) {
        if (time <= flickerTime) {
            for (int i = 0; i < totalFlickers; i++) {
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
        time -= flickerTime;

        if (time <= normalVisibleTime) {
            return 1f;
        }
        time -= normalVisibleTime;

        if (time <= flickerTime) {
            for (int i = 0; i < totalFlickers; i++) {
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
        count += millis;
        resolveState();
    }

}
