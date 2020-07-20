/* 
 * UniversalEvents
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * UniversalEvents is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UniversalEvents is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UniversalEvents. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.events;

import java.util.function.IntUnaryOperator;

import space.arim.omnibus.events.Listen;
import space.arim.omnibus.events.Listener;

public class IntOperatorTestListener implements Listener {

	static final IntUnaryOperator OPERATOR = (val) -> Math.addExact(val, 15);
	
	@Listen
	public void onTestEvent(TestEventWithInteger evt) {
		evt.someValue = OPERATOR.applyAsInt(evt.someValue);
	}
	
}