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
package com.wisc.csvParser.vocabProviders;

import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JPanel;
import org.jdom.Element;


/**
 * Interface defining a controlled vocabulary provider. Backend source
 * is irrelevant
 * 
 * @author lawinslow
 */
public interface IVocabProvider {
    public static String VOCAB_PROVIDER_TAG = "vocabProvider";
    public HashSet<String> getVocab(String vocabType)throws Exception;
    public String updateVocab(String vocabType,String currentVocab);
    public HashSet<String> getVocabTypes();
    public void updateLocalVocab();
    public JPanel getSettingsJPanel();
    public Element getSettingsXml();
    public void configure(Element e);
}
