package com.example.quranapp_part1;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MemorizationActivity extends BaseActivity {

    private int currentVerse;
    private int startVerse;
    private int endVerse;
    private int totalRepeats;
    private int currentRepeat;
    private boolean isPlaying;

    private String surahNameAr;
    private int    surahNumber;

    private TextView tvCurrentVerse;
    private TextView  tvRepeatCounter, tvSurahNameHeader, tvVerseProgress;
    private TextView  tvVerseBadge, tvProgressPercent;
    private ImageView imgMushafPage;
    private ProgressBar progressBar;
    private Button    btnPlayPause, btnPrevious, btnNext, btnBack;

    private Handler  repeatHandler  = new Handler();
    private Runnable repeatRunnable;
    private int      repeatDelayMs  = 4000;

    private MediaPlayer mediaPlayer;

    private static final String[] READER_IDS = {
            "ar.alafasy",              // 0 → مشاري العفاسي
            "ar.abdulbasitmurattal",   // 1 → عبدالباسط عبدالصمد
            "ar.abdurrahmaansudais",   // 2 → عبدالرحمن السديس
            "ar.saoodshuraym",         // 3 → سعود الشريم
            "ar.hudhaify",             // 4 → علي الحذيفي
            "ar.hanirifai",            // 5 → هاني الرفاعي
            "ar.mahermuaiqly"          // 6 → ماهر المعيقلي
    };
    private String readerApiId = READER_IDS[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorization);

        // ===== استقبال البيانات من الـ Intent =====
        surahNumber  = getIntent().getIntExtra(SurahListActivity.EXTRA_SURAH_NUMBER, 1);
        surahNameAr  = getIntent().getStringExtra(SurahListActivity.EXTRA_SURAH_NAME_AR);
        startVerse   = getIntent().getIntExtra(MemorizationSettingsActivity.EXTRA_START_VERSE, 1);
        endVerse     = getIntent().getIntExtra(MemorizationSettingsActivity.EXTRA_END_VERSE, 6);
        totalRepeats = getIntent().getIntExtra(MemorizationSettingsActivity.EXTRA_REPEAT_COUNT, 3);

        // ===== جلب آخر موضع محفوظ من قاعدة البيانات =====
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor lastProgress = dbHelper.getLastOpenedSurah();

        if (lastProgress != null && lastProgress.moveToFirst()) {
            int savedSurah = lastProgress.getInt(0);
            int savedVerse = lastProgress.getInt(1);
            int savedStartVerse = lastProgress.getInt(5);
            int savedEndVerse = lastProgress.getInt(6);
            int savedCompletedRep = lastProgress.getInt(4);
            lastProgress.close();

            if (savedSurah == surahNumber && savedVerse >= savedStartVerse && savedVerse <= savedEndVerse) {
                // نفس السورة والنطاق → ابدأ من آخر موضع
                currentVerse = savedVerse;
                currentRepeat = savedCompletedRep + 1;
                startVerse = savedStartVerse;
                endVerse = savedEndVerse;
            } else {
                // سورة أو نطاق مختلف
                currentVerse = startVerse;
                currentRepeat = 1;
            }
        } else {
            currentVerse = startVerse;
            currentRepeat = 1;
        }
        dbHelper.close();

        isPlaying = false;

        // ===== ربط الـ Views =====
        tvRepeatCounter   = findViewById(R.id.tvRepeatCounter);
        tvSurahNameHeader = findViewById(R.id.tvSurahNameHeader);
        tvVerseProgress   = findViewById(R.id.tvVerseProgress);
        tvVerseBadge      = findViewById(R.id.tvVerseBadge);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        imgMushafPage     = findViewById(R.id.imgMushafPage);
        progressBar       = findViewById(R.id.progressBar);
        btnPlayPause      = findViewById(R.id.btnPlayPause);
        btnPrevious       = findViewById(R.id.btnPrevious);
        btnNext           = findViewById(R.id.btnNext);
        btnBack           = findViewById(R.id.btnBack);
        tvCurrentVerse    = findViewById(R.id.tvCurrentVerse);

        loadPreferences();

        tvSurahNameHeader.setText(surahNameAr);
        updateUI();

        new Handler().postDelayed(() -> fetchVerseFromApi(currentVerse), 300);
        fetchAndStoreVerses();


        // ===== أزرار التحكم =====
        btnBack.setOnClickListener(v -> {
            stopRepeat();
            finish();
        });

        // ===== زر إيقاف مؤقت وأكمل لاحقاً =====
        Button btnPauseAndResume = findViewById(R.id.btnPauseAndResume);
        btnPauseAndResume.setOnClickListener(v -> {
            stopRepeat();

            // حفظ التقدم الحالي مع النطاق
            DatabaseHelper db = new DatabaseHelper(this);
            db.saveProgress(surahNumber, currentVerse, startVerse, endVerse, totalRepeats, currentRepeat, surahNameAr);
            db.close();

            Toast.makeText(this, "تم الحفظ ✓", Toast.LENGTH_SHORT).show();

            // الرجوع للرئيسية بعد ثانية
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(MemorizationActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }, 1000);
        });

        btnPlayPause.setOnClickListener(v -> {
            if (isPlaying) {
                stopRepeat();
                Toast.makeText(this, "⏸ إيقاف مؤقت", Toast.LENGTH_SHORT).show();
            } else {
                isPlaying = true;
                btnPlayPause.setText("⏸");
                playCurrentVerseAudio();
            }
        });

        btnNext.setOnClickListener(v -> {
            stopRepeat();
            goToNextVerse();
        });

        btnPrevious.setOnClickListener(v -> {
            stopRepeat();
            goToPreviousVerse();
        });
    }

    private void goToNextVerse() {
        if (currentVerse < endVerse) {
            currentVerse++;
            currentRepeat = 1;
            updateUI();
            fetchVerseFromApi(currentVerse);

            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.saveProgress(surahNumber, currentVerse, startVerse, endVerse, totalRepeats, currentRepeat, surahNameAr);
            dbHelper.close();
        } else {
            openCompletionScreen();
        }
    }

    private void goToPreviousVerse() {
        if (currentVerse > startVerse) {
            currentVerse--;
            currentRepeat = 1;
            updateUI();
            fetchVerseFromApi(currentVerse);

            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.saveProgress(surahNumber, currentVerse, startVerse, endVerse, totalRepeats, currentRepeat, surahNameAr);
            dbHelper.close();
        } else {
            Toast.makeText(this, "أنت في أول آية!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {
        int totalVerses  = (endVerse - startVerse) + 1;
        int verseIndex   = (currentVerse - startVerse) + 1;

        tvRepeatCounter.setText(currentRepeat + " / " + totalRepeats);
        tvVerseProgress.setText("الآية " + currentVerse + " من " + endVerse);
        tvVerseBadge.setText("📖 " + surahNameAr + " : آية " + currentVerse);

        int progressPercent = (int) ((float) verseIndex / totalVerses * 100);
        tvProgressPercent.setText(progressPercent + "%");
        progressBar.setProgress(progressPercent);
    }

    private void openCompletionScreen() {
        Intent intent = new Intent(this, CompletionActivity.class);
        intent.putExtra(SurahListActivity.EXTRA_SURAH_NAME_AR, surahNameAr);
        intent.putExtra(SurahListActivity.EXTRA_SURAH_NUMBER,  surahNumber);
        intent.putExtra(MemorizationSettingsActivity.EXTRA_REPEAT_COUNT, totalRepeats);
        intent.putExtra(MemorizationSettingsActivity.EXTRA_START_VERSE, startVerse);
        intent.putExtra(MemorizationSettingsActivity.EXTRA_END_VERSE, endVerse);
        startActivity(intent);
    }

    private void fetchVerseFromApi(int verseNum) {
        String apiUrl = "https://api.alquran.cloud/v1/ayah/"
                + surahNumber + ":" + verseNum;

        new Thread(() -> {
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.connect();

                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                    reader.close();

                    JSONObject json = new JSONObject(sb.toString());
                    JSONObject data = json.getJSONObject("data");
                    int pageNumber  = data.getInt("page");
                    String verseText = data.getString("text");

                    runOnUiThread(() -> {
                        tvCurrentVerse.setText(verseText);
                        showMushafPage(pageNumber);
                    });
                }
            } catch (Exception e) {
                // تعذّر التحميل
            }
        }).start();
    }

    private void showMushafPage(int pageNumber) {
        String imageName = "quran_pages_main/" + pageNumber + ".png";
        new Thread(() -> {
            try {
                InputStream is = getAssets().open(imageName);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                is.close();
                runOnUiThread(() -> {
                    imgMushafPage.setImageBitmap(bitmap);
                    if (isDarkMode()) {
                        imgMushafPage.setColorFilter(new android.graphics.ColorMatrixColorFilter(
                                new float[]{
                                        -1, 0, 0, 0, 255,
                                        0, -1, 0, 0, 255,
                                        0, 0, -1, 0, 255,
                                        0, 0,  0, 1,   0
                                }
                        ));
                    } else {
                        imgMushafPage.clearColorFilter();
                    }
                });
            } catch (Exception e) {
                // الصورة ما وُجدت
            }
        }).start();
    }

    private void playCurrentVerseAudio() {
        String audioApiUrl = "https://api.alquran.cloud/v1/ayah/"
                + surahNumber + ":" + currentVerse + "/" + readerApiId;

        new Thread(() -> {
            try {
                URL url = new URL(audioApiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.connect();

                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                    reader.close();

                    JSONObject json = new JSONObject(sb.toString());
                    String audioUrl = json.getJSONObject("data").getString("audio");

                    runOnUiThread(() -> startMediaPlayer(audioUrl));
                }
            } catch (Exception e) {
                // الصوت غير متاح
            }
        }).start();
    }

    private void startMediaPlayer(String audioUrl) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);

            mediaPlayer.setOnCompletionListener(mp -> {
                if (isPlaying) {
                    scheduleNextRepeat();
                }
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> true);

        } catch (Exception e) {
            // لا صوت
        }
    }

    private void scheduleNextRepeat() {
        repeatRunnable = () -> {
            if (!isPlaying) return;

            if (currentRepeat < totalRepeats) {
                currentRepeat++;
                updateUI();

                DatabaseHelper dbHelper = new DatabaseHelper(this);
                dbHelper.saveProgress(surahNumber, currentVerse, startVerse, endVerse, totalRepeats, currentRepeat, surahNameAr);
                dbHelper.close();

                playCurrentVerseAudio();
            } else {
                currentRepeat = 1;
                if (currentVerse < endVerse) {
                    currentVerse++;
                    updateUI();
                    fetchVerseFromApi(currentVerse);

                    DatabaseHelper dbHelper = new DatabaseHelper(this);
                    dbHelper.saveProgress(surahNumber, currentVerse, startVerse, endVerse, totalRepeats, currentRepeat, surahNameAr);
                    dbHelper.close();

                    playCurrentVerseAudio();
                } else {
                    isPlaying = false;
                    btnPlayPause.setText("◀");
                    openCompletionScreen();
                }
            }
        };
        repeatHandler.postDelayed(repeatRunnable, repeatDelayMs);
    }

    private void stopRepeat() {
        isPlaying = false;
        btnPlayPause.setText("◀");
        if (repeatRunnable != null) {
            repeatHandler.removeCallbacks(repeatRunnable);
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(
                SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        repeatDelayMs = prefs.getInt(SettingsActivity.KEY_REPEAT_DELAY, 4000);
        int readerIndex = prefs.getInt(SettingsActivity.KEY_READER, 0);
        if (readerIndex >= 0 && readerIndex < READER_IDS.length) {
            readerApiId = READER_IDS[readerIndex];
        }
    }

    // ===== جلب وتخزين آيات السورة من الـ API =====
    private void fetchAndStoreVerses() {
        new Thread(() -> {
            try {
                for (int verse = startVerse; verse <= endVerse; verse++) {
                    String apiUrl = "https://api.alquran.cloud/v1/ayah/"
                            + surahNumber + ":" + verse;
                    URL url = new URL(apiUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.connect();

                    if (conn.getResponseCode() == 200) {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(conn.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) sb.append(line);
                        reader.close();

                        JSONObject json = new JSONObject(sb.toString());
                        JSONObject data = json.getJSONObject("data");
                        int pageNumber = data.getInt("page");
                        String verseText = data.getString("text");

                        DatabaseHelper db = new DatabaseHelper(MemorizationActivity.this);
                        db.addVerse(surahNumber, verse, pageNumber, verseText);
                        db.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(MemorizationActivity.this,
                                "خطأ في الاتصال بالإنترنت. تأكد من الاتصال وحاول مجدداً.", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private boolean isDarkMode() {
        return getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE)
                .getBoolean(SettingsActivity.KEY_NIGHT_MODE, false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRepeat();
        // حفظ التقدم عند الخروج مع النطاق والتكرار
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.saveProgress(surahNumber, currentVerse, startVerse, endVerse, totalRepeats, currentRepeat, surahNameAr);
        dbHelper.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRepeat();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}