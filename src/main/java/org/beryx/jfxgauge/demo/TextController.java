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

import javafx.fxml.FXML;
import org.beryx.jfxgauge.DoubleGauge;
import org.beryx.jfxgauge.IntGauge;
import static org.beryx.jfxgauge.demo.DemoUtil.*;

public class TextController {
	@FXML IntGauge text0;
	@FXML IntGauge text1;
	@FXML IntGauge text2;
	@FXML DoubleGauge text3;

	@FXML public void initialize() { initGauges(text0, text1, text2, text3); }

    @FXML private void toggle0() { toggle(text0); }
    @FXML private void toggle1() { toggle(text1); }
    @FXML private void toggle2() { toggle(text2); }
    @FXML private void toggle3() { toggle(text3); }
}
