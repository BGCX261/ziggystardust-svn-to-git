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
    
    public static String settingsFilePath;
    public static boolean autoLoadSettings;
    public static boolean autoSaveSettings;
    public static boolean settingsLoaded = false;
    public static INotificationProvider provider = new EmailNotificationProvider();
    
    public static IVocabProvider vocabProvider;
   
    public static void loadSettings(){
        Preferences prefs = Preferences.userRoot();
        settingsFilePath = prefs.get("settingsFilePath", "");
        autoLoadSettings = prefs.getBoolean("autoLoad", false);
        autoSaveSettings = prefs.getBoolean("autoSave", false);
        settingsLoaded = true;
        
    }
    public static void saveSettings(){
        Preferences prefs = Preferences.userRoot();
        prefs.put("settingsFilePath", settingsFilePath);
        prefs.putBoolean("autoLoad",autoLoadSettings);
        prefs.putBoolean("autoSave",autoSaveSettings);
        try{
            prefs.flush();    
        }catch(Exception e){
            //do nothing, backing store exception is stupid
        }

    }
    
    public static String getSettingsFilePath(){
        if(!settingsLoaded)
            loadSettings();
        
        return settingsFilePath;
    }
    
    public static void setSettingsFilePath(String settingsFilePath){
        GlobalProgramSettings.settingsFilePath = settingsFilePath;
    }

    public static boolean getAutoLoadSettings() {
        if(!settingsLoaded)
            loadSettings();
        
        return autoLoadSettings;
    }

    public static void setAutoLoadSettings(boolean autoLoadSettings) {
        GlobalProgramSettings.autoLoadSettings = autoLoadSettings;
    }

    public static boolean getAutoSaveSettings() {
        if(!settingsLoaded)
            loadSettings();
        
        return autoSaveSettings;
    }

    public static void setAutoSaveSettings(boolean autoSaveSettings) {
        GlobalProgramSettings.autoSaveSettings = autoSaveSettings;
    }
    
    

}
