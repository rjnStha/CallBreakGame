package edu.ramapo.rshresth.callbreak;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        //do nothing
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        //onclick listener to newGame button
        Button buttonHome = findViewById(R.id.home);
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //what the button does on click
                NewGame();
            }
        });

        displayScore(bundle);
    }

    /**/
	/*
	displayScore()
	NAME
        displayScore
	SYNOPSIS
		protected void displayScore(Bundle bundle)
		bundle --> Bundle from GameActivity containing score arrays
	DESCRIPTION
		The function will display scores of all player from all round and
		display the winner in textBox
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        9:06pm 07/29/2019
	*/
    /**/
    public void displayScore(Bundle bundle){

        //dynamically updating the score in the Score Board
        TableLayout ll = (TableLayout) findViewById(R.id.scoreResult);
        for (int i = 0; i <Game.MAX_ROUND; i++) {

            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            for (int j = 0; j < 4; j++) {
                String key = "scoreBoard" + j;
                double scoreBoard[] = bundle.getDoubleArray(key);
                TextView playerScore = new TextView(this);
                playerScore.setText("" + scoreBoard[i]);
                TableRow.LayoutParams params = new TableRow.LayoutParams(180, TableRow.LayoutParams.WRAP_CONTENT);
                playerScore.setLayoutParams(params);
                playerScore.setGravity(Gravity.CENTER);
                row.addView(playerScore);
            }
            ll.addView(row);
        }

        //list of all the final score textView
        TextView[] finalScore = {findViewById(R.id.totalScore0),findViewById(R.id.totalScore1),
                findViewById(R.id.totalScore2),findViewById(R.id.totalScore3)};

        //Find the highest score and find the player with the higest score
        double highestScore = -Double.MAX_VALUE;
        int highestScorePlayer = 0;
        for(int i =0; i<4;i++){
            String key = "scoreBoard" + i;
            double scoreBoard[] = bundle.getDoubleArray(key);
            double currentScore = scoreBoard[Game.MAX_ROUND];
            if(currentScore>highestScore){
                highestScore = currentScore;
                highestScorePlayer = i;
            }
            finalScore[i].setText(""+currentScore);
        }

        //Display the winner of the game
        TextView winner = (TextView) findViewById(R.id.winnner);
        winner.setText("Winnner : ");
        switch (highestScorePlayer){
            case 0: winner.append("User");
                break;
            case 1: winner.append("BOT1");
                break;
            case 2: winner.append("BOT2");
                break;
            default: winner.append("BOT3");
        }
    }

    /**/
	/*
	NewGame()
	NAME
        NewGame
	SYNOPSIS
		protected void NewGame()
	DESCRIPTION
	    ends activity and moves to WelcomeActivity
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        9:08pm 07/29/2019
	*/
    /**/
    public void NewGame(){
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }
}
