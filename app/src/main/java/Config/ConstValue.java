package Config;

/**
 * Created by LENOVO on 4/20/2016.
 * Please Note following BASE_URL is an url of your backend link where you hosted backend.. you can give your ip or domain path here..
 * This is end point of API url.. through this url all api calls.
 *
 * Currency use in app..
 */
public class ConstValue {
    public  static String BASE_URL = "http://iclauncher.com/clinicapp";
    public static String CURRENCY = "Rs.";

    /** PAYPAL SETTINGS **/
    public static final Boolean enable_paypal = false;  /** value "true" if you wont to enable paypal payment **/
    public static final String paypal_target_url_prefix="iclauncher.com";

    public static String PREFS_LANGUAGE = "lang_pref";
    public static boolean ALLOW_ARABIC_LANG = false; /** value "true" if you wont ot enable choose arabic language button on account screen **/
}
