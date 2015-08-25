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
 *
 * @author lawinslow
 */
public class HoboVocabProvider implements IVocabProvider{


    public HashSet<String> getVocab(String vocabType) {
        HashSet<String> vocab = new HashSet<String>();
        
        if(vocabType.compareToIgnoreCase("sites")==0){
            vocab.add("crystal bog n");
            vocab.add("crystal bog s");
            vocab.add("crystal bog e");
            vocab.add("crystal bog w");
            vocab.add("crystal bog center");
        }else if(vocabType.compareToIgnoreCase("offsettypes")==0){
            vocab.add("depth");
        }else if(vocabType.compareToIgnoreCase("sources")==0){
            vocab.add("cfl");
            vocab.add("DNR-TLS");
        }else if(vocabType.compareToIgnoreCase("aggmethods")==0){
            vocab.add("inst");
            vocab.add("sum");
            vocab.add("mean");
        }else if(vocabType.compareToIgnoreCase("units")==0){
            vocab.add("m");
            vocab.add("mm");
            vocab.add("C");
            vocab.add("millisiemens");
        }else if(vocabType.compareToIgnoreCase("variables")==0){
            vocab.add("precipitation");
            vocab.add("water_temp");
            vocab.add("conductivity");
            vocab.add("depth");
        }
        
        
        return vocab;
    }

    public HashSet<String> getVocabTypes() {
        HashSet<String> vocab = new HashSet<String>();
        vocab.add("sites");
        vocab.add("offsettypes");
        vocab.add("sources");
        vocab.add("aggmethods");
        vocab.add("units");
        vocab.add("variables");
        
        return vocab;
    }

    public void updateLocalVocab() {
        //do nothing
    }

    public JPanel getSettingsJPanel() {
        return new JPanel();
    }

    public String updateVocab(String vocabType, String currentVocab) {
        if(vocabType.compareToIgnoreCase("variables")==0 && 
                currentVocab.compareToIgnoreCase("precip")==0){
            return "precipitation";
        }
        
        return currentVocab;
        
    }

    public Element getSettingsXml() {
        Element e = new Element(IVocabProvider.VOCAB_PROVIDER_TAG);
        e.setAttribute("type",HoboVocabProvider.class.getName());
        return e;
    }

    public void configure(Element e) {
        //do nothing
    }


    
}
