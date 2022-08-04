package org.smartut.runtime.mock.biz;

import com.alibaba.fastjson.JSON;

import org.smartut.runtime.mock.StaticReplacementMock;

public class MockJSON implements StaticReplacementMock {
    @Override
    public String getMockedClassName() {
        return JSON.class.getName();
    }


    /**
     * static mock for JSON.toJSONString
     * @param object
     * @return
     */
    public static String toJSONString(Object object) {
        return JSONHelper.mockToJsonString(object);
    }

    /**
     * static mock for JSON.toJSON
     * @param object
     * @return
     */
    public static Object toJSON(Object object){
        return JSONHelper.mockToJson(object);
    }
}
