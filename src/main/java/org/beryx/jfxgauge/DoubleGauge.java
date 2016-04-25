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

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class DoubleGauge extends Gauge<SimpleDoubleProperty> {
	private final SimpleDoubleProperty value = new SimpleDoubleProperty(0.0);
	private final SimpleDoubleProperty lowValue = new SimpleDoubleProperty(0.0);
	private final SimpleDoubleProperty highValue = new SimpleDoubleProperty(1.0);
    private final SimpleStringProperty valueFormat = new SimpleStringProperty("%.2f");

	@Override
	public SimpleDoubleProperty valueProperty() {
		return value;
	}
	public double getValue() {
		return value.get();
	}
	public void setValue(double newValue) {
		this.value.set(newValue);
	}

	@Override
	public SimpleDoubleProperty lowValueProperty() {
		return lowValue;
	}
	public double getLowValue() {
		return lowValue.get();
	}
	public void setLowValue(double newValue) {
		this.lowValue.set(newValue);
	}

	@Override
	public SimpleDoubleProperty highValueProperty() { return highValue; }
	public double getHighValue() {
		return highValue.get();
	}
	public void setHighValue(double newValue) {
		this.highValue.set(newValue);
	}

    public SimpleStringProperty valueFormatProperty() { return valueFormat; }
    public String getValueFormat() {
        return valueFormat.get();
    }
    public void setValueFormat(String newFormat) {
        this.valueFormat.set(newFormat);
    }

	@Override
	public String getFormattedValue(Number val) {
		return (val == null) ? "???" : String.format(getValueFormat(), val.doubleValue());
	}

}
