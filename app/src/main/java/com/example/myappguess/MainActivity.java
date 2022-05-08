package com.example.myappguess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText txtGuess;
    private Button btnGuess;
    private Button btnAgain;
    private TextView lblOutput;
    public static TextView lblInterval;
    private int theNumber, amount, min = 0, max = 100,  maxTries;
    public static boolean showKeyboard;

    public void checkGuess () {
        String guessText = txtGuess.getText().toString();
        String message = "";
        amount++;
        try {
            int guess = Integer.parseInt(guessText);
            if (guess < theNumber)
                message = "Ihr vorausplanende Zahl ist " + guess + " WENIGER als benötigt. Versuchen Sie noch ein mal";
            else if (guess > theNumber)
                message =  "Ihr vorausplanende Zahl ist " + guess + " GRÖßER als benötigt. Versuchen Sie noch ein mal";
            else {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                int n = preferences.getInt("n", 0) + 1;
                editor.putInt("n", n);
                if( amount <= maxTries) {
                    message = guess + " Gratulation! Sie haben gewonnen! amount = " + amount;
//        newGame();
                    int gamesWon = preferences.getInt("gamesWon", 0) + 1;

                    editor.putInt("gamesWon", gamesWon);
                    editor.apply();
                } else {
                    message = guess + " Scheisse! Sie haben verloren! amount = " + amount;
                    int gamesLoss = preferences.getInt("gamesLoss", 0) + 1;
//                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("gamesLoss", gamesLoss);
                    editor.apply();
                }
                btnAgain.setVisibility(View.VISIBLE);
                btnGuess.setVisibility(View.INVISIBLE);

                showKeyboard = false;
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            message = "Enter a whole number between" + min + " and " + max + ".";

        } finally {
            lblOutput.setText(message + "\nyou have " + (maxTries - amount) + " attempts left");
            txtGuess.requestFocus();
            txtGuess.selectAll();
        }
    }
    public void newGame () {
        theNumber = (int) (Math.random() * (max-(min)) + (min) + 1);
        txtGuess.setText("");
        lblInterval.setText("Guess a number etween " + min + " and " + max + ":");
        btnAgain.setVisibility(View.INVISIBLE);
        btnGuess.setVisibility(View.VISIBLE);
        amount = 0;
        showKeyboard = true;
        lblOutput.setText("Enter a number then click Guess!");
        maxTries = (int) (Math.log(max) / Math.log(2) + 1);
        System.out.println(maxTries);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtGuess = (EditText) findViewById(R.id.txtGuess);
        btnGuess = (Button) findViewById(R.id.btnGuess);
        btnAgain = (Button) findViewById(R.id.btnAgain);
        lblOutput = (TextView) findViewById(R.id.lblOutput);
        lblInterval = (TextView) findViewById(R.id.lblInterval);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        max = preferences.getInt("range", 100);

        newGame();
        btnGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               checkGuess();
            }
        });
        txtGuess.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                checkGuess();
                return showKeyboard;
            }
        });
        btnAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               newGame();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                System.out.println("SETTING");
                final CharSequence[] items = {"1 to 10", "1 to 100", "1 to 1000"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select the Range:");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch (item) {
                        case 0:
                            max = 10;
                            storeRange(10);
                            newGame();
                            break;
                        case 1:
                            max = 100;
                            storeRange(100);
                            newGame();
                            break;
                        case 2:
                            max = 1000;
                            storeRange(1000);
                            newGame();
                            break;
                    }
                    dialog.dismiss();
                }
                });
                AlertDialog alert = builder.create();
                alert.show();

                return true;
            case R.id.action_newgame:
                System.out.println("NEW GAME");
                    newGame();
                return true;
            case R.id.action_gamestats:
                System.out.println("STATS");

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

                int gamesWon = preferences.getInt("gamesWon", 0);
                int gamesLoss = preferences.getInt("gamesLoss", 0);
                int n = preferences.getInt("n", 0);
                double average = ((double) gamesWon / n * 100);
                AlertDialog statDialog = new AlertDialog.Builder(MainActivity.this).create();
                statDialog.setTitle("Guessing Game Stats");
                statDialog.setMessage("You have won "+ gamesWon + " games. Way to go! \n You have lost " + gamesLoss +" Keep going you will definitely do it! \n you have an average " + average + "% of wins");
                statDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
                statDialog.show();
                return true;
            case R.id.action_about:
                System.out.println("ABOUT");

                AlertDialog aboutDialog = new AlertDialog.Builder(MainActivity.this).create();
                aboutDialog.setTitle("About Guessing Game");
                aboutDialog.setMessage("(c)2022 Anton Game");
                aboutDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                aboutDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
            public void storeRange(int newRange) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("range", newRange);
                editor.apply();
            }
}