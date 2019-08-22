package edu.ramapo.rshresth.callbreak;

import android.content.Intent;
import android.support.v4.text.HtmlCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TutorialActivity extends AppCompatActivity {

    String htmlText = "<h2>What is CallBreak?</h2>\n" +
            "<p>I would rather you google it</p>\n" +
            "<h2>How to Play?</h2>\n"+
            "<p>"+
            " * Every round will start after distribution of the cards with every player getting 13 cards each.<br>" +
            " * If any player do not get any trump card (spades) or at least one card greater rank than 10, then dealer will distribute the cards again.<br>" +
            " * After successful distribution of cards, players have to give their calls one by one in anti-clockwise direction. <br>" +
            " * Player who call first will throw the first card.<br>" +
            " * Next player should throw the card of greater value than previous card of led suit.<br>" +
            " * If player does not have greater value of card then he can throw any card of led suit.<br>" +
            " * If player does not have led suit then he can throw the trump card.<br>" +
            " * If player does not have led suit and trump card then he can throw card of any suit.<br>" +
            " * In one hand, player who will throw the highest priority card,he will win the hand and get one point.<br>" +
            " * If player not able to get points equals to his call ,then he will get minus points equal to his call.</p>\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        //onclick listener to newGame button
        Button buttonHome = findViewById(R.id.home);
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewGame();
            }
        });

        //set the tutorial text
        TextView htmlToTextView = findViewById(R.id.tutorialHelp);
        htmlToTextView.setText(HtmlCompat.fromHtml(htmlText, 0));
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
