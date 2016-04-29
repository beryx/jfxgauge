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
import javafx.beans.property.Property;
import javafx.scene.control.SkinBase;
import javafx.scene.text.Text;

/**
 * An extremely simple skin consisting only of a {@link Text} component.
 */
public class TextSkin extends SkinBase<Gauge<?>> {
    private final Gauge<?> gauge;
    private final Text text = new Text();

    public TextSkin(Gauge<?> gauge) {
        super(gauge);
        this.gauge = gauge;
        text.getStyleClass().setAll("text");
        getChildren().setAll(text);
        Property<Number> valProp = gauge.valueProperty();
        text.textProperty().bind(Bindings.createStringBinding(() -> gauge.getFormattedValue(valProp.getValue()), valProp));

        gauge.widthProperty().addListener(obs -> redraw());
        gauge.heightProperty().addListener(obs -> redraw());
        gauge.valueProperty().addListener(ev -> redraw());
        gauge.statusProperty().addListener(ev -> redraw());

        redraw();
    }

    private void redraw() {
        if(gauge.isValueVisible()) {
            text.getStyleClass().setAll("value");
            String style = gauge.getStatus();
            if(style != null) {
                text.getStyleClass().add("value-" + style);
            }
        }
    }
}
