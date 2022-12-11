package io.github.zerobyteword.jsondiff.core;

public enum DiffType {
    ADD("新增"),
    DELETE("删除"),
    MODIFY("更改"),
    TYPE_DIFF("类型不一致");

    private String desc;

    DiffType(String desc) {
        this.desc = desc;
    }
}
