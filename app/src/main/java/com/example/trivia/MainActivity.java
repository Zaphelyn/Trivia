package com.example.trivia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.model.Player;
import com.example.trivia.model.Question;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView questionCounter;
    private TextView questionText;
    private Button buttonTrue;
    private Button buttonFalse;
    private CardView cardViewQuestions;
    private TextView textViewScore;
    private TextView textViewHighScore;
    private TextView textViewAttemptsRemaining;
    private int currentQuestionIndex = 0;
    private int currentScore = 0;
    private int highScore;
    private int triesLeft = 3;
    private List<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setButtonOnClickListeners();
        getSharedPrefs();

        if (savedInstanceState == null){
            getQuestions();
        } else {
            Gson gson = new Gson();
            String response = savedInstanceState.getString("questions");
            questions = gson.fromJson(response, new TypeToken<List<Question>>(){}.getType());
            assert questions != null;
            questionText.setText(questions.get(currentQuestionIndex).getAnswer());
        }

        setCounter();



        boolean checkForPlayerListReset = getIntent().getBooleanExtra("resetHighScore", false);
        if (checkForPlayerListReset){
            highScore = 0;
            updateHighScore();
        }

    }

    private void getSharedPrefs() {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        String highScoreText = "High Score: " + appSharedPrefs.getInt("highScore", 0);
        highScore = appSharedPrefs.getInt("highScore", 0);
        textViewHighScore.setText(highScoreText);
    }

    private void setSharedPrefs() {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        prefsEditor.putInt("highScore", highScore);
        prefsEditor.apply();
    }

    @Override
    protected void onDestroy() {
        setSharedPrefs();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        setSharedPrefs();
        super.onBackPressed();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentQuestionIndex = savedInstanceState.getInt("currentQuestionIndex");
        textViewScore.setText(savedInstanceState.getString("currentScoreText"));
        currentScore = savedInstanceState.getInt("currentScore");
        textViewHighScore.setText(savedInstanceState.getString("highScoreText"));
        highScore = savedInstanceState.getInt("highScore");
        triesLeft = savedInstanceState.getInt("triesLeft");

        Gson gson = new Gson();
        String response = savedInstanceState.getString("questions");
        questions = gson.fromJson(response, new TypeToken<List<Question>>(){}.getType());
        updateQuestion();
        updateAttempts();
    }

    /*private void getBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentScore = savedInstanceState.getInt("currentScore");
            highScore = savedInstanceState.getInt("highScore");
            currentQuestionIndex = savedInstanceState.getInt("currentQuestionIndex");
            triesLeft = savedInstanceState.getInt("triesLeft");
            updateScore();
            updateHighScore();
        }
    }*/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentScoreText", textViewScore.getText().toString().trim());
        outState.putInt("currentScore", currentScore);
        outState.putString("highScoreText", textViewHighScore.getText().toString().trim());
        outState.putInt("highScore", highScore);
        outState.putInt("currentQuestionIndex", currentQuestionIndex);
        outState.putInt("triesLeft", triesLeft);


        Gson gson = new Gson();
        String jsonQuestions = gson.toJson(questions);
        outState.putString("questions", jsonQuestions);
    }


    private void findViews() {
        questionCounter = findViewById(R.id.text_view_counter);
        questionText = findViewById(R.id.text_view_questions);
        buttonTrue = findViewById(R.id.button_true);
        buttonFalse = findViewById(R.id.button_false);
        cardViewQuestions = findViewById(R.id.card_view_questions);
        textViewScore = findViewById(R.id.text_view_score);
        textViewHighScore = findViewById(R.id.text_view_high_score);
        textViewAttemptsRemaining = findViewById(R.id.text_view_attempts_remaining);
    }

    private void setButtonOnClickListeners() {
        buttonTrue.setOnClickListener(this);
        buttonFalse.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_true:
                checkAnswer(true);
                break;
            case R.id.button_false:
                checkAnswer(false);
                break;
        }

    }



    private void getQuestions() {
        questions = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                Collections.shuffle(questionArrayList);
                questionText.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                setCounter();
                //Log.d("JSON", "processFinished: " + questionArrayList);
            }
        });
    }

    private void updateQuestion() {
        questionText.setText(questions.get(currentQuestionIndex).getAnswer());
        setCounter();
    }

    private void updateScore() {
        String scoreText = "Score: " + currentScore;
        textViewScore.setText(scoreText);
    }

    private void updateHighScore(){
        String highScoreText = "High Score: " + highScore;
        textViewHighScore.setText(highScoreText);
    }

    private void updateAttempts(){
        String attemptsString = "Attempts remaining: " + triesLeft;
        textViewAttemptsRemaining.setText(attemptsString);
    }

    private void checkHighScore() {
        if (currentScore > highScore) {
            highScore = currentScore;
        }
    }

    private void setCounter() {
        String counterText = "Question: " + (currentQuestionIndex + 1) + "/" + (questions.size() + 1);
        questionCounter.setText(counterText);
    }




    private void checkAnswer(boolean userChooseCorrect) {
        boolean answerIsTrue = questions.get(currentQuestionIndex).isAnswerTrue();


        if (userChooseCorrect == answerIsTrue) {
            currentScore++;
            currentQuestionIndex = (currentQuestionIndex + 1) % questions.size();
            fadeView();
            makeDing();
            updateScore();
            updateQuestion();
        } else {
            currentQuestionIndex = (currentQuestionIndex + 1) % questions.size();
            shakeAnimation();
            makeBuzzer();
            updateQuestion();
            triesLeft--;
            updateAttempts();
            if (triesLeft == 0) {
                checkHighScore();
                Toast.makeText(MainActivity.this, "Game Over!", Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                intent.putExtra("score", currentScore);
                intent.putExtra("highScore", highScore);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);
        cardViewQuestions.startAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardViewQuestions.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardViewQuestions.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void fadeView() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        cardViewQuestions.startAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardViewQuestions.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardViewQuestions.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void makeBuzzer() {
        MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.buzzer);
        mediaPlayer.start();
    }

    private void makeDing() {
        MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.ding);
        mediaPlayer.start();
    }


}