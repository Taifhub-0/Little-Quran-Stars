package com.example.quranapp_part1;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SurahAdapter extends RecyclerView.Adapter<SurahAdapter.SurahViewHolder> {

    public interface OnSurahClickListener {
        void onSurahClick(SurahModel surah);
    }

    private List<SurahModel>      allSurahs;     // القائمة الكاملة (للبحث)
    private List<SurahModel>      displayList;   // القائمة المعروضة
    private OnSurahClickListener  listener;
    private Context               context;

    public SurahAdapter(Context context, List<SurahModel> surahs, OnSurahClickListener listener) {
        this.context     = context;
        this.allSurahs   = new ArrayList<>(surahs);
        this.displayList = new ArrayList<>(surahs);
        this.listener    = listener;
    }

    // ======= فلترة البحث =======
    public void filter(String query) {
        displayList.clear();
        if (query == null || query.trim().isEmpty()) {
            displayList.addAll(allSurahs);
        } else {
            String q = query.trim().toLowerCase();
            for (SurahModel s : allSurahs) {
                if (s.getNameAr().contains(q) || s.getNameEn().toLowerCase().contains(q)) {
                    displayList.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SurahViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_surah, parent, false);
        return new SurahViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SurahViewHolder holder, int position) {
        SurahModel surah = displayList.get(position);
        holder.bind(surah);
        holder.itemView.setOnClickListener(v -> listener.onSurahClick(surah));
    }

    @Override
    public int getItemCount() { return displayList.size(); }

    // ======= ViewHolder =======
    static class SurahViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber, tvNameAr, tvNameEn, tvType, tvVerseCount;

        SurahViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber     = itemView.findViewById(R.id.tvSurahNumber);
            tvNameAr     = itemView.findViewById(R.id.tvSurahNameAr);
            tvNameEn     = itemView.findViewById(R.id.tvSurahNameEn);
            tvType       = itemView.findViewById(R.id.tvSurahType);
            tvVerseCount = itemView.findViewById(R.id.tvVerseCount);
        }

        void bind(SurahModel s) {
            tvNumber.setText(String.valueOf(s.getNumber()));
            tvNameAr.setText(s.getNameAr());
            tvNameEn.setText(s.getNameEn());
            tvType.setText(s.getType());
            tvVerseCount.setText(s.getVerseCount() + " آية");
        }
    }
}
