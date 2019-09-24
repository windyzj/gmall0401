package com.atguigu.gmall0401.payment.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StreamUtil {


    public static String inputStream2String(InputStream inStream, String encoding){
        int _buffer_size =1024;
        String result = null;
        try {
            if(inStream != null){
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] tempBytes = new byte[_buffer_size];
                int count = -1;
                while((count = inStream.read(tempBytes, 0, _buffer_size)) != -1){
                    outStream.write(tempBytes, 0, count);
                }
                tempBytes = null;
                outStream.flush();
                result = new String(outStream.toByteArray(), encoding);
            }
        } catch (Exception e) {
            result = null;
        }
        return result;
    }
}
