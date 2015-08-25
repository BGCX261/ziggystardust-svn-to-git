

/*
 * This class extends the File Class and represents a Configuration File
 */
package com.wisc.configFileConverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Timer;
import java.lang.StringBuffer;
import java.io.*;
import javax.swing.JFileChooser;

/**
 * The Class ConfigFile extends the File class and creates a file object that represents a config File
 */
public class ConfigFile extends File{
	// Here starts the refactoring for less confusing design
	// Almost all objects like this will be in a hierarchy with
	// the configFile class as the root.
	private Timer loopTimer;
	private StringTokenizer st= null;
	private final String DELIM= ",";
	private BufferedReader FR;
	private String HostName;
	private int cacheSize;
	private int archiveSize;
	private String inputFileName;
	private String srcName;
	// private int count=0;
	private boolean exists;
	// private String token;
	// private int count=0;
	private ArrayList<Integer> ArrayInt= new ArrayList<Integer>(1);
	// private StringTokenizer st2=null;
	private final String DELIM2= ":";
	private Hashtable<Integer,ConfigSection> ArrayIDs= new Hashtable<Integer,ConfigSection>(1);
	private static final long serialVersionUID= 44;

	/**
	 * The construct invokes the SuperTypes Constructor and then creates a ConfigFile object
	 * 
	 * @param pathname
	 *            the path of the config file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public ConfigFile(String pathname) throws FileNotFoundException,IOException{
		super(pathname);
		exists= readFile();
	}

	/**
	 * reads the file and return false if an exception occurs
	 * 
	 * @return the connection status
	 */
	private boolean readFile() throws FileNotFoundException,IOException,java.util.NoSuchElementException{
		String inLine;
		FR= new BufferedReader(new FileReader(this));
		inLine= FR.readLine();
		st= new StringTokenizer(inLine,DELIM);
		HostName= st.nextToken().trim();
		cacheSize= Integer.parseInt(st.nextToken().trim());
		archiveSize= Integer.parseInt(st.nextToken().trim());
		inputFileName= st.nextToken().trim();
		srcName= st.nextToken().trim();
		// chanNames= new String[st.countTokens()];
		StringTokenizer newSecCheck;
		while((inLine= FR.readLine())!= null){
			newSecCheck= new StringTokenizer(inLine,DELIM2);
			if(newSecCheck.nextToken().trim().compareToIgnoreCase("COLUMN_START")== 0){
				final int arrayID= Integer.valueOf(newSecCheck.nextToken().trim()).intValue();
				final ArrayList<String> newSec= new ArrayList<String>(1);
				while((inLine= FR.readLine()).trim().compareToIgnoreCase("COLUMN_END")!= 0){
					newSec.add(inLine);
				}
				ArrayIDs.put(Integer.valueOf(arrayID),new ConfigSection(newSec));
				ArrayInt.add(Integer.valueOf(arrayID));
			}
		}
		return true;
	}


	/**
	 * an accessor for the datetime index
	 * 
	 * @param ArrayIndex
	 *            The index of the array
	 * @return the host name
	 */
	public int getDateTime(Integer ArrayIndex){
		if(ArrayIDs.containsKey(ArrayIndex)){
			return ArrayIDs.get(ArrayIndex).datetimePos;
		}
		return -1;
	}

	/**
	 * an accessor for the host name
	 * 
	 * @return the host name
	 */
	public String getHostName(){
		return HostName;
	}

	/**
	 * an accessor for the Chache Size
	 * 
	 * @return the cacheSize
	 */
	public int getCacheSize(){
		return cacheSize;
	}

	/**
	 * an accessor for the Archive Size
	 * 
	 * @return the archive size
	 */
	public int getArchiveSize(){
		return archiveSize;
	}

	/**
	 * an accessor for the file name
	 * 
	 * @return the file name
	 */
	public String getFileName(){
		return inputFileName;
	}

	/**
	 * an accessor for the source Name
	 * 
	 * @return the Source Name
	 */
	public String getSrcName(){
		return srcName;
	}

	/**
	 * an accessor for the channel names
	 * 
	 * @param index
	 *            the index of the channel name to return
	 * @param ArrayIndex
	 *            the array Section to accessed
	 * @return the channel name of the given index
	 */
	public String getChanNames(int index,int ArrayIndex){
		// System.out.println(ArrayIndex + ArrayIDs.get(ArrayIndex).chanNames.get(index));
		return ArrayIndex+ ArrayIDs.get(Integer.valueOf(ArrayIndex)).chanNames.get(index);
	}

	/**
	 * an accessor for the length
	 * 
	 * @param ArrayIndex
	 *            the array section to be accessed
	 * @return the length of chanNames
	 */
	public int getLengthCN(int ArrayIndex){
		return ArrayIDs.get(Integer.valueOf(ArrayIndex)).chanNames.size();
	}

	/**
	 * checks to see that the object exists
	 */
	@Override
	public boolean exists(){
		return exists;
	}

	/**
	 * Returns an XML string of the given index
	 * 
	 * @param index
	 *            the index of the xml string
	 * @param ArrayIndex
	 *            the array section to be accessed
	 * @return the xml string
	 */
	public String getXML(int index,int ArrayIndex){
		// why was this Integer.valueOf(ArrayIndex) ? when ArrayIndex is an int?
		return ArrayIDs.get(Integer.valueOf(ArrayIndex)).XMLstring.get(index);
	}

	/**
	 * Returns the year
	 * 
	 * @param ArrayIndex
	 *            the index of the array
	 * @return year
	 */
	public int getYear(int ArrayIndex){
		return ArrayIDs.get(Integer.valueOf(ArrayIndex)).yearPos;
	}

	/**
	 * returns the Day Number
	 * 
	 * @param ArrayIndex
	 * @return the day
	 */
	public int getDayNum(int ArrayIndex){
		return ArrayIDs.get(Integer.valueOf(ArrayIndex)).dayNumPos;
	}

	/**
	 * returns the time
	 * 
	 * @param ArrayIndex
	 *            The array index
	 * @return the time
	 */
	public int getTime(int ArrayIndex){
		return ArrayIDs.get(Integer.valueOf(ArrayIndex)).timePos;
	}

	/**
	 * returns an Intger of the Key for the hash table
	 * 
	 * @param index
	 *            the index of the Key
	 * @return the Key
	 */
	public ArrayList<Integer> getIDat(){
		return ArrayInt;
	}

	@Override
	public String toString(){
		return this.getName();
	}
        
        public void saveConfigFileNewFormat(String path){
            StringBuffer output = new StringBuffer();
            boolean isTableBased = false;
            isTableBased = this.getDateTime(1) != -1;
            
            output.append("<?xml version='1.0'?>\n\n");
            output.append("<sourceConfiguration version='2'>");
            output.append("\n");
                
            if(isTableBased){//table based format
                output.append("<DataParserConfiguration type='CSI_TABLEBASED_CSV'>\n");
                output.append("<File>\n<SourceFile>"+inputFileName+"</SourceFile>\n");
                output.append("<DateTimeFormat type='CSIString'>\n" +
                                "<DateTimeColumnIndex>" + this.getDateTime(1) + "</DateTimeColumnIndex>\n" +
				"<FormatString>yyyy-MM-dd HH:mm:ss</FormatString>\n" +
                                "</DateTimeFormat>\n");
                output.append("<Columns>\n");
                for(int i=0;i<this.getLengthCN(1);i++){
                    if(this.getChanNames(i,1).contains("ignore")){
                        output.append("<Column index='" + i + "'><ColType>ignore</ColType></Column>\n");
                    }else{
                        output.append("<Column index='"+i+"'><ColType>data</ColType><MetaData>"+
                                this.getXML(i,1).replace("</DataPoint>","").replace("</datapoint>","")+
                                "</MetaData></Column>\n");
                    }
                }
                output.append("</Columns>\n</File>\n");
                output.append("</DataParserConfiguration>");
            }else{//arrayID based format
                
                output.append("<DataParserConfiguration type='DataParserCSIArrayID'>\n");
                output.append("<SourceFile>"+inputFileName+"</SourceFile>\n");
                output.append("<ArrayIDIndex>0</ArrayIDIndex>");
                for(int id:ArrayIDs.keySet()){
                    output.append("<ArrayID id='" + id + "'>\n");
                    output.append("<DateTimeFormat type='CSIYEAR_DAYNUM_TYPE'>\n");
                    output.append("<YearIndex>"+ this.getYear(id) +"</YearIndex>\n");
                    output.append("<DaynumIndex>" + this.getDayNum(id) + "</DaynumIndex>\n");
                    output.append("<TimeIndex>" + this.getTime(id) + "</TimeIndex>\n");
                    output.append("</DateTimeFormat>\n<Columns>\n");
                    for(int i=0;i<this.getLengthCN(id);i++){
                        if(this.getChanNames(i,id).contains("ignore")){
                            output.append("<Column index='" + i + "'><ColType>ignore</ColType></Column>\n");
                        }else{
                            output.append("<Column index='"+i+"'><ColType>data</ColType><MetaData>"+
                                    this.getXML(i,id).replace("</DataPoint>","").replace("</datapoint>","")+
                                    "</MetaData></Column>\n");
                        }
                    }
                    output.append("</Columns>\n");
                    output.append("</ArrayID>\n");
                }
                
                output.append("</DataParserConfiguration>");
            }
            
            output.append("</sourceConfiguration>");
            
            try{
                OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(path));    
                out.write(output.toString());
                out.close();
            }catch(FileNotFoundException fnfe){
                
            }catch(IOException ioe){
                
            }
            
        }

	/**
	 * Represents one section of the configuration file
	 */
	public class ConfigSection{
		/**
		 * The channel names represent the name of the channel
		 */
		public ArrayList<String> chanNames= new ArrayList<String>(36);
		/**
		 * represents the XML string used to coordinate data
		 */
		public ArrayList<String> XMLstring= new ArrayList<String>(36);
		/**
		 * public declaration removes synthetic method and improves preformance
		 */
		public int yearPos= -1;// private int arrayid = -1;
		/**
		 * public declaration removes synthetic method and improves preformance
		 */
		public int dayNumPos= -1;
		/**
		 * public declaration removes synthetic method and improves preformance
		 */
		public int timePos= -1;
		// Files can either have a datetime column or year, daynum and time
		/**
		 * public declaration removes synthetic method and improves preformance
		 */
		public int datetimePos= -1;

		/**
		 * @param rows
		 */
		public ConfigSection(ArrayList<String> rows){
			String token;
			StringTokenizer st1;
			for(int i= 0;i< rows.size();i++){
				st1= new StringTokenizer(rows.get(i),DELIM);
				token= st1.nextToken();
				if(token.trim().toLowerCase().compareTo("datetime")== 0){
					datetimePos= i;
					chanNames.add("ignore");
					XMLstring.add("ignore");
				}else if(token.trim().toLowerCase().compareTo("year")== 0){
					yearPos= i;
					chanNames.add("ignore");
					XMLstring.add("ignore");
				}else if(token.trim().toLowerCase().compareTo("time")== 0){
					timePos= i;
					chanNames.add("ignore");
					XMLstring.add("ignore");
				}else if(token.trim().toLowerCase().compareTo("daynum")== 0){
					dayNumPos= i;
					chanNames.add("ignore");
					XMLstring.add("ignore");
				}else if(token.trim().toLowerCase().compareTo("ignore")== 0){
					chanNames.add("ignore");
					XMLstring.add("ignore");
				}else{
					chanNames.add(token.trim());
					XMLstring.add(st1.nextToken().trim());
				}
			}
		}
	}
        
        public static void dispDialogToConvert(){
            JFileChooser chooser = new JFileChooser();
            ConfigFile toConvert;
            try{
                if(chooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
                    toConvert = new ConfigFile(chooser.getSelectedFile().getAbsolutePath());
                    toConvert.saveConfigFileNewFormat(chooser.getSelectedFile().getAbsolutePath());;
                }
            }catch(Exception e){
                //Either file not found, or file not properly formatted settings file.
                //do nothing
            }
            
        }
        public static void main(String[] args){
            dispDialogToConvert();
        }
}
