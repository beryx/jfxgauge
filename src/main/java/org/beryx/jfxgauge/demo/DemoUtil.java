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

import org.beryx.jfxgauge.Gauge;

public class DemoUtil {
    public static void initGauges(Gauge... gauges) {
        int factor = 1;
        for(Gauge<?> gauge : gauges) {
            double lowVal = gauge.lowValueProperty().getValue().doubleValue();
            double highVal = gauge.highValueProperty().getValue().doubleValue();
            if(gauge.getThresholds().isEmpty()) {
                double warningThreshold = (lowVal + highVal) * factor / 3;
                factor = 3 - factor;
                double errorThreshold = (lowVal + highVal) * factor / 3;
                gauge.configureWarningAndErrorThresholds(warningThreshold, errorThreshold);
            }
            new Timer(gauge, lowVal - 50, highVal + 50, 5_000_000L).start();
        }
    }

    public static void toggle(Gauge<?> gauge) {
        gauge.setImposedStatus((gauge.getImposedStatus() == null) ? "not-available" : null);
    }
}
