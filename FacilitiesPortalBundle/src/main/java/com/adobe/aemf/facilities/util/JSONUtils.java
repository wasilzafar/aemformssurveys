package com.adobe.aemf.facilities.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {
	
	public static JSONObject generateJSON(Map<String,Object> objectsMap) throws JSONException{
		JSONObject outJSON = new JSONObject();
		for (Map.Entry<String,Object> entry : (Set<Entry<String,Object>>) objectsMap.entrySet()) {
			outJSON.put(entry.getKey(), entry.getValue());
		}
		return outJSON;
	}
	
	public static JSONObject generateJSON(String key, List objectsList) throws JSONException{
		JSONObject outJSON = new JSONObject();
		outJSON.put(key, objectsList);
		return outJSON;
	}

	public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
	    Map<String, Object> retMap = new HashMap<String, Object>();
	    if(json != JSONObject.NULL) {
	        retMap = toMap(json);
	    }
	    return retMap;
	}

	public static Map<String, Object> toMap(JSONObject object) throws JSONException {
	    Map<String, Object> map = new HashMap<String, Object>();

	    Iterator<String> keysItr = object.keys();
	    while(keysItr.hasNext()) {
	        String key = keysItr.next();
	        Object value = object.get(key);

	        if(value instanceof JSONArray) {
	            value = toList((JSONArray) value);
	        }

	        else if(value instanceof JSONObject) {
	            value = toMap((JSONObject) value);
	        }
	        map.put(key, value);
	    }
	    return map;
	}

	public static List<Object> toList(JSONArray array) throws JSONException {
	    List<Object> list = new ArrayList<Object>();
	    for(int i = 0; i < array.length(); i++) {
	        Object value = array.get(i);
	        if(value instanceof JSONArray) {
	            value = toList((JSONArray) value);
	        }

	        else if(value instanceof JSONObject) {
	            value = toMap((JSONObject) value);
	        }
	        list.add(value);
	    }
	    return list;
	}
}
