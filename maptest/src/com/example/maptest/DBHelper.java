package com.example.maptest;

import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "runner";
    private final static int DATABASE_VERSION = 1;
    private final static String TABLE_NAME = "running";

    public final static String FIELD_ID = "_id";
    public final static String TIME = "time";
    public final static String DISTANCE = "distance";
    public final static String SPEED = "speed";
    public final static String ENERGY = "energy";
    public final static String DATE = "datetime";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql=" create table if not exists " + TABLE_NAME + " ( " +
                FIELD_ID + " integer primary key autoincrement, " +
                DATE + " text, " +
                DISTANCE + " real, " +
                TIME + " text, " +
                ENERGY + " real " +
                " ); ";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        String sql=" drop table if exists "+ TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

    public long insert(Map<String, Object> map) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DATE, (String) map.get(DATE));
        cv.put(DISTANCE, (Float) map.get(DISTANCE));
        cv.put(TIME, (String) map.get(TIME));
        cv.put(ENERGY, (Float) map.get(ENERGY));

        return db.insert(TABLE_NAME, null, cv);

    }

    public void delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = FIELD_ID + " =? ";
        String[] whereValue = {Integer.toString(id)};
        db.delete(TABLE_NAME, where, whereValue);
    }

    public Cursor select()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        //String[] query = new String[]{FIELD_ID, DATE, DISTANCE, TIME, ENERGY};
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,  " _id desc");
        return cursor;
    }

    public void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql=" drop table if exists "+ TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }
}
