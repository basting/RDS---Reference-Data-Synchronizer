package com.amdocs.bgh.rds.udp;


import java.util.ArrayList;

public class UDPL {
	private String title;
	private String refId;
	private ArrayList<ArrayList<UDPLElem>> elementsAtEachLevel;
	private ArrayList<UDPLElem> childElems;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return title + ": " + childElems;
	}
	
	public ArrayList<UDPLElem> getChildElems() {
		return childElems;
	}

	public void setChildElems(ArrayList<UDPLElem> childElems) {
		this.childElems = childElems;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public ArrayList<ArrayList<UDPLElem>> getElementsAtEachLevel() {
		return elementsAtEachLevel;
	}

	public void setElementsAtEachLevel(
			ArrayList<ArrayList<UDPLElem>> elementsAtEachLevel) {
		this.elementsAtEachLevel = elementsAtEachLevel;
	}
}
