package com.chinadaas.utils;

import java.io.Reader;
import java.sql.Clob;

/**
 * ascii ȥ����
 * 
 * @author xiaoxu
 *
 */
public class AsciiTranUtils {
	
	/**
	 * ȥ���س�����,�Լ�������ascii����,�Լ�'����,�������ַ�ע�ص���ע��
	 * ascii������鿴:https://blog.csdn.net/xfg0218/article/details/80901752
	 * 
	 * @param clob clob �ֶ�
	 * @return
	 */
	public static StringBuffer toStringBuffer(Clob clob) {
	    try {
	        Reader reader = clob.getCharacterStream();
	        if (reader == null) {
	            return null;
	        }
	        StringBuffer sb = new StringBuffer();
	        char[] charbuf = new char[4096];
	        for (int i = reader.read(charbuf); i > 0; i = reader.read(charbuf)) {
	            sb.append(charbuf, 0, i);
	        }
	     return new StringBuffer(sb.toString().replaceAll("'","").replaceAll("[\\x01-\\x1f]", ""));
	    } catch (Exception e) {
	    }
		return null;
	}
}
