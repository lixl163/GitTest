package com.zsinfo.guoranhao.beans;

/**
 * @author cyk
 * @date 2017/3/1 17:02
 * @email choe0227@163.com
 * @desc
 * @modifier
 * @modify_time
 * @modify_remark
 */
public class LoginBean {


    /**
     * statusStr : 请求成功
     * data : {"cuserInfo":{"birthday":"1991-11-27","realNameApp":"","note":"","faceImg":"http://api.grhao.com/apiimg/faceimg/2601.jpg","city":"3301","wxOpenId":"oK75WuKj-WELhlnzTFEjVoGIohxk","openId":"","wbOpenId":"","faceImgApp":"http://family.grhao.com/static/face/20171/13/c2601034254.png","county":"330108","regMethod":0,"pwdQuestion":"我在哪里","petName":"wuhudafei","uid":"","password":"","lastActivateTime":{"date":5,"seconds":59,"hours":21,"month":5,"year":115,"timezoneOffset":240,"minutes":30,"time":1433554259000,"day":5},"regTimeApp":"","province":"33","id":"2601","faceImgPathApp":"/home/projects/guoranhao/go/src/guoranhao/static/face/20171/13/c2601034254.png","cuserId":0,"email":"1219419544@qq.com","qq":"","pwdAnswer":"1306","tokenId":"","secretKey":"","userGrade":"VIP","sex":1,"qqOpenId":"","mobile":"18315318515","sinaNum":"","successBuyCount":5,"createTimeApp":"","realName":"朱高飞","firmId":0,"regTime":"2015-06-05 19:30:59","idcard":"342623199211273630","status":1},"tokenId":"7eea3c11b8d72214d9e9c9cfdcd2bf65","secretKey":"sm2z77i6pgg61mc73wqj49oyfn6nviv","userMonthCard":{"note":"用户月卡信息","systemMoneyStr":"","totalMoney":0.07,"discount":1,"id":"2","user":2601,"systemMoney":0.07,"status":1,"maxMoney":0.01},"userAccountInfo":{"score":0,"note":"","systemMoneyStr":"","userAccountId":0,"grade":0,"id":"1","experience":0,"golds":"","user":2601,"systemMoney":0,"status":1}}
     * statusCode : 100000
     */
    private String statusStr;
    private DataEntity data;
    private String statusCode;

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public DataEntity getData() {
        return data;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public class DataEntity {
        /**
         * cuserInfo : {"birthday":"1991-11-27","realNameApp":"","note":"","faceImg":"http://api.grhao.com/apiimg/faceimg/2601.jpg","city":"3301","wxOpenId":"oK75WuKj-WELhlnzTFEjVoGIohxk","openId":"","wbOpenId":"","faceImgApp":"http://family.grhao.com/static/face/20171/13/c2601034254.png","county":"330108","regMethod":0,"pwdQuestion":"我在哪里","petName":"wuhudafei","uid":"","password":"","lastActivateTime":{"date":5,"seconds":59,"hours":21,"month":5,"year":115,"timezoneOffset":240,"minutes":30,"time":1433554259000,"day":5},"regTimeApp":"","province":"33","id":"2601","faceImgPathApp":"/home/projects/guoranhao/go/src/guoranhao/static/face/20171/13/c2601034254.png","cuserId":0,"email":"1219419544@qq.com","qq":"","pwdAnswer":"1306","tokenId":"","secretKey":"","userGrade":"VIP","sex":1,"qqOpenId":"","mobile":"18315318515","sinaNum":"","successBuyCount":5,"createTimeApp":"","realName":"朱高飞","firmId":0,"regTime":"2015-06-05 19:30:59","idcard":"342623199211273630","status":1}
         * tokenId : 7eea3c11b8d72214d9e9c9cfdcd2bf65
         * secretKey : sm2z77i6pgg61mc73wqj49oyfn6nviv
         * userMonthCard : {"note":"用户月卡信息","systemMoneyStr":"","totalMoney":0.07,"discount":1,"id":"2","user":2601,"systemMoney":0.07,"status":1,"maxMoney":0.01}
         * userAccountInfo : {"score":0,"note":"","systemMoneyStr":"","userAccountId":0,"grade":0,"id":"1","experience":0,"golds":"","user":2601,"systemMoney":0,"status":1}
         */
        private CuserInfoEntity cuserInfo;
        private String tokenId;
        private String secretKey;
        private UserMonthCardEntity userMonthCard;
        private UserAccountInfoEntity userAccountInfo;

        public void setCuserInfo(CuserInfoEntity cuserInfo) {
            this.cuserInfo = cuserInfo;
        }

        public void setTokenId(String tokenId) {
            this.tokenId = tokenId;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public void setUserMonthCard(UserMonthCardEntity userMonthCard) {
            this.userMonthCard = userMonthCard;
        }

        public void setUserAccountInfo(UserAccountInfoEntity userAccountInfo) {
            this.userAccountInfo = userAccountInfo;
        }

        public CuserInfoEntity getCuserInfo() {
            return cuserInfo;
        }

        public String getTokenId() {
            return tokenId;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public UserMonthCardEntity getUserMonthCard() {
            return userMonthCard;
        }

        public UserAccountInfoEntity getUserAccountInfo() {
            return userAccountInfo;
        }

        public class CuserInfoEntity {
            /**
             * birthday : 1991-11-27
             * realNameApp :
             * note :
             * faceImg : http://api.grhao.com/apiimg/faceimg/2601.jpg
             * city : 3301
             * wxOpenId : oK75WuKj-WELhlnzTFEjVoGIohxk
             * openId :
             * wbOpenId :
             * faceImgApp : http://family.grhao.com/static/face/20171/13/c2601034254.png
             * county : 330108
             * regMethod : 0
             * pwdQuestion : 我在哪里
             * petName : wuhudafei
             * uid :
             * password :
             * lastActivateTime : {"date":5,"seconds":59,"hours":21,"month":5,"year":115,"timezoneOffset":240,"minutes":30,"time":1433554259000,"day":5}
             * regTimeApp :
             * province : 33
             * id : 2601
             * faceImgPathApp : /home/projects/guoranhao/go/src/guoranhao/static/face/20171/13/c2601034254.png
             * cuserId : 0
             * email : 1219419544@qq.com
             * qq :
             * pwdAnswer : 1306
             * tokenId :
             * secretKey :
             * userGrade : VIP
             * sex : 1
             * qqOpenId :
             * mobile : 18315318515
             * sinaNum :
             * successBuyCount : 5
             * createTimeApp :
             * realName : 朱高飞
             * firmId : 0
             * regTime : 2015-06-05 19:30:59
             * idcard : 342623199211273630
             * status : 1
             */
            private String birthday;
            private String realNameApp;
            private String note;
            private String faceImg;
            private String city;
            private String wxOpenId;
            private String openId;
            private String wbOpenId;
            private String faceImgApp;
            private String county;
            private String regMethod;
            private String pwdQuestion;
            private String petName;
            private String uid;
            private String password;
            private LastActivateTimeEntity lastActivateTime;
            private String regTimeApp;
            private String province;
            private String id;
            private String faceImgPathApp;
            private String cuserId;
            private String email;
            private String qq;
            private String pwdAnswer;
            private String tokenId;
            private String secretKey;
            private String userGrade;
            private String sex;
            private String qqOpenId;
            private String mobile;
            private String sinaNum;
            private String successBuyCount;
            private String createTimeApp;
            private String realName;
            private String firmId;
            private String regTime;
            private String idcard;
            private String status;

            public void setBirthday(String birthday) {
                this.birthday = birthday;
            }

            public void setRealNameApp(String realNameApp) {
                this.realNameApp = realNameApp;
            }

            public void setNote(String note) {
                this.note = note;
            }

            public void setFaceImg(String faceImg) {
                this.faceImg = faceImg;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public void setWxOpenId(String wxOpenId) {
                this.wxOpenId = wxOpenId;
            }

            public void setOpenId(String openId) {
                this.openId = openId;
            }

            public void setWbOpenId(String wbOpenId) {
                this.wbOpenId = wbOpenId;
            }

            public void setFaceImgApp(String faceImgApp) {
                this.faceImgApp = faceImgApp;
            }

            public void setCounty(String county) {
                this.county = county;
            }

            public void setRegMethod(String regMethod) {
                this.regMethod = regMethod;
            }

            public void setPwdQuestion(String pwdQuestion) {
                this.pwdQuestion = pwdQuestion;
            }

            public void setPetName(String petName) {
                this.petName = petName;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public void setLastActivateTime(LastActivateTimeEntity lastActivateTime) {
                this.lastActivateTime = lastActivateTime;
            }

            public void setRegTimeApp(String regTimeApp) {
                this.regTimeApp = regTimeApp;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public void setId(String id) {
                this.id = id;
            }

            public void setFaceImgPathApp(String faceImgPathApp) {
                this.faceImgPathApp = faceImgPathApp;
            }

            public void setCuserId(String cuserId) {
                this.cuserId = cuserId;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public void setQq(String qq) {
                this.qq = qq;
            }

            public void setPwdAnswer(String pwdAnswer) {
                this.pwdAnswer = pwdAnswer;
            }

            public void setTokenId(String tokenId) {
                this.tokenId = tokenId;
            }

            public void setSecretKey(String secretKey) {
                this.secretKey = secretKey;
            }

            public void setUserGrade(String userGrade) {
                this.userGrade = userGrade;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public void setQqOpenId(String qqOpenId) {
                this.qqOpenId = qqOpenId;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public void setSinaNum(String sinaNum) {
                this.sinaNum = sinaNum;
            }

            public void setSuccessBuyCount(String successBuyCount) {
                this.successBuyCount = successBuyCount;
            }

            public void setCreateTimeApp(String createTimeApp) {
                this.createTimeApp = createTimeApp;
            }

            public void setRealName(String realName) {
                this.realName = realName;
            }

            public void setFirmId(String firmId) {
                this.firmId = firmId;
            }

            public void setRegTime(String regTime) {
                this.regTime = regTime;
            }

            public void setIdcard(String idcard) {
                this.idcard = idcard;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getBirthday() {
                return birthday;
            }

            public String getRealNameApp() {
                return realNameApp;
            }

            public String getNote() {
                return note;
            }

            public String getFaceImg() {
                return faceImg;
            }

            public String getCity() {
                return city;
            }

            public String getWxOpenId() {
                return wxOpenId;
            }

            public String getOpenId() {
                return openId;
            }

            public String getWbOpenId() {
                return wbOpenId;
            }

            public String getFaceImgApp() {
                return faceImgApp;
            }

            public String getCounty() {
                return county;
            }

            public String getRegMethod() {
                return regMethod;
            }

            public String getPwdQuestion() {
                return pwdQuestion;
            }

            public String getPetName() {
                return petName;
            }

            public String getUid() {
                return uid;
            }

            public String getPassword() {
                return password;
            }

            public LastActivateTimeEntity getLastActivateTime() {
                return lastActivateTime;
            }

            public String getRegTimeApp() {
                return regTimeApp;
            }

            public String getProvince() {
                return province;
            }

            public String getId() {
                return id;
            }

            public String getFaceImgPathApp() {
                return faceImgPathApp;
            }

            public String getCuserId() {
                return cuserId;
            }

            public String getEmail() {
                return email;
            }

            public String getQq() {
                return qq;
            }

            public String getPwdAnswer() {
                return pwdAnswer;
            }

            public String getTokenId() {
                return tokenId;
            }

            public String getSecretKey() {
                return secretKey;
            }

            public String getUserGrade() {
                return userGrade;
            }

            public String getSex() {
                return sex;
            }

            public String getQqOpenId() {
                return qqOpenId;
            }

            public String getMobile() {
                return mobile;
            }

            public String getSinaNum() {
                return sinaNum;
            }

            public String getSuccessBuyCount() {
                return successBuyCount;
            }

            public String getCreateTimeApp() {
                return createTimeApp;
            }

            public String getRealName() {
                return realName;
            }

            public String getFirmId() {
                return firmId;
            }

            public String getRegTime() {
                return regTime;
            }

            public String getIdcard() {
                return idcard;
            }

            public String getStatus() {
                return status;
            }

            public class LastActivateTimeEntity {
                /**
                 * date : 5
                 * seconds : 59
                 * hours : 21
                 * month : 5
                 * year : 115
                 * timezoneOffset : 240
                 * minutes : 30
                 * time : 1433554259000
                 * day : 5
                 */
                private String date;
                private String seconds;
                private String hours;
                private String month;
                private String year;
                private String timezoneOffset;
                private String minutes;
                private long time;
                private String day;

                public void setDate(String date) {
                    this.date = date;
                }

                public void setSeconds(String seconds) {
                    this.seconds = seconds;
                }

                public void setHours(String hours) {
                    this.hours = hours;
                }

                public void setMonth(String month) {
                    this.month = month;
                }

                public void setYear(String year) {
                    this.year = year;
                }

                public void setTimezoneOffset(String timezoneOffset) {
                    this.timezoneOffset = timezoneOffset;
                }

                public void setMinutes(String minutes) {
                    this.minutes = minutes;
                }

                public void setTime(long time) {
                    this.time = time;
                }

                public void setDay(String day) {
                    this.day = day;
                }

                public String getDate() {
                    return date;
                }

                public String getSeconds() {
                    return seconds;
                }

                public String getHours() {
                    return hours;
                }

                public String getMonth() {
                    return month;
                }

                public String getYear() {
                    return year;
                }

                public String getTimezoneOffset() {
                    return timezoneOffset;
                }

                public String getMinutes() {
                    return minutes;
                }

                public long getTime() {
                    return time;
                }

                public String getDay() {
                    return day;
                }
            }
        }

        public class UserMonthCardEntity {
            /**
             * note : 用户月卡信息
             * systemMoneyStr :
             * totalMoney : 0.07
             * discount : 1
             * id : 2
             * user : 2601
             * systemMoney : 0.07
             * status : 1
             * maxMoney : 0.01
             */
            private String note;
            private String systemMoneyStr;
            private String totalMoney;
            private String discount;
            private String id;
            private String user;
            private String systemMoney;
            private String status;
            private String maxMoney;

            public void setNote(String note) {
                this.note = note;
            }

            public void setSystemMoneyStr(String systemMoneyStr) {
                this.systemMoneyStr = systemMoneyStr;
            }

            public void setTotalMoney(String totalMoney) {
                this.totalMoney = totalMoney;
            }

            public void setDiscount(String discount) {
                this.discount = discount;
            }

            public void setId(String id) {
                this.id = id;
            }

            public void setUser(String user) {
                this.user = user;
            }

            public void setSystemMoney(String systemMoney) {
                this.systemMoney = systemMoney;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public void setMaxMoney(String maxMoney) {
                this.maxMoney = maxMoney;
            }

            public String getNote() {
                return note;
            }

            public String getSystemMoneyStr() {
                return systemMoneyStr;
            }

            public String getTotalMoney() {
                return totalMoney;
            }

            public String getDiscount() {
                return discount;
            }

            public String getId() {
                return id;
            }

            public String getUser() {
                return user;
            }

            public String getSystemMoney() {
                return systemMoney;
            }

            public String getStatus() {
                return status;
            }

            public String getMaxMoney() {
                return maxMoney;
            }
        }

        public class UserAccountInfoEntity {
            /**
             * score : 0
             * note :
             * systemMoneyStr :
             * userAccountId : 0
             * grade : 0
             * id : 1
             * experience : 0
             * golds :
             * user : 2601
             * systemMoney : 0
             * status : 1
             */
            private String score;
            private String note;
            private String systemMoneyStr;
            private String userAccountId;
            private String grade;
            private String id;
            private String experience;
            private String golds;
            private String user;
            private String systemMoney;
            private String status;

            public void setScore(String score) {
                this.score = score;
            }

            public void setNote(String note) {
                this.note = note;
            }

            public void setSystemMoneyStr(String systemMoneyStr) {
                this.systemMoneyStr = systemMoneyStr;
            }

            public void setUserAccountId(String userAccountId) {
                this.userAccountId = userAccountId;
            }

            public void setGrade(String grade) {
                this.grade = grade;
            }

            public void setId(String id) {
                this.id = id;
            }

            public void setExperience(String experience) {
                this.experience = experience;
            }

            public void setGolds(String golds) {
                this.golds = golds;
            }

            public void setUser(String user) {
                this.user = user;
            }

            public void setSystemMoney(String systemMoney) {
                this.systemMoney = systemMoney;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getScore() {
                return score;
            }

            public String getNote() {
                return note;
            }

            public String getSystemMoneyStr() {
                return systemMoneyStr;
            }

            public String getUserAccountId() {
                return userAccountId;
            }

            public String getGrade() {
                return grade;
            }

            public String getId() {
                return id;
            }

            public String getExperience() {
                return experience;
            }

            public String getGolds() {
                return golds;
            }

            public String getUser() {
                return user;
            }

            public String getSystemMoney() {
                return systemMoney;
            }

            public String getStatus() {
                return status;
            }
        }
    }
}
