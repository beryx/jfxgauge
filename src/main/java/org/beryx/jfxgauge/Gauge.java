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

public abstract class Gauge<P extends Property<Number>> extends Control {
    private final SimpleStringProperty status = new SimpleStringProperty();
    private final SortedMap<Number, String> thresholds = new TreeMap<>();
    private String lowestStatus;
    private final SimpleBooleanProperty valueVisible = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty rangeVisible = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty thresholdsVisible = new SimpleBooleanProperty(true);

    private String cssResourceName;

    public abstract P valueProperty();
	public abstract P lowValueProperty();
	public abstract P highValueProperty();
	public abstract String getFormattedValue(Number val);

	public Gauge() {
        getStyleClass().add("gauge");
	}
		
	public String getStatus() {
		return status.get();
	}
	public void setStatus(String newStatus) {
		status.set(newStatus);
	}
	public SimpleStringProperty statusProperty() {		
		return status;
	}
	
	public void setLowestThreshold(String lowestStatus) {
		this.lowestStatus = lowestStatus;
	}

	public Map<Number, String> getThresholds() {
		return thresholds;
	}

	/**
	 * Convenience method for configuring a gauge with three operation modes: ok, warning and error.
	 * If warningThreshold < errorThreshold, the ok mode is associated with values below the warningThreshold. Otherwise, with values above the warningThreshold.
	 */
	public Gauge<P> configureWarningAndErrorThresholds(Number warningThreshold, Number errorThreshold) {
		if(warningThreshold == null || errorThreshold == null) {
			throw new IllegalArgumentException("Null values not permitted: warningThreshold = " + warningThreshold + ", errorThreshold = " + errorThreshold);
		}
		thresholds.clear();
		if(warningThreshold.doubleValue() < errorThreshold.doubleValue()) {
			lowestStatus = "ok";
			thresholds.put(warningThreshold, "warning");
			thresholds.put(errorThreshold, "error");
		} else {
			lowestStatus = "error";
			thresholds.put(errorThreshold, "warning");
			thresholds.put(warningThreshold, "ok");
		}
		return this;
	}
	
	public Gauge<P> bindStatusToThresholds() {
		status.bind(Bindings.createStringBinding(this::computeStatus, valueProperty()));
		return this;
	}
	
	/**
	 * This implementation uses the thresholds to determine the status.
	 * Subclasses may override this method to provide alternative ways of computing the status.
	 */
	protected String computeStatus() {		
		Number val = valueProperty().getValue();
		if(val == null) return null;
		String currStatus = lowestStatus;
		for(Entry<Number,String> entry : thresholds.entrySet()) {
			Number limit = entry.getKey();
			if(limit == null) return null;
			if(val.doubleValue() < limit.doubleValue()) break;
			currStatus = entry.getValue();
		}
		return currStatus;
	}

    public boolean isValueVisible() {
        return valueVisible.get();
    }
    public void setValueVisible(boolean visible) {
        this.valueVisible.set(visible);
    }
    public SimpleBooleanProperty valueVisibleProperty() {
        return valueVisible;
    }

    public boolean isRangeVisible() {
        return rangeVisible.get();
    }
    public void setRangeVisible(boolean visible) {
        this.rangeVisible.set(visible);
    }
    public SimpleBooleanProperty rangeVisibleProperty() {
        return rangeVisible;
    }

    public boolean getThresholdsVisible() {
        return thresholdsVisible.get();
    }
    public void setThresholdsVisible(boolean visible) {
        this.thresholdsVisible.set(visible);
    }
    public SimpleBooleanProperty thresholdsVisibleProperty() {
        return thresholdsVisible;
    }

    public void setCssResourceName(String cssResourceName) {
		this.cssResourceName = cssResourceName;
	}

    /**
	 * This implementation returns a {@link TextSkin}. 
	 * It may be overridden by subclasses, but the preferred way to set the skin is by setting the CSS property '-fx-skin'.
	 */
	@Override protected Skin<?> createDefaultSkin() {
        return new ThermometerSkin(this);
    }

    @Override public String getUserAgentStylesheet() {
        if(cssResourceName == null) return null;
		URL resource = getClass().getResource(cssResourceName);
		return (resource == null) ? null : resource.toExternalForm();
    }
}
