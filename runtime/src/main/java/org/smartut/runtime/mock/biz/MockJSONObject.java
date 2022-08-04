package org.smartut.runtime.mock.biz;

import com.alibaba.fastjson.JSONObject;
import org.smartut.runtime.mock.MethodStaticReplacementMock;

public class MockJSONObject implements MethodStaticReplacementMock {



    /**
     * static override for JSONObject.toJSONString
     * @param object
     * @return
     */
    public static String toJSONString(Object object) {
        return JSONHelper.mockToJsonString(object);
    }

    /**
     * static override for JSONObject.toJSON
     */
    public static Object toJSON(Object object){
        return JSONHelper.mockToJson(object);
    }


    @Override
    public String getMockedClassName() {
        return JSONObject.class.getName();
    }
}
