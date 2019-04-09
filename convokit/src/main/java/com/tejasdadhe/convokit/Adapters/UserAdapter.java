package com.tejasdadhe.convokit.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tejasdadhe.convokit.Modals.User;
import com.tejasdadhe.convokit.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    private List<User> moviesList;
    private ItemEventsListener itemEventsListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;

        public MyViewHolder(View view) {
            super(view);
            userName =  view.findViewById(R.id.userName);

        }
    }


    public UserAdapter(List<User> moviesList,ItemEventsListener itemEventsListener) {
        this.moviesList = moviesList;
        this.itemEventsListener = itemEventsListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        User user = moviesList.get(position);
        holder.userName.setText(user.getDisplayName());
        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemEventsListener.ItemOnClickListener(position);
            }
        });

        holder.userName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemEventsListener.ItemLongClickListener(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public interface ItemEventsListener
    {
        void ItemOnClickListener(int position);
        void ItemLongClickListener(int position);
    }

}
