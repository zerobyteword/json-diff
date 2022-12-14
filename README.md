## 📚简介
Json-diff 是一个json对比的工具类库。

json-dff-jackson 是基于jackson类库实现

json-dff-fastjson 是基于fastjson类库实现

-------------------------------------------------------------------------------

## 📚安装
maven
1. 基于fastjson实现
```xml
<dependency>
  <groupId>io.github.zerobyteword</groupId>
  <artifactId>json-diff-fastjson</artifactId>
  <version>1.0.0-release</version>
</dependency>
```
2. 基于jackson实现
```xml
<dependency>
    <groupId>io.github.zerobyteword</groupId>
    <artifactId>json-diff-jackson</artifactId>
    <version>1.0.0-release</version>
</dependency>
```

## 📚使用方式
0. 介绍 

jsonPath: 以$开头表示根节点，以“.”分割,如key=user,则jsonPath=$.user,多级节点$.user.name.xxx;
DiffType: 差异类型
```java
    ADD("新增"),
    DELETE("删除"),
    MODIFY("更改"),
    TYPE_DIFF("类型不一致");
```
1. 比较两个json差异
```java
JsonDiff jsonDiff = new JacksonDiff();
// JsonDiff jsonDiff = new FastjsonDiff();
String s1="{\"name\":\"zhangsan\",\"age\":4,\"score\":22.0,\"sex\":\"man\"}";
String s2="{\"name\":\"lsi\",\"age\":4,\"score\":22,\"size\":66}";
List<ValueDifference> differenceList = jsonDiff.diff(s1, s2);
```
2. 忽略一些差异或仅关注某些差异
```java
JsonDiff jsonDiff=new JacksonDiff();
jsonDiff.excludeDiffByJsonPath("$.name");// 忽略指定jsonpath的差异
jsonDiff.includeDiffByJsonPath("$.age"); //  仅关注一些路径的差异
jsonDiff.excludeDiffByType(DiffType.ADD,"$.name"); //忽略某种差异类型，可以指定jsonPath,也可以不指定
jsonDiff.includeDiffByType(DiffType.ADD,"$.name"); // 仅关注某种些差异类型，可以指定jsonPath,也可以不指定
jsonDiff.ignoreStringCase("$.name"); // 忽略字符串大小写
jsonDiff.ignoreNumberAccuracy("$.score"); // 忽略数字精度
String s1="{\"name\":\"zhangsan\",\"age\":4,\"score\":22.0,\"sex\":\"man\"}";
String s2="{\"name\":\"lsi\",\"age\":4,\"score\":22,\"size\":66}";
List<ValueDifference> differenceList=jsonDiff.diff(s1,s2);
```
同一差异即符合exclude条件又满足include条件，以exclude优先