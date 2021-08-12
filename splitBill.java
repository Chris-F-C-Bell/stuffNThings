package com.example.christopher.tipcalc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class splitBill extends AppCompatActivity {


    private double finalBill;
    private int numDiners;
    private double billPerPerson;

    TextView finalBillTextView;
    TextView billPerPersonTextView;
    EditText numDinersEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_bill);

        // set up parameters - numDiners cannot be 0 due to "dividing by zero" runtime error
        finalBill = 0.0;
        numDiners = 1;
        billPerPerson = 0.0;

        // Initialise TextViews to corresponding IDs
        finalBillTextView = (TextView) findViewById(R.id.finalBillTextView);
        billPerPersonTextView = (TextView) findViewById(R.id.billPerPersonTextView);
        numDinersEditText = (EditText) findViewById(R.id.numDinersEditText);

        // listener for numDinersEditText
        numDinersEditText.addTextChangedListener(numDinersListener);

        // Create an intent place holder object to store intent passed from tipCalc
        Intent intent = getIntent();
        // get the string message from the intent
        String message = intent.getStringExtra(tipCalc.MESSAGE);
        // parse message to double to allow calculations with the final bill
        finalBill = Double.parseDouble(message);

        // set the finalBill TextView to show the value of the final bill passed from tipCalc
        finalBillTextView.setText(message);

        // call method it update billPerPerson - will only display the final bill divided by 1
        // looks nicer than having 0.00
        updateBillPerPerson();
    }

    private TextWatcher numDinersListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // implemented method. not needed
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            // try/catch to solve runtime errors
            try {
                // if the EditText is null or 0 then set the value to 1 before updating billPerPerson
                // prevent NullPointerException and Dividing by zero error

                // regex added to stop multiple zeros "00+) from displaying the billPerPerson as
                // infinity
                if(numDinersEditText.getText().toString().equals(null)
                        || numDinersEditText.getText().toString().matches("[0]+")){
                    numDiners = 1;
                    updateBillPerPerson();
                }
                else{
                    // other wise calculate billPerPerson with given parameters
                    numDiners = Integer.parseInt(numDinersEditText.getText().toString());
                    updateBillPerPerson();
                }

            }catch (NumberFormatException | NullPointerException e){
                e.printStackTrace();
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
            // implemented method. not needed
        }
    };

    private void updateBillPerPerson(){

        // calculate billPerPerson
        billPerPerson = finalBill / numDiners;

        // set the TextView to display the calculated value
        billPerPersonTextView.setText(String.format("%.02f", billPerPerson));

    }
}
