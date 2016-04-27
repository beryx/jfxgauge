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
