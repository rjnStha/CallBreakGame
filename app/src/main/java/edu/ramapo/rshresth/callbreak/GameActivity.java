package edu.ramapo.rshresth.callbreak;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Vector;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{

    //member Variables
    //made static to allow access to MoveCheck
    static Game callBreakGame;

    //variable required to manage the UI display timing
    //set the animation depending upon the turn cycle in a hand round
    private int animationTimeControl;

    /**/
	/*
	onBackPressed()
	NAME
        onBackPressed
	SYNOPSIS
		public void onBackPressed()
	DESCRIPTION
		The function will return to WelcomeActivity when back button pressed
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        8:03pm 07/29/2019
	*/
    /**/
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, WelcomeActivity.class));
    }

    /**/
	/*
	onCreate()
	NAME
        onCreate
	SYNOPSIS
		protected void onCreate(Bundle savedInstanceState)
		savedInstanceState --> Bundle from WelcomeActivity
	DESCRIPTION
		The function will initialize member variables depending upon the
		game status whether a new or a load game
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        8:06pm 07/29/2019
	*/
    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //inspect bundle from previous activity to load either new game or load game
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        //getting the info from prev Activity via bundle
        String gameStatus = bundle.getString("fileName");

        //create new game instance
        callBreakGame = new Game();

        if (gameStatus.equals("newGame")) {
            //display the turn of the player
            TextView displayInfo = findViewById(R.id.announcement);
            displayInfo.append(""+callBreakGame.getCurrentTurnNum());

            //set the time controller to 0
            animationTimeControl = 0;
            //update the hands of the user
            updateUserHands();

            //set the contract num for each player
            //also triggers playGame
            setContractNum();
        }
        else{
            //call the function with argument as filename
            if(!callBreakGame.loadGame(gameStatus)){

                //new intent
                intent = new Intent(this, WelcomeActivity.class);
                //Sending data to Welcome activity
                intent.putExtra("LoadFailed...\n File" + gameStatus+" load fail", "loadSaveFile");
                startActivity(intent);

            }
            else {

                //reset the background of the cards
                resetPlayedCard();

                //load game success
                updateUserHands();
                for(int i=0;i<4;i++){ updateScoreInfo(i); }

                //loadBotCard();
                String playedCards[] = callBreakGame.getCurrentPlayedCards();
                Button[] buttonBot = {findViewById(R.id.opponent1), findViewById(R.id.opponent2),
                        findViewById(R.id.opponent3)};
                for(int i = callBreakGame.getCountHandPlayed();i>0;i--){
                    int index = Math.abs(3-i);
                    String imageName = "card_" + playedCards[index+1].toLowerCase();
                    int resId = getResources().getIdentifier("" + imageName, "drawable", getPackageName());
                    buttonBot[index].setBackgroundResource(resId);
                }

                //set the countHandPlayed value to 0 since user can only save when it is his turn
                callBreakGame.setCurrentTurnNum(0);

            }
            String [] fileName = gameStatus.split("/");
            TextView displayInfo = findViewById(R.id.announcement);
            displayInfo.setText("LoadComplete...\n File "+fileName[fileName.length-1]+" load successful!!");
        }

        //set on click listener to score and save button
        Button btn = findViewById(R.id.save);
        btn.setOnClickListener(this);
        btn = findViewById(R.id.score);
        btn.setOnClickListener(this);

    }

    /**/
	/*
	onClick()
	NAME
        onClick
	SYNOPSIS
		protected void onClick(View view)
		view --> view whose onclick function is to defined
	DESCRIPTION
		The function will define actions to be taken when the given view
		is clicked
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        8:06pm 07/29/2019
	*/
    /**/
    @Override
    public void onClick(View view){
        //get a local variable of the TextView to display info usually when buttons are pressed
        TextView displayInfo = findViewById(R.id.announcement);

        //get a local variable of the selected button
        Button buttonSelected = (Button) view;

        //Check if its human turn
        //to restrict action from buttons
        //button selection restriction solely depends on this condition
        if(view.getId() == R.id.save ) {
            //only allow to save when it is user's turn
            if(callBreakGame.getCurrentTurnNum() != 0) {return;}

            //function to save the game, inputs file name from the user
            inputFileName();
        }
        else if(view.getId() == R.id.score){

            //only allow to view score when it's user's turn
            if(callBreakGame.getCurrentTurnNum() != 0) {return;}
            //Display the dialogue
            dialogScoreBoard(false);
        }
        else{
            //Allow the button press only when it is user's turn
            //need change since its useless
            if(callBreakGame.getCurrentTurnNum() != 0) {return;}

            //get the index of the selected button
            int index = Integer.parseInt(buttonSelected.getResources().getResourceEntryName(buttonSelected.getId()).substring(1));
            //get the card of given index
            String card = callBreakGame.getUserHand().get(index);
            //check if the card is a valid selection
            if(!callBreakGame.validUserHand(card)){
                displayInfo.setText("Please select a valid card(same suit / higher value)");
                return;
            }

            displayInfo.setText(buttonSelected.getText());
            buttonSelected.setOnClickListener(null);

            String imageName = "card_" + card.toLowerCase();
            int resId = getResources().getIdentifier("" + imageName, "drawable", getPackageName());

            //Set the background of the user button
            Button userButton = (Button) findViewById(R.id.user);
            //button[i].setText(imageName);
            userButton.setBackgroundResource(resId);

            //change the turn, increase the count hand played and reset animation control
            callBreakGame.turnChange();
            callBreakGame.setCountHandPlayed(callBreakGame.getCountHandPlayed()+1);
            //set to 1 so that animation restarts in few time after user selection
            animationTimeControl = 1;

            //updates the UI display of the user hand cards
            updateUserHands();

            //resume the hand
            playGame();
        }
    }

    /**/
	/*
	updateUserHands()
	NAME
        updateUserHands
	SYNOPSIS
		public void updateUserHands()
	DESCRIPTION
		The function will update the background of the player's hand cards
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        8:08pm 07/29/2019
	*/
    /**/
    public void updateUserHands(){

        //set respective background to the table cards
        Vector<String> handCards = callBreakGame.getUserHand();


        Button[] buttonTable = {findViewById(R.id.t0), findViewById(R.id.t1),
                findViewById(R.id.t2), findViewById(R.id.t3), findViewById(R.id.t4),
                findViewById(R.id.t5), findViewById(R.id.t6), findViewById(R.id.t7),
                findViewById(R.id.t8), findViewById(R.id.t9), findViewById(R.id.t10),
                findViewById(R.id.t11), findViewById(R.id.t12)};

        //index to access the element of the table vector
        int index = 0;

        //loop to create and set the background of the cards
        for(int j = 0; j< buttonTable.length ;j++) {

            //only set the background of the cards that are on table board
            if(index < handCards.size()) {

                //
                buttonTable[j].setAnimation(null);

                //set action listener first time
                buttonTable[j].setOnClickListener(this);

                //set the background of the buttons with the respective cards
                String imageName = "card_" + handCards.get(index).toLowerCase();
                int resId = getResources().getIdentifier("" + imageName, "drawable", getPackageName());

                buttonTable[index++].setBackgroundResource(resId);



            } else {

                //Animate the empty cards to fade out
                Animation animation = AnimationUtils.loadAnimation(GameActivity.this,R.anim.fadeout);
                buttonTable[j].startAnimation(animation);

                //set action listener to null since the cards are empty
                buttonTable[j].setOnClickListener(null);

                int resId = getResources().getIdentifier("card_empty", "drawable", getPackageName());
                buttonTable[j].setBackgroundResource(resId);


            }

        }
    }

    /**/
	/*
	setContractNum()
	NAME
        setContractNum
	SYNOPSIS
		public void setContractNum()
	DESCRIPTION
		The function will set the contract num for each player
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        8:09pm 07/29/2019
	*/
    /**/
    public void setContractNum(){

        //loop until it is user turn while getting the contract number for other player
        //whose turn is before user
        while(callBreakGame.getCurrentTurnNum() != 0){
            int tempBotNum = callBreakGame.getCurrentTurnNum();
            callBreakGame.setBotContractNum();
            updateScoreInfo(tempBotNum);
        }

        /*
            get the contract num from the user and trigger playGame()
        */

        //local variable for Dialog
        final Dialog d = new Dialog(GameActivity.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_contractinput);

        //prevent closing the dialogue when clicking outside the dialogue box
        d.setCanceledOnTouchOutside(false);
        //prevent closing when clicking back button
        d.setCancelable(false);

        //display the round number when asking for the contract number
        //since contract dialog is displayed before the start of every new round
        TextView roundInfo = (TextView) d.findViewById(R.id.roundInfo);
        int roundNum = callBreakGame.getRound()+1;
        roundInfo.setText("Round "+roundNum);
        if(roundNum == Game.MAX_ROUND){roundInfo.append(" (Last Round)");}

        //NumberPicker
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker);
        //min and max value for the Num picker
        np.setMaxValue(8);
        np.setMinValue(1);

        //set the contract num to 1 as default value
        //required since callBreakGame.setUserContractNum(int) would not be called
        // if user does not change the value in the number picker
        callBreakGame.setUserContractNum(1);

        //onValue change listener to the num picker
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //update the constrain value with the user selected num
                callBreakGame.setUserContractNum(newVal);
            }
        });

        //OK button
        Button b1 = (Button) d.findViewById(R.id.okButton);
        //on click listener to OK button
        b1.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                //change the turn
                //if user is the first one to go --> change turn to get contract num for other players
                //if user is last --> change turn to start the game
                callBreakGame.turnChange();

                //close the dialog
                d.dismiss();

                //update the score and contract info of the player
                updateScoreInfo(0);

                //get the contract number for remaining players after the user
                //Checks if current player has already contracted, true -> all players contracted
                while(!callBreakGame.currentPlayerContracted()){
                    int tempBotNum = callBreakGame.getCurrentTurnNum();
                    callBreakGame.setBotContractNum();
                    updateScoreInfo(tempBotNum);
                }

                //Start the game
                playGame();
            }
        });

        d.show();
    }

    /**/
	/*
	updateScoreInfo()
	NAME
        updateScoreInfo
	SYNOPSIS
		public void updateScoreInfo(int playerNum)
		playerNum --> int representing the player
	DESCRIPTION
		The function will update the info for the player of given number
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        8:09pm 07/29/2019
	*/
    /**/
    public void updateScoreInfo(int playerNum){
        TextView displayInfo;
        String playerInfo = "BOT"+playerNum;

        switch (playerNum) {
            case 0: displayInfo = findViewById(R.id.user_info);
                playerInfo = "USER ";
                break;
            case 1:
                displayInfo = findViewById(R.id.opponent1_info);
                break;
            case 2:
                displayInfo = findViewById(R.id.opponent2_info);
                break;
            default:
                displayInfo = findViewById(R.id.opponent3_info);
        }

        //display the info cotract num and score
        displayInfo.setText(playerInfo+" "+callBreakGame.getContractNum(playerNum)+
                "|"+callBreakGame.getScore(playerNum));
    }

    /**/
	/*
	displayBotCard()
	NAME
        displayBotCard
	SYNOPSIS
		public void displayBotCard(String card)
		card --> the value of card played by the player
	DESCRIPTION
		The function will update the background of the card played by the bot
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        8:11pm 07/29/2019
	*/
    /**/
    public void displayBotCard(String card){
        final Button[] buttonBot = {findViewById(R.id.opponent1), findViewById(R.id.opponent2),
                findViewById(R.id.opponent3)};

        //set the background of the buttons with the respective cards
        String imageName = "card_" + card.toLowerCase();
        final int resId = getResources().getIdentifier("" + imageName, "drawable", getPackageName());

        final int indexButton = callBreakGame.getCurrentTurnNum() - 1;

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                buttonBot[indexButton].setBackgroundResource(resId);
            }
        }, 500*animationTimeControl);
    }

    /**/
	/*
	resetPlayedCard()
	NAME
        resetPlayedCard
	SYNOPSIS
		public void resetPlayedCard()
	DESCRIPTION
		The function will reset all the cards and update the card played by the bot
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        8:12pm 07/29/2019
	*/
    /**/
    public void resetPlayedCard(){
        final Button[] buttonBot = {findViewById(R.id.opponent1), findViewById(R.id.opponent2),
                findViewById(R.id.opponent3),findViewById(R.id.user)};
        for(int i =0;i<4;i++){
            buttonBot[i].setBackground(getDrawable(R.drawable.card_back));
        }
    }

    /**/
	/*
	playGame()
	NAME
        playGame
	SYNOPSIS
		public void playGame()
	DESCRIPTION
		The function will start the Round and checks for the round completion to call
		dialogWinner function
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        8:13pm 07/29/2019
	*/
    /**/
    public void playGame(){

        //problem arises when a one hand round is complete and it is difficult to show user
        //who won the current hand
        //SOLUTION: dialogue specifying who won the hand with an OK button since button would pause the app

        //Check for the end of one complete hand round
        if(callBreakGame.getCountHandPlayed()== 4){
            dialogWinner();
            return;
        }

        //loop until end of the round
        while(callBreakGame.getCurrentTurnNum() != 0){
            displayBotCard(callBreakGame.playBotCard());
            //update the flags after the player makes the move
            // change turn, increase the player num who have already played their hand
            // and increase the animation time
            callBreakGame.turnChange();
            callBreakGame.setCountHandPlayed(callBreakGame.getCountHandPlayed()+1);
            animationTimeControl++;

            //Check for the end of one complete hand round
            if(callBreakGame.getCountHandPlayed()== 4){
                dialogWinner();
                return;
            }

            //Check if the turn after current is user's turn
            //delay the turnChange so as to prevent user from unwanted
            // clicking of the buttons
            if(callBreakGame.getCurrentTurnNum() == 3 ){
                displayBotCard(callBreakGame.playBotCard());
                callBreakGame.setCountHandPlayed(callBreakGame.getCountHandPlayed()+1);
                //Check for the end of one complete hand round
                if(callBreakGame.getCountHandPlayed()== 4){
                    dialogWinner();
                    return;
                }

                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        //change turn to allow user to click buttons
                        callBreakGame.turnChange();
                    }
                }, 500*(animationTimeControl+1));

                return;
            }
        }
    }

    /**/
	/*
	dialogWinner()
	NAME
        dialogWinner
	SYNOPSIS
		public void dialogWinner()
	DESCRIPTION
		The function will display dialog box with the winner of the current hand
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        8:15pm 07/29/2019
	*/
    /**/
    public void dialogWinner(){

        //local variable for Dialog
        final Dialog d = new Dialog(GameActivity.this);
        //no title
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_handwinner);

        //set the the dialog position at fixed position in the screen
        Window window = d.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP | Gravity.START;
        wlp.y = 700;   //y position

        //prevent closing the dialogue when clicking outside the dialogue box
        d.setCanceledOnTouchOutside(false);
        //prevent closing when clicking back button
        d.setCancelable(false);

        //OK button
        Button b1 = (Button) d.findViewById(R.id.okButton);
        //on click listener to OK button
        b1.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                endRoundCheck();
                //close the dialog
                d.dismiss();

            }
        });

        //delay the display to match the animation schedule
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //prints the hands of each player after the move completion
                callBreakGame.printPlayerHand();

                //display the winner card and hand over the turn to the winner
                //display the turn of the player
                TextView displayInfo = (TextView) d.findViewById(R.id.winnerHandText);
                displayInfo.setText("Hand Won By: "+callBreakGame.getWinnerHandPlayer());

                //update the background of the winner Hand card
                Button card = (Button) d.findViewById(R.id.winnerHandCard);
                String imageName = "card_" +callBreakGame.getWinnerHandCard().toLowerCase();
                int resId = getResources().getIdentifier("" + imageName, "drawable", getPackageName());
                card.setBackgroundResource(resId);

                d.show();

            }
        }, 500*animationTimeControl);
    }

    /**/
	/*
	endRoundCheck()
	NAME
        endRoundCheck
	SYNOPSIS
		public void endRoundCheck()
	DESCRIPTION
		The function will update the score after each hand and check for the end of round
        or end of the game. Also will check for winner at the end of the game
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        8:15pm 07/29/2019
	*/
    /**/
    public void endRoundCheck(){
        //updates the score of the winner
        callBreakGame.updateScore();
        //updates the UI with the score info
        //current turn num since the current player got his turn after wining
        //the last hand which was updated when calling getWinnerHandPlayer()
        updateScoreInfo(callBreakGame.getCurrentTurnNum());
        //reset all the card
        resetPlayedCard();
        //set the count hand played to 0
        callBreakGame.setCountHandPlayed(0);
        //set to 0 to restart animation time after clicking ok
        animationTimeControl = 0;

        //Check for the end of round
        //and the end of game
        if(callBreakGame.getUserHand().size() == 0){

            //End of the game
            if(!callBreakGame.endOfRound()){
                endActivity(callBreakGame.getScoreBoard());
                return;
            }

            //display the score board at the end of the round
            dialogScoreBoard(true);

            //since game will continue since dialogScoreBoard() will
            // call playGame()
            return;
        }

        //start next hand
        playGame();
    }

    /**/
	/*
	dialogScoreBoard()
	NAME
        dialogScoreBoard
	SYNOPSIS
		public void dialogScoreBoard(final boolean newRound)
		newRound --> flag to indicate if it is end of the round
	DESCRIPTION
		The function will display dialog box with the score board
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        8:15pm 07/29/2019
	*/
    /**/
    public void dialogScoreBoard(final boolean newRound){
        //Display the dialogue

        //local variable for Dialog
        final Dialog d = new Dialog(GameActivity.this);
        //no title
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_scoreboard);

        //prevent closing the dialogue when clicking outside the dialogue box
        d.setCanceledOnTouchOutside(false);
        //prevent closing when clicking back button
        d.setCancelable(false);

        //dynamically updating the score in the Score Board
        TableLayout ll = (TableLayout) d.findViewById(R.id.scoreBoard);
        for (int i = 0; i <callBreakGame.getRound()+1; i++) {

            TableRow row= new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            for(int j =0;j<4;j++){
                TextView playerScore = new TextView(this);
                playerScore.setText(""+callBreakGame.getScoreBoard()[i][j]);
                TableRow.LayoutParams params = new TableRow.LayoutParams(160, TableRow.LayoutParams.WRAP_CONTENT);
                playerScore.setLayoutParams(params);
                playerScore.setGravity(Gravity.CENTER);
                row.addView(playerScore );
            }
            ll.addView(row);
        }

        //OK button
        Button b1 = (Button) d.findViewById(R.id.okButton);
        //on click listener to OK button
        b1.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                //Check if new round
                if(newRound){
                    //update the hands and input the contract num for the new round

                    //update the hands of the user
                    updateUserHands();
                    //set the contract Num
                    setContractNum();
                }
                //close the dialog
                d.dismiss();

            }
        });

        d.show();
    }

    /**/
	/*
	inputFileName()
	NAME
        inputFileName
	SYNOPSIS
		public void inputFileName()
	DESCRIPTION
		The function will input the file name and save the game
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        8:15pm 07/29/2019
	*/
    /**/
    public void inputFileName(){

        //Dialog box
        final Dialog d = new Dialog(GameActivity.this);
        d.setTitle("Save Game");
        d.setContentView(R.layout.dialog_savegame);

        //EditText to input the filename, final since using inside onClickListener
        final EditText e1 = d.findViewById(R.id.userInputFile);

        //OK button
        Button b1 = d.findViewById(R.id.okSave);

        //since using the same button to save and load
        //changed the text value
        b1.setText("Save");

        final Intent intent = new Intent(this, WelcomeActivity.class);

        //on click listener to OK button
        b1.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {

                //display failed or not
                TextView displayInfo = findViewById(R.id.announcement);

                //Checks if save was successful to and if true
                //ends the activity while passing the file name to Welcome Activity
                if(callBreakGame.saveGame(""+e1.getText())){

                    callBreakGame.loadGame(""+e1.getText());

                    startActivity(intent);

                    //close the screen/end the activity
                    //Sending data to as intent
                    intent.putExtra("SaveComplete...\n Game saved as " + e1.getText() + ".txt","loadSaveFile");

                }
                else{

                    displayInfo.setText("SaveFailure," + e1.getText() + ".txt was not saved");

                }

                //close the dialog
                d.dismiss();

            }
        });

        d.show();
    }

    /**/
	/*
	endActivity()
	NAME
        endActivity
	SYNOPSIS
		public void endActivity(double scoreBoard[][])
		scoreBoard --> the 2D array with score of all players from each round
	DESCRIPTION
		The function will end the activity and create new intent to ResultActivity.
		It will also pass the score info to ResultActivity.
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        8:15pm 07/29/2019
	*/
    /**/
    public void endActivity(double scoreBoard[][]){
        final Intent intent = new Intent(this, ResultActivity.class);
        //Sending data to result activity

        //parse the array of array into 4 arrays each containing
        // individual player score
        for(int i=0;i<4;i++){
            String scoreBoardName = "scoreBoard"+i;
            double scoreBoardPlayer[] = new double[scoreBoard.length+1];

            //get scores from each round for player of index i
            double totalScore = 0;
            for(int j=0;j<scoreBoard.length;j++){
                scoreBoardPlayer[j] = scoreBoard[j][i];
                totalScore += scoreBoardPlayer[j];
            }
            //rounding to 1 decimal point because of the nature Java stores a double value
            scoreBoardPlayer[scoreBoard.length] = Math.round(totalScore * 10) / 10.0;
            //put the intent
            intent.putExtra(scoreBoardName,scoreBoardPlayer);

        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        }, 200);

    }
}
