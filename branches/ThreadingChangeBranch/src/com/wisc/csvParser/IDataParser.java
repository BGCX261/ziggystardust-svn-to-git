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
import org.jdom.Element;

/**
 *
 * @author lawinslow
 */
public interface IDataParser extends IStatusPanel{
    public static final String DATA_PARSER_TYPE_TAG = "type";
    public static final String DATA_PARSER_TAG = "DataParserConfiguration";
    public boolean Start();
    public boolean Stop();
    public boolean isStarted();
    public String getParserDescription();
    public String getParserShortname();
    public void configure(Element e) throws Exception;
    public void setRepository(IDataRepository repository);
    public IDataRepository getRepository();
    public Element getSettingsXml();
}