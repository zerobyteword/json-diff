package io.github.zerobyteword.jsondiff.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public abstract class JsonDiff {
    public static final String ROOT_PATH = "$";

    private TreeMap<String, TreeSet<EqualTo>> equalMap = new TreeMap<>(JSON_PATH_COMPARATOR);
    private final Set<String> ignoreDiffJsonPathSet = new HashSet<>();
    private final Map<DiffType, Set<String>> ignoreDiffTypeMap = new HashMap<>();
    private static Comparator<String> JSON_PATH_COMPARATOR = (o1, o2) -> {
        if (o1.length() != o2.length()) {
            return o2.length() - o1.length();
        }
        return o1.compareTo(o2);
    };


    private static Comparator<EqualTo> EQUAL_TO_COMPARATOR = (o1, o2) -> {
        Class<?> type1 = getComparatorType(o1);
        Class<?> type2 = getComparatorType(o2);
        if (type1.isAssignableFrom(type2)) {
            return 1;
        }
        return type1.getTypeName().compareTo(type2.getTypeName());
    };

    /**
     * 根据jsonPath忽略diff的不一致
     *
     * @param jsonPaths
     */
    public void ignoreDiffJsonPath(String... jsonPaths) {
        this.ignoreDiffJsonPathSet.addAll(Arrays.asList(jsonPaths));
    }

    /**
     * 根据jsonPath忽略diff的不一致
     *
     * @param jsonPaths
     */
    public void ignoreDiffJsonPath(List<String> jsonPaths) {
        this.ignoreDiffJsonPathSet.addAll(jsonPaths);
    }

    /**
     * 忽略指定类型的diff不一致
     *
     * @param diffType
     * @param jsonPaths
     */
    public void ignoreDiffType(DiffType diffType, String... jsonPaths) {
        Set<String> set = this.ignoreDiffTypeMap.get(diffType);
        if (set == null) {
            set = new HashSet<>(Arrays.asList(jsonPaths));
            this.ignoreDiffTypeMap.put(diffType, set);
        } else {
            set.addAll(Arrays.asList(jsonPaths));
        }
    }

    /**
     * 忽略指定类型的diff不一致
     *
     * @param diffType
     * @param jsonPaths
     */
    public void ignoreDiffType(DiffType diffType, List<String> jsonPaths) {
        Set<String> set = this.ignoreDiffTypeMap.get(diffType);
        if (set == null) {
            set = new HashSet<>(jsonPaths);
            this.ignoreDiffTypeMap.put(diffType, set);
        } else {
            set.addAll(jsonPaths);
        }
    }


    /**
     * 忽略字符串大小写
     *
     * @param jsonPaths
     */
    public void ignoreStringCase(String... jsonPaths) {
        for (String jsonPath : jsonPaths) {
            addCompare(jsonPath, getIgnoreStringCaseEqualTo());
        }
    }

    /**
     * 返回忽略大小比较器
     *
     * @return
     */
    protected EqualTo getIgnoreStringCaseEqualTo() {
        return StringIgnoreCaseEqualTo.DEFAULT;
    }

    /**
     * 设置jsonPath忽略大小写的
     *
     * @param jsonPaths
     */
    public void ignoreStringCase(List<String> jsonPaths) {
        for (String jsonPath : jsonPaths) {
            addCompare(jsonPath, getIgnoreStringCaseEqualTo());
        }
    }

    /**
     * 忽略数字精度
     *
     * @param jsonPaths
     */
    public void ignoreNumberAccuracy(String... jsonPaths) {
        for (String jsonPath : jsonPaths) {
            addCompare(jsonPath, getIgnoreNumberAccuracyEqualTo());
        }
    }

    /**
     * 忽略数字精度
     *
     * @param jsonPaths
     */
    public void ignoreNumberAccuracy(List<String> jsonPaths) {
        for (String jsonPath : jsonPaths) {
            addCompare(jsonPath, getIgnoreNumberAccuracyEqualTo());
        }
    }

    /**
     * 忽略数字精度的比较器
     *
     * @return
     */
    protected EqualTo<?> getIgnoreNumberAccuracyEqualTo() {
        return NumberIgnoreAccuracyEqualTo.DEFAULT;
    }



    private static Class<?> getComparatorType(EqualTo<?> equalTo) {
        ParameterizedType parameterizedType = (ParameterizedType) equalTo.getClass().getGenericInterfaces()[0];
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        return (Class<?>) actualTypeArguments[0];
    }


    /**
     * 添加是否相等比较器
     * @param jsonPath
     * @param equalTo
     */
    public void addCompare(String jsonPath, EqualTo<?> equalTo) {
        TreeSet<EqualTo> comparatorTreeSet = equalMap.get(jsonPath);
        if (comparatorTreeSet == null) {
            comparatorTreeSet = new TreeSet<>(EQUAL_TO_COMPARATOR);
            comparatorTreeSet.add(equalTo);
            equalMap.put(jsonPath, comparatorTreeSet);
        } else {
            comparatorTreeSet.add(equalTo);
        }
    }

    /**
     * 比较两个json字符串的字符串
     * @param source 基础json字符串
     * @param target 目标json字符串
     * @return
     * @throws Exception
     */
    public List<ValueDifference> diff(String source, String target) throws Exception {
        List<ValueDifference> differenceList = new ArrayList<>();
        if (source == null && target == null) {
            return differenceList;
        }
        // 比较
        if (source == null) {
            differenceList.add(new ValueDifference(DiffType.ADD, ROOT_PATH, ROOT_PATH, null, target));
            return differenceList;
        }
        if (target == null) {
            differenceList.add(new ValueDifference(DiffType.DELETE, ROOT_PATH, ROOT_PATH, source, null));
            return differenceList;
        }
        diff(toJsonNode(source), toJsonNode(target), differenceList, ROOT_PATH, ROOT_PATH);
        return differenceList.stream()
                .filter(e -> this.ignoreDiffJsonPathSet.stream().noneMatch(s -> e.getJsonPath().startsWith(s)))
                .filter(e -> {
                    Set<String> set = this.ignoreDiffTypeMap.get(e.getDiffType());
                    if (set == null || set.isEmpty()) {
                        return true;
                    }
                    return set.stream().noneMatch(s -> e.getJsonPath().startsWith(s));
                })
                .collect(Collectors.toList());
    }

    protected abstract Object toJsonNode(String source) throws Exception;

    private void diff(Object sourceNode, Object targetNode, List<ValueDifference> differenceList, String jsonPath, String diffPath) {
        if (sourceNode == null && targetNode == null) {
            return;
        }
        if (sourceNode == null) {
            differenceList.add(new ValueDifference(DiffType.ADD, jsonPath, diffPath, null, jsonNode2String(targetNode)));
            return;
        }
        if (targetNode == null) {
            differenceList.add(new ValueDifference(DiffType.DELETE, jsonPath, diffPath, jsonNode2String(sourceNode), null));
            return;
        }
        if (isTypeMatch(sourceNode, targetNode)) {
            // 是否是数组
            if (isValueNode(sourceNode)) {
                // 直接比较
                if (!compareValueNode(jsonPath, sourceNode, targetNode)) {
                    differenceList.add(new ValueDifference(DiffType.MODIFY, jsonPath, diffPath, jsonNode2String(sourceNode), jsonNode2String(targetNode)));
                }
            } else if (isArray(sourceNode)) {
                Iterator<?> sourceIterator = getArrayNodeIterator(sourceNode);
                Iterator<?> targetIterator = getArrayNodeIterator(targetNode);
                // 共同
                int index = 0;
                while (sourceIterator.hasNext() && targetIterator.hasNext()) {
                    Object sourceItemNode = sourceIterator.next();
                    Object targetItemNode = targetIterator.next();
                    diff(sourceItemNode, targetItemNode, differenceList, jsonPath, getArrayDiffPath(diffPath, index));
                    index++;
                }
                // source
                while (sourceIterator.hasNext()) {
                    Object sourceItemNode = sourceIterator.next();
                    differenceList.add(new ValueDifference(DiffType.DELETE, jsonPath, getArrayDiffPath(diffPath, index), jsonNode2String(sourceItemNode), null));
                    index++;
                }
                // target
                while (targetIterator.hasNext()) {
                    Object targetItemNode = targetIterator.next();
                    differenceList.add(new ValueDifference(DiffType.DELETE, jsonPath, getArrayDiffPath(diffPath, index), null, jsonNode2String(targetItemNode)));
                    index++;
                }
            } else {
                // 都是对象，以对象形式比较
                Iterator<Map.Entry<String, ?>> sourceIterator = (Iterator<Map.Entry<String, ?>>) getFieldsFromObject(sourceNode);
                // source
                while (sourceIterator.hasNext()) {
                    Map.Entry<String, ?> sourceItemEntry = sourceIterator.next();
                    String fieldName = sourceItemEntry.getKey();
                    Object jsonNode = sourceItemEntry.getValue();
                    diff(jsonNode, getFieldValueFromObject(targetNode, fieldName), differenceList, getJsonPath(jsonPath, fieldName), getObjectDiffPath(diffPath, fieldName));
                }
                // target
                Iterator<Map.Entry<String, ?>> targetIterator = (Iterator<Map.Entry<String, ?>>) getFieldsFromObject(targetNode);
                while (targetIterator.hasNext()) {
                    Map.Entry<String, ?> targetItemEntry = targetIterator.next();
                    String fieldName = targetItemEntry.getKey();
                    Object jsonNode = targetItemEntry.getValue();
                    if (getFieldValueFromObject(sourceNode, fieldName) != null) {
                        continue;
                    }
                    diff(getFieldValueFromObject(sourceNode, fieldName), jsonNode, differenceList, getJsonPath(jsonPath, fieldName), getObjectDiffPath(diffPath, fieldName));
                }
            }
        } else {
            differenceList.add(new ValueDifference(DiffType.TYPE_DIFF, jsonPath, diffPath, jsonNode2String(sourceNode), jsonNode2String(targetNode)));
        }
    }

    /**
     * 从jsonObject获取字段值
     * @param targetNode
     * @param fieldName
     * @return
     */
    protected abstract Object getFieldValueFromObject(Object targetNode, String fieldName);

    protected abstract Iterator<?> getFieldsFromObject(Object sourceNode);

    protected abstract Iterator<?> getArrayNodeIterator(Object sourceNode);

    protected abstract boolean isArray(Object sourceNode);

    protected abstract boolean isValueNode(Object sourceNode);

    protected abstract String jsonNode2String(Object targetNode);

    private String getJsonPath(String jsonPath, String fieldName) {
        return jsonPath + "." + fieldName;
    }

    private boolean compareValueNode(String jsonPath, Object sourceNode, Object targetNode) {
        Object sourceValue = getNodeOriginValue(sourceNode);
        Object targetValue = getNodeOriginValue(targetNode);
        EqualTo equalTo = equalMap.entrySet().stream()
                .filter(e -> jsonPath.startsWith(e.getKey()))
                .flatMap(e -> e.getValue().stream())
                .filter(c -> sourceValue != null && getComparatorType(c).isAssignableFrom(sourceValue.getClass()))
                .findFirst()
                .orElse(null);
        if (equalTo != null) {
            return equalTo.equalTo(sourceValue, targetValue);
        }
        return Objects.equals(sourceNode, targetNode);
    }

    protected Object getNodeOriginValue(Object sourceNode) {
        return sourceNode;
    }

    private String getObjectDiffPath(String parentJsonPath, String fieldName) {
        return parentJsonPath + "." + fieldName;
    }

    private String getArrayDiffPath(String parentJsonPath, int index) {
        return parentJsonPath + "[" + index + "]";
    }

    protected boolean isTypeMatch(Object sourceNode, Object targetNode) {
        return sourceNode.getClass() == targetNode.getClass();
    }

}
