package com.amdocs.bgh.rds.udp;


import java.util.ArrayList;

public class UDPLElem {
	private String title;
	private String refId;
	
	private int level;
	
	private ArrayList<UDPLElem> childElems;

	// diffFlag = 0 --> No change in element
	// diffFlag = 1 --> Element to be modified
	// diffFlag = 2 --> New element to be modified
	private int diffFlag;
	
	private String choiceObjid;
	
	public UDPLElem(){
		diffFlag = UDPElementConstants.NEWELEM;
	}
	
	public String getChoiceObjid() {
		return choiceObjid;
	}

	public void setChoiceObjid(String choiceObjid) {
		this.choiceObjid = choiceObjid;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return level+":"+title + ":"+refId+"::"+childElems;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj != null){
			if(obj instanceof UDPLElem){
				UDPLElem elem = (UDPLElem)obj;
				if(this.refId == elem.refId){
					return true;
				}
			}
		}
		return false;
	}

	public void setNoChangeFlag(){
		diffFlag = UDPElementConstants.NOCHANGEELEM;
	}
	
	public void setModifiedElemFlag(){
		diffFlag = UDPElementConstants.MODIFYELEM;
	}
	
	public void setNewElemFlag(){
		diffFlag = UDPElementConstants.NEWELEM;
	}
	
	public int getDiffFlag(){
		return diffFlag;
	}
	
	
	public void addChildElems(UDPLElem childElem){
		if(childElems == null){
			childElems = new ArrayList<UDPLElem>();
		}
		childElems.add(childElem);
	}
	
	public ArrayList<UDPLElem> getChildElems() {
		return childElems;
	}

	public void setChildElems(ArrayList<UDPLElem> childElems) {
		this.childElems = childElems;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
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
	
	
}
