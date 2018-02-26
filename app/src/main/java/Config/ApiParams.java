package Config;

/**
 * Created by subhashsanghani on 10/18/16.
 * All end points for api is given here....
 *
 * Please find api code in  application/controllers/api.php  file here.. all function releated end points is given..
 * /index.php/api/login   mean  public function login() is for login.
 */
public class ApiParams {
    public static String PARM_RESPONCE = "responce";
    public static String PARM_DATA = "data";
    public static String PARM_ERROR = "error";

    public  static  String LOGIN_URL = "/index.php/api/login";
    public  static  String REGISTER_URL = "/index.php/api/signup";
    public  static  String BOOKAPPOINTMENT_URL = "/index.php/api/add_appointment";
    public static String CHANGE_PASSWORD_URL = "/index.php/api/change_password";
    public static String FORGOT_PASSWORD_URL = "/index.php/api/forgot_password";
    public static String USERDATA_URL = "/index.php/api/get_userdata";
    public static String UPDATEPROFILE_URL = "/index.php/api/update_profile";
    public static String MYAPPOINTMENTS_URL = "/index.php/api/my_appointments";
    public static String CANCELAPPOINTMENTS_URL = "/index.php/api/cancel_appointment";

    public static String TIMESLOT_URL = "/index.php/api/get_time_slot";
    public static String SERVICES_URL = "/index.php/api/get_services";
    public static String CLINC_INFO_URL = "/index.php/api/get_clinicdetails";
    public static String PHOTOS_URL = "/index.php/api/get_photos";
    public static String REVIEWS_URL = "/index.php/api/get_reviews";
    public static String REVIEWS_ADD_URL = "/index.php/api/add_business_review";
    public static String GET_TIPS_URL = "/index.php/api/get_tips";
    public static String CATEGORY_TIPS_URL = "/index.php/api/get_tips_categories";
    public static String DETAILED_TIPS_URL = "/index.php/api/get_tips_details";
    public static String LIKE_TIPS_URL = "/index.php/api/add_tips_like";
    public static String TIPS_COMMENTS_URL = "/index.php/api/get_tips_reviews";
    public static String TIPS_ADDCOMMENT_URL = "/index.php/api/add_tips_review";
    public static String REGISTER_FCM_URL = "/index.php/api/register_fcm";

    public static String GET_CHAT_URL = "/index.php/api/get_user_chats";
    public static String SEND_CHAT_FCM_URL = "/index.php/api/send_user_chat";

    public static String PAYMENT_URL = "/index.php/payorder/paypal";
    public  static  String BOOKAPPOINTMENT_TEMP_URL = "/index.php/api/add_appointment_temp";

    public  static  String PREF_NAME = "clinicapp.pref";
    public static String PREF_CATEGORY = "pref_category";
    public static  String COMMON_KEY = "user_id";
    public static  String USER_FULLNAME = "user_fullname";
    public static  String USER_EMAIL = "user_email";
    public static  String USER_PHONE = "user_phone";
    public static String PRICE_CART = "price_cart";
    public static String PREF_ERROR = "error_stack";
    public static String USER_DATA = "user_data";
}
