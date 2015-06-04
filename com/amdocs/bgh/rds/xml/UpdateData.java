package com.amdocs.bgh.rds.xml;

import java.sql.*;

public class UpdateData {
	public static void main(String[] args) throws SQLException{
		UpdateData ud = new UpdateData();
		ud.queryData();
	}
	public void queryData() throws SQLException{
		String connString="jdbc:oracle:thin:@linhbi08:1521:XLCIN10G";
		try {
			Class.forName ("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection conn = null;
		conn = DriverManager.getConnection(connString,"xlcdb81","xlcdb81");
		
		String sql = "select credit_reason_code,credit_reason_description from ar1_credit_reason " +
				" where manual_ind='Y' and Category_code='C'";
		PreparedStatement ps = conn.prepareStatement(sql);
		//boolean success = 
		ps.execute();
		ResultSet rs = ps.getResultSet();
		while(rs.next()){
			String crc = rs.getString("credit_reason_code");
			String crd = rs.getString("credit_reason_description");
			
			System.out.println(crc + " : " + crd );
		}
	}	
	
}