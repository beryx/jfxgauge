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
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class IntGauge extends Gauge<SimpleIntegerProperty> {
	private final SimpleIntegerProperty value = new SimpleIntegerProperty(0);
	private final SimpleIntegerProperty lowValue = new SimpleIntegerProperty(0);
	private final SimpleIntegerProperty highValue = new SimpleIntegerProperty(100);
    private final SimpleStringProperty valueFormat = new SimpleStringProperty("%d");

    public IntGauge() {
        statusWrapper.bind(Bindings.createStringBinding(this::computeStatus, value, imposedStatusProperty()));
    }

	@Override
	public SimpleIntegerProperty valueProperty() {
		return value;
	}
	public int getValue() {
		return value.get();
	}
	public void setValue(int newValue) {
		this.value.set(newValue);
	}

	@Override
	public SimpleIntegerProperty lowValueProperty() {
		return lowValue;
	}
	public int getLowValue() {
		return lowValue.get();
	}
	public void setLowValue(int newValue) {
		this.lowValue.set(newValue);
	}

	@Override
	public SimpleIntegerProperty highValueProperty() {
		return highValue;
	}
	public int getHighValue() {
		return highValue.get();
	}
	public void setHighValue(int newValue) {
		this.highValue.set(newValue);
	}

    public String getValueFormat() {
        return valueFormat.get();
    }
    public void setValueFormat(String newFormat) {
        valueFormat.set(newFormat);
    }
    public SimpleStringProperty valueFormatProperty() {
        return valueFormat;
    }

	@Override
	public String getFormattedValue(Number val) {
		return (val == null) ? "???" : (String.format(valueFormat.get(), val.intValue()));
	}

}
