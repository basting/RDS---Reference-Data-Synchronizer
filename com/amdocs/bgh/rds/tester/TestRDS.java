package com.amdocs.bgh.rds.tester;

import java.util.ArrayList;

import com.amdocs.bgh.rds.data.DataGatherer;
import com.amdocs.bgh.rds.data.DataSynchronizer;
import com.amdocs.bgh.rds.udp.UDPStructure;
import com.amdocs.bgh.rds.xml.EnvMaster;
import com.amdocs.bgh.rds.xml.InputReaderRDS;
import com.amdocs.bgh.rds.xml.PopupXMLConstants;
import com.clarify.cbo.Application;
import com.clarify.cbo.BoContext;
import com.clarify.cbo.Session;

public class TestRDS {
	public static void main(String[] args) {
		InputReaderRDS inputReader = new InputReaderRDS();
		inputReader.readInputXMLFileForUDPL(args[0]);
		ArrayList<UDPStructure> udpStructures = inputReader.getUDPStructures();
		DataGatherer dataGatherer = new DataGatherer();
		DataSynchronizer dataSynchronizer = new DataSynchronizer();
		BoContext boContext = getBoContext();
		int udpStructSize = udpStructures.size();
		for(int i=0;i<udpStructSize;i++){
			UDPStructure udpStructure = udpStructures.get(i);
			dataGatherer.queryDataFromBillingForUDP(udpStructure);
			dataSynchronizer.synchronize(udpStructure,boContext);
		}
		//System.out.println(udpStructures);
		endApplication(boContext);
	}
	private static BoContext getBoContext(){
		Application app = new Application();
		Session session =  app.createSession();
		String loginName = EnvMaster.crmEnvMap.get(PopupXMLConstants.LOGIN_NAME);
		String password =  EnvMaster.crmEnvMap.get(PopupXMLConstants.DB_PASSWORD);
		session.login(loginName,password);
		
		return session.createBoContext(); 
	}
	
	private static void endApplication(BoContext boContext){
		Session session = boContext.getSession();
		Application app = session.getApp();
		session.logout();
		app.shutdown();
		
	}
}
