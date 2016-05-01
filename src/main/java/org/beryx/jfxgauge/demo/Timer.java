/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beryx.jfxgauge.demo;

import javafx.animation.AnimationTimer;
import org.beryx.jfxgauge.Gauge;

public class Timer extends AnimationTimer {
    private final Gauge<?> gauge;
    private final long interval;
    private final double startVal;
    private final double endVal;

    private long lastTimerCall = System.nanoTime();
    private double val = 0;
    private double increment = 1;

    public Timer(Gauge<?> gauge, double startVal, double endVal, long interval) {
        this.gauge = gauge;
        this.startVal = startVal;
        this.endVal = endVal;
        this.interval = interval;
    }

    @Override
    public void handle(long now) {
        if (now > lastTimerCall + interval) {
            if(val <= startVal) increment = 0.97;
            if(val >= endVal) increment = -0.97;
            val += increment;
            gauge.valueProperty().setValue(val);
            lastTimerCall = now;
        }
    }
}
