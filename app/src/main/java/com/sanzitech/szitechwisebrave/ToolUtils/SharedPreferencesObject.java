package com.sanzitech.szitechwisebrave.ToolUtils;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

import com.sanzitech.szitechwisebrave.Bean.DataBean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.List;
/**
 * Created by Mr Q on 2018/01/16.
 */
public class SharedPreferencesObject {
	public static String save(Context context,String rid,List<DataBean> irDatas) {
		String list = "";
	    SharedPreferences preferences = context.getSharedPreferences("list_rid",Context.MODE_PRIVATE);  
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    try {  
	        ObjectOutputStream oos = new ObjectOutputStream(baos);
	        oos.writeObject(irDatas);
	        list = new String(Base64.encode(baos.toByteArray(),Base64.DEFAULT));
	        Editor editor = preferences.edit();  
	        editor.putString(rid, list);  
	        editor.commit();  
	    } catch (IOException e) {  
	    	e.printStackTrace();
	    }
		return list;  
	}
	
	public static List<DataBean> read(Context context,String rid) {
		List<DataBean> irDatas = null;
	    SharedPreferences preferences = context.getSharedPreferences("list_rid", Context.MODE_PRIVATE);  
	    String productBase64 = preferences.getString(rid, "");
	    byte[] base64 = Base64.decode(productBase64.getBytes(),Base64.DEFAULT);
	    ByteArrayInputStream bais = new ByteArrayInputStream(base64);
	    try {  
	        ObjectInputStream bis = new ObjectInputStream(bais);
	        try {  
	        	irDatas = (List<DataBean>) bis.readObject();
	        } catch (ClassNotFoundException e) {  
	            e.printStackTrace();  
	        }  
	    } catch (StreamCorruptedException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  
	    return irDatas;  
	}
}
