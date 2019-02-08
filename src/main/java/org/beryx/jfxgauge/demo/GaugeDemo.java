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
package org.beryx.jfxgauge.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class GaugeDemo extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
        TabPane tabPane = new TabPane();
        Scene scene = new Scene(tabPane, 1000, 760);
        stage.setTitle("Gauge Demo");
        stage.setScene(scene);

        configureTab(tabPane, "Thermometer Skin", "demoThermometer");
        configureTab(tabPane, "Text Skin", "demoText");

        stage.show();
	}

    private Pane configureTab(TabPane tabPane, String tabTitle, String resourceName) throws IOException {
        URL resource = getClass().getResource(resourceName + ".fxml");
        if(resource == null) {
            throw new IOException("Cannot get resource " + resourceName + ".fxml");
        }
        Pane tabContent = FXMLLoader.load(resource);
        tabPane.getTabs().add(new Tab(tabTitle, tabContent));
        URL cssResource = getClass().getResource(resourceName + ".css");
        if(cssResource == null) {
            throw new RuntimeException("Resource not found: " + resourceName);
        }
        tabContent.getStylesheets().add(cssResource.toExternalForm());
        return tabContent;
    }
}
