/*
 * UniversalRegistry, a Bukkit/BungeeCord service registration API
 * Copyright © 2019 Anand Beh <https://www.arim.space>
 * 
 * UniversalRegistry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UniversalRegistry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UniversalRegistry. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.registry.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class UniversalLogFormatter extends Formatter {
	
	private final DateFormat date = new SimpleDateFormat(System.getProperty("net.md_5.bungee.log-date-format", "HH:mm:ss"));

	@Override
	public String format(LogRecord record) {
		StringBuilder formatted = new StringBuilder();
		formatted.append(this.date.format(record.getMillis()));
		formatted.append(' ').append('[');
		formatted.append(record.getLevel().getLocalizedName());
		formatted.append(']').append(' ');
		formatted.append(this.formatMessage(record));
		formatted.append('\n');
		if (record.getThrown() != null) {
			StringWriter writer = new StringWriter();
			record.getThrown().printStackTrace(new PrintWriter(writer));
			formatted.append(writer);
		}
		return formatted.toString();
	}
	
}
