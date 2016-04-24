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
import org.beryx.jfxgauge.IntGauge;

public class GaugeDemo extends Application {

	@FXML IntGauge gauge0;
	@FXML IntGauge gauge1;
	@FXML IntGauge gauge2;
	@FXML IntGauge gauge3;
	@FXML IntGauge gauge4;
	@FXML IntGauge gauge5;
	
	final List<IntGauge> gauges = new ArrayList<>(); 

	@FXML public void initialize() {
		this.check("gauge0");
		this.check("gauge1");
		this.check("gauge2");
		this.check("gauge3");
		this.check("gauge4");
		this.check("gauge5");

		this.gauges.addAll(Arrays.asList(this.gauge0, this.gauge1, this.gauge2, this.gauge3, this.gauge4, this.gauge5));
		
		int factor = 1;
		for(final IntGauge gauge : this.gauges) {
            final int lowVal = gauge.getLowValue();
            final int highVal = gauge.getHighValue();
			final int warningThreshold = (lowVal + highVal) * factor / 3;
			factor = 3 - factor;
			final int errorThreshold = (lowVal + highVal) * factor / 3;
			gauge.configureWarningAndErrorThresholds(warningThreshold, errorThreshold).bindStatusToThresholds();
            new Timer(gauge, lowVal - 50, highVal + 50, 5_000_000L).start();
		}
	}
	
	private void check(final String name) {
    	EventTarget item = null;
		try {
			final Field field = this.getClass().getDeclaredField(name);
			field.setAccessible(true);
			item = (EventTarget)field.get(this);
		} catch (final Exception e) {
            throw new RuntimeException("Undefined FXML field '" + name + "' in " + this.getClass().getName(), e);
		}
        if(item == null) {
            throw new RuntimeException("fx:id=\"" + name + "\" was not injected: check your FXML file '" + this.getClass().getSimpleName() + ".fxml'.");
        }
    }	
	
	
	private static class Timer extends AnimationTimer {
		private final IntGauge gauge;
		private final long interval;
		private final int startVal;
		private final int endVal;
		
		private long lastTimerCall = System.nanoTime();
    	private int val = 0;
    	private int increment = 1;
		
        public Timer(final IntGauge gauge, final int startVal, final int endVal, final long interval) {
			this.gauge = gauge;
			this.startVal = startVal;
			this.endVal = endVal;
			this.interval = interval;
		}
    	
		@Override
		public void handle(final long now) {
            if (now > this.lastTimerCall + this.interval) {
            	if(this.val <= this.startVal) this.increment = 1;
            	if(this.val >= this.endVal) this.increment = -1;
            	this.val += this.increment;
            	this.gauge.setValue(this.val);
                this.lastTimerCall = now;
//                System.err.println("val = " + this.val);
            }
		}		
	}
	
	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception {
		final GridPane root = FXMLLoader.load(this.getClass().getResource("gaugeDemo.fxml"));

		final Scene scene = new Scene(root, 1280, 960);

        stage.setTitle("Gauge test");
        stage.setScene(scene);        
        
        final String resourceName = "gaugeDemo.css";
		final URL cssResource = this.getClass().getResource(resourceName);
		if(cssResource == null) {
			throw new RuntimeException("Resource not found: " + resourceName);
		}
		scene.getStylesheets().add(cssResource.toExternalForm());
        
        stage.show();
	}
}
