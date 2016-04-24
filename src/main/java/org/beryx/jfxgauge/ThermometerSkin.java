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

import java.util.List;

import com.guigarage.css.CssHelper;
import com.guigarage.css.SkinPropertyBasedCssMetaData;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class ThermometerSkin extends SkinBase<Gauge<?>>{
	private static final double DEFAULT_ASPECT_RATIO = 0.6;
	private static final Paint DEFAULT_FLUID_FILL = Color.GRAY;

	private final Pane pane = new Pane();
	private final Circle tubeTop = new Circle();
	private final Circle tubeBottom = new Circle();
	private final Rectangle tubeBody = new Rectangle();
	private final Line tubeLeftWall = new Line();
	private final Line tubeRightWall = new Line();	
	private final Rectangle fluidBody = new Rectangle();
		
	private StyleableObjectProperty<Number> aspectRatio;
	private StyleableObjectProperty<Paint> fluidFill;	
	
    private static class StyleableProperties {
        private static final SkinPropertyBasedCssMetaData<Gauge<?>, Number> ASPECT_RATIO = CssHelper.createSkinMetaData("-fx-aspect-ratio", StyleConverter.getSizeConverter(), "aspectRatio", DEFAULT_ASPECT_RATIO);
        private static final SkinPropertyBasedCssMetaData<Gauge<?>, Paint> FLUID_FILL = CssHelper.createSkinMetaData("-fx-fluid-fill", StyleConverter.getPaintConverter(), "fluidFill", DEFAULT_FLUID_FILL);
        @SuppressWarnings("unchecked")
		private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES = CssHelper.createCssMetaDataList(SkinBase.getClassCssMetaData(), ASPECT_RATIO, FLUID_FILL);
    }
	
	public ThermometerSkin(final Gauge<?> control) {
		super(control);
		control.setCssResourceName("thermometer.css");
		
		this.pane.getChildren().setAll(this.tubeLeftWall, this.tubeRightWall, this.tubeBottom, this.tubeTop, this.tubeBody, this.fluidBody);
		this.getChildren().add(this.pane);

		this.pane.getStyleClass().setAll("pane");
		
		this.tubeTop.getStyleClass().setAll("tube-inside", "tube-outside");
		this.tubeBottom.getStyleClass().setAll("tube-outside", "fluid");
		this.tubeLeftWall.getStyleClass().setAll("tube-outside");
		this.tubeRightWall.getStyleClass().setAll("tube-outside");
		this.tubeBody.getStyleClass().setAll("tube-inside");		
		this.fluidBody.getStyleClass().setAll("fluid");
		
		this.getSkinnable().widthProperty().addListener(obs -> this.redraw());
        this.getSkinnable().heightProperty().addListener(obs -> this.redraw());
        this.getSkinnable().valueProperty().addListener(ev -> this.redraw());
        this.getSkinnable().statusProperty().addListener(ev -> this.redraw());
        
        this.redraw();
	}

	private void redraw() {
        final Gauge<?> gauge = this.getSkinnable();
		double width  = gauge.getWidth() - gauge.getInsets().getLeft() - gauge.getInsets().getRight();
        final double height = gauge.getHeight() - gauge.getInsets().getTop() - gauge.getInsets().getBottom();
        final double aspectRatio = Math.min(1.0, Math.max(0.01, this.getAspectRatio()));
        if(width * (1 + aspectRatio) > height) {
        	width =  height / (1 + aspectRatio);
        }
        if (width <= 0 || height <= 0) return;
        
        this.pane.setMaxSize(width, height);
        this.pane.relocate((gauge.getWidth() - width) * 0.5, (gauge.getHeight() - height) * 0.5);
        
		final double bottomRadius = width / 2;
		final double topRadius = bottomRadius * aspectRatio;
		final double tubeLength = height - (bottomRadius + topRadius + Math.sqrt(bottomRadius * bottomRadius - topRadius * topRadius));
		
		this.tubeLeftWall.setStartX(bottomRadius - topRadius);
		this.tubeLeftWall.setStartY(topRadius);
		this.tubeLeftWall.setEndX(bottomRadius - topRadius);
		this.tubeLeftWall.setEndY(topRadius + tubeLength);
		
		this.tubeRightWall.setStartX(bottomRadius + topRadius);
		this.tubeRightWall.setStartY(topRadius);
		this.tubeRightWall.setEndX(bottomRadius + topRadius);
		this.tubeRightWall.setEndY(topRadius + tubeLength);

		this.tubeBody.setX(bottomRadius - topRadius + 1);
		this.tubeBody.setY(topRadius);
		this.tubeBody.setWidth(2 * topRadius - 2);
		this.tubeBody.setHeight(tubeLength);
		
		this.tubeTop.setCenterX(width / 2);
		this.tubeTop.setCenterY(topRadius);
		this.tubeTop.setRadius(topRadius);
		
		this.tubeBottom.setCenterX(width / 2);
		this.tubeBottom.setCenterY(height - bottomRadius);
		this.tubeBottom.setRadius(bottomRadius);
		
		final double highVal = gauge.highValueProperty().getValue().doubleValue();
		final double lowVal = gauge.lowValueProperty().getValue().doubleValue();
		if(highVal > lowVal) {			
			final String style = this.getSkinnable().getStatus();
			final String fluidStyle = (style == null) ? "fluid" : ("fluid-" + style);
			this.fluidBody.getStyleClass().setAll(fluidStyle);
			this.tubeBottom.getStyleClass().setAll("tube-outside", fluidStyle);
			
			final double val = Math.max(lowVal, Math.min(highVal, gauge.valueProperty().getValue().doubleValue()));			
			final double offsetFromFull = tubeLength * (highVal - val) / (highVal - lowVal);
			this.fluidBody.setX(bottomRadius - topRadius + 1);
			this.fluidBody.setY(topRadius + offsetFromFull);
			this.fluidBody.setWidth(2 * topRadius - 2);
			this.fluidBody.setHeight(tubeLength + 1 - offsetFromFull);		
		}
	}
	
	public double getAspectRatio() {
        return this.aspectRatio == null ? DEFAULT_ASPECT_RATIO : this.aspectRatio.get().doubleValue();
    }
    public void setAspectRatio(final double newAspectRatio) {
        this.aspectRatio.set(newAspectRatio);
    }
    public StyleableObjectProperty<Number> aspectRatioProperty() {
        if (this.aspectRatio == null) {
            this.aspectRatio = CssHelper.createProperty(StyleableProperties.ASPECT_RATIO, this);
            this.aspectRatio.addListener(obs -> this.redraw());
        }
        return this.aspectRatio;
    }
  
	public Paint getFluidFill() {
        return this.fluidFill == null ? DEFAULT_FLUID_FILL : this.fluidFill.get();
    }
    public void setFluidFill(final Paint fluidFill) {
        this.fluidFill.set(fluidFill);
    }
    public StyleableObjectProperty<Paint> fluidFillProperty() {
        if (this.fluidFill == null) {
            this.fluidFill = CssHelper.createProperty(StyleableProperties.FLUID_FILL, this);
            this.fluidFill.addListener(obs -> this.redraw());
        }
        return this.fluidFill;
    }
  
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }
 
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }	
}
