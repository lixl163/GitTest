package com.zsinfo.guoranhao.beans;

/**
 * @author cyk
 * @date 2017/4/13 09:50
 * @email choe0227@163.com
 * @desc
 * @modifier
 * @modify_time
 * @modify_remark
 */
public class WXBean {

    /**
     * {"statusCode":"100000",
     * "statusStr":"请求成功",
     * "data":{"sign":"0EF6F5A8A0AEB3E922136A9AE59CB38F","timestamp":"1492091438",
     * "noncestr":"uqhesrzaa5utqrcm5la3mgtt6cwf15uc","partnerid":"1262227201",
     * "prepayid":"wx20170413095304b88fe3f6790267482810","packageN":"Sign=WXPay",
     * "appid":"wx02afc2a1605d4d78"}}
     */

    private String statusCode;
    private String statusStr;
    private DataEntity  data;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public DataEntity getData() {
        return data;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public class DataEntity{
        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getPackageN() {
            return packageN;
        }

        public void setPackageN(String packageN) {
            this.packageN = packageN;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        private String sign;
        private String timestamp;
        private String noncestr;
        private String partnerid;
        private String prepayid;
        private String packageN;
        private String appid;
    }
}
