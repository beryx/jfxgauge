package org.beryx.jfxgauge;

import javafx.scene.Node;

public class SkinUtil {
    public static void setStyleClasses(Node node, String status, String... styles) {
        node.getStyleClass().setAll(styles);
        if(status != null) {
            for(String style : styles) {
                node.getStyleClass().add(style + "-" + status);
            }
        }
    }
}
