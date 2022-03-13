package com.xentric.gimme.expandablelistdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by goutam on 11/4/16.
 */
public class ExpandableListData {
    public static HashMap<String,List<String>> getData(){
        HashMap<String,List<String>> expandableListDetails = new HashMap<String, List<String>>();
        ArrayList<String> menuHeaderList = new ArrayList<String>();
        menuHeaderList.add("Todays Collection Option");
        menuHeaderList.add("Visited Clients Option");
        menuHeaderList.add("Urgent Payment Collection Required Option");
        menuHeaderList.add("Due List Of Clients Option");
        menuHeaderList.add("Monthly Billing Of Clients Option");
        menuHeaderList.add("Collection Details Option");
        menuHeaderList.add("Handed Over The Collections Option");
        menuHeaderList.add("Logout Option");

        List<String> todays_collection = new ArrayList<String>();


        List<String> visited_client_option = new ArrayList<String>();
        visited_client_option.add("Visited Clients");

        List<String> urgent_payment = new ArrayList<String>();

        List<String> due_list = new ArrayList<String>();

        List<String> monthly_bill = new ArrayList<String>();

        List<String> collection_details = new ArrayList<String>();

        List<String> handover_collection = new ArrayList<String>();

        List<String> logout = new ArrayList<String>();
        logout.add("Logout");

        expandableListDetails.put(menuHeaderList.get(0),todays_collection);
        expandableListDetails.put(menuHeaderList.get(1),visited_client_option);
        expandableListDetails.put(menuHeaderList.get(2),urgent_payment);
        expandableListDetails.put(menuHeaderList.get(3),due_list);
        expandableListDetails.put(menuHeaderList.get(4),monthly_bill);
        expandableListDetails.put(menuHeaderList.get(5),collection_details);
        expandableListDetails.put(menuHeaderList.get(6),handover_collection);
        expandableListDetails.put(menuHeaderList.get(7),logout);

        return expandableListDetails;
    }
}
