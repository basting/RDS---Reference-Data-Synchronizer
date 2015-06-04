package com.amdocs.bgh.rds.xml;


import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.amdocs.bgh.rds.udp.LevelStructure;
import com.amdocs.bgh.rds.udp.UDPLElem;
import com.amdocs.bgh.rds.udp.UDPStructure;

public class InputReaderRDS {
	private ArrayList<UDPStructure> udpStructures;

	public InputReaderRDS(){
		udpStructures = new ArrayList<UDPStructure>();
	}

	public void readInputXMLFileForUDPL(String fileName) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document doc = null;

		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			System.exit(0);
		}
		try {
			doc = db.parse( fileName );
		} catch (SAXException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		createUDPLStructure(null,doc,null,null);

	}

	public ArrayList<UDPStructure> getUDPStructures(){
		return udpStructures;
	}

	private void createUDPLStructure(String currUDPName,Node node,String parent,String currLevel){
		short nodeType = node.getNodeType();
		switch(nodeType){
		case Node.DOCUMENT_NODE:
			Document doc = (Document)node;
			Node docElementNode =  doc.getDocumentElement();
			if(!PopupXMLConstants.RDS.equals(docElementNode.getNodeName())){
				System.out.println("Input file format not valid!...exiting.. ");
				System.exit(0);
			}
			createUDPLStructure(null,docElementNode,null,null);
			break;
		case Node.ELEMENT_NODE:
			Node currNode = node;
			String currNodeString = currNode.getNodeName();
			if(PopupXMLConstants.RDS.equals(currNodeString)){
				NodeList nodeList = node.getChildNodes();
				int nodeListLen = nodeList.getLength();
				for(int i=0;i<nodeListLen;i++){
					Node childNode = nodeList.item(i);
					createUDPLStructure(null,childNode,null,null);					
				}
			}else if(PopupXMLConstants.SYSTEMS.equals(currNodeString)){
				NodeList nodeList = node.getChildNodes();
				int nodeListLen = nodeList.getLength();
				for(int i=0;i<nodeListLen;i++){
					Node childNode = nodeList.item(i);
					createUDPLStructure(null,childNode,null,null);					
				}
			}else if(PopupXMLConstants.SYSTEM.equals(currNodeString)){
				String systemId = getAttrValue(currNode,PopupXMLConstants.ID);
				if(systemId.equals(PopupXMLConstants.CRM)){
					EnvMaster.crmEnvMap.put(PopupXMLConstants.LOGIN_NAME,getAttrValue(currNode,PopupXMLConstants.LOGIN_NAME));
					EnvMaster.crmEnvMap.put(PopupXMLConstants.DB_PASSWORD,getAttrValue(currNode,PopupXMLConstants.DB_PASSWORD));
					EnvMaster.crmEnvMap.put(PopupXMLConstants.DB_NAME,getAttrValue(currNode,PopupXMLConstants.DB_NAME));
					EnvMaster.crmEnvMap.put(PopupXMLConstants.DB_SERVER,getAttrValue(currNode,PopupXMLConstants.DB_SERVER));
				}else if(systemId.equals(PopupXMLConstants.ABP)){
					EnvMaster.abpEnvmap.put(PopupXMLConstants.URL, getAttrValue(currNode,PopupXMLConstants.URL));
					EnvMaster.abpEnvmap.put(PopupXMLConstants.USER, getAttrValue(currNode,PopupXMLConstants.USER));
					EnvMaster.abpEnvmap.put(PopupXMLConstants.PASSWORD, getAttrValue(currNode,PopupXMLConstants.PASSWORD));
				}
			}else if(PopupXMLConstants.UDPLS.equals(currNodeString)){
				NodeList nodeList = node.getChildNodes();
				int nodeListLen = nodeList.getLength();
				for(int i=0;i<nodeListLen;i++){
					Node childNode = nodeList.item(i);
					createUDPLStructure(null,childNode,null,null);					
				}
			}else if(PopupXMLConstants.UDPL.equals(currNodeString)){
				String title = getAttrValue(node, PopupXMLConstants.TITLE);
				String refId = getAttrValue(node, PopupXMLConstants.REFID);
				String desc = getAttrValue(node, PopupXMLConstants.DESCRIPTION);
				//elementsAtEachLevel = new ArrayList<ArrayList<UDPLElem>>();
				//createListForElementsAtEachLevel(totalLevels,elementsAtEachLevel);

				UDPStructure currUdpl = new UDPStructure();
				if(title == null || refId ==null){
					System.out.println("Title/RefId for UDPL should be specified in the XML");
					System.exit(0);
				}
				currUdpl.setTitle(title);
				currUdpl.setRefId(refId);
				currUdpl.setDescription(desc);
				//currUdpl.setElementsAtEachLevel(elementsAtEachLevel);

				udpStructures.add(currUdpl);

				NodeList childNodeList = node.getChildNodes();
				int nodeListLen = childNodeList.getLength();
				for(int i=0;i<nodeListLen;i++){
					Node childNode = childNodeList.item(i);
					if(childNode.getNodeName().equals(PopupXMLConstants.LEVEL1)){
						NamedNodeMap attrMap = childNode.getAttributes();
						String hardcoded = attrMap.getNamedItem(PopupXMLConstants.HARDCODED).getNodeValue();
						if(hardcoded.equals(PopupXMLConstants.N)){
							LevelStructure levelStructure = new LevelStructure();
							levelStructure.setHardcoded(false);
							levelStructure.setLevel(1);

							NodeList propertyNodeList = childNode.getChildNodes();
							int propertyNodeListLen = propertyNodeList.getLength();
							for(int j=0;j<propertyNodeListLen;j++){
								Node propertyNode = propertyNodeList.item(j);
								if(propertyNode.getNodeName().equals(PopupXMLConstants.CONDITION)){
									levelStructure.setCondition(propertyNode.getTextContent());
								}else if(propertyNode.getNodeName().equals(PopupXMLConstants.FROMTABLE)){
									levelStructure.setFromTable(propertyNode.getTextContent());
								}else if(propertyNode.getNodeName().equals(PopupXMLConstants.REFID_FROM)){
									levelStructure.setRefidFrom(propertyNode.getTextContent());
								}else if(propertyNode.getNodeName().equals(PopupXMLConstants.TITLE_FROM)){
									levelStructure.setTitleFrom(propertyNode.getTextContent());
								}							
							}
							currUdpl.addLevelStructure(levelStructure);
						}else{ // Hardcoded values
							LevelStructure levelStructure = new LevelStructure();
							levelStructure.setHardcoded(true);
							levelStructure.setLevel(1);
							levelStructure.setUdpElements(getAllHardcodedElements(childNode,1));

							currUdpl.addLevelStructure(levelStructure);
						}
					}else if(childNode.getNodeName().equals(PopupXMLConstants.LEVEL2)){
						NamedNodeMap attrMap = childNode.getAttributes();
						String hardcoded = attrMap.getNamedItem(PopupXMLConstants.HARDCODED).getNodeValue();
						if(hardcoded.equals(PopupXMLConstants.N)){
							LevelStructure levelStructure = new LevelStructure();
							levelStructure.setHardcoded(false);
							levelStructure.setLevel(2);

							NodeList propertyNodeList = childNode.getChildNodes();
							int propertyNodeListLen = propertyNodeList.getLength();
							for(int j=0;j<propertyNodeListLen;j++){
								Node propertyNode = propertyNodeList.item(j);
								if(propertyNode.getNodeName().equals(PopupXMLConstants.EXTSRC_KEY)){
									levelStructure.setExtsrcKey(propertyNode.getTextContent());
								}else if(propertyNode.getNodeName().equals(PopupXMLConstants.CONDITION)){
									levelStructure.setCondition(propertyNode.getTextContent());
								}else if(propertyNode.getNodeName().equals(PopupXMLConstants.FROMTABLE)){
									levelStructure.setFromTable(propertyNode.getTextContent());
								}else if(propertyNode.getNodeName().equals(PopupXMLConstants.REFID_FROM)){
									levelStructure.setRefidFrom(propertyNode.getTextContent());
								}else if(propertyNode.getNodeName().equals(PopupXMLConstants.TITLE_FROM)){
									levelStructure.setTitleFrom(propertyNode.getTextContent());
								}else if(propertyNode.getNodeName().equals(PopupXMLConstants.PARENTKEY)){
									levelStructure.setParentKey(propertyNode.getTextContent());
								}
							}
							currUdpl.addLevelStructure(levelStructure);
						}else{ // Hardcoded values
							LevelStructure levelStructure = new LevelStructure();
							levelStructure.setHardcoded(true);
							levelStructure.setLevel(2);
							levelStructure.setUdpElements(getAllHardcodedElements(childNode,2));

							currUdpl.addLevelStructure(levelStructure);
						}
					}else if(childNode.getNodeName().equals(PopupXMLConstants.LEVEL3)){

					}else if(childNode.getNodeName().equals(PopupXMLConstants.LEVEL4)){

					}else if(childNode.getNodeName().equals(PopupXMLConstants.LEVEL5)){

					}
				}
			}
			break;
		case Node.TEXT_NODE:	
			break;
		}
	}




	private ArrayList<UDPLElem> getAllHardcodedElements(Node node,int level) {
		ArrayList<UDPLElem> elements = new ArrayList<UDPLElem>();
		NodeList childNodeList = node.getChildNodes();
		int nodeListLen = childNodeList.getLength();
		for(int i=0;i<nodeListLen;i++){
			Node childNode = childNodeList.item(i);
			if(childNode.getNodeName().equals(PopupXMLConstants.VALUE)){
				UDPLElem elem = new UDPLElem();
				elem.setTitle(getAttrValue(childNode, PopupXMLConstants.TITLE));
				elem.setRefId(getAttrValue(childNode, PopupXMLConstants.REFID));
				elem.setLevel(level);	
				elements.add(elem);
			}
		}
		return elements;
	}

	private String getAttrValue(Node node,String name){
		NamedNodeMap attrMap =  node.getAttributes();
		Node attrNode = attrMap.getNamedItem(name);
		if(attrNode != null)
			return attrNode.getNodeValue();
		else
			return null;
	}

}

