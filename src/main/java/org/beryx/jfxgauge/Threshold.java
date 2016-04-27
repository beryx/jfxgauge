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

public class Threshold implements Comparable<Threshold> {
    private final SimpleStringProperty status = new SimpleStringProperty();
    private final SimpleDoubleProperty value = new SimpleDoubleProperty();

    // Needed by FXMLLoader
    public Threshold() {
        this("", 0);
    }

    public Threshold(String status, double value) {
        this.status.set(status);
        this.value.set(value);
    }

    public String getStatus() { return status.get(); }
    public void setStatus(String newStatus) { status.set(newStatus); }

    public double getValue() { return value.get(); }
    public void setValue(double newValue) { value.set(newValue); }

    @Override
    public int compareTo(Threshold t) {
        return Double.compare(getValue(), t.getValue());
    }
}
