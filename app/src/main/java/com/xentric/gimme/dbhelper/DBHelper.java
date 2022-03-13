package com.xentric.gimme.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xentric.gimme.beanmodelhelper.BankModel;
import com.xentric.gimme.beanmodelhelper.CustomerModel;
import com.xentric.gimme.beanmodelhelper.UserModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by goutam on 13/6/16.
 */
public class DBHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "collection.db";

    private static final String TABLE_USER = "user_table";
    private static final String TABLE_CUSTOMER = "customer_table";
    private static final String TABLE_BANK  = "back_table";
    //USER Table Fields Name
    private static final String KEY_USER_USERNAME = "UserName";
    private static final String KEY_USER_ID = "Id";
    private static final String KEY_USER_USEREMAIL = "UserEmailAddress";
    private static final String KEY_USER_PASSWORD  = "Password";
    private static final String KEY_USER_FNAME = "FirstName";
    private static final String KEY_USER_LNAME = "LastName";
    private static final String KEY_USER_PHONENUMBER = "PhoneNumber";
    //Customer Table Fields Name
    private static final String KEY_CUSTOMER_NAME = "Name";
    private static final String KEY_CUSTOMER_ID = "Id";
    private static final String KEY_CUSTOMER_REFCODE = "Code";
    private static final String KEY_CUSTOMER_ADDRESS = "Address";
    private static final String KEY_CUSTOMER_CONTACT = "ContactMobile";
    private static final String KEY_CUSTOMER_LID = "Lid";
    //Bank Table Fields Name
    private static final String KEY_BANK_ID = "Id";
    private static final String KEY_BANK_NAME = "BankName";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER = "CREATE TABLE " + TABLE_USER + "("
                + KEY_USER_ID + " INTEGER PRIMARY KEY NOT NULL,"
                + KEY_USER_USERNAME + " TEXT NOT NULL,"
                + KEY_USER_PASSWORD + " TEXT NOT NULL,"
                + KEY_USER_USEREMAIL + " TEXT NOT NULL,"
                + KEY_USER_FNAME + " TEXT NOT NULL,"
                + KEY_USER_LNAME + " TEXT NOT NULL,"
                + KEY_USER_PHONENUMBER + " TEXT NOT NULL"
                +")";

        String CREATE_CUSTOMER = "CREATE TABLE " + TABLE_CUSTOMER + "("
                + KEY_CUSTOMER_ID + " INTEGER PRIMARY KEY NOT NULL,"
                + KEY_CUSTOMER_NAME + " TEXT NOT NULL,"
                + KEY_CUSTOMER_REFCODE + " TEXT NOT NULL,"
                + KEY_CUSTOMER_ADDRESS + " TEXT NOT NULL,"
                + KEY_CUSTOMER_CONTACT + " TEXT NOT NULL,"
                + KEY_CUSTOMER_LID + " TEXT NOT NULL"
                +")";

        String CREATE_BANK = "CREATE TABLE " + TABLE_BANK + "("
                + KEY_BANK_ID + " INTEGER PRIMARY KEY NOT NULL,"
                + KEY_BANK_NAME + " TEXT NOT NULL"
                + ")";
        //Create Table
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_CUSTOMER);
        db.execSQL(CREATE_BANK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE_USER = "DROP TABLE IF EXISTS " + TABLE_USER ;
        String DROP_TABLE_CUSTOMER = "DROP TABLE IF EXISTS " + TABLE_CUSTOMER;
        String DROP_TABLE_BANK = "DROP TABLE IF EXISTS " + TABLE_BANK;
        db.execSQL(DROP_TABLE_USER);
        db.execSQL(DROP_TABLE_CUSTOMER);
        db.execSQL(DROP_TABLE_BANK);
        onCreate(db);
    }

    //Add all user
    public void addUser(UserModel user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID,user.getId());
        values.put(KEY_USER_FNAME,user.getFname());
        values.put(KEY_USER_LNAME,user.getLname());
        values.put(KEY_USER_USEREMAIL,user.getEmail());
        values.put(KEY_USER_USERNAME, user.getUsername());
        values.put(KEY_USER_PASSWORD, user.getPassword());
        values.put(KEY_USER_PHONENUMBER, user.getPhonenumber());

        //db.insert(TABLE_USER, null, values);
        db.insertWithOnConflict(TABLE_USER, null,
                values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    //Get A single user
    public UserModel getUser(String username,String password){
        SQLiteDatabase db = this.getWritableDatabase();
        UserModel user = null;
        Cursor cursor = null;
        String SELECTION_QUERY = "SELECT * FROM " + TABLE_USER
                + " WHERE "
                + KEY_USER_USERNAME +  "=" + "'" + username + "'"
                +" AND "
                +KEY_USER_PASSWORD + "=" + "'" + password + "'";
        try {
            cursor = db.rawQuery(SELECTION_QUERY,null);
            if (cursor!=null && cursor.moveToFirst()){
                user = new UserModel(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6));
            }
        }catch (Exception e){
            e.printStackTrace();

        }

        cursor.close();
        db.close();
        return  user;
    }

    ///////////////////////////////////////////////////////////
    //Add all Customer
    public void addCustomer(CustomerModel customer){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(KEY_CUSTOMER_ID,customer.getCustomerId());
        content.put(KEY_CUSTOMER_NAME,customer.getName());
        content.put(KEY_CUSTOMER_ADDRESS,customer.getAddress());
        content.put(KEY_CUSTOMER_CONTACT, customer.getContact());
        content.put(KEY_CUSTOMER_REFCODE, customer.getRefCode());
        content.put(KEY_CUSTOMER_LID,customer.getCustomerLid());
        //db.insert(TABLE_CUSTOMER,null,content);
        db.insertWithOnConflict(TABLE_CUSTOMER, null,
                content, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    //Get all Customer
    public List<CustomerModel> getAllCustomer(){
        SQLiteDatabase db = this.getWritableDatabase();
        List<CustomerModel> customerList = new ArrayList<CustomerModel>();
        String selectQuery = "SELECT * FROM " + TABLE_CUSTOMER;
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor!=null && cursor.moveToFirst()){
            do {
                CustomerModel customer = new CustomerModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5)
                );
                customerList.add(customer);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return  customerList;
    }

    //Get all Customer using name
    public ArrayList<CustomerModel> getAllCustomerUsingName(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<CustomerModel> customerList = new ArrayList<CustomerModel>();
        String selectQuery = "SELECT * FROM " + TABLE_CUSTOMER + " WHERE " + KEY_CUSTOMER_NAME +" = " + "'" + name + "'";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor!=null && cursor.moveToFirst()){
            do {
                CustomerModel customer = new CustomerModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5)
                );
                customerList.add(customer);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return  customerList;
    }

    //Get Customer Details from name
    public CustomerModel getCustomerDetails(String customerName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        CustomerModel customer = null;
        String selectQuery = "SELECT * FROM " + TABLE_CUSTOMER +
                " WHERE " + KEY_CUSTOMER_NAME + " = " + "'"+customerName+"'";
        cursor = db.rawQuery(selectQuery,null);
        if (cursor !=null && cursor.moveToNext()){
            customer = new CustomerModel(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5)
            );
        }
        cursor.close();
        db.close();
        return customer;
    }

    //Get Customer Details from id
    public CustomerModel getCustomerDetailsUsingId(String customerId){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        CustomerModel customer = null;
        String selectQuery = "SELECT * FROM " + TABLE_CUSTOMER +
                " WHERE " + KEY_CUSTOMER_ID + " = " + "'"+customerId+"'";
        cursor = db.rawQuery(selectQuery,null);
        if (cursor !=null && cursor.moveToNext()){
            customer = new CustomerModel(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5)
            );
        }
        cursor.close();
        db.close();
        return customer;
    }

    ////Bank Table Process
    //Add all user
    public void addBank(BankModel bank){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_BANK_ID,bank.getId());
        values.put(KEY_BANK_NAME,bank.getBankName());

        //db.insert(TABLE_USER, null, values);
        db.insertWithOnConflict(TABLE_BANK, null,
                values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    //Get all Banks
    public List<BankModel> getAllBank(){
        SQLiteDatabase db = this.getWritableDatabase();
        List<BankModel> bankList = new ArrayList<BankModel>();
        String selectQuery = "SELECT * FROM " + TABLE_BANK;
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor!=null && cursor.moveToFirst()){
            do {
                BankModel bank = new BankModel(
                        cursor.getString(0),
                        cursor.getString(1)
                );
                bankList.add(bank);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return  bankList;
    }
}
