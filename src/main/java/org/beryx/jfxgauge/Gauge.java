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
package org.beryx.jfxgauge;

import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.text.Text;

public abstract class Gauge<P extends Property<Number>> extends Control {
    private final SimpleStringProperty status = new SimpleStringProperty();
    private final SortedMap<Number, String> thresholds = new TreeMap<>();
    private String lowestStatus;
    private final SimpleBooleanProperty thresholdsVisible = new SimpleBooleanProperty(true);
	
    private String cssResourceName;

    public abstract P valueProperty();
	public abstract P lowValueProperty();
	public abstract P highValueProperty();
	public abstract String getFormattedValue(Number val);

	public Gauge() {
        this.getStyleClass().add("gauge");
	}
		
	public String getStatus() {
		return this.status.get();
	}
	public void setStatus(final String newStatus) {
		this.status.set(newStatus);
	}
	public SimpleStringProperty statusProperty() {		
		return this.status;
	}
	
	public void setLowestThreshold(final String lowestStatus) {
		this.lowestStatus = lowestStatus;
	}

	public Map<Number, String> getThresholds() {
		return this.thresholds;
	}

	/**
	 * Convenience method for configuring a gauge with three operation modes: ok, warning and error.
	 * If warningThreshold < errorThreshold, the ok mode is associated with values below the warningThreshold. Otherwise, with values above the warningThreshold.
	 */
	public Gauge<P> configureWarningAndErrorThresholds(final Number warningThreshold, final Number errorThreshold) {
		if(warningThreshold == null || errorThreshold == null) {
			throw new IllegalArgumentException("Null values not permitted: warningThreshold = " + warningThreshold + ", errorThreshold = " + errorThreshold);
		}
		this.thresholds.clear();
		if(warningThreshold.doubleValue() < errorThreshold.doubleValue()) {
			this.lowestStatus = "ok";
			this.thresholds.put(warningThreshold, "warning");
			this.thresholds.put(errorThreshold, "error");
		} else {
			this.lowestStatus = "error";
			this.thresholds.put(errorThreshold, "warning");
			this.thresholds.put(warningThreshold, "ok");
		}
		return this;
	}
	
	public Gauge<P> bindStatusToThresholds() {
		this.status.bind(Bindings.createStringBinding(this::computeStatus, this.valueProperty()));
		return this;
	}
	
	/**
	 * This implementation uses the thresholds to determine the status.
	 * Subclasses may override this method to provide alternative ways of computing the status.
	 */
	protected String computeStatus() {		
		final Number val = this.valueProperty().getValue();
		if(val == null) return null;
		String currStatus = this.lowestStatus;
		for(final Entry<Number,String> entry : this.thresholds.entrySet()) {
			final Number limit = entry.getKey();
			if(limit == null) return null;
			if(val.doubleValue() < limit.doubleValue()) break;
			currStatus = entry.getValue();
		}
		return currStatus;
	}

	public boolean getThresholdsVisible() {
		return this.thresholdsVisible.get();
	}
	public void setThresholdsVisible(final boolean visible) {
		this.thresholdsVisible.set(visible);
	}
	public SimpleBooleanProperty thresholdsVisibleProperty() {
		return this.thresholdsVisible;
	}
	
	public void setCssResourceName(final String cssResourceName) {
		this.cssResourceName = cssResourceName;
	}

	/**
	 * An extremely simple skin used as default and consisting only of a {@link Text} component.
	 * @param <PP>
	 */
	public static class TextSkin<PP extends Property<Number>> extends SkinBase<Gauge<PP>> {
		private final Text text = new Text();
		protected TextSkin(final Gauge<PP> control) {
			super(control);
			this.getChildren().setAll(this.text);
			final Gauge<PP> gauge = this.getSkinnable();
			final PP valProp = gauge.valueProperty();
			this.text.textProperty().bind(Bindings.createStringBinding(() -> gauge.getFormattedValue(valProp.getValue()), valProp));
		}		
	}
	
	/**
	 * This implementation returns a {@link TextSkin}. 
	 * It may be overridden by subclasses, but the preferred way to set the skin is by setting the CSS property '-fx-skin'.
	 */
	@Override protected Skin<?> createDefaultSkin() {
        return new TextSkin<P>(this);
    }

    @Override public String getUserAgentStylesheet() {
        if(this.cssResourceName == null) return null;
		final URL resource = this.getClass().getResource(this.cssResourceName);
		return (resource == null) ? null : resource.toExternalForm();
    }
}
