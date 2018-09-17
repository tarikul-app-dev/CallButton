package planet.it.limited.callbutton.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import planet.it.limited.callbutton.util.BlackListModel;
import planet.it.limited.callbutton.util.UserInfoModel;


public class DataHelper {
    // db version
    private static final int DATABASE_VERSION = 1;
    private static final String DB_NAME = "call_record";
    private static final String DB_TABLE_CALL_BUTTON = "table_call_button";
    private static final String DB_TABLE_BLACKLIST = "table_black_list";
    private static final String DB_TABLE_SAVE_AUDIO = "table_save_audio";


    private DataHelper.DBHelper dbhelper;
    private final Context context;
    private SQLiteDatabase database;

    // insert row
    public static final String KEY_ROWID = "id";
    public static final String KEY_MOBILE_NUMBER = "mobile_number";
    public static final String KEY_DATE = "date";
    public static final String KEY_VOICE_FILE_PATH = "voice_file_path";
    public static final String KEY_VOICE_DURATION = "voice_duration";
    public static final String KEY_START_TIME = "voice_start_time";
    public static final String KEY_END_TIME = "voice_end_time";


    private static class DBHelper extends SQLiteOpenHelper {

        @SuppressLint("NewApi")
        public DBHelper(Context context) {
            super(context, DB_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            // create table to store msgs
            db.execSQL(" CREATE TABLE " + DB_TABLE_CALL_BUTTON + " ("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_MOBILE_NUMBER + " TEXT, "
                    + KEY_DATE + " TEXT, "
                    + KEY_START_TIME + " INTEGER, "
                    + KEY_END_TIME + " INTEGER, "
                    + KEY_VOICE_FILE_PATH + " TEXT );");

            db.execSQL(" CREATE TABLE " + DB_TABLE_SAVE_AUDIO + " ("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_MOBILE_NUMBER + " TEXT, "
                    + KEY_DATE + " TEXT, "
                    + KEY_VOICE_DURATION + " TEXT, "
                    + KEY_VOICE_FILE_PATH + " TEXT );");



            db.execSQL(" CREATE TABLE " + DB_TABLE_BLACKLIST + " ("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_MOBILE_NUMBER + " TEXT );");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_CALL_BUTTON);
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_BLACKLIST);
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_SAVE_AUDIO);

            onCreate(db);
        }

    }
    // constructor
    public DataHelper(Context c) {
        context = c;
    }

    // open db
    public DataHelper open() {
        dbhelper = new  DBHelper(context);
        database = dbhelper.getWritableDatabase();
        return this;
    }

    // close db
    public void close() {
        dbhelper.close();
    }

    public void insertUserProfile(String number, String date, String startTime ,String endTime, String voicePath){
        ContentValues cv = new ContentValues();
        cv.put(KEY_MOBILE_NUMBER, number);
        cv.put(KEY_DATE, date);
        cv.put(KEY_START_TIME, startTime);
        cv.put(KEY_END_TIME, endTime);
        cv.put(KEY_VOICE_FILE_PATH, voicePath);
        long dbInsert = database.insert(DB_TABLE_CALL_BUTTON, null, cv);

        if(dbInsert != -1) {
            // Toast.makeText(context, "Contacts Save Success" + dbInsert, Toast.LENGTH_SHORT).show();
        }else{
            //Toast.makeText(context, "Something wrong", Toast.LENGTH_SHORT).show();
        }

    }

    public void saveAudioFile(String number,String date ,String voiceDuration ,String voicePath){
        ContentValues cv = new ContentValues();
        cv.put(KEY_MOBILE_NUMBER, number);
        cv.put(KEY_DATE, date);
        cv.put(KEY_VOICE_DURATION, voiceDuration);
        cv.put(KEY_VOICE_FILE_PATH, voicePath);
        long dbInsert = database.insert(DB_TABLE_SAVE_AUDIO, null, cv);

        if(dbInsert != -1) {
            Toast.makeText(context, "Successfully Save to Storage" + dbInsert, Toast.LENGTH_SHORT).show();
        }else{
            //Toast.makeText(context, "Something wrong", Toast.LENGTH_SHORT).show();
        }

    }

    public void addBlackList(String number){
        ContentValues cv = new ContentValues();
        cv.put(KEY_MOBILE_NUMBER, number);
        long dbInsert = database.insert(DB_TABLE_BLACKLIST, null, cv);

        if(dbInsert != -1) {
             Toast.makeText(context, "Successfully add to blacklist" + dbInsert, Toast.LENGTH_SHORT).show();
        }else{
            //Toast.makeText(context, "Something wrong", Toast.LENGTH_SHORT).show();
        }

    }


    public ArrayList getAllBlackList() {
        ArrayList<BlackListModel> blackList = new ArrayList<>();
        String select_query = "SELECT  * FROM " + DB_TABLE_BLACKLIST ;
        Cursor cursor = database.rawQuery(select_query,null);
        int iUserNumber = cursor.getColumnIndex(KEY_MOBILE_NUMBER);

        for (cursor.moveToLast(); ! cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            //    for (cursor.moveToFirst(); ! cursor.isAfterLast(); cursor.moveToNext()) {

            BlackListModel blackListModel = new BlackListModel();
            blackListModel.setMobNum(cursor.getString(iUserNumber));

            blackList.add(blackListModel);


        }
        cursor.close();
        return blackList;
    }


    public ArrayList getAllSaveInfo(){
        ArrayList<UserInfoModel> userInfoList = new ArrayList<>();
        String select_query = "SELECT  * FROM " + DB_TABLE_SAVE_AUDIO ;


        Cursor cursor = database.rawQuery(select_query,null);

        // if(cursor != null && cursor.moveToFirst()){
        //int iDbId = cursor.getColumnIndex(KEY_ROWID);
        int iUserNumber = cursor.getColumnIndex(KEY_MOBILE_NUMBER);
        int iDate = cursor.getColumnIndex(KEY_DATE);
        int iVoiceFilePath = cursor.getColumnIndex(KEY_VOICE_FILE_PATH);
        int iVoiceDuration = cursor.getColumnIndex(KEY_VOICE_DURATION);


        for (cursor.moveToLast(); ! cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            //    for (cursor.moveToFirst(); ! cursor.isAfterLast(); cursor.moveToNext()) {

            UserInfoModel userInfoModel = new UserInfoModel();
            userInfoModel.setUserNumber(cursor.getString(iUserNumber));
            userInfoModel.setDate(cursor.getString(iDate));
            userInfoModel.setVoiceFilePath(cursor.getString(iVoiceFilePath));
            userInfoModel.setEndTime(cursor.getString(iVoiceDuration));
            userInfoList.add(userInfoModel);


        }
        cursor.close();
        return userInfoList;

    }

    public boolean isBlackList(String receiveNumber){
        int res;
        boolean isBlackList ;

        Cursor cursor = database.query(DB_TABLE_BLACKLIST, new String[] { KEY_ROWID,
                }, KEY_MOBILE_NUMBER + "=?",
                new String[] { receiveNumber }, null, null, null, null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            res = cursor.getInt(cursor.getColumnIndex(KEY_ROWID));
            isBlackList = true;
        }
        else {
           // res = NOT_EXIST;
            isBlackList = false;
        }
        if (cursor != null) {
            cursor.close();
        }
        return isBlackList;

    }

    public void updateEndTime(String number,String endTime){
        ContentValues cv = new ContentValues();
        cv.put(KEY_END_TIME, endTime);
        database.update(DB_TABLE_CALL_BUTTON, cv, "mobile_number=" + number, null);

    }

    public void removeBlackListItem(String blackListNo) {
        database.execSQL("delete from " + DB_TABLE_BLACKLIST + " where mobile_number = '" + blackListNo + "'");
    }
    public void removeUserItem(String number) {
        database.execSQL("delete from " + DB_TABLE_CALL_BUTTON + " where mobile_number = '" + number + "'");
    }

    public void removeSaveItem(String number) {
        database.execSQL("delete from " + DB_TABLE_SAVE_AUDIO + " where mobile_number = '" + number + "'");
    }

    public long getUserNumber() {
        long count = DatabaseUtils.queryNumEntries(database, DB_TABLE_CALL_BUTTON);
        database.close();
        return count;
    }

    public void clearAllUser(){
        database.execSQL("delete from "+ DB_TABLE_CALL_BUTTON);
    }

}
