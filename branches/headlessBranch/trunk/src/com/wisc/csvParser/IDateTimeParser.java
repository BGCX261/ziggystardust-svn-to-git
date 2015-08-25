/*
 *  This file is part of ZiggyStardust.
 *
 *  ZiggyStardust is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ZiggyStardust is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.wisc.csvParser;

import java.util.Date;
import org.jdom.*;
import java.awt.Dialog;

/**
 *
 * @author lawinslow
 */
public interface IDateTimeParser {
    /**
     * XML Tag that holds configuration information for instances of date time
     * format parser.
     */
    public static final String DATE_TIME_FORMAT_TAG = "DateTimeFormat";
    public void configure(Element e) throws Exception;
    public Date ParseDate(String[] row) throws Exception;
    public String getParserDescription();
    public String getParserShortname();
    public Element getSettingsXml();
    public Dialog getSettingsDialog();
}
