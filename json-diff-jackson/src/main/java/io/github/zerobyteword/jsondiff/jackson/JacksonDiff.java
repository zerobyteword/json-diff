package io.github.zerobyteword.jsondiff.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.zerobyteword.jsondiff.core.JsonDiff;

import java.util.Iterator;

public class JacksonDiff extends JsonDiff {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected JsonNode toJsonNode(String source) throws Exception {
        return objectMapper.readTree(source);
    }

    @Override
    protected Object getFieldValueFromObject(Object targetNode, String fieldName) {
        return ((JsonNode) targetNode).get(fieldName);
    }

    @Override
    protected Iterator<?> getFieldsFromObject(Object sourceNode) {
        return ((JsonNode) sourceNode).fields();
    }

    @Override
    protected Iterator<?> getArrayNodeIterator(Object sourceNode) {
        return ((JsonNode) sourceNode).elements();
    }

    @Override
    protected boolean isArray(Object sourceNode) {
        return ((JsonNode) sourceNode).isArray();
    }

    @Override
    protected boolean isValueNode(Object sourceNode) {
        return ((JsonNode) sourceNode).isValueNode();
    }

    @Override
    protected String jsonNode2String(Object targetNode) {
        return ((JsonNode) targetNode).asText();
    }

    @Override
    protected Object getNodeOriginValue(Object sourceNode) {
        return ((JsonNode) sourceNode).isNumber() ? ((JsonNode) sourceNode).decimalValue() : ((JsonNode) sourceNode).asText();
    }

    @Override
    protected boolean isTypeMatch(Object sourceNode, Object targetNode) {
        return ((JsonNode) sourceNode).getNodeType() == ((JsonNode) targetNode).getNodeType();
    }
}
