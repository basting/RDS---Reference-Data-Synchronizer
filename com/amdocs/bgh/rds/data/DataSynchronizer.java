package com.amdocs.bgh.rds.data;

import java.util.ArrayList;

import com.amdocs.bgh.rds.udp.LevelStructure;
import com.amdocs.bgh.rds.udp.UDPElementConstants;
import com.amdocs.bgh.rds.udp.UDPLElem;
import com.amdocs.bgh.rds.udp.UDPStructure;
import com.clarify.cbo.BoContext;
import com.clarify.cbo.Choice;
import com.clarify.cbo.ChoiceList;
import com.clarify.cbo.DdDataDict;
import com.clarify.cbo.Generic;

public class DataSynchronizer {
	public void synchronize(UDPStructure udpStructure, BoContext boContext){
		ArrayList<LevelStructure> levelStructures = udpStructure.getLevelStructures();
		ArrayList<UDPLElem> udpElements = levelStructures.get(0).getUdpElements();

		DdDataDict ddDataDict = (DdDataDict)boContext.createObj("Clarify.DdDataDict");

		boolean udpFound = getReportForUDPL(udpElements,ddDataDict,udpStructure.getRefId());

		String udpRefId = udpStructure.getRefId();
		if(udpFound){
			updateDataForUDP(udpElements,boContext);
			addNewElementsForUDP(udpElements,null,boContext,udpStructure.getRefId());
			removeElementsForUDP(levelStructures,boContext,udpStructure.getRefId(),ddDataDict);
		}else{
			createNewUDPL(udpStructure,boContext);
			addNewElementsForUDP(udpElements,null,boContext,udpStructure.getRefId());
		}
		System.out.println("UDPL with ref_id '"+udpRefId+"' synchronized");
		System.out.println("===========================================================================================");
	}

	private void removeElementsForUDP(ArrayList<LevelStructure> levelStructures, BoContext boContext, String udplRefId, DdDataDict ddDataDict) {
		ChoiceList choiceList = null;
		try{
			choiceList = ddDataDict.getUserChoiceListByRefId(udplRefId);
		}catch(Exception e){
			e.printStackTrace();			
		}
		makeElementsInactive(levelStructures,choiceList,boContext,udplRefId,ddDataDict);
	}
	
	private void makeElementsInactive(ArrayList<LevelStructure> levelStructures, ChoiceList choiceList, BoContext boContext, String udplRefId, DdDataDict ddDataDict) {
		if(choiceList == null)
			return;
		int choiceChildElemCount = choiceList.getCount();
		//System.out.println("Input child elements: "+childElems);
		for(int j=0;j<choiceChildElemCount;j++){
			Choice currChoice = choiceList.getItem(j+1);
			String currChoiceRefId = currChoice.getRefId();
			boolean found = findInLevelStructure(levelStructures,currChoiceRefId); 
			//Choice currChoice = choiceList.getChoiceByRefId(inputRefId);
			//System.out.println("CurrChoice "+currChoice);
			if(!found){
				makeInactive(currChoice.getId(),boContext);
			}
			ChoiceList childChoiceList = currChoice.getChildChoiceList();
			makeElementsInactive(levelStructures,childChoiceList,boContext,udplRefId,ddDataDict);
		}		
	}

	private void makeInactive(String choiceObjid, BoContext boContext) {
		Generic boHgbstElm = boContext.createGenericBO("hgbst_elm");
		boHgbstElm.setDataFields("objid,state");
		boHgbstElm.setFilter("objid='"+choiceObjid+"' and state!='Inactive'");
		boHgbstElm.query();
		
		if(boHgbstElm.getCount() > 0){
			boHgbstElm.setValue("state", "Inactive");
			boHgbstElm.update();
		}
	}

	private boolean findInLevelStructure(ArrayList<LevelStructure> levelStructures, String currChoiceRefId) {
		int levelStructureCount = levelStructures.size();
		for(int i=0;i<levelStructureCount;i++){
			LevelStructure currLevelStructure = levelStructures.get(i);
			ArrayList<UDPLElem> udpElements = currLevelStructure.getUdpElements();
			int udpElementsSize = udpElements.size();
			for(int j=0;j<udpElementsSize;j++){
				if(udpElements.get(j).getRefId().equals(currChoiceRefId))
					return true;
			}	
		}
		return false;
	}

	private void addNewElementsForUDP(ArrayList<UDPLElem> currElems, UDPLElem parentElem, BoContext boContext, String udplRefId) {
		if(currElems == null){
			return;
		}
		int udpElementsSize = currElems.size();
		for(int i=0;i<udpElementsSize;i++){
			UDPLElem currElem = currElems.get(i);
			int diffFlag = currElem.getDiffFlag();
			if(diffFlag == UDPElementConstants.NEWELEM){
				addElement(currElem,parentElem,boContext,udplRefId);
			}
			addNewElementsForUDP(currElem.getChildElems(),currElem,boContext,udplRefId);
		}
	}

	private void addElement(UDPLElem currElem, UDPLElem parentElem, BoContext boContext, String udplRefId) {
		// Query for the parent element & add element under it.
		Generic boHgbstShow = boContext.createGenericBO("hgbst_show");
		boHgbstShow.setDataFields("*");

		Generic boHgbstElmParent = null;
		//If parent element is null, then it is the first level
		if(parentElem == null){
			// Show of the current level.. querying based on list
			boHgbstShow.setFilterRelated("hgbst_show2hgbst_lst;ref_id='"+udplRefId+"'");
		}else{
			// Show of the current level.. querying based on parent element objid

			boHgbstElmParent = boContext.createGenericBO("hgbst_elm");
			boHgbstElmParent.setFilter("objid='"+parentElem.getChoiceObjid()+"'");
			
			//String filterToListShow =  getFilterToList(currElem.getLevel(),UDPElementConstants.SHOW);
			String filter = "chld_prnt2hgbst_show:hgbst_show2hgbst_elm;objid='"+parentElem.getChoiceObjid()+"'"; 
			//+ ";;"+filterToList+";ref_id='"+udplRefId+"'";
			boHgbstShow.setParentBO(boHgbstElmParent);
			boHgbstShow.setParentRelation("hgbst_elm2hgbst_show");
			boHgbstShow.setFilterRelated(filter);
		}
		Generic boHgbstElm = boContext.createGenericBO("hgbst_elm");
		boHgbstElm.setDataFields("*");
		boHgbstElm.setFilter("ref_id='"+currElem.getRefId()+"'");
		boHgbstElm.setParentBO(boHgbstShow);
		boHgbstElm.setParentRelation("hgbst_show2hgbst_elm");
		
		if(parentElem == null){
			boHgbstShow.query();
		}else{
			boHgbstElmParent.query();
		}

		// Some elements already present in the same level
		/*int boHgbstShowCount = boHgbstShow.getCount();
		boolean elmFlag = false;
		while(boHgbstShowCount > 0){
			if(boHgbstElm.getCount() > 0){
				elmFlag = true;
				break;
			}else{
				boHgbstShow.moveNext();
			}
			boHgbstShowCount--;
		}
		if(!elmFlag){
			boHgbstShow.moveFirst();			
		}*/
		
		if(boHgbstShow.getCount() > 0){	
			if(boHgbstElm.getCount() > 0){
				// the element present, but is in Inactive state
				boHgbstElm.setValue("state", "Active");
				boHgbstElm.setValue("title", currElem.getTitle());
				boHgbstElm.update();
			}else{
				//the element not present under the show item
				//String hgbstElmObjid=boContext.getSession().generateObjid("hgbst_elm");
				boHgbstElm.addNew();
				boHgbstElm.setValue("title", currElem.getTitle());
				boHgbstElm.setValue("ref_id", currElem.getRefId());
				boHgbstElm.setValue("rank", "0");
				boHgbstElm.setValue("state", "Active");
				boHgbstElm.setValue("intval1", "0");
				boHgbstElm.setValue("flags", "0"); // not sure what should be set for this!!
				boHgbstElm.update();
				
			}
			currElem.setChoiceObjid(boHgbstElm.getId());
		}else{
			// No elements already present in the same level..
			// Need to add new show item & new element
			Generic boHgbstShowParent = null;
			Generic boHgbstLst = null;
			if(parentElem == null){
				boHgbstLst = boContext.createGenericBO("hgbst_lst");
				boHgbstLst.setFilter("ref_id='"+udplRefId+"'");
				boHgbstLst.query();

				//boHgbstShow.setParentBO(boHgbstLst);
				//boHgbstShow.setParentRelation("hgbst_lst2hgbst_show");
			}else{
				
				boHgbstShowParent = boContext.createGenericBO("hgbst_show");
				boHgbstShowParent.setFilterRelated("hgbst_show2hgbst_elm;objid='"+parentElem.getChoiceObjid()+"'");
				boHgbstShowParent.setParentBO(boHgbstElmParent);
				boHgbstShowParent.setParentRelation("hgbst_elm2hgbst_show");
				// Adding a new show item to database as there is no child elements
				// Relating the show element to the parent show element
				//boHgbstShow.setDataFields("*");
				//boHgbstShow.setParentRelation("prnt_chld2hgbst_show");
				//boHgbstShow.setParentBO(boHgbstShowParent);
				
				boHgbstElmParent.query();				
			}
			//boHgbstShow.moveFirst();
			
			boHgbstShow.setDataFields("*");
			boHgbstShow.addNew();
			boHgbstShow.setValue("title", "Level "+currElem.getLevel());
			boHgbstShow.setValue("def_val","0");
			
			if(parentElem == null){
				boHgbstShow.relateById(boHgbstLst.getId(), "hgbst_show2hgbst_lst");
			}else{
				boHgbstShow.relateById(boHgbstShowParent.getId(), "chld_prnt2hgbst_show");	
			}
			
			Generic boHgbstElm1 = boContext.createGenericBO("hgbst_elm");
			boHgbstElm1.setDataFields("*");
			boHgbstElm1.setParentBO(boHgbstShow);
			boHgbstElm1.setParentRelation("hgbst_show2hgbst_elm");
			// Adding a new element to database as there is no child elements
			// Relating the element to the show item
			boHgbstElm1.addNew();
			boHgbstElm1.setValue("title", currElem.getTitle());
			boHgbstElm1.setValue("ref_id", currElem.getRefId());
			boHgbstElm1.setValue("rank", "0");
			boHgbstElm1.setValue("state", "Active");
			boHgbstElm1.setValue("intval1", "0");
			boHgbstElm1.setValue("flags", "0"); // not sure what should be set for this!!

			boHgbstShow.update();
			boHgbstElm1.update();

			currElem.setChoiceObjid(boHgbstElm1.getId());
			/*if(parentElem != null){
				Generic boHgbstElmParent1 = boContext.createGenericBO("hgbst_elm");
				boHgbstElmParent1.setFilter("objid='"+parentElem.getChoiceObjid()+"'");
				boHgbstElmParent1.query();

				boHgbstElmParent1.relateById(boHgbstShow.getId(), "hgbst_elm2hgbst_show");

				boHgbstElmParent1.update();	
			}*/

		}
	}

	/*private String getFilterToList(int level,int whichItem) {
		//Not required for I level as it directly connects to List
		String filter = null;
		if(whichItem == UDPElementConstants.SHOW){
			if(level ==2){
				filter = "chld_prnt2hgbst_show:hgbst_show2hgbst_lst";
			}else if(level ==3){
				filter = "chld_prnt2hgbst_show:chld_prnt2hgbst_show:hgbst_show2hgbst_lst";
			}else if(level ==4){
				filter = "chld_prnt2hgbst_show:chld_prnt2hgbst_show:chld_prnt2hgbst_show:hgbst_show2hgbst_lst";
			}else if(level ==5){
				filter = "chld_prnt2hgbst_show:chld_prnt2hgbst_show:chld_prnt2hgbst_show:chld_prnt2hgbst_show:hgbst_show2hgbst_lst";
			}
		}else if(whichItem == UDPElementConstants.ELM){
			if(level ==1){
				filter = "hgbst_elm2hgbst_show:hgbst_show2hgbst_lst";
			}else if(level ==2){
				filter = "hgbst_elm2hgbst_show:chld_prnt2hgbst_show:hgbst_show2hgbst_lst";
			}else if(level ==3){
				filter = "hgbst_elm2hgbst_show:chld_prnt2hgbst_show:chld_prnt2hgbst_show:hgbst_show2hgbst_lst";
			}else if(level ==4){
				filter = "hgbst_elm2hgbst_show:chld_prnt2hgbst_show:chld_prnt2hgbst_show:chld_prnt2hgbst_show:hgbst_show2hgbst_lst";
			}else if(level ==5){
				filter = "hgbst_elm2hgbst_show:chld_prnt2hgbst_show:chld_prnt2hgbst_show:chld_prnt2hgbst_show:chld_prnt2hgbst_show:hgbst_show2hgbst_lst";
			}
		}
		return filter;
	}
*/
	private void updateDataForUDP(ArrayList<UDPLElem> currElems, BoContext boContext) {
		if(currElems == null){
			return;
		}
		int udpElementsSize = currElems.size();
		for(int i=0;i<udpElementsSize;i++){
			UDPLElem currElem = currElems.get(i);
			int diffFlag = currElem.getDiffFlag();
			if(diffFlag == UDPElementConstants.MODIFYELEM){
				updateElement(currElem,boContext);
			}
			updateDataForUDP(currElem.getChildElems(),boContext);
		}
		/*if(diffFlag == UDPElementConstants.NEWELEM){
			addElementInUDPL(udplRefId,currElem,null,boContext);
		}*/
	}

	private void updateElement(UDPLElem currElem,BoContext boContext) {
		Generic boHgbstElm = boContext.createGenericBO("hgbst_elm");
		boHgbstElm.setDataFields("objid,title");
		boHgbstElm.setFilter("objid='"+currElem.getChoiceObjid()+"'");
		boHgbstElm.query();

		boHgbstElm.setValue("title", currElem.getTitle());
		boHgbstElm.update();
	}

	private void createNewUDPL(UDPStructure udpStructure, BoContext boContext) {
		String udplRefId = udpStructure.getRefId();
		Generic boHgbstLst = boContext.createGenericBO("hgbst_lst");
		boHgbstLst.setFilter("ref_id='"+udplRefId+"'");
		boHgbstLst.query();
		
		if(boHgbstLst.getCount() > 0){
			return;
		}
		boHgbstLst.setDataFields("*");
		boHgbstLst.addNew();
		boHgbstLst.setValue("title", udpStructure.getTitle());
		boHgbstLst.setValue("ref_id", udplRefId);
		boHgbstLst.setValue("description", udpStructure.getDescription());
		boHgbstLst.setValue("deletable", "1");
		boHgbstLst.setValue("flags", "1");
		boHgbstLst.setValue("locale", "0");
		boHgbstLst.setValue("list_id", "0");
		
		boHgbstLst.update();
	}

	private boolean getReportForUDPL(ArrayList<UDPLElem> levelOneElems, DdDataDict ddDataDict,String udplRefId) {
		ChoiceList choiceList = null;
		try{
			choiceList = ddDataDict.getUserChoiceListByRefId(udplRefId);
		}catch(Exception e){
			System.out.println("===========================================================================================");
			System.out.println("UDPL with refid '"+udplRefId+"' NOT found in db");
			System.out.println("===========================================================================================");
			return false;
		}
		System.out.println("===========================================================================================");
		System.out.println("The UDPL with RefId '"+udplRefId+"' found in Db...Analyzing the elements..");
		System.out.println("-------------------------------------------------------------------------------------------");
		int inputChildElemCount = levelOneElems.size();
		//System.out.println("Input child elements: "+childElems);
		for(int j=0;j<inputChildElemCount;j++){
			UDPLElem currChildElem = levelOneElems.get(j); 
			String inputRefId = currChildElem.getRefId();
			Choice currChoice = choiceList.getChoiceByRefId(inputRefId);
			//System.out.println("CurrChoice "+currChoice);
			if(currChoice == null){
				currChildElem.setNewElemFlag();
				/*System.out.println("The choice with refId '"+inputRefId+"' at level '"+currChildElem.getLevel()+
						"' of UDPL '"+udplRefId+"'\n");*/
			}else{
				currChildElem.setChoiceObjid(currChoice.getId());
				if(currChildElem.getTitle().equals(currChoice.getTitle())){
					currChildElem.setNoChangeFlag();
				}else{
					currChildElem.setModifiedElemFlag();					
				}
				CompareChildElementsOfUDPElement(currChildElem,currChoice,udplRefId);
			}
		}
		return true;
	}
	private void CompareChildElementsOfUDPElement(UDPLElem currElem, Choice currChoice, String udplName) {
		//String currRefId = currElem.getRefId();
		//System.out.println("Analysing the child elements of '"+currRefId+"'");
		ArrayList<UDPLElem> inputChildList = currElem.getChildElems();
		if(inputChildList != null){
			int inputChildElemCount = inputChildList.size();
			ChoiceList childListDb = currChoice.getChildChoiceList();
			for(int j=0;j<inputChildElemCount;j++){
				UDPLElem currChildElem = inputChildList.get(j);
				String inputRefId = currChildElem.getRefId();
				if(childListDb != null){
					Choice currChildChoice = childListDb.getChoiceByRefId(inputRefId);
					if(currChildChoice == null){
						currChildElem.setNewElemFlag();
						/*System.out.println("The choice with refId '"+inputRefId+"' at level '"+currChildElem.getLevel()+
								"' of UDPL '"+udplName+"' under the parent choice '"+currRefId+"'\n");*/
					}else{
						currChildElem.setChoiceObjid(currChildChoice.getId());
						if(currChildElem.getTitle().equals(currChildChoice.getTitle())){
							currChildElem.setNoChangeFlag();
						}else{
							currChildElem.setModifiedElemFlag();
						}
						CompareChildElementsOfUDPElement(currChildElem,currChildChoice,udplName);
					}
				}else{
					currChildElem.setNewElemFlag();
					/*System.out.println("-------------------------------------------------------------------------------------------\n");
					System.out.println("NO child elements in db for element with refid '"+inputRefId +"' as compared to '"+inputChildElemCount+"' in input file\n");
					System.out.println("-------------------------------------------------------------------------------------------\n");*/
				}
			}
		}
	}


}
