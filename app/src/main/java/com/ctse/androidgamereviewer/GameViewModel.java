package com.ctse.androidgamereviewer;

import android.app.Application;

import com.ctse.androidgamereviewer.data.GameRepository;
import com.ctse.androidgamereviewer.data.entities.Game;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

/**
 * Abstracts Data handling logic out of the MainActivity UI controller.
 * Consists of a gameRepository which handles CRUD operations at a high level.
 * <p>
 * See <a href="https://developer.android.com/topic/libraries/architecture/viewmodel">
 * Documentation </a> for more details.
 */
public class GameViewModel extends AndroidViewModel {

    private GameRepository gameRepository;
    private LiveData<List<Game>> allGames;

    public GameViewModel(@NonNull Application application) {
        super(application);
        gameRepository = new GameRepository(application);
        allGames = gameRepository.getAllGames();
    }

    public void insert(Game game) {
        gameRepository.insert(game);
    }

    public void update(Game game) {
        gameRepository.update(game);
    }

    public void delete(Game game) {
        gameRepository.delete(game);
    }

    public LiveData<List<Game>> getAllGames() {
        return allGames;
    }

    public GameRepository getGameRepository() {
        return gameRepository;
    }
}
