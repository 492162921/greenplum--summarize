package com.chinadaas.loaddata;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.chinadaas.connection.GreenplumConnection;
import com.chinadaas.connection.OracleConnection;
import com.chinadaas.utils.AsciiTranUtils;
import com.chinadaas.utils.ConfigureFileValue;

/**
 * ���ݼ�������
 * 
 * @author xiaoxu 
 *
 */
public class OracleToGreenplum {
	
	private static Connection oracleconnect = null;
	private static Statement oraclestm = null;
	private static ResultSet oraclers = null;

	/**
	 * 
	 */
	public static void loaddata() {
		//  ���ݶ����ύһ��
		int batchsize = Integer.parseInt(ConfigureFileValue.getConfigureValue("batchsize"));
		// oracle ��ѯ���
		String oraclesql = ConfigureFileValue.getConfigureValue("oraclesql");
		// gp ��ѯ���
		String gpsql = ConfigureFileValue.getConfigureValue("gpsql");
		
		Integer index = 1;
		String value1;
		String value2;
		Connection gpconnect = null;
		Statement gpstmt = null;
		long startTime = System.currentTimeMillis();
		
		String insertsql = null;
		StringBuffer suffix = new StringBuffer();
		try {
			// Greenplum connection
			gpconnect = GreenplumConnection.greenplumDBConn();
			gpstmt = gpconnect.createStatement();
			gpconnect.setAutoCommit(false);

			// oracle connection
			oracleconnect = OracleConnection.oracleDBConn();
			oraclestm = oracleconnect.createStatement();
			oraclers = oraclestm.executeQuery(oraclesql);
			while (oraclers.next()) {
				try {
					value1 = oraclers.getString(1).replace("'", "");
					value2 = oraclers.getString(2).replace("'", "");
					Clob clob = (Clob) oraclers.getClob(3);
					StringBuffer clobfiled = AsciiTranUtils.toStringBuffer(clob);
					// ƴ���ַ���
					if(index % batchsize != 0){
						suffix.append("('" + value1 + "','"+ value2 + "','" + clobfiled + "'),");
					}
					// �������εĴ�С���ύ����
					if (index % batchsize == 0) {
						suffix.append("('" + value1 + "','"+ value2 + "','" + clobfiled + "'),");
						insertsql = gpsql + suffix.substring(0, suffix.length() - 1);
						gpstmt.executeUpdate(insertsql);
						gpconnect.commit();
						suffix = new StringBuffer();
						long endTime = System.currentTimeMillis();
						System.out.println("insert count:" + index + "��ʱ  " + ((endTime - startTime) / 1000) + "s");
					}
					index++;
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("��ǰ��SQL����:" + insertsql);
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// �ر������ύ����
				insertsql = gpsql + suffix.substring(0, suffix.length() - 1);
				gpstmt.executeUpdate(insertsql);
				gpconnect.commit();
				oraclestm.close();
				oraclers.close();
				oracleconnect.close();
				gpstmt.close();
				gpconnect.close();
				long endTime = System.currentTimeMillis();
				System.out.println("insert count:"+index+"��ȫ��������,��ʱ:" + ((endTime - startTime) / 1000) + "s");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
