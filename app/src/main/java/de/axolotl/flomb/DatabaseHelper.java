package de.axolotl.flomb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Anton on 12.02.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "flomb.db";
    public static final String TABLE_NAME = "Ausgaben";
    public static final String COL_0 = "ID";
    public static final String COL_1 = "AMOUNT";
    public static final String COL_2 = "CATEGORY";
    public static final String COL_3 = "SUBCATEGORY";
    public static final String COL_4 = "DESCRIPTION";
    public static final String COL_5 = "YEAR";
    public static final String COL_6 = "MONTH";
    public static final String COL_7 = "DAY";
    public static final String COL_8 = "PLACE";
    public static final String COL_9 = "DATESTR";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+" ("+COL_0+" INTEGER PRIMARY KEY AUTOINCREMENT,"+COL_1+" INTEGER,"+COL_2+" TEXT,"+
        COL_3+" TEXT,"+COL_4+" TEXT,"+COL_5+" INTEGER,"+COL_6+" INTEGER,"+COL_7+" INTEGER,"+COL_8+" TEXT,"+ COL_9+" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(int amount, String category, String subcategory, String description, int dateYear, int dateMonth, int dateDay, String place){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,amount);
        contentValues.put(COL_2,category);
        contentValues.put(COL_3,subcategory);
        contentValues.put(COL_4,description);
        contentValues.put(COL_5,dateYear);
        contentValues.put(COL_6,dateMonth);
        contentValues.put(COL_7,dateDay);
        contentValues.put(COL_8,place);
        contentValues.put(COL_9,dateYear+"-"+((dateMonth<10) ? "0" : "") + dateMonth + "-" + ((dateDay<10) ? "0" : "") + dateDay);
        long result = db.insert(TABLE_NAME,null,contentValues);

        return !(result==-1);
    }

    public boolean updateData(int id, int amount, String category, String subcategory, String description, int dateYear, int dateMonth, int dateDay, String place){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,amount);
        contentValues.put(COL_2,category);
        contentValues.put(COL_3,subcategory);
        contentValues.put(COL_4,description);
        contentValues.put(COL_5,dateYear);
        contentValues.put(COL_6,dateMonth);
        contentValues.put(COL_7,dateDay);
        contentValues.put(COL_8,place);
        contentValues.put(COL_9,dateYear+"-"+((dateMonth<10) ? "0" : "") + dateMonth + "-" + ((dateDay<10) ? "0" : "") + dateDay);
        long result = db.update(TABLE_NAME, contentValues, "ID = ?", new String[] {Integer.toString(id)});
        return !(result==-1);
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_NAME,null);
        return res;
    }

    public void insertDateColumn(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN DATESTR TEXT");
        db.execSQL("UPDATE " + TABLE_NAME + " SET DATESTR = date(YEAR||'-'||(CASE WHEN MONTH LIKE '_' THEN ('0'||MONTH) ELSE MONTH END)||'-'||(CASE WHEN DAY LIKE '_' THEN ('0'||DAY) ELSE DAY END))");
    }

    public Cursor getQueryData(String cat, String fr_str, String to_str){
        String selectionDate = COL_9+" BETWEEN '"+fr_str+"' AND '"+to_str+"'";
        //Log.wtf("QUERY",fr_str);
        //Log.wtf("QUERY",to_str);
        //Log.wtf("QUERY", selectionDate);
        //Log.wtf("",cat);
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE " + selectionDate + " AND " + COL_2 + " IN (" + cat + ") ORDER BY "+COL_9+" DESC",null);
    }

    public Cursor getNewestN(int n){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "SELECT * FROM "+ TABLE_NAME + " ORDER BY " + COL_9 + " DESC LIMIT " + n,null);
    }

    public Cursor getTimeSpanAll(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT julianday(MAX("+ COL_9 +")) - julianday(MIN("+COL_9+")) FROM " + TABLE_NAME, null);
    }

    public Cursor getSummary(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "SELECT "+COL_2+", SUM("+ COL_1 +") FROM "+ TABLE_NAME + " GROUP BY " + COL_2,null);
    }

    public Cursor getSummaryOfSub(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "SELECT "+COL_2+", "+COL_3+", SUM("+ COL_1 +") FROM "+ TABLE_NAME + " GROUP BY " + COL_2 + ", " + COL_3,null);
    }

    public Cursor getSummaryOfQuery(String cat, String fr_str, String to_str){
        String selectionDate = COL_9+" BETWEEN '"+fr_str+"' AND '"+to_str+"'";
        //Log.wtf("",cat);
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT "+COL_2+ ", SUM("+ COL_1 +") FROM "+TABLE_NAME+" WHERE "+selectionDate+" AND "+COL_2+" IN ("+cat+") GROUP BY "+COL_2,null);
    }

    public Cursor searchForUpdateEntry(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM "+ TABLE_NAME + " where " + COL_0 + "=" + id, null);
    }

    public void clearDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_NAME);
    }

    public Integer deleteData (String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[] {id});
    }

    public Integer deleteLastEntry(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = SELECT MAX(ID) FROM " + TABLE_NAME, null);
    }

    public String exportDatabase(String database) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = data.getAbsolutePath() + "//data//" +  MainActivity.PACKAGE_NAME + "//databases//" + database + "";
                String currentDBPathDir = data.getAbsolutePath() + "//data//" +  MainActivity.PACKAGE_NAME + "//databases//";
                String backupDBPath = sd.getAbsolutePath() + "//download/FATOBbackup.db";
                String backupDBPathDir = sd.getAbsolutePath() + "//download//";
                File currentDB = new File(currentDBPathDir, database);
                File backupDB = new File(backupDBPathDir, "FATOBbackup.db");

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    return "Funktioniert";
                }
                else return "currentDB dn exist";
            }
            else return "sd cant write";
        } catch (FileNotFoundException e) {
            return "file not found";
        } catch (IOException e) {
            return "anderer Fehler";
        }
    }
}
