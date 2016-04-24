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

public class DoubleGauge extends Gauge<SimpleDoubleProperty> {
	private final SimpleDoubleProperty value = new SimpleDoubleProperty(0.0);
	private final SimpleDoubleProperty lowValue = new SimpleDoubleProperty(0.0);
	private final SimpleDoubleProperty highValue = new SimpleDoubleProperty(1.0);

	@Override
	public SimpleDoubleProperty valueProperty() {
		return this.value;
	}
	public double getValue() {
		return this.value.get();
	}
	public void setValue(final double newValue) {
		this.value.set(newValue);
	}

	@Override
	public SimpleDoubleProperty lowValueProperty() {
		return this.lowValue;
	}
	public double getLowValue() {
		return this.lowValue.get();
	}
	public void setLowValue(final double newValue) {
		this.lowValue.set(newValue);
	}

	@Override
	public SimpleDoubleProperty highValueProperty() {
		return this.highValue;
	}
	public double getHighValue() {
		return this.highValue.get();
	}
	public void setHighValue(final double newValue) {
		this.highValue.set(newValue);
	}

	@Override
	public String getFormattedValue(final Number val) {
		return (val == null) ? "???" : String.format("%.2f", val.doubleValue());
	}

}
