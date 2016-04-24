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

import javafx.beans.property.SimpleIntegerProperty;

public class IntGauge extends Gauge<SimpleIntegerProperty> {
	private final SimpleIntegerProperty value = new SimpleIntegerProperty(0);
	private final SimpleIntegerProperty lowValue = new SimpleIntegerProperty(0);
	private final SimpleIntegerProperty highValue = new SimpleIntegerProperty(100);
	
	@Override
	public SimpleIntegerProperty valueProperty() {
		return this.value;
	}
	public int getValue() {
		return this.value.get();
	}
	public void setValue(final int newValue) {
		this.value.set(newValue);
	}

	@Override
	public SimpleIntegerProperty lowValueProperty() {
		return this.lowValue;
	}
	public int getLowValue() {
		return this.lowValue.get();
	}
	public void setLowValue(final int newValue) {
		this.lowValue.set(newValue);
	}

	@Override
	public SimpleIntegerProperty highValueProperty() {
		return this.highValue;
	}
	public int getHighValue() {
		return this.highValue.get();
	}
	public void setHighValue(final int newValue) {
		this.highValue.set(newValue);
	}

	@Override
	public String getFormattedValue(final Number val) {
		return (val == null) ? "???" : ("" + val.intValue());
	}

}
