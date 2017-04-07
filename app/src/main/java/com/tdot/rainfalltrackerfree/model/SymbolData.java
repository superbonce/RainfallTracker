package com.tdot.rainfalltrackerfree.model;

import java.util.ArrayList;
import java.util.List;

public class SymbolData {
	
	public float posX;
	public float posY;
	public int pixX;
	public int pixY;
	public List<String> times=new ArrayList();
	public List<String> values=new ArrayList();
	
	public SymbolData(float pX,float pY)
	{
		posX=pX;
		posY=pY;
	}
	
	public void addElement(String time,String value)
	{
		times.add(time);
		values.add(value);
	}
	
	public String getTime(int loc)
	{
		return times.get(loc);
	}
	
	public String getPOP(int loc)
	{
		return values.get(loc);
	}

}
