package com.smartpantry.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.app.R;
import com.smartpantry.app.models.PantryItem;

import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnItemActionListener {
        void onEditClick(PantryItem item);
        void onDeleteClick(PantryItem item);
    }

    private List<PantryItem> items;
    private final OnItemActionListener listener;
    private boolean showExpiryAlerts = true;

    public PantryAdapter(List<PantryItem> items, OnItemActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void updateList(List<PantryItem> newItems, boolean showExpiryAlerts) {
        this.items = newItems;
        this.showExpiryAlerts = showExpiryAlerts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.tvName.setText(item.getIngredientName());

        String qty = formatQuantity(item.getQuantity()) + " " + item.getUnit();
        holder.tvQuantity.setText(qty);

        // Visual Polish: Category Strip and Icon
        int color = getCategoryColor(item.getIngredientName());
        holder.categoryStrip.setBackgroundColor(color);
        holder.tvCategoryIcon.setText(getCategoryIcon(item.getIngredientName()));

        if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty() && showExpiryAlerts) {
            holder.tvExpiry.setVisibility(View.VISIBLE);
            holder.tvExpiry.setText("Expires: " + item.getExpiryDate());
            updateExpiryBadge(holder.tvExpiryBadge, item.getExpiryDate());
        } else {
            holder.tvExpiry.setVisibility(View.GONE);
            holder.tvExpiryBadge.setVisibility(View.GONE);
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    private void updateExpiryBadge(TextView badge, String dateStr) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
            java.util.Date expiry = sdf.parse(dateStr);
            java.util.Date today = new java.util.Date();
            
            long diff = expiry.getTime() - today.getTime();
            long days = diff / (1000 * 60 * 60 * 24);

            badge.setVisibility(View.VISIBLE);
            if (days < 0) {
                badge.setText("Expired");
                badge.setTextColor(ContextCompat.getColor(badge.getContext(), R.color.status_red));
                badge.setBackgroundResource(R.drawable.badge_matched); // Reuse shape
            } else if (days < 3) {
                badge.setText("Expiring");
                badge.setTextColor(ContextCompat.getColor(badge.getContext(), R.color.status_amber));
            } else {
                badge.setText("Fresh");
                badge.setTextColor(ContextCompat.getColor(badge.getContext(), R.color.status_green));
            }
        } catch (Exception e) {
            badge.setVisibility(View.GONE);
        }
    }

    private int getCategoryColor(String name) {
        String n = name.toLowerCase();
        if (n.contains("chicken") || n.contains("beef") || n.contains("meat") || n.contains("steak") || n.contains("lamb")) return 0xFFEF5350; // Red
        if (n.contains("tuna") || n.contains("fish") || n.contains("salmon") || n.contains("shrimp")) return 0xFF42A5F5; // Blue
        if (n.contains("tomato") || n.contains("onion") || n.contains("pepper") || n.contains("carrot") || n.contains("vegetable") || n.contains("potato") || n.contains("lettuce") || n.contains("garlic")) return 0xFF66BB6A; // Green
        if (n.contains("milk") || n.contains("cheese") || n.contains("egg") || n.contains("butter") || n.contains("cream") || n.contains("yogurt")) return 0xFFFFCA28; // Amber
        if (n.contains("pasta") || n.contains("rice") || n.contains("bread") || n.contains("flour") || n.contains("noodle") || n.contains("wrap") || n.contains("tortilla")) return 0xFF8D6E63; // Brown
        if (n.contains("oil") || n.contains("sauce") || n.contains("mayonnaise") || n.contains("ketchup") || n.contains("salt") || n.contains("spice") || n.contains("sugar")) return 0xFF78909C; // Grey
        return 0xFF26A69A; // Teal default
    }

    private String getCategoryIcon(String name) {
        String n = name.toLowerCase();
        if (n.contains("chicken") || n.contains("beef") || n.contains("meat") || n.contains("steak") || n.contains("lamb")) return "🥩";
        if (n.contains("tuna") || n.contains("fish") || n.contains("salmon") || n.contains("shrimp")) return "🐟";
        if (n.contains("tomato") || n.contains("carrot") || n.contains("potato") || n.contains("garlic")) return "🥕";
        if (n.contains("onion")) return "🧅";
        if (n.contains("pepper")) return "🫑";
        if (n.contains("lettuce") || n.contains("vegetable") || n.contains("cabbage") || n.contains("spinach")) return "🥗";
        if (n.contains("egg")) return "🥚";
        if (n.contains("milk") || n.contains("cream") || n.contains("yogurt")) return "🥛";
        if (n.contains("cheese")) return "🧀";
        if (n.contains("butter")) return "🧈";
        if (n.contains("pasta") || n.contains("noodle")) return "🍝";
        if (n.contains("rice")) return "🍚";
        if (n.contains("bread") || n.contains("toast")) return "🍞";
        if (n.contains("wrap") || n.contains("tortilla")) return "🌯";
        if (n.contains("oil")) return "🧴";
        if (n.contains("mayonnaise") || n.contains("mayo")) return "🥣"; // Creamy dressing in a bowl
        if (n.contains("sauce") || n.contains("ketchup") || n.contains("mustard")) return "🍶"; // Condiment bottle
        if (n.contains("salt") || n.contains("spice") || n.contains("sugar") || n.contains("powder")) return "🧂";
        if (n.contains("can") || n.contains("tin")) return "🥫";
        return "📦"; // General pantry box
    }

    private String formatQuantity(double qty) {
        if (qty == (long) qty) return String.valueOf((long) qty);
        return String.valueOf(qty);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity, tvExpiry, tvExpiryBadge, tvCategoryIcon;
        ImageButton btnEdit, btnDelete;
        View categoryStrip;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvIngredientName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
            tvExpiryBadge = itemView.findViewById(R.id.tvExpiryBadge);
            tvCategoryIcon = itemView.findViewById(R.id.tvCategoryIcon);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            categoryStrip = itemView.findViewById(R.id.viewCategoryStrip);
        }
    }
}
