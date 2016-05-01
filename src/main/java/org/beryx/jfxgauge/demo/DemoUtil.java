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
