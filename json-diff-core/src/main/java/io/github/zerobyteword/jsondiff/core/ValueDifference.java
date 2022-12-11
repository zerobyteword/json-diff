package io.github.zerobyteword.jsondiff.core;

public class ValueDifference {
    private DiffType diffType;

    public DiffType getDiffType() {
        return diffType;
    }

    public void setDiffType(DiffType diffType) {
        this.diffType = diffType;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public String getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(String sourceValue) {
        this.sourceValue = sourceValue;
    }

    public String getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }
    private String jsonPath;
    private String diffPath;

    private String sourceValue;
    private String targetValue;

    public ValueDifference(DiffType diffType, String jsonPath, String diffPath, String sourceValue, String targetValue) {
        this.diffType = diffType;
        this.jsonPath = jsonPath;
        this.diffPath = diffPath;
        this.sourceValue = sourceValue;
        this.targetValue = targetValue;
    }

    @Override
    public String toString() {
        return "ValueDifference{" +
                "diffType=" + diffType +
                ", jsonPath='" + jsonPath + '\'' +
                ", diffPath='" + diffPath + '\'' +
                ", sourceValue='" + sourceValue + '\'' +
                ", targetValue='" + targetValue + '\'' +
                '}';
    }
}
