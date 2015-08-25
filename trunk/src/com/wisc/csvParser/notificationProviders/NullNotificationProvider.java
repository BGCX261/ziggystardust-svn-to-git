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

import javax.swing.JPanel;
import org.jdom.Element;

/**
 *
 * @author user
 */
public class NullNotificationProvider extends GenericNotificationProvider{

    @Override
    protected void sendEventMessage(NotificationEvent event) {
        //do nothing
    }

    @Override
    public void configure(Element e) {
        //do nothing
    }

    @Override
    public Element getSettingsXml() {
        Element e = new Element(INotificationProvider.NOTIFICATION_PROVIDER_TAG);
        e.setAttribute("type","com.wisc.csvParser.notificationProviders.NullNotificationProvider");
        return e;
    }

    @Override
    public String getProviderName() {
        return "No Notification Provider";
    }

    @Override
    public JPanel getStatusJPanel() {
        return new JPanel();
    }

    @Override
    public String getPanelID() {
        return "Empty Notification Provider";
    }
    

}
