package com.example.zhengyangchen.amnesia.bean;

import org.json.JSONObject;

import java.util.List;

/**
 * 百度语音语义解析的结果实例为该对象
 * Created by zhengyangchen on 2015/11/11.
 */
public class SemanticParsingObject {
    /**
     * 语音解析的原始结果json文本
     */
    private String originalResultJson;
    /**
     * 用户输入原始文本
     */
    private String rawText;
    /**
     * 经过分词，纠错，改写处理后的文
     */
    private String parsedText;
    /**
     * 意图表示数组的json文本
     */
    private String results;
    /**
     * 意图对象集合
     * 意图对象（object）：描述意图的数据（键值对的集合），也是实现意图所需要的参数。
     * 意图表示以 json 语言描述，json 语言中“对象”指的是键值对的集合。
     */
    private List<ResultObject> objects;

    /**
     * 内部类 用来装载多个意图对象
     */
    public class ResultObject {
        /**
         * 领域（domain）：同一类型的数据或资源，以及围绕数据或资源提供的服务称为一个领域。
         * 领域数据一般是结构化的表格数据，有一个主键（主属性）；
         * 领域一般以名词命名。如：列车：围绕车次数据的列车时刻查询和预订服务，
         * 主键是车次天气：围绕天气数据的查询服务，主键是日期电话：
         * 围绕联系人-号码数据的电话拨打、通话记录查询服务，主键是电话号码
         */
        private String domain;
        /**
         * 意图（intent）：代表用户对领域数据的操作，如查询、查询某一个属性的值、预订、拨打等。一般以动词命名。
         */
        private String intent;
        /**
         * 置信度（score）：意图表示的置信度，多个意图表示对象按置信度降序排序。
         */
        private double score;
        /**
         * 意图对象（object）：描述意图的数据（键值对的集合），也是实现意图所需要的参数。
         * 意图表示以 json 语言描述，json 语言中“对象”指的是键值对的集合。
         */
        private JSONObject object;

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getIntent() {
            return intent;
        }

        public void setIntent(String intent) {
            this.intent = intent;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public JSONObject getObject() {
            return object;
        }

        public void setObject(JSONObject object) {
            this.object = object;
        }
    }

    public ResultObject getResultObjectInstance() {
        return new ResultObject();
    }

    public String getOriginalResultJson() {
        return originalResultJson;
    }

    public void setOriginalResultJson(String originalResultJson) {
        this.originalResultJson = originalResultJson;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public String getParsedText() {
        return parsedText;
    }

    public void setParsedText(String parsedText) {
        this.parsedText = parsedText;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public List<ResultObject> getObjects() {
        return objects;
    }

    public void setObjects(List<ResultObject> objects) {
        this.objects = objects;
    }
}
