package com.example.trivia;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trivia.controller.PlayerAdapter;
import com.example.trivia.model.Player;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeScreen extends AppCompatActivity {

    RecyclerView leaderBoard;
    private EditText name;
    private Button startGame;
    private ArrayList<Player> playerList;
    private String nameText;
    private final int REQUEST_CODE = 763;
    private PlayerAdapter adapter;
    private ConstraintLayout homeScreenLayout;
    private boolean isDarkModeOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        findViews();
        getSharedPrefs();

        if(savedInstanceState != null){
            nameText = savedInstanceState.getString("name");
            playerList = savedInstanceState.getParcelableArrayList("playerList");
        }

        getPlayerList();
        setPlayerAdapter();
        setStartGameOnClickListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.settings_menu, menu);
        if (isDarkModeOn){
            menu.findItem(R.id.menu_item_dark_mode).setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            menu.findItem(R.id.menu_item_dark_mode).setChecked(false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_item_dark_mode:
                if (item.isChecked()){
                    item.setChecked(false);
                    isDarkModeOn = false;
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                else {
                    item.setChecked(true);
                    isDarkModeOn = true;
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                break;
            case R.id.menu_item_reset_leaderboard:
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Reset leader board")
                        .setMessage("Are you sure you want to reset the leader board?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clearPlayerList();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStop() {
        super.onStop();
        setSharedPrefs();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", nameText);
        outState.putParcelableArrayList("playerList",playerList);
    }

    private void getSharedPrefs() {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        String response = appSharedPrefs.getString("playerList", "");
        isDarkModeOn = appSharedPrefs.getBoolean("isDarkModeOn", false);



        Gson gson = new Gson();
        playerList = gson.fromJson(response, new TypeToken<List<Player>>(){}.getType());
    }

    private void setSharedPrefs() {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String jsonPlayers = gson.toJson(playerList);
        prefsEditor.putString("playerList", jsonPlayers);
        prefsEditor.putBoolean("isDarkModeOn", isDarkModeOn);
        prefsEditor.apply();
    }


    private void findViews() {
        leaderBoard = findViewById(R.id.recycler_view_leader_board);
        name = findViewById(R.id.edit_text_name);
        startGame = findViewById(R.id.button_start_game);
        homeScreenLayout = findViewById(R.id.constraint_layout_home_screen);
    }

    private ArrayList<Player> getPlayerList() {
        if (playerList == null){
            playerList = new ArrayList<>();
        }
        return  playerList;
    }

    private void clearPlayerList() {
        getPlayerList().clear();
        adapter.notifyDataSetChanged();
    }

    private void setPlayerAdapter() {
        adapter = new PlayerAdapter(playerList);
        leaderBoard.setAdapter(adapter);
    }



    private void setStartGameOnClickListener() {
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameText = name.getText().toString().trim();
                Intent startGame = new Intent(HomeScreen.this, MainActivity.class);
                if (playerList.size() == 0){
                    startGame.putExtra("resetHighScore",true);
                }
                startActivityForResult(startGame,REQUEST_CODE);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            assert data != null;
            Player player = new Player();
            player.setName(nameText);
            player.setScore(data.getIntExtra("score",0));
            int highScore = data.getIntExtra("highScore",0);

            if (player.getScore() == highScore){
                getPlayerList().add(player);
                Collections.sort(getPlayerList());
                adapter.notifyDataSetChanged();
            }

            //getPlayerList().add(player);
            //Collections.sort(getPlayerList());
            //adapter.notifyDataSetChanged();

        }


    }
}