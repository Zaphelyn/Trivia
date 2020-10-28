package com.example.trivia.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trivia.R;
import com.example.trivia.model.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private ArrayList<Player> playerList;


    public PlayerAdapter(ArrayList<Player> playerList) {
        this.playerList = playerList;
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player,parent,false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        holder.bind(playerList.get(position));

    }



    static class PlayerViewHolder extends RecyclerView.ViewHolder{

        private TextView nameText;
        private TextView scoreText;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            findViews();
        }

        public void findViews(){
            nameText = itemView.findViewById(R.id.text_view_leader_board_name);
            scoreText = itemView.findViewById(R.id.text_view_leader_board_score);
        }

        public void bind(Player player){
            nameText.setText(player.getName());
            scoreText.setText(String.valueOf(player.getScore()));

        }


    }

}
