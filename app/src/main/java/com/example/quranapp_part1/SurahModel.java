package com.example.quranapp_part1;


public class SurahModel {

    private int    number;      // رقم السورة (1-114)
    private String nameAr;      // الاسم العربي
    private String nameEn;      // الاسم الإنجليزي
    private String type;        // مكية / مدنية
    private int    verseCount;  // عدد الآيات

    public SurahModel(int number, String nameAr, String nameEn, String type, int verseCount) {
        this.number     = number;
        this.nameAr     = nameAr;
        this.nameEn     = nameEn;
        this.type       = type;
        this.verseCount = verseCount;
    }

    // Getters
    public int    getNumber()     { return number; }
    public String getNameAr()     { return nameAr; }
    public String getNameEn()     { return nameEn; }
    public String getType()       { return type; }
    public int    getVerseCount() { return verseCount; }
}
