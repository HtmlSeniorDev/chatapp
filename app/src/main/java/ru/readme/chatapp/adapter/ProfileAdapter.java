package ru.readme.chatapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import ru.readme.chatapp.R;
import ru.readme.chatapp.object.ProfileElement;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileHolder> {

    private Context context;
    private List<ProfileElement> elements = new ArrayList<>();

    public ProfileAdapter(Context context) {
        this.context = context;
    }

    public void clear() {
        int size = elements.size();
        elements = new ArrayList<>();
        notifyItemRangeRemoved(0, size);
    }

    public void add(ProfileElement rr) {
        elements.add(rr);
        notifyItemInserted(elements.size() - 1);
    }

    @Override
    public ProfileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.profile_info_element, null);
        v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ProfileHolder(v);
    }

    @Override
    public void onBindViewHolder(ProfileHolder holder, int position) {
        holder.tvName.setText(elements.get(position).getName());
        holder.tvValue.setText(elements.get(position).getValue());
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public static class ProfileHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvValue;
        View v;

        public ProfileHolder(View itemView) {
            super(itemView);
            v = itemView;
            tvName = (TextView) v.findViewById(R.id.tv_profile_info_element_name);
            tvValue = (TextView) v.findViewById(R.id.tv_profile_info_element_value);
        }
    }

}
