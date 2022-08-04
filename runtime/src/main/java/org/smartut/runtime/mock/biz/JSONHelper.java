package org.smartut.runtime.mock.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

public class JSONHelper {
    /**
     * entry for mockToJsonString
     * @param object
     * @return
     */
    public static String mockToJsonString(Object object){
        //先转JSON
        Object jsonObject = mockToJson(object);
        if(jsonObject != null){
            return JSON.toJSONString(jsonObject);
        }

        return "";
    }

    /**
     * entry for mock to json
     * @param object
     * @return
     */
    public static Object mockToJson(Object object){
        //List
        if(object instanceof Collection){
            return arrayToJson(object);
        }
        //Map
        if(object instanceof Map){
            return mapToJson(object);
        }
        //default
        return objectToJson(object);

    }

    /**
     * collections to json
     * @param object
     * @return
     */

    public static Object arrayToJson(Object object){
        if(object != null){
            JSONArray jsonArray = new JSONArray();
            // mock list
            if(isMockedObject(object)){
                return jsonArray;
            }
           // 非mock list
            Collection<Object> collection = (Collection<Object>) object;
            for(Object item : collection){
                jsonArray.add(objectToJson(item));
            }

            return jsonArray;
        }
        return null;
    }

    /**
     * map to json
     * @param object
     * @return
     */
    public static Object mapToJson(Object object){
        if(object != null){
            JSONObject jsonObject = new JSONObject();
            // mock map
            if(isMockedObject(object)){
                return jsonObject;
            }
            //非mock map
            Map<Object, Object> mapObj = (Map<Object, Object>) object;
            for (Map.Entry<Object, Object> entry : mapObj.entrySet()) {
                Object value = entry.getValue();
                jsonObject.put(TypeUtils.castToString(entry.getKey()), objectToJson(value));
            }
            return jsonObject;
        }

        return null;
    }

    /**
     * simple object to json
     * @param object
     * @return
     */
    public static Object objectToJson(Object object){
        if(object != null) {

            //递归
            if(object instanceof Collection || object instanceof Map){
                return mockToJson(object);
            }
            // finally

            if (isMockedObject(object)) {
                //直接是mock对象
                return new JSONObject();
            }else{
                //非mock对象，但可能嵌套mock，目前只处理一层
                JSONObject resObj = new JSONObject();
                boolean containsMock = false;
                Field[] fields = object.getClass().getDeclaredFields();
                if (fields.length > 0) {
                    for (Field field : fields) {
                        String fieldKey = field.getName();
                        String getMethodKey = String.format("get%s%s", fieldKey.substring(0, 1).toUpperCase(), fieldKey.substring(1));
                        try {
                            Method getMethod = object.getClass().getMethod(getMethodKey);
                            Object fieldValue = getMethod.invoke(object);
                            String fieldValueClassName = fieldValue.getClass().getName();
                            if (fieldValueClassName.contains("$MockitoMock$")) {
                                resObj.put(fieldKey, new JSONObject());
                                containsMock = true;
                            } else {
                                resObj.put(fieldKey, JSON.toJSON(fieldValue));
                            }
                        }catch (Exception e){
                            // NO-OP
                        }
                    }
                }
                return containsMock? resObj : JSON.toJSON(object);

            }

        }

        return null;
    }

    public static boolean isMockedObject(Object object){
        if(object != null){
            try{
                String objectClassName = object.getClass().getName();
                if (objectClassName.contains("$MockitoMock$")) {
                    return true;
                }
            }catch (Exception e){
                //NO-OP
            }
        }
        return false;
    }
}
