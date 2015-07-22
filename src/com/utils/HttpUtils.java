package com.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
/**
 * ���ֽ���ת��Ϊ�ַ����Ĺ�����
 */
public class HttpUtils {

	public static String readMyInputStream(InputStream is) {
		byte[] result;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len;
			while ((len = is.read(buffer))!=-1) {
				baos.write(buffer,0,len);
			}
			is.close();
			baos.close();
			result = baos.toByteArray();
			
		} catch (IOException e) {
			e.printStackTrace();
			String errorStr = "��ȡ����ʧ�ܡ�";
			return errorStr;
		}
		return new String(result);
	}

}
