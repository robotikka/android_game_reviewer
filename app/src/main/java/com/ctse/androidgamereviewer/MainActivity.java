package com.ctse.androidgamereviewer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ctse.androidgamereviewer.data.entities.Game;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.bson.types.ObjectId;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * This is the main activity of the app and will be the activity which is shown on launch.
 * It consists of a RecyclerView which contains a list of games and their respective reviews
 * The games are stored on a local 'Room' database as well as a remote mongoDB database.
 * The game list can be updated by swiping down on the SwipeRefreshLayout
 *
 * @see androidx.swiperefreshlayout.widget.SwipeRefreshLayout
 * basic CRUD operations can be carried out on games.
 * <p>
 * This app follows the application architecture guidelines found in the official android
 * documentation to create a robust, production quality application
 * <p>
 * See <a href="https://developer.android.com/jetpack/docs/guide">
 * android official guide to app architecture
 * </a> for more information.
 * <p>
 * This application uses the following components from the android architecture components
 * @see androidx.lifecycle.LiveData
 * @see androidx.room.Room
 * @see androidx.lifecycle.ViewModel
 * <p>
 * Authentication is handled using the firebaseUI library.
 * See the <a href="https://firebase.google.com/docs/auth">
 * official Firebase documentation
 * </a> for more information.
 */
public class MainActivity extends AppCompatActivity {

    public static final int ADD_GAME_REQUEST = 2;
    public static final int LOGIN_REQUEST = 1995;

    private GameViewModel gameViewModel;
    private ReviewViewModel reviewViewModel;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseUser user;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.login_menu_item:
                openLoginActivity();
                return true;
            case R.id.logout_menu_item:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (null == user) {
            menu.findItem(R.id.logout_menu_item).setVisible(false);
            menu.findItem(R.id.login_menu_item).setVisible(true);
        } else {
            menu.findItem(R.id.logout_menu_item).setVisible(true);
            menu.findItem(R.id.login_menu_item).setVisible(false);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = FirebaseAuth.getInstance().getCurrentUser();

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                gameViewModel.getGameRepository().refreshData(swipeRefreshLayout);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final GameViewAdapter adapter = new GameViewAdapter(this);
        recyclerView.setAdapter(adapter);

        reviewViewModel = ViewModelProviders.of(this).get(ReviewViewModel.class);
        reviewViewModel.getReviewRepository().refreshReviews();

        gameViewModel = ViewModelProviders.of(this).get(GameViewModel.class);
        gameViewModel.getAllGames().observe(this, new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                adapter.setGames(games);
            }
        });

        FloatingActionButton addGameButton = findViewById(R.id.button_add_game);
        addGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = FirebaseAuth.getInstance().getCurrentUser();

                if (null != user) {
                    // Name, email address
                    Log.d("gameApp", user.getDisplayName());
                    Log.d("gameApp", user.getEmail());
                    Intent intent = new Intent(MainActivity.this, AddGameActivity.class);
                    startActivityForResult(intent, ADD_GAME_REQUEST);
                } else {
                    Toast.makeText(MainActivity.this, "you must be logged in",
                            Toast.LENGTH_SHORT).show();
                    openLoginActivity();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_GAME_REQUEST && resultCode == RESULT_OK) {
            addGame(data);
        } else if (requestCode == LOGIN_REQUEST && resultCode == RESULT_OK) {
            this.invalidateOptionsMenu();
            this.supportInvalidateOptionsMenu();
        } else {
            Toast.makeText(this, "Game not saved", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method adds a game to the database using the insert() method in the GameViewModel
     *
     * @param data is an Intent containing information to be added to the game
     * @see GameViewModel
     */
    private void addGame(@Nullable Intent data) {

        String title = data.getStringExtra(AddGameActivity.EXTRA_TITLE);
        String description = data.getStringExtra(AddGameActivity.EXTRA_DESCRIPTION);
        String releaseDate = data.getStringExtra(AddGameActivity.EXTRA_RELEASE_DATE);
        String image = data.getStringExtra(AddGameActivity.EXTRA_IMAGE);

        Game game = new Game();
        ObjectId objectId = new ObjectId();

        game.setTitle(title);
        game.setGenre(description);
        game.setRelease_date(releaseDate);
        game.setImage(image);
        game.set_id(objectId.toString());

        gameViewModel.insert(game);

        Toast.makeText(this, "Game Saved", Toast.LENGTH_SHORT).show();
    }

    /**
     * method to open the login activity which starts the Fire base login flow
     * the login activity is started using startActivityForResult() because the result is required
     * to invalidate the option menu using InvalidateOptionsMenu() so that onPrepareOptionsMenu()
     * is called and the login menu item is replaced by the logout menu item.
     */
    private void openLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, LOGIN_REQUEST);
    }

    /**
     * This method is used to logout the fire base user. If the logout is successful, the logout
     * options menu item will be replaced with the login options menu item.
     *
     * @see AuthUI
     */
    private void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "sign out successful",
                                    Toast.LENGTH_SHORT).show();
                            MainActivity.this.invalidateOptionsMenu();
                            MainActivity.this.supportInvalidateOptionsMenu();
                        } else {
                            Toast.makeText(MainActivity.this, "sign out unsuccessful",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
