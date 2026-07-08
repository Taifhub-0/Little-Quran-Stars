package com.example.quranapp_part1;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SurahListActivity extends BaseActivity implements SurahAdapter.OnSurahClickListener {

    // مفاتيح الـ Intent (تُستخدم في MemorizationSettingsActivity)
    public static final String EXTRA_SURAH_NUMBER     = "surah_number";
    public static final String EXTRA_SURAH_NAME_AR    = "surah_name_ar";
    public static final String EXTRA_SURAH_NAME_EN    = "surah_name_en";
    public static final String EXTRA_SURAH_VERSE_COUNT = "surah_verse_count";

    private SurahAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surah_list);

        // زر الرجوع
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // إعداد RecyclerView
        RecyclerView recycler = findViewById(R.id.recyclerSurahs);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SurahAdapter(this, getSurahList(), this);
        recycler.setAdapter(adapter);

        // البحث الحي
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                adapter.filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    // ======= عند الضغط على سورة → يفتح شاشة الإعدادات =======
    @Override
    public void onSurahClick(SurahModel surah) {
        Intent intent = new Intent(this, MemorizationSettingsActivity.class);
        intent.putExtra(EXTRA_SURAH_NUMBER,      surah.getNumber());
        intent.putExtra(EXTRA_SURAH_NAME_AR,     surah.getNameAr());
        intent.putExtra(EXTRA_SURAH_NAME_EN,     surah.getNameEn());
        intent.putExtra(EXTRA_SURAH_VERSE_COUNT, surah.getVerseCount());
        startActivity(intent);
    }

    // ======= بيانات السور الـ 114 =======
    private List<SurahModel> getSurahList() {
        List<SurahModel> list = new ArrayList<>();
        list.add(new SurahModel(1,   "سُورَةُ الْفَاتِحَةِ",    "Al-Faatiha",   "مكية",  7));
        list.add(new SurahModel(2,   "سُورَةُ الْبَقَرَةِ",     "Al-Baqara",    "مدنية", 286));
        list.add(new SurahModel(3,   "سُورَةُ آلِ عِمْرَانَ",   "Aal-i-Imraan", "مدنية", 200));
        list.add(new SurahModel(4,   "سُورَةُ النِّسَاءِ",      "An-Nisa",      "مدنية", 176));
        list.add(new SurahModel(5,   "سُورَةُ الْمَائِدَةِ",    "Al-Maaida",    "مدنية", 120));
        list.add(new SurahModel(6,   "سُورَةُ الْأَنْعَامِ",    "Al-Anaam",     "مكية",  165));
        list.add(new SurahModel(7,   "سُورَةُ الْأَعْرَافِ",    "Al-Araaf",     "مكية",  206));
        list.add(new SurahModel(8,   "سُورَةُ الْأَنْفَالِ",    "Al-Anfaal",    "مدنية", 75));
        list.add(new SurahModel(9,   "سُورَةُ التَّوْبَةِ",     "At-Tawba",     "مدنية", 129));
        list.add(new SurahModel(10,  "سُورَةُ يُونُسَ",         "Yunus",        "مكية",  109));
        list.add(new SurahModel(11,  "سُورَةُ هُودٍ",           "Hud",          "مكية",  123));
        list.add(new SurahModel(12,  "سُورَةُ يُوسُفَ",         "Yusuf",        "مكية",  111));
        list.add(new SurahModel(13,  "سُورَةُ الرَّعْدِ",       "Ar-Rad",       "مدنية", 43));
        list.add(new SurahModel(14,  "سُورَةُ إِبْرَاهِيمَ",   "Ibrahim",      "مكية",  52));
        list.add(new SurahModel(15,  "سُورَةُ الْحِجْرِ",       "Al-Hijr",      "مكية",  99));
        list.add(new SurahModel(16,  "سُورَةُ النَّحْلِ",       "An-Nahl",      "مكية",  128));
        list.add(new SurahModel(17,  "سُورَةُ الْإِسْرَاءِ",   "Al-Isra",      "مكية",  111));
        list.add(new SurahModel(18,  "سُورَةُ الْكَهْفِ",       "Al-Kahf",      "مكية",  110));
        list.add(new SurahModel(19,  "سُورَةُ مَرْيَمَ",        "Maryam",       "مكية",  98));
        list.add(new SurahModel(20,  "سُورَةُ طَهَ",            "Taa-Haa",      "مكية",  135));
        list.add(new SurahModel(21,  "سُورَةُ الْأَنْبِيَاءِ",  "Al-Anbiya",    "مكية",  112));
        list.add(new SurahModel(22,  "سُورَةُ الْحَجِّ",        "Al-Hajj",      "مدنية", 78));
        list.add(new SurahModel(23,  "سُورَةُ الْمُؤْمِنُونَ", "Al-Muminoon",  "مكية",  118));
        list.add(new SurahModel(24,  "سُورَةُ النُّورِ",        "An-Nur",       "مدنية", 64));
        list.add(new SurahModel(25,  "سُورَةُ الْفُرْقَانِ",    "Al-Furqaan",   "مكية",  77));
        list.add(new SurahModel(26,  "سُورَةُ الشُّعَرَاءِ",    "Ash-Shuara",   "مكية",  227));
        list.add(new SurahModel(27,  "سُورَةُ النَّمْلِ",       "An-Naml",      "مكية",  93));
        list.add(new SurahModel(28,  "سُورَةُ الْقَصَصِ",       "Al-Qasas",     "مكية",  88));
        list.add(new SurahModel(29,  "سُورَةُ الْعَنْكَبُوتِ",  "Al-Ankaboot",  "مكية",  69));
        list.add(new SurahModel(30,  "سُورَةُ الرُّومِ",        "Ar-Room",      "مكية",  60));
        list.add(new SurahModel(31,  "سُورَةُ لُقْمَانَ",       "Luqman",       "مكية",  34));
        list.add(new SurahModel(32,  "سُورَةُ السَّجْدَةِ",     "As-Sajda",     "مكية",  30));
        list.add(new SurahModel(33,  "سُورَةُ الْأَحْزَابِ",    "Al-Ahzaab",    "مدنية", 73));
        list.add(new SurahModel(34,  "سُورَةُ سَبَأٍ",          "Saba",         "مكية",  54));
        list.add(new SurahModel(35,  "سُورَةُ فَاطِرٍ",         "Faatir",       "مكية",  45));
        list.add(new SurahModel(36,  "سُورَةُ يَسٍ",            "Yaseen",       "مكية",  83));
        list.add(new SurahModel(37,  "سُورَةُ الصَّافَّاتِ",    "As-Saaffaat",  "مكية",  182));
        list.add(new SurahModel(38,  "سُورَةُ صَادٍ",           "Saad",         "مكية",  88));
        list.add(new SurahModel(39,  "سُورَةُ الزُّمَرِ",       "Az-Zumar",     "مكية",  75));
        list.add(new SurahModel(40,  "سُورَةُ غَافِرٍ",         "Ghafir",       "مكية",  85));
        list.add(new SurahModel(41,  "سُورَةُ فُصِّلَتْ",       "Fussilat",     "مكية",  54));
        list.add(new SurahModel(42,  "سُورَةُ الشُّورَى",       "Ash-Shura",    "مكية",  53));
        list.add(new SurahModel(43,  "سُورَةُ الزُّخْرُفِ",     "Az-Zukhruf",   "مكية",  89));
        list.add(new SurahModel(44,  "سُورَةُ الدُّخَانِ",      "Ad-Dukhaan",   "مكية",  59));
        list.add(new SurahModel(45,  "سُورَةُ الْجَاثِيَةِ",    "Al-Jaathiya",  "مكية",  37));
        list.add(new SurahModel(46,  "سُورَةُ الْأَحْقَافِ",    "Al-Ahqaf",     "مكية",  35));
        list.add(new SurahModel(47,  "سُورَةُ مُحَمَّدٍ",       "Muhammad",     "مدنية", 38));
        list.add(new SurahModel(48,  "سُورَةُ الْفَتْحِ",       "Al-Fath",      "مدنية", 29));
        list.add(new SurahModel(49,  "سُورَةُ الْحُجُرَاتِ",    "Al-Hujuraat",  "مدنية", 18));
        list.add(new SurahModel(50,  "سُورَةُ قَافٍ",           "Qaaf",         "مكية",  45));
        list.add(new SurahModel(51,  "سُورَةُ الذَّارِيَاتِ",   "Adh-Dhaariyat","مكية",  60));
        list.add(new SurahModel(52,  "سُورَةُ الطُّورِ",        "At-Tur",       "مكية",  49));
        list.add(new SurahModel(53,  "سُورَةُ النَّجْمِ",       "An-Najm",      "مكية",  62));
        list.add(new SurahModel(54,  "سُورَةُ الْقَمَرِ",       "Al-Qamar",     "مكية",  55));
        list.add(new SurahModel(55,  "سُورَةُ الرَّحْمَنِ",     "Ar-Rahman",    "مدنية", 78));
        list.add(new SurahModel(56,  "سُورَةُ الْوَاقِعَةِ",    "Al-Waqia",     "مكية",  96));
        list.add(new SurahModel(57,  "سُورَةُ الْحَدِيدِ",      "Al-Hadid",     "مدنية", 29));
        list.add(new SurahModel(58,  "سُورَةُ الْمُجَادَلَةِ",  "Al-Mujadila",  "مدنية", 22));
        list.add(new SurahModel(59,  "سُورَةُ الْحَشْرِ",       "Al-Hashr",     "مدنية", 24));
        list.add(new SurahModel(60,  "سُورَةُ الْمُمْتَحَنَةِ", "Al-Mumtahana", "مدنية", 13));
        list.add(new SurahModel(61,  "سُورَةُ الصَّفِّ",        "As-Saff",      "مدنية", 14));
        list.add(new SurahModel(62,  "سُورَةُ الْجُمُعَةِ",     "Al-Jumua",     "مدنية", 11));
        list.add(new SurahModel(63,  "سُورَةُ الْمُنَافِقُونَ", "Al-Munafiqoon","مدنية", 11));
        list.add(new SurahModel(64,  "سُورَةُ التَّغَابُنِ",    "At-Taghabun",  "مدنية", 18));
        list.add(new SurahModel(65,  "سُورَةُ الطَّلَاقِ",      "At-Talaq",     "مدنية", 12));
        list.add(new SurahModel(66,  "سُورَةُ التَّحْرِيمِ",    "At-Tahrim",    "مدنية", 12));
        list.add(new SurahModel(67,  "سُورَةُ الْمُلْكِ",       "Al-Mulk",      "مكية",  30));
        list.add(new SurahModel(68,  "سُورَةُ الْقَلَمِ",       "Al-Qalam",     "مكية",  52));
        list.add(new SurahModel(69,  "سُورَةُ الْحَاقَّةِ",     "Al-Haaqqa",    "مكية",  52));
        list.add(new SurahModel(70,  "سُورَةُ الْمَعَارِجِ",    "Al-Maarij",    "مكية",  44));
        list.add(new SurahModel(71,  "سُورَةُ نُوحٍ",           "Nooh",         "مكية",  28));
        list.add(new SurahModel(72,  "سُورَةُ الْجِنِّ",        "Al-Jinn",      "مكية",  28));
        list.add(new SurahModel(73,  "سُورَةُ الْمُزَّمِّلِ",   "Al-Muzzammil", "مكية",  20));
        list.add(new SurahModel(74,  "سُورَةُ الْمُدَّثِّرِ",   "Al-Muddaththir","مكية", 56));
        list.add(new SurahModel(75,  "سُورَةُ الْقِيَامَةِ",    "Al-Qiyama",    "مكية",  40));
        list.add(new SurahModel(76,  "سُورَةُ الْإِنْسَانِ",    "Al-Insan",     "مدنية", 31));
        list.add(new SurahModel(77,  "سُورَةُ الْمُرْسَلَاتِ",  "Al-Mursalaat", "مكية",  50));
        list.add(new SurahModel(78,  "سُورَةُ النَّبَأِ",       "An-Naba",      "مكية",  40));
        list.add(new SurahModel(79,  "سُورَةُ النَّازِعَاتِ",   "An-Naaziat",   "مكية",  46));
        list.add(new SurahModel(80,  "سُورَةُ عَبَسَ",          "Abasa",        "مكية",  42));
        list.add(new SurahModel(81,  "سُورَةُ التَّكْوِيرِ",    "At-Takwir",    "مكية",  29));
        list.add(new SurahModel(82,  "سُورَةُ الْإِنْفِطَارِ",  "Al-Infitar",   "مكية",  19));
        list.add(new SurahModel(83,  "سُورَةُ الْمُطَفِّفِينَ", "Al-Mutaffifin","مكية",  36));
        list.add(new SurahModel(84,  "سُورَةُ الِانْشِقَاقِ",   "Al-Inshiqaq",  "مكية",  25));
        list.add(new SurahModel(85,  "سُورَةُ الْبُرُوجِ",      "Al-Burooj",    "مكية",  22));
        list.add(new SurahModel(86,  "سُورَةُ الطَّارِقِ",      "At-Tariq",     "مكية",  17));
        list.add(new SurahModel(87,  "سُورَةُ الْأَعْلَى",      "Al-Ala",       "مكية",  19));
        list.add(new SurahModel(88,  "سُورَةُ الْغَاشِيَةِ",    "Al-Ghashiya",  "مكية",  26));
        list.add(new SurahModel(89,  "سُورَةُ الْفَجْرِ",       "Al-Fajr",      "مكية",  30));
        list.add(new SurahModel(90,  "سُورَةُ الْبَلَدِ",       "Al-Balad",     "مكية",  20));
        list.add(new SurahModel(91,  "سُورَةُ الشَّمْسِ",       "Ash-Shams",    "مكية",  15));
        list.add(new SurahModel(92,  "سُورَةُ اللَّيْلِ",       "Al-Layl",      "مكية",  21));
        list.add(new SurahModel(93,  "سُورَةُ الضُّحَى",        "Ad-Duhaa",     "مكية",  11));
        list.add(new SurahModel(94,  "سُورَةُ الشَّرْحِ",       "Ash-Sharh",    "مكية",  8));
        list.add(new SurahModel(95,  "سُورَةُ التِّينِ",        "At-Tin",       "مكية",  8));
        list.add(new SurahModel(96,  "سُورَةُ الْعَلَقِ",       "Al-Alaq",      "مكية",  19));
        list.add(new SurahModel(97,  "سُورَةُ الْقَدْرِ",       "Al-Qadr",      "مكية",  5));
        list.add(new SurahModel(98,  "سُورَةُ الْبَيِّنَةِ",    "Al-Bayyina",   "مدنية", 8));
        list.add(new SurahModel(99,  "سُورَةُ الزَّلْزَلَةِ",   "Az-Zalzala",   "مدنية", 8));
        list.add(new SurahModel(100, "سُورَةُ الْعَادِيَاتِ",   "Al-Aadiyaat",  "مكية",  11));
        list.add(new SurahModel(101, "سُورَةُ الْقَارِعَةِ",    "Al-Qaaria",    "مكية",  11));
        list.add(new SurahModel(102, "سُورَةُ التَّكَاثُرِ",    "At-Takaathur", "مكية",  8));
        list.add(new SurahModel(103, "سُورَةُ الْعَصْرِ",       "Al-Asr",       "مكية",  3));
        list.add(new SurahModel(104, "سُورَةُ الْهُمَزَةِ",     "Al-Humaza",    "مكية",  9));
        list.add(new SurahModel(105, "سُورَةُ الْفِيلِ",        "Al-Fil",       "مكية",  5));
        list.add(new SurahModel(106, "سُورَةُ قُرَيْشٍ",        "Quraysh",      "مكية",  4));
        list.add(new SurahModel(107, "سُورَةُ الْمَاعُونِ",     "Al-Maun",      "مكية",  7));
        list.add(new SurahModel(108, "سُورَةُ الْكَوْثَرِ",     "Al-Kawthar",   "مكية",  3));
        list.add(new SurahModel(109, "سُورَةُ الْكَافِرُونَ",   "Al-Kaafiroon", "مكية",  6));
        list.add(new SurahModel(110, "سُورَةُ النَّصْرِ",       "An-Nasr",      "مدنية", 3));
        list.add(new SurahModel(111, "سُورَةُ الْمَسَدِ",       "Al-Masad",     "مكية",  5));
        list.add(new SurahModel(112, "سُورَةُ الْإِخْلَاصِ",   "Al-Ikhlaas",   "مكية",  4));
        list.add(new SurahModel(113, "سُورَةُ الْفَلَقِ",       "Al-Falaq",     "مكية",  5));
        list.add(new SurahModel(114, "سُورَةُ النَّاسِ",        "An-Naas",      "مكية",  6));
        return list;
    }
}
