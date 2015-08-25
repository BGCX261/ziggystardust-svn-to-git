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
import java.util.ArrayList;
import org.jdom.Element;

/**
 * Interface to which all data-accepting modules must adhere. 
 * 
 * @author lawinslow
 */
public interface IDataRepository extends IStatusPanel{
    /**
     * XML Tag used by all data repositories
     */
    public static final String DATA_REPOSITORY_TAG = "DataRepositoryConfig";
    public String getRepositoryDescription();
    public String getRepositoryShortname();
    public void configure(Element e) throws Exception;
    public boolean Start();
    public boolean Stop();
    public boolean isStarted();
    public boolean NewRow(ArrayList<ValueObject> newRow);
    public boolean NewValue(ValueObject newValue);
    public IDataRepository getChildRepository();
    public void setChildRepository(IDataRepository child);
    public Element getSettingsXml();
}
