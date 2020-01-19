package de.axolotl.flomb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
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
    public static final String C0 = "ID";
    public static final String C1 = "AMOUNT";
    public static final String C2 = "CATEGORY";
    public static final String C3 = "SUBCATEGORY";
    public static final String C4 = "DESCRIPTION";
    public static final String C5 = "YEAR";
    public static final String C6 = "MONTH";
    public static final String C7 = "DAY";
    public static final String C8 = "PLACE";
    public static final String C9 = "DATESTR";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+" ("+C0+" INTEGER PRIMARY KEY AUTOINCREMENT,"+C1+" INTEGER,"+C2+" TEXT,"+
        C3+" TEXT,"+C4+" TEXT,"+C5+" INTEGER,"+C6+" INTEGER,"+C7+" INTEGER,"+C8+" TEXT,"+C9+" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(int amount, String category, String subcategory, String description,
                              String place, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(C1,amount);
        contentValues.put(C2,category);
        contentValues.put(C3,subcategory);
        contentValues.put(C4,description);
        contentValues.put(C5,0);
        contentValues.put(C6,0);
        contentValues.put(C7,0);
        contentValues.put(C8,place);
        contentValues.put(C9,date);
        long result = db.insert(TABLE_NAME,null,contentValues);

        return !(result==-1);
    }

    public boolean updateData(int id, int amount, String category, String subcategory,
                              String description, String place, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(C1,amount);
        contentValues.put(C2,category);
        contentValues.put(C3,subcategory);
        contentValues.put(C4,description);
        contentValues.put(C5,0);
        contentValues.put(C6,0);
        contentValues.put(C7,0);
        contentValues.put(C8,place);
        contentValues.put(C9,date);
        long result = db.update(TABLE_NAME, contentValues, "ID = ?", new String[] {Integer.toString(id)});
        return !(result==-1);
    }

    public void insertDateColumn(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN DATESTR TEXT");
        db.execSQL("UPDATE " + TABLE_NAME + " SET DATESTR = date(YEAR||'-'||(CASE WHEN MONTH LIKE '_' " +
                "THEN ('0'||MONTH) ELSE MONTH END)||'-'||(CASE WHEN DAY LIKE '_' THEN ('0'||DAY) ELSE DAY END))");
    }

    public Cursor getQueryData(String cat, String fr_str, String to_str){
        String selectionDate = C9+" BETWEEN '"+fr_str+"' AND '"+to_str+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE " + selectionDate + " AND " +
                C2 + " IN (" + cat + ") ORDER BY "+C9+" DESC, " + C0,null);
    }

    public Cursor getPastMonth(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "SELECT * FROM "+ TABLE_NAME + " WHERE " + C9 +
                " BETWEEN date('now', '-1 month', '+1 day') AND date('now') ORDER BY " + C9 + " DESC",null);
    }

    public Cursor getSummaryOfPastMonth(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "SELECT "+C2+", SUM("+ C1 +") FROM "+ TABLE_NAME + " WHERE " +
                C9 + " BETWEEN date('now', '-1 month', '+1 day') AND date('now')"+ " GROUP BY " + C2,null);
    }

    public Cursor getDaysBetween(String d1, String d2){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "SELECT julianday('"+d2+"')-julianday('"+d1+"')",null);
    }

    public Cursor getSummaryOfQuery(String cat, String fr_str, String to_str){
        String selectionDate = C9+" BETWEEN '"+fr_str+"' AND '"+to_str+"'";
        Log.wtf("SUM",cat);
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT "+C2+ ", SUM("+ C1 +") FROM "+TABLE_NAME+" WHERE "+
                selectionDate+" AND "+C2+" IN ("+cat+") GROUP BY "+C2,null);
    }

    public Cursor searchForUpdateEntry(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM "+ TABLE_NAME + " WHERE ID = " + id, null);
    }

    public Cursor searchQuery(String s){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+ TABLE_NAME + " WHERE " + C4 +
                " LIKE '%" + s + "%' ORDER BY " + C9 + " DESC", null);
    }

    public Cursor searchQuerySummary(String s){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + C2 + ", SUM(" + C1 + ") FROM "+ TABLE_NAME + " WHERE " + C4 +
                " LIKE '%" + s + "%' GROUP BY "+C2, null);
    }


    public Cursor doQuery(String q){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(q, null);
    }

    public void clearDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_NAME);
    }

    public Integer deleteData (int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = " + id, null);
    }

    public int mapLoanToWorkingHours(String fr_str, String to_str, int sum, String purpose){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT SUM("+C1+") FROM "+TABLE_NAME+" WHERE "+
                C4+" LIKE '%"+purpose+"%' AND "+C9+" BETWEEN '"+fr_str+"' AND '"+to_str+"' AND "+C3+" = 'Wage'", null);
        res.moveToFirst();
        int hours = res.getInt(0);
        res.close();
        if (hours == 0)
            return 1;
        double perHour = sum / (double) hours;
        db.execSQL("UPDATE " + TABLE_NAME + " SET "+C4+" = '"+purpose+"'|| ': ' || "+C1+", " +
                C1+" = cast(("+C1+" * "+perHour+") as int) WHERE "+C4+" LIKE '%"+purpose+"%' " +
                "AND "+C9+" BETWEEN '"+fr_str+"' AND '"+to_str+"' AND "+C3+" = 'Wage'");
        return 0;
    }

    //TODO undoMapLoan
    public int undoMapLoan(String fr_str, String to_str, String purpose){
        SQLiteDatabase db = this.getWritableDatabase();
        int length = purpose.length()+3; // Länge + ": " + 1, da Substring bei 1 anfängt, nicht bei 0
        db.execSQL("UPDATE " + TABLE_NAME + " SET "+C4+" = '"+purpose+"', " +
                C1+" = cast(SUBSTR("+C4+","+length+") as int) WHERE "+C4+" LIKE '%"+purpose+"%' " +
                "AND "+C9+" BETWEEN '"+fr_str+"' AND '"+to_str+"' AND "+C3+" = 'Wage'");
        return 0;
    }

    public int getLastEntryID() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor r = db.rawQuery("SELECT MAX(ID) FROM "+TABLE_NAME, null);
        r.moveToNext();
        return r.getInt(0);
    }

    public Cursor getSuggestions(String cat, String subcat, int minimum){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT "+C4+", count(*) FROM "+TABLE_NAME+" WHERE "+C2+" = "+cat+" AND "
                +C3+" = "+subcat+" GROUP BY "+C4,null);
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
        //update entry, go back to settings layout
}
