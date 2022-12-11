package com.github.zerobyteword.jsondiff.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.zerobyteword.jsondiff.core.JsonDiff;

import java.util.Iterator;
import java.util.Objects;

public class FastJsonDiff extends JsonDiff {


    @Override
    protected JSON toJsonNode(String source) throws Exception {
        return JSON.parseObject(source);
    }

    @Override
    protected Object getFieldValueFromObject(Object targetNode, String fieldName) {
        return ((JSONObject) targetNode).get(fieldName);
    }

    @Override
    protected Iterator<?> getFieldsFromObject(Object sourceNode) {
        return ((JSONObject) sourceNode).entrySet().iterator();
    }

    @Override
    protected Iterator<Objects> getArrayNodeIterator(Object sourceNode) {
        return ((JSONArray) sourceNode).iterator();
    }

    @Override
    protected boolean isArray(Object sourceNode) {
        return sourceNode instanceof JSONArray;
    }

    @Override
    protected boolean isValueNode(Object sourceNode) {
        return !(sourceNode instanceof JSON);
    }

    @Override
    protected String jsonNode2String(Object targetNode) {
        if (targetNode instanceof JSON) {
            return JSON.toJSONString(targetNode);
        }
        return Objects.toString(targetNode);
    }

    @Override
    protected boolean isTypeMatch(Object sourceNode, Object targetNode) {
        if (!(sourceNode instanceof JSON) && !(targetNode instanceof JSON)) {
            return true;
        }
        if (sourceNode instanceof JSONObject && targetNode instanceof JSONObject) {
            return true;
        }
        if (sourceNode instanceof JSONArray && targetNode instanceof JSONArray) {
            return true;
        }
        return false;
    }
}
