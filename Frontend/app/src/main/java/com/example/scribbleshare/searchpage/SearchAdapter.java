package com.example.scribbleshare.searchpage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scribbleshare.MySingleton;
import com.example.scribbleshare.R;
import com.example.scribbleshare.profilepage.OtherProfilePage;
import com.example.scribbleshare.profilepage.ProfilePage;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.Holder> {
    List<SearchModel> searchModels;
    Context context;

    /**
     * Constructor to initialize post models for the search page
     *
     * @param context      Current context
     * @param searchModels List of post models for the search page
     */
    public SearchAdapter(Context context, List<SearchModel> searchModels) {
        this.context = context;
        this.searchModels = searchModels;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_search_result, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        String username = searchModels.get(position).getUsername();
        //int followerCount = searchModels.get(position).getFollowerCount();

        holder.username.setText(username);
        //holder.followerCount.setText(followerCount + "");

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.equals(MySingleton.getInstance(context).getApplicationUser().getUsername())){
                    Intent intent = new Intent(context, ProfilePage.class);
                    context.startActivity(intent);

                }else{
                Intent intent = new Intent(context, OtherProfilePage.class);
                intent.putExtra("username", username);
                context.startActivity(intent);}
            }
        });
    }

    /**
     * This method returns the item count inside the RecyclerView
     *
     * @return Item count inside the RecyclerView
     */
    @Override
    public int getItemCount() {
        return searchModels.size();
    }

    /**
     * RecyclerView ViewHolder necessary for a search result
     */
    class Holder extends RecyclerView.ViewHolder {
        TextView username;
//        TextView followerCount;

        /**
         * Constructor to initialize necessary information for a search result
         *
         * @param itemView Current item view
         */
        public Holder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.search_profile_name);
//            followerCount = itemView.findViewById(R.id.follower_count);
        }
    }
}