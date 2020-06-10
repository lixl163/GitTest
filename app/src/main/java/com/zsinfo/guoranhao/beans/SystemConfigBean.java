package com.zsinfo.guoranhao.beans;

/**
 * @author cyk
 * @date 2017/3/13 15:56
 * @email choe0227@163.com
 * @desc
 * @modifier
 * @modify_time
 * @modify_remark
 */
public class SystemConfigBean {


    /**
     * data : {"register_gift_score":"1000","recharge_discount":"0.9","vip_attain_money":"200","alipay_key":"bnjpn1n1iepc6aintt52f7cauwhelhks","system_kill_account_3":"20000","system_kill_account_4":"30000","system_kill_account_1":"69","score_goods_upload_path":"img_score_goods/","system_kill_account_2":"99","ads_upload_path":"img_ads/","about_us":"果然好","tuan_item_upload_path":"img_tuan_item/","default_face_img":"123456789.jpg","index_story_path":"img_story/","share_app_strs":"选择果然好，您吃的安心@所有水果经过严格检测、分拣及营养搭配；利用互联网手段，搭建起消费者心目中专业、放心的水果消费平台。","shop_logo_path":"firm_logo/","second_kill_upload_path":"img_second_kill/","alipay_rsa_alipay_public_new":"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB","goods_upload_path":"img_goods/","app_upload_path":"down_file/","alipay_partner":"2088011977659919","image_server_http":"http://ximg.grhao.com","app_tmp_path":"tmp_file/","feedbackQQ":"2472261970","system_current_time":"2017-03-13 16:52:42","share_goods_strs":"亲爱的,我发现这件宝贝超好的[链接http://www.grhao.com],你也赶快去下载手机果然好吧~","alipay_rsa_private":"MIICdAIBADANBgkqhkiG9w0BAQEFAASCAl4wggJaAgEAAoGBAMYIEa4OiY8n+FIyKk7k5yWtpzejLeOtts7k4Iq69+gf01kd87xaCp3xGvrTk+W1bqOQRK2hqWyjg/wLPyHUnSSnyp5GEk+l85wGjlfnGgikVlolFFHAfWKjN4qGTmOIyX8yKlP2MNedhcY2S6ZApGZWiQObgAGA58nGeWhHVQT1AgMBAAECgYAFVVBMlL2lqljotcsn5shG3n9jYzSoLhR/S/C0K8bjbH//pWvcBbzFdinY2XvhMtqw8wC2gGUUtO6oChGAMr6soulUvhmYxkusWcnwxqhPRuSAJrtCLvktBiEhkIRc/M9Ea24u69hG1/Lt2MgJs5ozcuQMJmHaa+HCjqhL+/shAQJBAOm3Esc6mU6zokBFj9pC9KKPQ/7ZqZy3a5zSaFAXr+TW8LN1NHXUcDz7GFXZ6lXfZGabraFIMfxwjPXX483Tr7UCQQDY6ffHmtAHaV006NORG0H0wZGL/VDvgYFZ6sMdbAERauicNW9FTMjQHMRBWXEgmC6Y1GH0IDJ5j6+gyb8IEMhBAkBi7NbJ9YfGxEo9IjSNkiGyXSnOyZr9drXaH0WAAUa2ejRQAaA+77jCVxYp4J9L0c7Rj7uitMhTreA8fzExghSxAj8x/bodRpCrJJ1WlFIuHxoUTog8uyTsDezBzFkrbrjQStNRLP93+TpOvFJ+vR5VZL1Ye5oXqVP4sCTJ6bUdJsECQC4rPyDLhjOh4MhrHMWpus9pspu0mf+d6eVmfhQWkBH6djc33UzaA52owCr/UagRARCrcajRSwzyWuLtOz+ertA=","feedback_method":"400-8000-8888","alipay_rsa_alipay_public":"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDEjHMeDLkRnb2D3KKa3qBB22lYDJiTYSndbN+vaqQgVDBqbHJw3inn/9vUpmfaL3RFwZr2g5OpFTC/+QTxPGJ+yr358FQBKau+TuQK2uxRyp4yMiwXOl3VI4/LhNM0c3EzFSjmWKcW1NhQiXSgOA0QJYB12p9Rnt5nZDx2CFIwcQIDAQAB","cuser_upload_path":"img_cuser/","system_alipay_account":"hzzs_info@163.com","image_upload_root_path":"/img_root/","speciall_item_upload_path":"img_speciall_item/","ios_download_uri":"https://itunes.apple.com/us/app/i-yue-ding/id798557890?ls=1&mt=8"}
     * statusStr : 请求成功
     * statusCode : 100000
     */
    private DataEntity data;
    private String statusStr;
    private String statusCode;

    public void setData(DataEntity data) {
        this.data = data;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public DataEntity getData() {
        return data;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public class DataEntity {
        /**
         * register_gift_score : 1000
         * recharge_discount : 0.9
         * vip_attain_money : 200
         * "ali_app_id":"2018042802605296",
         * alipay_key : bnjpn1n1iepc6aintt52f7cauwhelhks
         * system_kill_account_3 : 20000
         * system_kill_account_4 : 30000
         * system_kill_account_1 : 69
         * score_goods_upload_path : img_score_goods/
         * system_kill_account_2 : 99
         * ads_upload_path : img_ads/
         * about_us : 果然好
         * tuan_item_upload_path : img_tuan_item/
         * default_face_img : 123456789.jpg
         * index_story_path : img_story/
         * share_app_strs : 选择果然好，您吃的安心@所有水果经过严格检测、分拣及营养搭配；利用互联网手段，搭建起消费者心目中专业、放心的水果消费平台。
         * shop_logo_path : firm_logo/
         * second_kill_upload_path : img_second_kill/
         * alipay_rsa_alipay_public_new : MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB
         * goods_upload_path : img_goods/
         * app_upload_path : down_file/
         * alipay_partner : 2088011977659919
         * image_server_http : http://ximg.grhao.com
         * app_tmp_path : tmp_file/
         * feedbackQQ : 2472261970
         * system_current_time : 2017-03-13 16:52:42
         * share_goods_strs : 亲爱的,我发现这件宝贝超好的[链接http://www.grhao.com],你也赶快去下载手机果然好吧~
         * alipay_rsa_private : MIICdAIBADANBgkqhkiG9w0BAQEFAASCAl4wggJaAgEAAoGBAMYIEa4OiY8n+FIyKk7k5yWtpzejLeOtts7k4Iq69+gf01kd87xaCp3xGvrTk+W1bqOQRK2hqWyjg/wLPyHUnSSnyp5GEk+l85wGjlfnGgikVlolFFHAfWKjN4qGTmOIyX8yKlP2MNedhcY2S6ZApGZWiQObgAGA58nGeWhHVQT1AgMBAAECgYAFVVBMlL2lqljotcsn5shG3n9jYzSoLhR/S/C0K8bjbH//pWvcBbzFdinY2XvhMtqw8wC2gGUUtO6oChGAMr6soulUvhmYxkusWcnwxqhPRuSAJrtCLvktBiEhkIRc/M9Ea24u69hG1/Lt2MgJs5ozcuQMJmHaa+HCjqhL+/shAQJBAOm3Esc6mU6zokBFj9pC9KKPQ/7ZqZy3a5zSaFAXr+TW8LN1NHXUcDz7GFXZ6lXfZGabraFIMfxwjPXX483Tr7UCQQDY6ffHmtAHaV006NORG0H0wZGL/VDvgYFZ6sMdbAERauicNW9FTMjQHMRBWXEgmC6Y1GH0IDJ5j6+gyb8IEMhBAkBi7NbJ9YfGxEo9IjSNkiGyXSnOyZr9drXaH0WAAUa2ejRQAaA+77jCVxYp4J9L0c7Rj7uitMhTreA8fzExghSxAj8x/bodRpCrJJ1WlFIuHxoUTog8uyTsDezBzFkrbrjQStNRLP93+TpOvFJ+vR5VZL1Ye5oXqVP4sCTJ6bUdJsECQC4rPyDLhjOh4MhrHMWpus9pspu0mf+d6eVmfhQWkBH6djc33UzaA52owCr/UagRARCrcajRSwzyWuLtOz+ertA=
         * feedback_method : 400-8000-8888
         * alipay_rsa_alipay_public : MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDEjHMeDLkRnb2D3KKa3qBB22lYDJiTYSndbN+vaqQgVDBqbHJw3inn/9vUpmfaL3RFwZr2g5OpFTC/+QTxPGJ+yr358FQBKau+TuQK2uxRyp4yMiwXOl3VI4/LhNM0c3EzFSjmWKcW1NhQiXSgOA0QJYB12p9Rnt5nZDx2CFIwcQIDAQAB
         * cuser_upload_path : img_cuser/
         * system_alipay_account : hzzs_info@163.com
         * image_upload_root_path : /img_root/
         * speciall_item_upload_path : img_speciall_item/
         * ios_download_uri : https://itunes.apple.com/us/app/i-yue-ding/id798557890?ls=1&mt=8
         */
        private String register_gift_score;
        private String recharge_discount;
        private String vip_attain_money;
        private String alipay_key;
        private String ali_app_id;
        private String system_kill_account_3;
        private String system_kill_account_4;
        private String system_kill_account_1;
        private String score_goods_upload_path;
        private String system_kill_account_2;
        private String ads_upload_path;
        private String about_us;
        private String tuan_item_upload_path;
        private String default_face_img;
        private String index_story_path;
        private String share_app_strs;
        private String shop_logo_path;
        private String second_kill_upload_path;
        private String alipay_rsa_alipay_public_new;
        private String goods_upload_path;
        private String app_upload_path;
        private String alipay_partner;
        private String image_server_http;
        private String app_tmp_path;
        private String feedbackQQ;
        private String system_current_time;
        private String share_goods_strs;
        private String alipay_rsa_private;
        private String feedback_method;
        private String alipay_rsa_alipay_public;
        private String cuser_upload_path;
        private String system_alipay_account;
        private String image_upload_root_path;
        private String speciall_item_upload_path;
        private String ios_download_uri;
        private String android_download_uri;

        public String getAli_app_id() {
            return ali_app_id;
        }

        public void setAli_app_id(String ali_app_id) {
            this.ali_app_id = ali_app_id;
        }

        public String getAndroid_download_uri() {
            return android_download_uri;
        }

        public void setAndroid_download_uri(String android_download_uri) {
            this.android_download_uri = android_download_uri;
        }

        public void setRegister_gift_score(String register_gift_score) {
            this.register_gift_score = register_gift_score;
        }

        public void setRecharge_discount(String recharge_discount) {
            this.recharge_discount = recharge_discount;
        }

        public void setVip_attain_money(String vip_attain_money) {
            this.vip_attain_money = vip_attain_money;
        }

        public void setAlipay_key(String alipay_key) {
            this.alipay_key = alipay_key;
        }

        public void setSystem_kill_account_3(String system_kill_account_3) {
            this.system_kill_account_3 = system_kill_account_3;
        }

        public void setSystem_kill_account_4(String system_kill_account_4) {
            this.system_kill_account_4 = system_kill_account_4;
        }

        public void setSystem_kill_account_1(String system_kill_account_1) {
            this.system_kill_account_1 = system_kill_account_1;
        }

        public void setScore_goods_upload_path(String score_goods_upload_path) {
            this.score_goods_upload_path = score_goods_upload_path;
        }

        public void setSystem_kill_account_2(String system_kill_account_2) {
            this.system_kill_account_2 = system_kill_account_2;
        }

        public void setAds_upload_path(String ads_upload_path) {
            this.ads_upload_path = ads_upload_path;
        }

        public void setAbout_us(String about_us) {
            this.about_us = about_us;
        }

        public void setTuan_item_upload_path(String tuan_item_upload_path) {
            this.tuan_item_upload_path = tuan_item_upload_path;
        }

        public void setDefault_face_img(String default_face_img) {
            this.default_face_img = default_face_img;
        }

        public void setIndex_story_path(String index_story_path) {
            this.index_story_path = index_story_path;
        }

        public void setShare_app_strs(String share_app_strs) {
            this.share_app_strs = share_app_strs;
        }

        public void setShop_logo_path(String shop_logo_path) {
            this.shop_logo_path = shop_logo_path;
        }

        public void setSecond_kill_upload_path(String second_kill_upload_path) {
            this.second_kill_upload_path = second_kill_upload_path;
        }

        public void setAlipay_rsa_alipay_public_new(String alipay_rsa_alipay_public_new) {
            this.alipay_rsa_alipay_public_new = alipay_rsa_alipay_public_new;
        }

        public void setGoods_upload_path(String goods_upload_path) {
            this.goods_upload_path = goods_upload_path;
        }

        public void setApp_upload_path(String app_upload_path) {
            this.app_upload_path = app_upload_path;
        }

        public void setAlipay_partner(String alipay_partner) {
            this.alipay_partner = alipay_partner;
        }

        public void setImage_server_http(String image_server_http) {
            this.image_server_http = image_server_http;
        }

        public void setApp_tmp_path(String app_tmp_path) {
            this.app_tmp_path = app_tmp_path;
        }

        public void setFeedbackQQ(String feedbackQQ) {
            this.feedbackQQ = feedbackQQ;
        }

        public void setSystem_current_time(String system_current_time) {
            this.system_current_time = system_current_time;
        }

        public void setShare_goods_strs(String share_goods_strs) {
            this.share_goods_strs = share_goods_strs;
        }

        public void setAlipay_rsa_private(String alipay_rsa_private) {
            this.alipay_rsa_private = alipay_rsa_private;
        }

        public void setFeedback_method(String feedback_method) {
            this.feedback_method = feedback_method;
        }

        public void setAlipay_rsa_alipay_public(String alipay_rsa_alipay_public) {
            this.alipay_rsa_alipay_public = alipay_rsa_alipay_public;
        }

        public void setCuser_upload_path(String cuser_upload_path) {
            this.cuser_upload_path = cuser_upload_path;
        }

        public void setSystem_alipay_account(String system_alipay_account) {
            this.system_alipay_account = system_alipay_account;
        }

        public void setImage_upload_root_path(String image_upload_root_path) {
            this.image_upload_root_path = image_upload_root_path;
        }

        public void setSpeciall_item_upload_path(String speciall_item_upload_path) {
            this.speciall_item_upload_path = speciall_item_upload_path;
        }

        public void setIos_download_uri(String ios_download_uri) {
            this.ios_download_uri = ios_download_uri;
        }

        public String getRegister_gift_score() {
            return register_gift_score;
        }

        public String getRecharge_discount() {
            return recharge_discount;
        }

        public String getVip_attain_money() {
            return vip_attain_money;
        }

        public String getAlipay_key() {
            return alipay_key;
        }

        public String getSystem_kill_account_3() {
            return system_kill_account_3;
        }

        public String getSystem_kill_account_4() {
            return system_kill_account_4;
        }

        public String getSystem_kill_account_1() {
            return system_kill_account_1;
        }

        public String getScore_goods_upload_path() {
            return score_goods_upload_path;
        }

        public String getSystem_kill_account_2() {
            return system_kill_account_2;
        }

        public String getAds_upload_path() {
            return ads_upload_path;
        }

        public String getAbout_us() {
            return about_us;
        }

        public String getTuan_item_upload_path() {
            return tuan_item_upload_path;
        }

        public String getDefault_face_img() {
            return default_face_img;
        }

        public String getIndex_story_path() {
            return index_story_path;
        }

        public String getShare_app_strs() {
            return share_app_strs;
        }

        public String getShop_logo_path() {
            return shop_logo_path;
        }

        public String getSecond_kill_upload_path() {
            return second_kill_upload_path;
        }

        public String getAlipay_rsa_alipay_public_new() {
            return alipay_rsa_alipay_public_new;
        }

        public String getGoods_upload_path() {
            return goods_upload_path;
        }

        public String getApp_upload_path() {
            return app_upload_path;
        }

        public String getAlipay_partner() {
            return alipay_partner;
        }

        public String getImage_server_http() {
            return image_server_http;
        }

        public String getApp_tmp_path() {
            return app_tmp_path;
        }

        public String getFeedbackQQ() {
            return feedbackQQ;
        }

        public String getSystem_current_time() {
            return system_current_time;
        }

        public String getShare_goods_strs() {
            return share_goods_strs;
        }

        public String getAlipay_rsa_private() {
            return alipay_rsa_private;
        }

        public String getFeedback_method() {
            return feedback_method;
        }

        public String getAlipay_rsa_alipay_public() {
            return alipay_rsa_alipay_public;
        }

        public String getCuser_upload_path() {
            return cuser_upload_path;
        }

        public String getSystem_alipay_account() {
            return system_alipay_account;
        }

        public String getImage_upload_root_path() {
            return image_upload_root_path;
        }

        public String getSpeciall_item_upload_path() {
            return speciall_item_upload_path;
        }

        public String getIos_download_uri() {
            return ios_download_uri;
        }
    }
}
