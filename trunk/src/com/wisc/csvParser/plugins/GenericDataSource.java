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
package com.wisc.csvParser.plugins;

import com.wisc.csvParser.IDataParser;
import com.wisc.csvParser.IDataRepository;
import javax.swing.JPanel;
import org.jdom.Element;
import java.util.Random;

/**
 *
 * @author lawinslow
 */
public abstract class GenericDataSource implements IDataParser{

    private IDataRepository repository = null;
    private boolean isStarted = false;
    private String panelID = getParserShortname() + (new Random()).nextInt();
    
    @Override
    public boolean Start(){
        boolean result = false;
        if(this.getRepository() != null){
            result = this.getRepository().Start();
        }
        //if child started properly, attempt to start this module.
        if(result){
            result = result && this.privateStart();
        }else{
            return false;
        }
        //if everything started well, return true
        if(result){
            isStarted = true;
            return true;
        }else{
            //if this failed to start, stop repository and return false
            this.getRepository().Stop();
            isStarted = false;
            return false;
        }
    }
    


    @Override
    public boolean Stop() {
        if(this.getRepository() != null){
            boolean result = privateStop() && this.getRepository().Stop();
            isStarted = false;
            return result;
        }else{
            boolean result = privateStop();
            isStarted = false;
            return result;
        }
    }
    


    @Override
    public boolean isStarted() {
        return isStarted;
    }
    
    @Override
    public void setRepository(IDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public IDataRepository getRepository() {
        return repository;
    }
    @Override
    public String getPanelID() {
        return panelID;
    }
    
    protected abstract boolean privateStop();
    
    protected abstract boolean privateStart();
    
    @Override
    public abstract String getParserDescription();

    @Override
    public abstract String getParserShortname();

    @Override
    public abstract void configure(Element e) throws Exception ;

    @Override
    public abstract Element getSettingsXml();

    @Override
    public abstract JPanel getStatusJPanel();

}
