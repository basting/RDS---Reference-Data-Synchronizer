package com.amdocs.bgh.rds.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.amdocs.bgh.rds.udp.LevelStructure;
import com.amdocs.bgh.rds.udp.UDPLElem;
import com.amdocs.bgh.rds.udp.UDPStructure;
import com.amdocs.bgh.rds.xml.EnvMaster;
import com.amdocs.bgh.rds.xml.PopupXMLConstants;

public class DataGatherer {
	public void queryDataFromBillingForUDP(UDPStructure udpStructure) {
		Connection conn = connectToDb();

		ArrayList<LevelStructure> levelStructures = udpStructure.getLevelStructures();
		int levelStructuresCount = levelStructures.size();
		for(int i=0;i<levelStructuresCount;i++){
			LevelStructure levelStructure = levelStructures.get(i);
			int currentLevel = levelStructure.getLevel();
			if(!levelStructure.isHardcoded()){
				String sqlString = getSQLToQueryBilling(levelStructure);
				PreparedStatement pstmt = null;
				try {
					pstmt = conn.prepareStatement(sqlString);
					pstmt.execute();

					ResultSet rs = pstmt.getResultSet();
					if(currentLevel != 1){
						String parentLevelKey = levelStructure.getParentKey();
						while(rs.next()){
							String refId = rs.getString(levelStructure.getRefidFrom());
							String title = rs.getString(levelStructure.getTitleFrom());
							String extSrcKey = rs.getString(levelStructure.getExtsrcKey());
							//Asking for the parent element.. sending the parent level list, parent key(ref id/title) & ext src key.
							UDPLElem udpParentElem = getParentForElement(extSrcKey,parentLevelKey,levelStructures.get(i-1).getUdpElements());
							if(udpParentElem != null){
								UDPLElem childUdpElem = new UDPLElem();
								childUdpElem.setLevel(currentLevel);
								childUdpElem.setRefId(refId);
								childUdpElem.setTitle(title);
								udpParentElem.addChildElems(childUdpElem);
								levelStructure.addElement(childUdpElem);
							}
						}
					}else{//for Level 1
						/**
						 * 
						 * 
						 */
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}


	private UDPLElem getParentForElement(String extSrcKey, String parentLevelKey, ArrayList<UDPLElem> parentUdpElements) {
		UDPLElem udpElem = null;
		boolean found = false;
		int parentUdpElemSize = parentUdpElements.size();
		for(int i=0;i<parentUdpElemSize;i++){
			udpElem = parentUdpElements.get(i);
			if(parentLevelKey.equalsIgnoreCase(PopupXMLConstants.REFID)){
				if(udpElem.getRefId().equals(extSrcKey)){
					found = true;
					break;
				}
			}else if(parentLevelKey.equalsIgnoreCase(PopupXMLConstants.TITLE)){
				if(udpElem.getTitle().equals(extSrcKey)){
					found = true;
					break;
				}
			}
		}
		if(!found)
			return null;
		return udpElem;
	}

	private String getSQLToQueryBilling(LevelStructure levelStructure) {
		StringBuilder sqlString = new StringBuilder();
		String condition = levelStructure.getCondition();
		String fromTable = levelStructure.getFromTable();
		String titleFrom = levelStructure.getTitleFrom();
		String refIdFrom = levelStructure.getRefidFrom();
		String extSrcKey = levelStructure.getExtsrcKey();

		sqlString.append("select ").append(titleFrom).append(" , ").append(refIdFrom).append(" , ").
		append(extSrcKey).append(" from ").append(fromTable).append(" where ").append(condition);

		return sqlString.toString();
	}

	private Connection connectToDb(){
		String connString=EnvMaster.abpEnvmap.get(PopupXMLConstants.URL);
		try {
			Class.forName ("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection conn = null;
		String username = EnvMaster.abpEnvmap.get(PopupXMLConstants.USER);
		String password = EnvMaster.abpEnvmap.get(PopupXMLConstants.PASSWORD);
		try {
			conn = DriverManager.getConnection(connString,username,password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return conn;
	}
}
