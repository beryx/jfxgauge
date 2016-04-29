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

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.net.URL;

public abstract class Gauge<P extends Property<Number>> extends Control {
    protected final ReadOnlyStringWrapper statusWrapper = new ReadOnlyStringWrapper();
    private final ReadOnlyStringProperty status = statusWrapper.getReadOnlyProperty();
    private final SimpleStringProperty imposedStatus = new SimpleStringProperty();
    private final SimpleStringProperty lowestStatus = new SimpleStringProperty();
    private final SimpleListProperty<Threshold> thresholds = new SimpleListProperty(FXCollections.observableArrayList());
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
	public ReadOnlyStringProperty statusProperty() {
		return status;
	}

    /**
     * @return the status imposed on this gauge. If this property has a non-null value, it supersedes the status computed by {@link #computeStatus()}.
     */
    public String getImposedStatus() {
        return imposedStatus.get();
    }
    public void setImposedStatus(String newStatus) {
        imposedStatus.set(newStatus);
    }
    public SimpleStringProperty imposedStatusProperty() {
        return imposedStatus;
    }

    /**
     * @return the status associated with values below the first threshold
     */
    public String getLowestStatus() {
        return lowestStatus.get();
    }
    public void setLowestStatus(String newStatus) {
        lowestStatus.set(newStatus);
    }
    public SimpleStringProperty lowestStatusProperty() {
        return lowestStatus;
    }


    public ObservableList<Threshold> getThresholds() {
        return thresholds.get();
    }
    public void setThresholds(ObservableList<Threshold> newThresholds) { thresholds.setValue(new SortedList<Threshold>(newThresholds)); }
    public SimpleListProperty<Threshold> thresholdsProperty() {
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
			setLowestStatus("ok");
			thresholds.add(new Threshold("warning", warningThreshold.doubleValue()));
			thresholds.add(new Threshold("error", errorThreshold.doubleValue()));
		} else {
			setLowestStatus("error");
			thresholds.add(new Threshold("warning", errorThreshold.doubleValue()));
			thresholds.add(new Threshold("ok", warningThreshold.doubleValue()));
		}
		return this;
	}
	
	/**
	 * This implementation uses the thresholds to determine the status.
	 * Subclasses may override this method to provide alternative ways of computing the status.
	 */
	protected String computeStatus() {
        String currStatus = getImposedStatus();
        if(currStatus == null) {
            Number val = valueProperty().getValue();
            if(val != null) {
                currStatus = lowestStatus.getValue();
                for(Threshold threshold : thresholds) {
                    Number limit = threshold.getValue();
                    if(val.doubleValue() < limit.doubleValue()) break;
                    currStatus = threshold.getStatus();
                }
            }
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
	 * This implementation returns a {@link ThermometerSkin}.
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
