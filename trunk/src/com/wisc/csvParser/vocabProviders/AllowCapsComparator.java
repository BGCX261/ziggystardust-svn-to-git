/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wisc.csvParser.vocabProviders;
import java.util.Comparator;
/**
 *
 * @author glocke-ou
 */
public class AllowCapsComparator implements Comparator<String>{
    public int compare(String strA, String strB) {
        return strA.compareToIgnoreCase(strB);
    }
}
