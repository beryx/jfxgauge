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

public class ThermoController {
	@FXML IntGauge thermo0;
	@FXML IntGauge thermo1;
	@FXML IntGauge thermo2;
	@FXML DoubleGauge thermo3;
	@FXML DoubleGauge thermo4;
	@FXML DoubleGauge thermo5;
	
	@FXML public void initialize() { initGauges(thermo0, thermo1, thermo2, thermo3, thermo4, thermo5); }

    @FXML private void toggle0() { toggle(thermo0); }
    @FXML private void toggle1() { toggle(thermo1); }
    @FXML private void toggle2() { toggle(thermo2); }
    @FXML private void toggle3() { toggle(thermo3); }
    @FXML private void toggle4() { toggle(thermo4); }
    @FXML private void toggle5() { toggle(thermo5); }
}
