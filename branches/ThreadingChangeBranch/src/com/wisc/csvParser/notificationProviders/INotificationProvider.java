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

package com.wisc.csvParser.notificationProviders;

import org.jdom.Element;
import javax.swing.JPanel;

/**
 * This provides the application with the ability to notify 
 * technicians and other individuals of detected events of interest. 
 * For example, no new data for an excessive period of time, a failed sensor, 
 * or other detected events could be relayed to designated staff.
 * 
 * This handles notification and also the logic to prevent incessant (e.g., an
 * email every minute) notification of the same event. The standard is to limit
 * notification to once per day.
 *  
 * @author lawinslow
 */
public interface INotificationProvider {
    public static final String NOTIFICATION_PROVIDER_TAG = "NotificationProvider";
    public void newEvent(NotificationEvent event);
    public void configure(Element e);
    public Element getSettingsXml();
    public String getProviderName();
    public JPanel getStatusJPanel();
    public String getPanelID();
    public boolean start();
    public boolean stop();
}
