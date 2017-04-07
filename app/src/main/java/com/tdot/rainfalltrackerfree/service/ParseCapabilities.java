package com.tdot.rainfalltrackerfree.service;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

import com.tdot.rainfalltrackerfree.model.Constants;
import com.tdot.rainfalltrackerfree.model.DataStore;
import com.tdot.rainfalltrackerfree.model.LayerDescriptor;

public class ParseCapabilities implements Serializable {
	
	//utility - convert XML to Document
	public Document XMLfromString(String xml)
	{
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is); 
		} catch (ParserConfigurationException e) {
				            System.out.println("XML parse error: " + e.getMessage());
				            return null;
				        } catch (SAXException e) {
				            System.out.println("Wrong XML file structure: " + e.getMessage());
				            return null;
				        } catch (IOException e) {
				            System.out.println("I/O exeption: " + e.getMessage());
				            return null;
				        }
				 
				        return doc;		
	}
	//parser forecast
	public void parsefc(String xml,DataStore model, int submode)
    {
		Log.i("FC PARSER","about to parse submode is "+submode);
		
		String searchOn="";
		if(submode==Constants.SUB_MODE_FC_RAIN)
		{
			searchOn=Constants.REQUIRED_FIELD;
		}
		else
		{
			searchOn=Constants.REQUIRED_FIELD2;
		}
		Document xmlDoc= XMLfromString(model.getXML());
		NodeList nodes = xmlDoc.getElementsByTagName("Layer");
		
		NodeList timesteps = xmlDoc.getElementsByTagName("Timesteps");
		Log.i("0.FC Parser"," "+timesteps.getLength());
		  for (int i = 0; i < nodes.getLength(); i++) { 		            
	          Element e = (Element)nodes.item(i);
	          Element ets = (Element)timesteps.item(i);
	          Log.i("1. FC parser",e.getAttribute("displayName"));
	          if(e.getAttribute("displayName").equals(searchOn))
		      {
	            LayerDescriptor lD=new LayerDescriptor();
	            Log.i("2. FC parser", ets.getAttribute("defaultTime"));
	            lD.setBaseTime(ets.getAttribute("defaultTime"));
	            lD.setName(e.getAttribute("displayName"));
	            NodeList id = e.getElementsByTagName("LayerName");
	            NodeList times = e.getElementsByTagName("Timestep");
	            Node idstring = (Node)id.item(0); //Only 1 item
	            lD.setIdentifier(idstring.getTextContent());
	            ArrayList x = new ArrayList();
	            for (int j = 0; j < times.getLength(); j++) { 	
	            	Node ee = (Node)times.item(j);
	            	x.add(ee.getTextContent());
	            	Log.i("FCparser",""+j+" "+ee.getNodeName()+" "+ee.getTextContent());
	            }
	         
	            Collections.reverse(x); //because goes opposite way to obs
	           
	            lD.resetCache(times.getLength());
	            lD.setTimes(x);
	            if(submode==Constants.SUB_MODE_FC_RAIN)
	    		{
	               model.addLayer(lD,Constants.MODE_FC);
	               model.layerNumberForFcRain=Constants.MODE_FC; //model.getNumLayers()-1; 
	               Log.i("PARSE ","layernumforfcRain "+model.layerNumberForFcRain);
	    		}
	            else
	            {
	               model.addLayer(lD,Constants.SUB_MODE_FC_MSLP);
		           model.layerNumberForFcMSLP=Constants.SUB_MODE_FC_MSLP; 
		           Log.i("PARSE ","layernumforfcMSLP "+model.layerNumberForFcMSLP);
	            }
		      }
		  }
		
	}
	//parser observed
	public void parse(String xml,DataStore model)
	{
		Log.i("PARSER","about to parse");
		Document xmlDoc= XMLfromString(model.getXML());
    	NodeList nodes = xmlDoc.getElementsByTagName("Layer");
    	//model.getSiteDataL().setNumDaysRead(nodes.getLength());
    		        //fill in the list items from the XML document
    		        for (int i = 0; i < nodes.getLength(); i++) { 		            
    		           Element e = (Element)nodes.item(i);
    		           Log.i("parser",e.getAttribute("displayName"));
    		           if(e.getAttribute("displayName").equals(Constants.REQUIRED_FIELD))
    		           {
    		            LayerDescriptor lD=new LayerDescriptor();
    		            NodeList times = e.getElementsByTagName("Time");
    		            lD.setName(e.getAttribute("displayName"));
    		            NodeList id = e.getElementsByTagName("LayerName");
    		            Node idstring = (Node)id.item(0); //Only 1 item
    		            lD.setIdentifier(idstring.getTextContent());
    		            ArrayList x = new ArrayList();
    		            for (int j = 0; j < times.getLength(); j++) { 	
    		            	Node ee = (Node)times.item(j);
    		            	x.add(ee.getTextContent());
    		            	Log.i("parser",""+j+" "+ee.getNodeName()+" "+ee.getTextContent());
    		           }
    		            lD.resetCache(times.getLength());
    		            lD.setTimes(x);
    		            model.addLayer(lD,Constants.MODE_OB);
    		            model.layerNumberForObRain=Constants.MODE_OB; //model.getNumLayers()-1;
    		            Log.i("PARSE ","layernumforobRain "+model.layerNumberForObRain);
    		           }
    		        }   
    	
    		        /*
    	NodeList repNodes = xmlDoc.getElementsByTagName("Rep");
    	Log.i("<<","<<"+repNodes.getLength());
    	for (int i = 0; i < repNodes.getLength(); i++) { 		            
            Element e = (Element)repNodes.item(i);
            model.getSiteDataL().setWindDirection(i, e.getAttribute("D"));
            model.getSiteDataL().setWindSpeed(i, Integer.parseInt(e.getAttribute("S")));
            model.getSiteDataL().setWeatherCode(i, Integer.parseInt(e.getAttribute("W"))); 
            if(e.getAttribute("Dm") == "")
            {
                model.getSiteDataL().setTemperature(i, Integer.parseInt(e.getAttribute("Nm")));
            }
            else
            {
                model.getSiteDataL().setTemperature(i, Integer.parseInt(e.getAttribute("Dm")));
            }
           
        }   */
    	
    
	}
	
}