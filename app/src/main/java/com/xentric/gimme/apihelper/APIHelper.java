package com.xentric.gimme.apihelper;

/**
 * Created by kousik on 15/3/16.
 */
public class APIHelper {

    //Live URL
   // private static final String BASE_URL = "http://starservice.xentricwindows.com/StarService.svc/StarServiceRest/";
    private static final String BASE_URL = "http://gimmeservice.xentricwindows.com/StarService.svc/StarServiceRest/";
    //Demo URL
    //private static final String BASE_URL = "http://xensalesandservice.xentricwinserver.com/DemoService.svc/DemoServiceRest/";
    public static final String GET_USER_URL = BASE_URL + "GetUsers";
    public static final String GET_ALL_CUSTOMER_URL = BASE_URL + "GetCustomers";
    public static final String GET_SALES_INVOICE_BY_CUSTOMER = BASE_URL + "SalesInvoiceListByCustomer";
    public static final String GET_DUES_BY_CUSTOMER = BASE_URL + "GetDues";
    public static final String SAVE_COLLECTION_URL = BASE_URL + "SaveCollection";
    public static final String NO_COLLECTION_URL = BASE_URL + "ReasonForNoCollection";
    public static final String VISIT_URL = BASE_URL + "Visit";
    public static final String TRACKER_URL = "http://starservice.xentricwindows.com/StarService.svc/StarServiceRest/SaveTracker";
    //public static final String TRACKER_URL = "http://xensalesandservice.xentricwinserver.com/DemoService.svc/DemoServiceRest/SaveTracker"
}
