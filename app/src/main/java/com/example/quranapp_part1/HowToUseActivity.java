package com.example.quranapp_part1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class HowToUseActivity extends BaseActivity {

    // بيانات الصفحات
    private final String[] emojis = {"📖", "⚙️", "🔊", "⭐"};
    private final String[] titles = {
            "اختر سورة",
            "حدد الآيات",
            "استمع وكرر",
            "اختبر نفسك"
    };
    private final String[] descs = {
            "اضغط \"ابدأ الحفظ\" واختر\nالسورة التي تريد حفظها",
            "حدد من أي آية إلى أي آية\nواختر عدد مرات التكرار",
            "اضغط زر التشغيل واستمع\nللآيات تتكرر تلقائياً",
            "بعد الحفظ اضغط \"ابدأ الاختبار\"\nوشوف كم حفظت!"
    };

    private ViewPager2 viewPager;
    private LinearLayout dotsLayout;
    private Button btnNext;
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_use);

        viewPager  = findViewById(R.id.viewPagerHowTo);
        dotsLayout = findViewById(R.id.dotsLayout);
        btnNext    = findViewById(R.id.btnNext);

        // ربط الـ Adapter
        viewPager.setAdapter(new SlideAdapter());

        // رسم النقاط
        setupDots(0);

        // مراقبة تغيير الصفحة
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                setupDots(position);
                if (position == emojis.length - 1) {
                    btnNext.setText("ابدأ الآن! 🚀");
                } else {
                    btnNext.setText("التالي ←");
                }
            }
        });

        // زر التالي / إنهاء
        btnNext.setOnClickListener(v -> {
            if (currentPage < emojis.length - 1) {
                viewPager.setCurrentItem(currentPage + 1, true);
            } else {
                // رجوع للرئيسية
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    // ===== رسم النقاط =====
    private void setupDots(int activeIndex) {
        dotsLayout.removeAllViews();
        for (int i = 0; i < emojis.length; i++) {
            TextView dot = new TextView(this);
            dot.setText("●");
            dot.setTextSize(14);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);

            if (i == activeIndex) {
                dot.setTextColor(getResources().getColor(R.color.green_primary, null));
                dot.setTextSize(18);
            } else {
                dot.setTextColor(getResources().getColor(R.color.text_gray, null));
            }
            dotsLayout.addView(dot);
        }
    }

    // ===== Adapter الصفحات =====
    private class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.SlideViewHolder> {

        @NonNull
        @Override
        public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_how_to_slide, parent, false);
            return new SlideViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
            holder.tvEmoji.setText(emojis[position]);
            holder.tvTitle.setText(titles[position]);
            holder.tvDesc.setText(descs[position]);
        }

        @Override
        public int getItemCount() {
            return emojis.length;
        }

        class SlideViewHolder extends RecyclerView.ViewHolder {
            TextView tvEmoji, tvTitle, tvDesc;

            SlideViewHolder(@NonNull View itemView) {
                super(itemView);
                tvEmoji = itemView.findViewById(R.id.tvSlideEmoji);
                tvTitle = itemView.findViewById(R.id.tvSlideTitle);
                tvDesc  = itemView.findViewById(R.id.tvSlideDesc);
            }
        }
    }
}