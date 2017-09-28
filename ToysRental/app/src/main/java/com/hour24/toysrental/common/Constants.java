package com.hour24.toysrental.common;

/**
 * Created by 장세진 on 2016-08-03.
 */
public class Constants {
    // 서버통신
    public static String HOST_URL = "http://yappdb.cafe24.com";
    public static String HOST_SERVICE = "/skill/routes/service";
    public static String HOST_VIEW = "/skill/views/jsp";
    public static String HOST_DAO = "/skill/routes/dao";
    public static String HOST_DAO_TOY = "/toy/routes/dao";

    // screen width height
    public static int SCREEN_WIDTH = 0;
    public static int SCREEN_HEIGHT = 0;

    public static String MEMBER_SEQ = null;
    public static String AUTH_CD = null;

    public class CODE {

        public static final String SERVICE_CD = "90002";

        public static final int REQUEST_LOGIN = 10001;
        public static final int REQUEST_LOGOUT = 10002;
        public static final int REQUEST_WRITE = 10003;
        public static final int REQUEST_MODIFY = 10004;
        public static final int REQUEST_GALLERY = 10005;
        public static final int REQUEST_SEARCH = 10006;

        public static final int REQUEST_PERMISSION_STORAGE = 10001;

        public static final int LOGIN_GOOGLE = 10001;
        public static final int LOGIN_FACEBOOOK = 10002;
        public static final int LOGIN_NAVER = 10003;

    }
}
