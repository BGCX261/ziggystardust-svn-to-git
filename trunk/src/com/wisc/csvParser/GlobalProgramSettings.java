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
import java.util.prefs.Preferences;
import com.wisc.csvParser.notificationProviders.*;
import com.wisc.csvParser.vocabProviders.IVocabProvider;
/**
 *
 * @author lawinslow
 */
public class GlobalProgramSettings {
    
    public static boolean settingsLoaded = false;
    public static INotificationProvider provider = new EmailNotificationProvider();
    
    public static IVocabProvider vocabProvider;


    /**
     * Loads settings from persistent storage.
     */
    public static void loadSettings(){
        Preferences prefs = Preferences.userRoot();
        settingsLoaded = true;
        
    }
    /**
     * Persists any settings to permanent storage. Need to potentially
     * combine with the datachain specific settings
     */
    public static void saveSettings(){
        Preferences prefs = Preferences.userRoot();

        try{
            prefs.flush();    
        }catch(Exception e){
            //do nothing, backing store exception is stupid
        }

    }

}
