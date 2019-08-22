package edu.ramapo.rshresth.callbreak;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.io.File;
import java.util.Date;

public class WelcomeActivity extends AppCompatActivity {
    //the local variable
    Button buttonTemp;

    //required when loading file
    //introduced when adding delete option
    boolean isLoad;
    TextView display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        //inspect bundle from previous activity to load either new game or load game
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        //display the text
        if(intent.hasExtra("loadSaveFile")){
            //getting the info from prev Activity via bundle
            String loadSaveStatus = bundle.getString("loadSaveFile");
            display.setText(loadSaveStatus);
        }

        //onclick listener to newGame button
        buttonTemp = findViewById(R.id.newGame);
        buttonTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //what the button does on click
                endActivity("newGame");

            }
        });

        //onclick listener to load game button
        buttonTemp = findViewById(R.id.loadGame);
        buttonTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get File name and end Activity
                inputFileName();
            }
        });

        //onclick listener to tutorial button
        buttonTemp = findViewById(R.id.tutorial);
        buttonTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutorial();
            }
        });

        //Text view to display info
        display = findViewById(R.id.displayWelcome);
    }

    /**/
	/*
	inputFileName()
	NAME
        inputFileName
	SYNOPSIS
		public void inputFileName()
	DESCRIPTION
		The function will called when load button pressed and will display dialog for user to input file name
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        8:21pm 07/29/2019
	*/
    /**/
    public void inputFileName(){

        //set flag to true as default
        isLoad = true;

        //local variable for Dialog
        final Dialog d = new Dialog(WelcomeActivity.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_loadgame);

        //local variable for delete and load button layout
        final LinearLayout deleteLoadButton = d.findViewById(R.id.deleteLoadButton);

        //save button
        buttonTemp = new Button(this);
        createDeleteLoadButton("Load");
        deleteLoadButton.addView(buttonTemp);

        //delete button
        buttonTemp = new Button(this);
        createDeleteLoadButton("Delete");
        deleteLoadButton.addView(buttonTemp);

        //get the list of files from the external storage
        final String path = Environment.getExternalStorageDirectory().toString()+"/callBreak";

        //.getAbsolutePath();
        System.out.println(path);
        File f = new File(path);
        File files[] = f.listFiles();

        //table layout
        TableLayout table = d.findViewById(R.id.loadGameList);

        //get the number of files
        int count = 0;

        //loop accessing all files in file[]
        for (final File file : files) {

            if(!file.isFile()){continue;}

            //horizontal linear layout to store button and text view
            LinearLayout llayout = new LinearLayout(this);
            llayout.setOrientation(LinearLayout.HORIZONTAL);

            //edit the button and get filename
            final String fileName = path+"/"+file.getName();
            buttonTemp = new Button(this);
            buttonTemp.setText(file.getName());
            buttonTemp.setTextSize(15);
            buttonTemp.setLayoutParams(new LinearLayout.LayoutParams(240, 80));

            //get the date modified in string
            String dateModified = new Date(file.lastModified()).toString();

            //display the date modified
            TextView dateText = new TextView(this);
            dateText.setText(""+dateModified);
            dateText.setTextSize(15);
            dateText.setBackgroundColor(Color.DKGRAY);

            //Button
            final Button b1 = buttonTemp;
            b1.setTextSize(12);
            //listener ending the activity while passing the filename
            b1.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v) {

                    if(isLoad) {

                        //ends activity passing the file name
                        endActivity(fileName);
                        display.setText(file.getName()+" loading...");
                        //close the dialog
                        d.dismiss();

                    } else{

                        //delete the file
                        file.delete();
                        file.isFile();
                        display.setText(file.getName()+" delete success!!!");
                        //close the dialog
                        d.dismiss();
                    }
                }
            });


            //adding view
            llayout.addView(b1);
            llayout.addView(dateText);

            //add view
            table.addView(llayout);

            //increment table height multiple
            count++;
        }

        //Set alignments and margin of table layout
        TableRow.LayoutParams trTable = new TableRow.LayoutParams();
        trTable.height = 80*(count+1);
        table.setLayoutParams(trTable);

        d.show();
    }

    /**/
	/*
	createDeleteLoadButton()
	NAME
        createDeleteLoadButton
	SYNOPSIS
		public void createDeleteLoadButton(final String type)
		type --> type of the button to be created delete or load
	DESCRIPTION
		The function will be called by inputFileName() and will create delete and load button and required edit
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        8:22pm 07/29/2019
	*/
    /**/
    public void createDeleteLoadButton(final String type){

        buttonTemp.setText(type);
        buttonTemp.setTextSize(18);
        buttonTemp.setLayoutParams(new LinearLayout.LayoutParams(320, 100));
        buttonTemp.setTextSize(20);
        //buttonTemp.setBackgroundResource(R.drawable.action);
        buttonTemp.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {

                //Check condition for Load and Delete Button
                if(!isLoad & type.equals("Load")){ isLoad = true; }
                else if(isLoad & type.equals("Delete")){ isLoad = false; }

            }
        });
    }

    /**/
	/*
	endActivity()
	NAME
        endActivity
	SYNOPSIS
		public void endActivity(String gameName)
		gameName --> game status, either filename or new(as in new game)
	DESCRIPTION
		The function will ends activity and passes Extra to Game Activity,Extra includes information for new game or load game(fileName)
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        8:25pm 07/29/2019
	*/
    /**/
    public void endActivity(String gameName){
        final Intent intent = new Intent(this, GameActivity.class);
        //Sending data to result activity
        intent.putExtra("fileName",gameName);
        startActivity(intent);
    }


    /**/
	/*
	tutorial()
	NAME
        tutorial
	SYNOPSIS
		public void tutorial()
	DESCRIPTION
		The function will ends activity and intent to TutorialActivity
	RETURNS
		void
	AUTHOR
		Rojan Shrestha
	DATE
        8:25pm 07/29/2019
	*/
    /**/
    //Ends activity and opens Tutorial Activity
    public void tutorial(){
        final Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
    }

}
