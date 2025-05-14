package com.sipa.boot.java8.tool.translate.strategy.youdao.form;

/**
 * @author caszhou
 * @date 2021/9/10
 */
public class YoudaoForm {
    private String q;

    private String from;

    private String to;

    private String appKey;

    private String salt;

    private String sign;

    private String signType = "v3";

    private String curtime;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getCurtime() {
        return curtime;
    }

    public void setCurtime(String curtime) {
        this.curtime = curtime;
    }

    public static final class YoudaoFormBuilder {
        private String q;

        private String from;

        private String to;

        private String appKey;

        private String salt;

        private String sign;

        private String signType = "v3";

        private String curtime;

        private YoudaoFormBuilder() {}

        public static YoudaoFormBuilder anYoudaoForm() {
            return new YoudaoFormBuilder();
        }

        public YoudaoFormBuilder withQ(String q) {
            this.q = q;
            return this;
        }

        public YoudaoFormBuilder withFrom(String from) {
            this.from = from;
            return this;
        }

        public YoudaoFormBuilder withTo(String to) {
            this.to = to;
            return this;
        }

        public YoudaoFormBuilder withAppKey(String appKey) {
            this.appKey = appKey;
            return this;
        }

        public YoudaoFormBuilder withSalt(String salt) {
            this.salt = salt;
            return this;
        }

        public YoudaoFormBuilder withSign(String sign) {
            this.sign = sign;
            return this;
        }

        public YoudaoFormBuilder withSignType(String signType) {
            this.signType = signType;
            return this;
        }

        public YoudaoFormBuilder withCurtime(String curtime) {
            this.curtime = curtime;
            return this;
        }

        public YoudaoForm build() {
            YoudaoForm youdaoForm = new YoudaoForm();
            youdaoForm.setQ(q);
            youdaoForm.setFrom(from);
            youdaoForm.setTo(to);
            youdaoForm.setAppKey(appKey);
            youdaoForm.setSalt(salt);
            youdaoForm.setSign(sign);
            youdaoForm.setSignType(signType);
            youdaoForm.setCurtime(curtime);
            return youdaoForm;
        }
    }
}
