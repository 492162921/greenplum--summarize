package com.chinadaas.connection;

import java.sql.Connection;
import java.sql.DriverManager;

import com.chinadaas.utils.ConfigureFileValue;

/**
 * Greenplum  ���ӹ���
 * 
 * @author xiaoxu 
 *
 */
public class GreenplumConnection {
	
	/**
	 * oracle ����
	 * 
	 * @return
	 */
	public static synchronized  Connection greenplumDBConn() {
		Connection oracleconn = null;
		String gpdriver = ConfigureFileValue.getConfigureValue("gpdriver");
		String gpusername = ConfigureFileValue.getConfigureValue("gpusername");
		String gppassword = ConfigureFileValue.getConfigureValue("gppassword");
		String gpURL = ConfigureFileValue.getConfigureValue("gpURL");
		try {
			Class.forName(gpdriver);
			oracleconn = DriverManager.getConnection(gpURL, gpusername, gppassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return oracleconn;
	}
}
