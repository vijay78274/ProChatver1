package com.example.prochatver1.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prochatver1.Acitivities.MainActivity;
import com.example.prochatver1.Acitivities.StatusActivity;
import com.example.prochatver1.Models.Status;
import com.example.prochatver1.Models.User_status;
import com.example.prochatver1.Models.Users;
import com.example.prochatver1.R;
import com.example.prochatver1.databinding.ItemStatusBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {
    Context context;
    ArrayList<User_status> userStatuses;

    public StatusAdapter(Context context, ArrayList<User_status> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status,parent,false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {
        User_status user_status = userStatuses.get(position);
        Status last = user_status.getStatuses().get(user_status.getStatuses().size()-1);
        Glide.with(context).load(last.getImageUrl()).into(holder.binding.image);

        User_status pos =  userStatuses.get(position);
        holder.binding.name.setText(pos.getName());
        long time1 = pos.getLastUpdate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        holder.binding.time.setText(dateFormat.format(new Date(time1)));

        holder.binding.circularStatusView.setPortionsCount(user_status.getStatuses().size());
        holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for(Status status : user_status.getStatuses()){
                    myStories.add(new MyStory(status.getImageUrl()));
                }
                new StoryView.Builder(((StatusActivity)context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(user_status.getName()) // Default is Hidden
                        .setSubtitleText("") // Default is Hidden
                        .setTitleLogoUrl(user_status.getProfileImage()) // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public class StatusViewHolder extends RecyclerView.ViewHolder{
    ItemStatusBinding binding;
        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemStatusBinding.bind(itemView);
        }
    }
}
