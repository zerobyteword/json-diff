package io.github.zerobyteword.jsondiff.jackson;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Test;
import io.github.zerobyteword.jsondiff.core.DiffType;
import io.github.zerobyteword.jsondiff.core.JsonDiff;
import io.github.zerobyteword.jsondiff.core.ValueDifference;

import java.util.List;

public class JacksonDiffTest {

    /**
     * 普通json
     *
     * @throws
     */
    @Test
    public void difference_case1() throws Exception {
        String s1 = "{\n" +
                "    \"employee\":\n" +
                "    {\n" +
                "        \"id\": \"1212\",\n" +
                "        \"fullName\": \"John Miles\",\n" +
                "        \"age\": 34\n" +
                "    }\n" +
                "}";
        String s2 = "{   \n" +
                "    \"employee\":\n" +
                "    {\n" +
                "        \"id\": \"1212\",\n" +
                "        \"age\": 34,\n" +
                "        \"fullName\": \"John Miles\"\n" +
                "    }\n" +
                "}";
        JsonDiff jsonDiff = newJsonDiff();
        List<ValueDifference> differenceList = jsonDiff.diff(s1, s2);
        Assert.assertTrue(differenceList.isEmpty());
    }


    /**
     * 普通json
     *
     * @throws JsonProcessingException
     */
    @Test
    public void difference_case_ignore_by_json_path() throws Exception {
        String s1 = "{\n" +
                "    \"employee\":\n" +
                "    {\n" +
                "        \"id\": \"1212\",\n" +
                "        \"fullName\": \"John Miles\",\n" +
                "        \"age\": 34\n" +
                "    }\n" +
                "}";
        String s2 = "{   \n" +
                "    \"employee\":\n" +
                "    {\n" +
                "        \"id\": \"1212\",\n" +
                "        \"age\": 34,\n" +
                "        \"age2\": 34,\n" +
                "        \"fullName\": \"John Miles\"\n" +
                "    }\n" +
                "}";
        JsonDiff jsonDiff = newJsonDiff();
        jsonDiff.excludeDiffByType(DiffType.ADD, "$.employee");
        List<ValueDifference> differenceList = jsonDiff.diff(s1, s2);

        Assert.assertTrue(differenceList.isEmpty());
    }

    protected JsonDiff newJsonDiff() {
        return new JacksonDiff();
    }


    /**
     * 普通json
     *
     * @throws JsonProcessingException
     */
    @Test
    public void difference_nested_element() throws Exception {
        String s1 = "{ \n" +
                "    \"employee\":\n" +
                "    {\n" +
                "        \"id\": \"1212\",\n" +
                "        \"fullName\":\"John Miles\",\n" +
                "        \"age\": 34,\n" +
                "        \"contact\":\n" +
                "        {\n" +
                "            \"email\": \"john@xyz.com\",\n" +
                "            \"phone\": \"9999999999\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        String s2 = "{\n" +
                "    \"employee\":\n" +
                "    {\n" +
                "        \"id\": \"1212\",\n" +
                "        \"age\": 34,\n" +
                "        \"fullName\": \"John Miles\",\n" +
                "        \"contact\":\n" +
                "        {\n" +
                "            \"email\": \"john@xyz.com\",\n" +
                "            \"phone\": \"9999999999\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        JsonDiff jsonDiff = newJsonDiff();
        List<ValueDifference> differenceList = jsonDiff.diff(s1, s2);
        Assert.assertTrue(differenceList.isEmpty());
    }


    /**
     * 普通json
     *
     * @throws JsonProcessingException
     */
    @Test
    public void difference_containing_list() throws Exception {
        String s1 = "{\n" +
                "    \"employee\":\n" +
                "    {\n" +
                "        \"id\": \"1212\",\n" +
                "        \"fullName\": \"John Miles\",\n" +
                "        \"age\": 34,\n" +
                "        \"skills\": [\"Java\", \"C++\", \"Python\"]\n" +
                "    }\n" +
                "}";
        String s2 = "{\n" +
                "    \"employee\":\n" +
                "    {\n" +
                "        \"id\": \"1212\",\n" +
                "        \"age\": 34,\n" +
                "        \"fullName\": \"John Miles\",\n" +
                "        \"skills\": [\"Java\", \"C++\", \"Python\"] \n" +
                "    } \n" +
                "}";
        JsonDiff jsonDiff = newJsonDiff();
        List<ValueDifference> differenceList = jsonDiff.diff(s1, s2);
        Assert.assertTrue(differenceList.isEmpty());
    }


    /**
     * 普通json
     *
     * @throws JsonProcessingException
     */
    @Test
    public void difference_number() throws Exception {
        String s1 = "{\n" +
                "    \"name\": \"John\",\n" +
                "    \"score\": 5.0\n" +
                "}";
        String s2 = "{\n" +
                "    \"name\": \"John\",\n" +
                "    \"score\": 5\n" +
                "}";
        JsonDiff jsonDiff = newJsonDiff();
        jsonDiff.ignoreNumberAccuracy("$.score");
        List<ValueDifference> differenceList = jsonDiff.diff(s1, s2);
        Assert.assertTrue(differenceList.isEmpty());
    }


    /**
     * 普通json
     *
     * @throws JsonProcessingException
     */
    @Test
    public void difference_ignore_case_insensitive() throws Exception {
        String s1 = "{\n" +
                "    \"name\": \"john\", \n" +
                "    \"score\": 5 \n" +
                "}";
        String s2 = "{ \n" +
                "    \"name\": \"JOHN\", \n" +
                "    \"score\": 5.0 \n" +
                "}";
        JsonDiff jsonDiff = newJsonDiff();
        jsonDiff.ignoreStringCase("$.name");
        jsonDiff.ignoreNumberAccuracy("$.score");
        List<ValueDifference> differenceList = jsonDiff.diff(s1, s2);
        Assert.assertTrue(differenceList.isEmpty());
    }
}