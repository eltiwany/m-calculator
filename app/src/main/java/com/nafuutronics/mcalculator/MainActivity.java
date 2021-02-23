package com.nafuutronics.mcalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> clickedValues = new ArrayList<>();
    boolean isLastValueADigit = true;
    boolean isLastValueASign = false;
    String displayValue = "";
    String currentDigits = "";
    final String[] symbol = {"÷", "×", "+", "-"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences calculatorPreferences = getSharedPreferences("calculatorPreferences", Context.MODE_MULTI_PROCESS);
        if (!calculatorPreferences.contains("firstInstall")) {
            // set a preference
            SharedPreferences.Editor editor = calculatorPreferences.edit();
            editor.putString("firstInstall", "1");
            editor.apply();

            // display log messages
            ConstraintLayout welcomeView = findViewById(R.id.welcome);
            welcomeView.setVisibility(View.VISIBLE);
            TextView changeLog = findViewById(R.id.editTextTextPersonName);
            changeLog.setText(Html.fromHtml(
                    "Thank you for using Magic Calculator" +
                            "<br/><br/>" +
                            "App Designed &amp; Developed by Ali Saleh" +
                            "<br/><br/>" +
                            "<b>V 0.12 Change Logs:</b>" +
                            "<p>1. Improved Performance</p>" +
                            "<p>2. Less Crashes</p>" +
                            "<label><i>Tap Anywhere to Close</i></label>"
            ));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("GitHub");
        menu.add("Contact Developer");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = null;
        Uri uri;
        switch (item.toString()) {
            case "GitHub":
                uri = Uri.parse("https://github.com/eltiwany/m-calculator");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                break;
            case "Contact Developer":
                uri = Uri.parse("tel:+255655464655");
                intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
                break;
        }
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    public void clickButton(View view) {
        TextView showToScreen = findViewById(R.id.result);
        String viewClicked = view.getTag().toString();

        if (viewClicked.matches("\\d+(?:\\.\\d+)?") && !viewClicked.equals(".")) {
            currentDigits += viewClicked;
            isLastValueASign = false;
        }else if (viewClicked.equals(".") && currentDigits.equals("")) {
            if (isLastValueASign) clickedValues.add("0" + viewClicked);
            else clickedValues.add(viewClicked);
        } else{
            isLastValueADigit = false;
            switch (viewClicked) {
                case "AC":
                    clickedValues.clear();
                    currentDigits = "";
                    isLastValueADigit = isLastValueASign = true;
                    break;
                case "=":
                    clickedValues.add(currentDigits);
                    currentDigits = "";
                    calculate();
                    break;
                case "DEL":
                    if (!isLastValueASign) clickedValues.add(currentDigits);
                    if (clickedValues.size() > 0) clickedValues.remove(clickedValues.size() - 1);
                    break;
                default:
                    if (!isLastValueASign) {
                        clickedValues.add(currentDigits);
                        currentDigits = "";
                        clickedValues.add(viewClicked);
                    }
                    isLastValueASign = true;
            }
        }
        if (!viewClicked.equals("=")) {
            displayValue = "";
            for (int i = 0; i < clickedValues.size(); i++)
                displayValue = displayValue.concat(clickedValues.get(i));
            displayValue += !isLastValueASign ? currentDigits : "";
            showToScreen.setText(displayValue);
        }
    }
    public void calculate() {
        TextView showToScreen = findViewById(R.id.result);
        /*
         *  Lets perform BODMAS,
         *  well .. actually ODMAS because
         *  there's no brackets on this one
        */
        cleanArray(clickedValues);
        for (int i = 0; i < 4; i++) {
            performCalculation(symbol[i]);
        }
        showToScreen.setText(clickedValues.get(0));
    }
    public void performCalculation(String symbol) {
        double val; int index;
        while (true) {
            index = clickedValues.lastIndexOf(symbol);
            if (index >= 0) {
                val = calculateHelper(Double.parseDouble((index - 1) < 0 ? "0" : clickedValues.get(index - 1)), Double.parseDouble(clickedValues.get(index + 1)), clickedValues.get(index));
                clickedValues.set(index - 1, String.valueOf(val));
                clickedValues.remove(index);
                clickedValues.remove(index);
            } else break;
        }
    }
    double calculateHelper(double num1, double num2, String operation) {
        switch (operation) {
            case "÷":
                return num1 / num2;
            case "×":
                return num1 * num2;
            case "+":
                return num1 + num2;
            default:
                return num1 - num2;
        }
    }
    public void cleanArray(ArrayList<String> array) {
        int indexEmpty;
        while (true) {
            for (int i = 0; i < 4; i++) {
                if ((array.lastIndexOf(symbol[i]) + 1) == array.size()) {
                    array.remove((array.lastIndexOf(symbol[i])));
                    isLastValueASign = false;
                }
            }
            indexEmpty = array.lastIndexOf("");
            if (indexEmpty >= 0) {
                array.remove(indexEmpty);
            } else break;
        }
    }
    public void closeWelcomeScreen(View view) {
        view.setVisibility(View.INVISIBLE);
    }
}