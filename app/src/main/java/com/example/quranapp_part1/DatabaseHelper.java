package com.example.quranapp_part1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // اسم قاعدة البيانات والنسخة
    private static final String DATABASE_NAME = "QuranApp.db";
    private static final int DATABASE_VERSION = 3;

    // أسماء الجداول
    private static final String TABLE_VERSES = "verses";
    private static final String TABLE_PROGRESS = "user_progress";

    // أعمدة جدول الآيات
    private static final String COLUMN_VERSE_ID = "id";
    private static final String COLUMN_VERSE_SURAH_NUMBER = "surah_number";
    private static final String COLUMN_VERSE_NUMBER = "verse_number";
    private static final String COLUMN_PAGE_NUMBER = "page_number";
    private static final String COLUMN_VERSE_TEXT = "verse_text";

    // أعمدة جدول التقدم (لكل سورة على حدة)
    private static final String COLUMN_PROGRESS_ID = "id";
    private static final String COLUMN_SURAH_NUMBER = "surah_number";
    private static final String COLUMN_CURRENT_VERSE = "current_verse";
    private static final String COLUMN_START_VERSE = "start_verse";
    private static final String COLUMN_END_VERSE = "end_verse";
    private static final String COLUMN_REPEAT_COUNT = "repeat_count";
    private static final String COLUMN_COMPLETED_REPEATS = "completed_repeats";
    private static final String COLUMN_LAST_UPDATED = "last_updated";
    private static final String COLUMN_SURAH_NAME_AR = "surah_name_ar";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // إنشاء جدول الآيات
        String CREATE_VERSES_TABLE = "CREATE TABLE " + TABLE_VERSES + " (" +
                COLUMN_VERSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_VERSE_SURAH_NUMBER + " INTEGER NOT NULL, " +
                COLUMN_VERSE_NUMBER + " INTEGER NOT NULL, " +
                COLUMN_PAGE_NUMBER + " INTEGER, " +
                COLUMN_VERSE_TEXT + " TEXT" +
                ")";
        db.execSQL(CREATE_VERSES_TABLE);

        // إنشاء جدول التقدم (لكل سورة)
        String CREATE_PROGRESS_TABLE = "CREATE TABLE " + TABLE_PROGRESS + " (" +
                COLUMN_PROGRESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SURAH_NUMBER + " INTEGER NOT NULL UNIQUE, " +
                COLUMN_CURRENT_VERSE + " INTEGER DEFAULT 1, " +
                COLUMN_START_VERSE + " INTEGER DEFAULT 1, " +
                COLUMN_END_VERSE + " INTEGER DEFAULT 7, " +
                COLUMN_REPEAT_COUNT + " INTEGER DEFAULT 3, " +
                COLUMN_COMPLETED_REPEATS + " INTEGER DEFAULT 0, " +
                COLUMN_SURAH_NAME_AR + " TEXT, " +
                COLUMN_LAST_UPDATED + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";
        db.execSQL(CREATE_PROGRESS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // عند ترقية النسخة، احذف الجداول القديمة وأنشئ جديدة
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VERSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRESS);
        onCreate(db);
    }

    // ===== دوال التعامل مع جدول الآيات =====


     // إضافة آية جديدة إلى قاعدة البيانات
    public long addVerse(int surahNumber, int verseNumber, int pageNumber, String verseText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_VERSE_SURAH_NUMBER, surahNumber);
        values.put(COLUMN_VERSE_NUMBER, verseNumber);
        values.put(COLUMN_PAGE_NUMBER, pageNumber);
        values.put(COLUMN_VERSE_TEXT, verseText);
        return db.insert(TABLE_VERSES, null, values);
    }


     // جلب آية محددة
    public Cursor getVerse(int surahNumber, int verseNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_VERSES,
                new String[]{COLUMN_VERSE_ID, COLUMN_VERSE_SURAH_NUMBER, COLUMN_VERSE_NUMBER,
                        COLUMN_PAGE_NUMBER, COLUMN_VERSE_TEXT},
                COLUMN_VERSE_SURAH_NUMBER + " = ? AND " + COLUMN_VERSE_NUMBER + " = ?",
                new String[]{String.valueOf(surahNumber), String.valueOf(verseNumber)},
                null, null, null);
    }


    // دوال التعامل مع جدول التقدم

    // حفظ تقدم السورة الحالية مع النطاق والتكرار
    public void saveProgress(int surahNumber, int currentVerse, int startVerse, int endVerse,
                             int repeatCount, int completedRepeats, String surahNameAr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SURAH_NUMBER, surahNumber);
        values.put(COLUMN_CURRENT_VERSE, currentVerse);
        values.put(COLUMN_START_VERSE, startVerse);
        values.put(COLUMN_END_VERSE, endVerse);
        values.put(COLUMN_REPEAT_COUNT, repeatCount);
        values.put(COLUMN_COMPLETED_REPEATS, completedRepeats);
        values.put(COLUMN_SURAH_NAME_AR, surahNameAr);
        values.put(COLUMN_LAST_UPDATED, System.currentTimeMillis());

        // استبدل أو أدرج (UPDATE OR INSERT)
        db.insertWithOnConflict(TABLE_PROGRESS, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * جلب آخر سورة تم فتحها (بناءً على تاريخ التحديث)
     * ترجع: surah_number, current_verse, surah_name_ar, repeat_count, completed_repeats, start_verse, end_verse
     */
    public Cursor getLastOpenedSurah() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_PROGRESS,
                new String[]{COLUMN_SURAH_NUMBER, COLUMN_CURRENT_VERSE, COLUMN_SURAH_NAME_AR,
                        COLUMN_REPEAT_COUNT, COLUMN_COMPLETED_REPEATS,
                        COLUMN_START_VERSE, COLUMN_END_VERSE},
                null, null, null, null,
                COLUMN_LAST_UPDATED + " DESC",
                "1");
    }

}