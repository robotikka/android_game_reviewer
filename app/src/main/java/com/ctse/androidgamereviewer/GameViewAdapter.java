package com.ctse.androidgamereviewer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ctse.androidgamereviewer.data.entities.Game;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * To feed all game data to the list in the MainActivity. This object creates views for items,
 * and replaces the content of some of the views with new items when the original item is no longer
 * visible. It is a controller for the RecyclerView.
 *
 * <a href="https://developer.android.com/guide/topics/ui/layout/recyclerview"> Documentation</a>
 *
 * @see androidx.recyclerview.widget.RecyclerView.Adapter
 */
public class GameViewAdapter extends RecyclerView.Adapter<GameViewAdapter.GameHolder> {

    private List<Game> games = new ArrayList<>();
    private Context mContext;

    public static final String EXTRA_POSITION = "com.ctse.androidgamereviewer.POSITION";
    public static final String EXTRA_GAME_ID = "com.ctse.androidgamereviewer.GAME_ID";

    public GameViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public GameHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new GameHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GameHolder holder, final int position) {
        final Game currentGame = games.get(position);
        holder.tvTitle.setText(currentGame.getTitle());
        holder.tvDescription.setText(currentGame.getGenre());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewGameDetailsActivity.class);
                intent.putExtra(EXTRA_POSITION, position);
                intent.putExtra(EXTRA_GAME_ID, currentGame.get_id());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    void setGames(List<Game> games) {
        this.games = games;
        notifyDataSetChanged();
    }

    /**
     *  Provides a reference class for each individual item in the list.
     */
    class GameHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvDescription;

        public GameHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.text_view_game_title);
            tvDescription = itemView.findViewById(R.id.text_view_genre);
        }
    }
}
