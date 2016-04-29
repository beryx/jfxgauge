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

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.beryx.jfxgauge.DoubleGauge;
import org.beryx.jfxgauge.Gauge;
import org.beryx.jfxgauge.IntGauge;

public class GaugeDemo extends Application {

	@FXML IntGauge gauge0;
	@FXML IntGauge gauge1;
	@FXML IntGauge gauge2;
	@FXML DoubleGauge gauge3;
	@FXML DoubleGauge gauge4;
	@FXML DoubleGauge gauge5;
	
	final List<Gauge<?>> gauges = new ArrayList<>();

	@FXML public void initialize() {
		check("gauge0");
		check("gauge1");
		check("gauge2");
		check("gauge3");
		check("gauge4");
		check("gauge5");

		gauges.addAll(Arrays.asList(gauge0, gauge1, gauge2, gauge3, gauge4, gauge5));
		
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
//            gauge.bindStatusToValue();
            new Timer(gauge, lowVal - 50, highVal + 50, 5_000_000L).start();
		}
	}
	
	private void check(String name) {
    	EventTarget item = null;
		try {
			Field field = getClass().getDeclaredField(name);
			field.setAccessible(true);
			item = (EventTarget)field.get(this);
		} catch (Exception e) {
            throw new RuntimeException("Undefined FXML field '" + name + "' in " + getClass().getName(), e);
		}
        if(item == null) {
            throw new RuntimeException("fx:id=\"" + name + "\" was not injected: check your FXML file '" + getClass().getSimpleName() + ".fxml'.");
        }
    }	
	
	
	private static class Timer extends AnimationTimer {
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

    private void toggle(Gauge<?> gauge) {
        gauge.setImposedStatus((gauge.getImposedStatus() == null) ? "not-available" : null);
    }
    @FXML private void toggle0() { toggle(gauge0); }
    @FXML private void toggle1() { toggle(gauge1); }
    @FXML private void toggle2() { toggle(gauge2); }
    @FXML private void toggle3() { toggle(gauge3); }
    @FXML private void toggle4() { toggle(gauge4); }
    @FXML private void toggle5() { toggle(gauge5); }

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		GridPane root = FXMLLoader.load(getClass().getResource("gaugeDemo.fxml"));

		Scene scene = new Scene(root, 960, 720);

        stage.setTitle("Gauge Demo");
        stage.setScene(scene);        
        
        String resourceName = "gaugeDemo.css";
		URL cssResource = getClass().getResource(resourceName);
		if(cssResource == null) {
			throw new RuntimeException("Resource not found: " + resourceName);
		}
		scene.getStylesheets().add(cssResource.toExternalForm());
        
        stage.show();
	}
}
