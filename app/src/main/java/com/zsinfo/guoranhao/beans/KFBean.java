package com.zsinfo.guoranhao.beans;

import java.io.Serializable;

/**
 * Created by lixl on 2018/7/19.
 *
 * 客服列表
 */
public class KFBean implements Serializable{
    /**
     * data : {"createTime":"2018-08-15 17:21:10","id":"5","imId":"","imNumber":"18315318515","imType":"","note":"","openFireName":"8515","openFirePass":"grh123","staffCode":"8515","staffName":"zhu","status":1,"websiteNode":"3301","websiteNodeName":""}
     * statusCode : 100000
     * statusStr : 请求成功
     */

    private DataBean data;
    private String statusCode;
    private String statusStr;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

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

    public static class DataBean {
        /**
         * createTime : 2018-08-15 17:21:10
         * id : 5
         * imId :
         * imNumber : 18315318515
         * imType :
         * note :
         * openFireName : 8515
         * openFirePass : grh123
         * staffCode : 8515
         * staffName : zhu
         * status : 1
         * websiteNode : 3301
         * websiteNodeName :
         */

        private String createTime;
        private String id;
        private String imId;
        private String imNumber;
        private String imType;
        private String note;
        private String openFireName;
        private String openFirePass;
        private String staffCode;
        private String staffName;
        private int status;
        private String websiteNode;
        private String websiteNodeName;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImId() {
            return imId;
        }

        public void setImId(String imId) {
            this.imId = imId;
        }

        public String getImNumber() {
            return imNumber;
        }

        public void setImNumber(String imNumber) {
            this.imNumber = imNumber;
        }

        public String getImType() {
            return imType;
        }

        public void setImType(String imType) {
            this.imType = imType;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getOpenFireName() {
            return openFireName;
        }

        public void setOpenFireName(String openFireName) {
            this.openFireName = openFireName;
        }

        public String getOpenFirePass() {
            return openFirePass;
        }

        public void setOpenFirePass(String openFirePass) {
            this.openFirePass = openFirePass;
        }

        public String getStaffCode() {
            return staffCode;
        }

        public void setStaffCode(String staffCode) {
            this.staffCode = staffCode;
        }

        public String getStaffName() {
            return staffName;
        }

        public void setStaffName(String staffName) {
            this.staffName = staffName;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getWebsiteNode() {
            return websiteNode;
        }

        public void setWebsiteNode(String websiteNode) {
            this.websiteNode = websiteNode;
        }

        public String getWebsiteNodeName() {
            return websiteNodeName;
        }

        public void setWebsiteNodeName(String websiteNodeName) {
            this.websiteNodeName = websiteNodeName;
        }
    }
}
