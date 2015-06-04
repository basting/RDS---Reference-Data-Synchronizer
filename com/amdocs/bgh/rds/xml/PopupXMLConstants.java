package com.amdocs.bgh.rds.xml;

public interface PopupXMLConstants {
	
	public static final String RDS = "rds";
	public static final String SYSTEMS = "systems";
	public static final String SYSTEM = "system";
	public static final String ID = "id";
	public static final String DB_NAME = "db_name";
	public static final String DB_SERVER = "db_server";
	public static final String LOGIN_NAME = "login_name";
	public static final String DB_PASSWORD = "db_password";
	
	public static final String URL = "url";
	public static final String USER = "user";
	public static final String PASSWORD = "password";
	
	public static final String UDPLS = "udpls";
	public static final String UDPL = "udpl";
	public static final String TITLE = "title";
	
	public static final String LEVEL1 = "level1";
	public static final String LEVEL2 = "level2";
	public static final String LEVEL3 = "level3";
	public static final String LEVEL4 = "level4";
	public static final String LEVEL5 = "level5";
	
	public static final String FROMTABLE = "fromtable";
	public static final String VALUE = "value";
	public static final String REFID = "refid";
	public static final String DESCRIPTION = "description";
	public static final String TITLE_FROM = "title_from";
	public static final String REFID_FROM = "refid_from";
	public static final String PARENTKEY = "parentkey";
	public static final String EXTSRC_KEY = "extsrc_key";
	public static final String CONDITION = "condition";
	public static final String PARENT_REFID = "parent_refid";
	public static final String HARDCODED = "hardcoded";
	public static final String Y = "Y";
	public static final String N = "N";
	
	public static final String CRM = "crm";
	public static final String ABP = "abp";
	/*
<?xml version='1.0' encoding='UTF-8'?>
<rds>
	<systems>
		<system id='crm' db_name='Python' db_server='xlcrm81' login_name='sa' db_password='sa' />
		<system id='abp' url='jdbc:oracle:thin:@linhbi08:1521:XLCIN10G' user='xlcdb81' password='xlcdb81'/>
	</systems>
	<udpls>
		<udpl title='X_ADJUSTMENT_REASON_UDP'>
			<level1 fromtable='ar1_credit_reason'>
				<value title='Account' refid='A'/>
				<value title='Invoice' refid='I'/>
				<value title='Charge' refid='C'/>
				<level2 fromtable='ar1_credit_reason' title_from='credit_reason_description' refid_from='credit_reason_code' parentkey='refid' extsrc_key='activity_level_code' condition="manual_ind='Y' and Category_code='C'"/>
			</level1>
		</udpl>
	</udpls>
</rds>
*/
}