package com.example.blogapp.Activites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blogapp.R;

import java.util.List;

public class Comment_Adapter extends RecyclerView.Adapter<Comment_Adapter.commentAdapter> {

    List<comment> mData;
    Context mContext;

    public Comment_Adapter(List<comment> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public commentAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.comment_row,parent,false);
        return new Comment_Adapter.commentAdapter(row);
    }

    @Override
    public void onBindViewHolder(@NonNull commentAdapter holder, int position) {

        holder.uname.setText(mData.get(position).getUname().toString());
        holder.ucomment.setText(mData.get(position).getContent());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class commentAdapter extends RecyclerView.ViewHolder{

        TextView uname, ucomment;

        public commentAdapter(@NonNull View itemView) {
            super(itemView);

            uname = itemView.findViewById(R.id.comment_uname);
            ucomment = itemView.findViewById(R.id.comment_content);

        }
    }
}
