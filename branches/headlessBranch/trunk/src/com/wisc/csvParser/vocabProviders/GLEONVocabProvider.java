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

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JPanel;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import java.net.URL;


/**
 *
 * @author lawinslow
 */
public class GLEONVocabProvider implements IVocabProvider {

    public static String VOCAB_ITEM_TAG = "vocabItem";
    public static String VOCAB_NAME_TAG = "name";
    public static String VOCAB_OLD_NAME_TAG = "oldname";
    
    
    HashSet<String> vocabTypes = new HashSet<String>();
    Hashtable<String,HashSet<String>> vocab = new Hashtable<String,HashSet<String>>();
    Hashtable<String,Hashtable<String,String>> vocabUpdate = 
            new Hashtable<String,Hashtable<String,String>>();
    
    public GLEONVocabProvider(){
        vocabTypes.add("sites");
        vocabTypes.add("variables");
        vocabTypes.add("units");
        vocabTypes.add("offsettypes");
        vocabTypes.add("sources");
        vocabTypes.add("aggmethods");
    }
    
    
    public HashSet<String> getVocab(String vocabType)throws Exception {
        if(vocab.containsKey(vocabType)){
            return vocab.get(vocabType);
        }else{
            throw new Exception("No vocab set of type " + vocabType + " available");
        }
    }

    public String updateVocab(String vocabType, String currentVocab) {
        if(vocabUpdate.containsKey(vocabType)){
            if(vocabUpdate.get(vocabType).containsKey(currentVocab)){
                //get updated vocab for that variable
                return vocabUpdate.get(vocabType).get(currentVocab);
            }else{
                //no updated vocab for that vocab
                return currentVocab;
            }
        }else{
            //that collection is not depricatable
            return currentVocab;
        }
    }

    public HashSet<String> getVocabTypes() {
        return vocabTypes;
    }

    public void updateLocalVocab() {
        SAXBuilder sax = new SAXBuilder();
        Document doc;
        for(String v:vocabTypes){
            try{
                doc = sax.build(new URL(
                        "http://webbadger.gleonrcn.org/vocab/controlledVocab.aspx" +
                        "?collection=" + v));
                if(doc.getRootElement().getAttributeValue("type").compareToIgnoreCase("simple")==0){
                    handleSimpleVocab(v,doc);
                }else{
                    handleDepricatableVocab(v,doc);
                }
                
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
    }
    
    private void handleSimpleVocab(String type, Document doc){
        HashSet<String> voc = new HashSet<String>();
        
        List<Element> items = doc.getRootElement().getChildren(VOCAB_ITEM_TAG);
        for(int i = 0;i<items.size();i++){
            voc.add(items.get(i).getText());
        }
        
        vocab.put(type, voc);
    }
    
    
    
    private void handleDepricatableVocab(String type, Document doc){
        HashSet<String> voc = new HashSet<String>();
        Hashtable<String,String> updateLookup = new Hashtable<String,String>();
        
        List<Element> items = doc.getRootElement().getChildren(VOCAB_ITEM_TAG);
        for(int i = 0;i<items.size();i++){
            //all items will have the name tag
            voc.add(items.get(i).getChildText(VOCAB_NAME_TAG));
            
            //If this item is depricated, it will have an oldName tag
            // and a name tag, name is the updated vocab
            if(items.get(i).getChild(VOCAB_OLD_NAME_TAG)!=null){
                updateLookup.put(items.get(i).getChildText(VOCAB_OLD_NAME_TAG),
                        items.get(i).getChildText(VOCAB_NAME_TAG));
            }
        }
        vocab.put(type, voc);
        vocabUpdate.put(type, updateLookup);
    }

    public JPanel getSettingsJPanel() {
        return new JPanel();
    }

    public Element getSettingsXml() {
        Element e = new Element(IVocabProvider.VOCAB_PROVIDER_TAG);
        e.setAttribute("type",HoboVocabProvider.class.getName());
        
            
        return e;
    }

    public void configure(Element e) {
        
    }
    
    public static void main(String args[]){
        GLEONVocabProvider vcab = new GLEONVocabProvider();
        vcab.updateLocalVocab();
        try{
            for(String s:vcab.getVocab("variables")){
                System.out.println(s);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }

}
