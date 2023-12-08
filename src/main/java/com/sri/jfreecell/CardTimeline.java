package com.sri.jfreecell;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;
import org.pushingpixels.trident.interpolator.KeyFrames;
import org.pushingpixels.trident.interpolator.KeyTimes;
import org.pushingpixels.trident.interpolator.KeyValues;

import java.awt.*;

public abstract class CardTimeline {


    protected Color backgroundColor;

    protected boolean highlight = false;

    protected transient Timeline blinkTimeline;

    public void blink(int delay) {
        highlight = true;
        backgroundColor = Color.red;
        KeyValues<Float> xValues = KeyValues.create(1f, 0.75f, 0.5f, 0.75f, 1f, 0.75f, 0.5f, 0.75f, 1f);
        KeyTimes alphaTimes = new KeyTimes(0f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 1f);
        try {
            blinkTimeline.setInitialDelay(delay);
        } catch (Exception ex) {
        }
        blinkTimeline.addPropertyToInterpolate("opacity", new KeyFrames<Float>(xValues, alphaTimes));
        this.blinkTimeline.replay();
    }

    protected void createBlinkTimeline() {

        blinkTimeline = new Timeline(this);
        blinkTimeline.setDuration(2000);
        blinkTimeline.addCallback(new TimelineCallbackAdapter() {
            @Override
            public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float duration, float timelinePos) {
                if (newState == Timeline.TimelineState.DONE) {
                    backgroundColor = Color.yellow;
                    highlight = false;
                }
            }
        });

    }

}
