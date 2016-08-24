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

import com.guigarage.css.CssHelper;
import com.guigarage.css.SkinPropertyBasedCssMetaData;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.geometry.HorizontalDirection;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.beryx.jfxgauge.SkinUtil.setStyleClasses;

public class ThermometerSkin extends SkinBase<Gauge<?>>{
    private static final double DEFAULT_ASPECT_RATIO = 0.6;
    private static final HorizontalDirection DEFAULT_RANGE_POSITION = HorizontalDirection.RIGHT;
    private static final HorizontalDirection DEFAULT_THRESHOLD_POSITION = HorizontalDirection.LEFT;

    private final Gauge<?> gauge;

    private final Pane pane = new Pane();
    private final Circle tubeTop = new Circle();
    private final Circle tubeBottom = new Circle();
    private final Rectangle tubeBody = new Rectangle();
    private final Line tubeLeftWall = new Line();
    private final Line tubeRightWall = new Line();
    private final Rectangle fluidBody = new Rectangle();

    private final List<Node> markings = new ArrayList<>();

    private double width;
    private double height;
    private double bottomRadius;
    private double topRadius;
    private double tubeLength;
    private double highVal;
    private double lowVal;

    private StyleableObjectProperty<Number> aspectRatio;
    private StyleableObjectProperty<HorizontalDirection> rangePosition;
    private StyleableObjectProperty<HorizontalDirection> thresholdPosition;

    @SuppressWarnings("rawtypes")
    private static final StyleConverter HDIR_CONVERTER = StyleConverter.getEnumConverter(HorizontalDirection.class);

    private static class StyleableProperties {
        private static final SkinPropertyBasedCssMetaData<Gauge<?>, Number> ASPECT_RATIO = CssHelper.createSkinMetaData("-fx-aspect-ratio", StyleConverter.getSizeConverter(), "aspectRatio", DEFAULT_ASPECT_RATIO);
        @SuppressWarnings("unchecked")
        private static final SkinPropertyBasedCssMetaData<Gauge<?>, HorizontalDirection> RANGE_POSITION = CssHelper.createSkinMetaData("-fx-range-position", HDIR_CONVERTER, "rangePosition", DEFAULT_RANGE_POSITION);
        @SuppressWarnings("unchecked")
        private static final SkinPropertyBasedCssMetaData<Gauge<?>, HorizontalDirection> THRESHOLD_POSITION = CssHelper.createSkinMetaData("-fx-threshold-position", HDIR_CONVERTER, "thresholdPosition", DEFAULT_THRESHOLD_POSITION);
        @SuppressWarnings("unchecked")
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES = CssHelper.createCssMetaDataList(SkinBase.getClassCssMetaData(), ASPECT_RATIO, RANGE_POSITION, THRESHOLD_POSITION);
    }

    public ThermometerSkin(Gauge<?> gauge) {
        super(gauge);
        this.gauge = gauge;
        gauge.setCssResourceName("thermometer.css");

        pane.getChildren().setAll(tubeLeftWall, tubeRightWall, tubeBottom, tubeTop, tubeBody, fluidBody);
        getChildren().add(pane);

        Arrays.asList(
                gauge.valueProperty(),
                gauge.statusProperty(),
                gauge.valueVisibleProperty(),
                gauge.rangeVisibleProperty(),
                gauge.widthProperty(),
                gauge.heightProperty()
        ).forEach(prop -> prop.addListener(obs -> redraw()));

        redraw();
    }

    private void redraw() {
        setAllStyleClasses();

        computeSizes();
        if (width <= 0 || height <= 0) return;

        pane.setMaxSize(width, height);
        pane.relocate((gauge.getWidth() - width) * 0.5, (gauge.getHeight() - height) * 0.5);
        pane.getChildren().removeAll(markings);
        markings.clear();

        drawContainer();
        if(highVal > lowVal) drawFluid();
        if(gauge.isRangeVisible()) drawRange();
        if(gauge.getThresholdsVisible()) drawThresholds();
        if(gauge.isValueVisible()) drawValue();
    }

    private void setAllStyleClasses() {
        String status = gauge.getStatus();
        setStyleClasses(gauge, status, "gauge");
        setStyleClasses(pane, status, "pane");
        setStyleClasses(tubeTop, status, "tube-inside", "tube-outside", "tube-top");
        setStyleClasses(tubeBottom, status, "tube-outside", "fluid", "tube-bottom");
        setStyleClasses(tubeLeftWall, status, "tube-outside");
        setStyleClasses(tubeRightWall, status, "tube-outside");
        setStyleClasses(tubeBody, status, "tube-inside", "tube-body");
        setStyleClasses(fluidBody, status, "fluid");
    }

    private void computeSizes() {
        highVal = gauge.highValueProperty().getValue().doubleValue();
        lowVal = gauge.lowValueProperty().getValue().doubleValue();
        width  = gauge.getWidth() - gauge.getInsets().getLeft() - gauge.getInsets().getRight();
        height = gauge.getHeight() - gauge.getInsets().getTop() - gauge.getInsets().getBottom();
        double aspectRatio = restrain(getAspectRatio(), 0.01, 1.0);
        if(width * (1 + aspectRatio) > height) {
            width =  height / (1 + aspectRatio);
        }
        bottomRadius = width / 2;
        topRadius = bottomRadius * aspectRatio;
        tubeLength = height - (bottomRadius + topRadius + Math.sqrt(bottomRadius * bottomRadius - topRadius * topRadius));
    }

    private void drawContainer() {
        tubeLeftWall.setStartX(bottomRadius - topRadius);
        tubeLeftWall.setStartY(topRadius);
        tubeLeftWall.setEndX(bottomRadius - topRadius);
        tubeLeftWall.setEndY(topRadius + tubeLength);

        tubeRightWall.setStartX(bottomRadius + topRadius);
        tubeRightWall.setStartY(topRadius);
        tubeRightWall.setEndX(bottomRadius + topRadius);
        tubeRightWall.setEndY(topRadius + tubeLength);

        tubeBody.setX(bottomRadius - topRadius + 1);
        tubeBody.setY(topRadius);
        tubeBody.setWidth(2 * topRadius - 2);
        tubeBody.setHeight(tubeLength);

        tubeTop.setCenterX(width / 2);
        tubeTop.setCenterY(topRadius);
        tubeTop.setRadius(topRadius);

        tubeBottom.setCenterX(width / 2);
        tubeBottom.setCenterY(height - bottomRadius);
        tubeBottom.setRadius(bottomRadius);
    }

    private void drawFluid() {
        double val = Math.max(lowVal, Math.min(highVal, gauge.valueProperty().getValue().doubleValue()));
        double offsetFromFull = tubeLength * (highVal - val) / (highVal - lowVal);
        fluidBody.setX(bottomRadius - topRadius + 1);
        fluidBody.setY(topRadius + offsetFromFull);
        fluidBody.setWidth(2 * topRadius - 2);
        fluidBody.setHeight(tubeLength + 1 - offsetFromFull);
    }

    private void drawRange() {
        Arrays.asList(lowVal, highVal).forEach(val -> drawMarking(val, getRangePosition(), 6, "range"));
    }

    private void drawThresholds() {
        gauge.getThresholds().forEach(t ->
                drawMarking(t.getValue(), getThresholdPosition(), 6, "threshold", "threshold-" + t.getStatus()));
    }

    private void drawValue() {
        double wrappingWidth = restrain(2 * bottomRadius, 60, 240);
        double x = bottomRadius - wrappingWidth / 2;
        double y = (height + topRadius + tubeLength) / 2 + 4;
        double value = gauge.valueProperty().getValue().doubleValue();
        Text text = new Text(x, y, gauge.getFormattedValue(value));
        setStyleClasses(text, gauge.getStatus(), "value");
        text.setTextAlignment(TextAlignment.CENTER);
        text.setWrappingWidth(wrappingWidth);
        pane.getChildren().add(text);
        markings.add(text);
    }

    private void drawMarking(double value, HorizontalDirection side, double markWidth, String... styles) {
        double y = getValueHeight(value);
        double x = (side == HorizontalDirection.LEFT) ? (bottomRadius - topRadius - markWidth) : (bottomRadius + topRadius);
        Line line = new Line(x, y, x + markWidth, y);
        line.getStyleClass().setAll(Arrays.stream(styles).map(style -> style + "-marking").collect(Collectors.toList()));
        pane.getChildren().add(line);
        markings.add(line);

        Text text = new Text(gauge.getFormattedValue(value));
        text.setY(y + 4);
        text.getStyleClass().setAll(Arrays.stream(styles).map(style -> style + "-text").collect(Collectors.toList()));
        if(side == HorizontalDirection.LEFT) {
            text.setTextAlignment(TextAlignment.RIGHT);
            double wrappingWidth = restrain(2 * bottomRadius, 60, 240);
            text.setWrappingWidth(wrappingWidth);
            text.setX(x - wrappingWidth - 4);
        } else {
            text.setTextAlignment(TextAlignment.LEFT);
            text.setX(x + markWidth + 4);
        }
        pane.getChildren().add(text);
        markings.add(text);
    }

    private double getValueHeight(double value) {
        if(highVal <= lowVal) return topRadius;
        double val = restrain(value, lowVal, highVal);
        return topRadius + tubeLength * (highVal - val) / (highVal - lowVal);
    }

    private static double restrain(double value, double minVal, double maxVal) {
        return Math.min(maxVal, Math.max(minVal, value));
    }

    public double getAspectRatio() {
        return aspectRatio == null ? DEFAULT_ASPECT_RATIO : aspectRatio.get().doubleValue();
    }
    public void setAspectRatio(double newAspectRatio) {
        aspectRatio.set(newAspectRatio);
    }
    public StyleableObjectProperty<Number> aspectRatioProperty() {
        if (aspectRatio == null) {
            aspectRatio = CssHelper.createProperty(StyleableProperties.ASPECT_RATIO, this);
            aspectRatio.addListener(obs -> redraw());
        }
        return aspectRatio;
    }

    public HorizontalDirection getRangePosition() {
        return rangePosition == null ? DEFAULT_RANGE_POSITION : rangePosition.get();
    }
    public void setRangePosition(HorizontalDirection newPosition) {
        rangePosition.set(newPosition);
    }
    public StyleableObjectProperty<HorizontalDirection> rangePositionProperty() {
        if (rangePosition == null) {
            rangePosition = CssHelper.createProperty(StyleableProperties.RANGE_POSITION, this);
            rangePosition.addListener(obs -> redraw());
        }
        return rangePosition;
    }

    public HorizontalDirection getThresholdPosition() {
        return thresholdPosition == null ? DEFAULT_THRESHOLD_POSITION : thresholdPosition.get();
    }
    public void setThresholdPosition(HorizontalDirection newPosition) {
        thresholdPosition.set(newPosition);
    }
    public StyleableObjectProperty<HorizontalDirection> thresholdPositionProperty() {
        if (thresholdPosition == null) {
            thresholdPosition = CssHelper.createProperty(StyleableProperties.THRESHOLD_POSITION, this);
            thresholdPosition.addListener(obs -> redraw());
        }
        return thresholdPosition;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }
}
