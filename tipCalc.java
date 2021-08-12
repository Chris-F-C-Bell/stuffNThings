package com.example.christopher.tipcalc;

// android imports
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

// tipCalc activity - functionality of tip calculator - parent activity
public class tipCalc extends AppCompatActivity {

    // message for adding final bill to bundle to use in splitBill activity
    public static final String MESSAGE = "com.example.christopher.tipCalc.MESSAGE";
    public String finalBillMessage = " ";

    // Constants used when saving and restoring view
    private static final String TOTAL_BILL = "TOTAL_BILL";
    private static final String CURRENT_TIP = "CURRENT_TIP";
    private static final String BILL_WITHOUT_TIP = "BILL_WITHOUT_TIP";

    // variables used for functionality
    private double billBeforeTip;   // Users bill before tip
    private double tipAmount;         // Percentage to calculate tip
    private double finalBill;       // Bill Plus Tip

    // EditText declarations - used to get user input for bill amount and tip
    EditText billBeforeTipEditText;
    EditText tipAmountEditText;

    // SeekBar declaration - manually set tip percentage using seekbar
    SeekBar tipSeekBar;

    // array used for storing tip percentage values used in advanced tipper
    // size is determined by the number of options - 3 for check boxes, 3 for radio buttons and
    // 3 for spinner options
    private int[] checklistValues = new int[9];

    // CheckBox declarations - each checked box adds a pre-determined
    CheckBox friendlyCheckBox;
    CheckBox specialsCheckBox;
    CheckBox drinksCheckBox;

    // RadioGroup and RadioButton declarations - radio buttons used to determine how available the
    // server was during visit - whatever radio button is checked will add to the checklist array
    // and add a corresponding tip amount
    RadioGroup availableRadioGroup;
    RadioButton availableBadRadio;
    RadioButton availableOKRadio;
    RadioButton availableGoodRadio;

    // Spinner Declaration
    Spinner foodQualitySpinner;

    // TextView Declarations - used to display the tip amount and the final bill value
    TextView finalTipUpdateTextView;
    TextView finalBillUpdateTextView;

    // Button Declarations - button to switch to splitBill activity
    Button splitBillButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calc);

        // Check if app just started, or if it is being restored
        if (savedInstanceState == null) {
            // App just started
            billBeforeTip = 0.0;
            tipAmount = 0.15;
            finalBill = 0.0;
        }
        else {
            // App is being restored
            billBeforeTip = savedInstanceState.getDouble(BILL_WITHOUT_TIP);
            tipAmount = savedInstanceState.getInt(CURRENT_TIP);
            finalBill = savedInstanceState.getDouble(TOTAL_BILL);

        }

        /**
         * Set up GUI elements to match IDs in activity_tipCalc
         */

        // Initialise the EditTexts
        billBeforeTipEditText = (EditText) findViewById(R.id.billEditText);
        tipAmountEditText = (EditText) findViewById(R.id.tipEditText);

        // Initialise seekbar and add a change listener
        tipSeekBar = (SeekBar) findViewById(R.id.changeTipSeekBar);

        // Create change listener for seekbar to update tip
        tipSeekBar.setOnSeekBarChangeListener(tipSeekBarListener);

        // Add change listener for when the billBeforeTip is changed
        billBeforeTipEditText.addTextChangedListener(billBeforeTipListener);

        // Add change listener to update the final bill when the tip amount is manually entered
        tipAmountEditText.addTextChangedListener(tipUpdateListener);

        // Initialise CheckBoxes
        friendlyCheckBox = (CheckBox) findViewById(R.id.friendlyCheckBox);
        specialsCheckBox = (CheckBox) findViewById(R.id.specialsCheckBox);
        drinksCheckBox = (CheckBox) findViewById(R.id.drinksCheckBox);

        // method call - creates change listener for CheckBoxes - defines the tip amount to be added
        // to the array
        setUpCheckBoxes();

        // Initialise RadioGroup and RadioButtons
        availableRadioGroup = (RadioGroup) findViewById(R.id.availableRadioGroup);
        availableBadRadio = (RadioButton) findViewById(R.id.availableBadRadio);
        availableOKRadio = (RadioButton) findViewById(R.id.availableOKRadio);
        availableGoodRadio = (RadioButton) findViewById(R.id.availableGoodRadio);

        // Add change listener to radio buttons - updates the tip array depending on which
        // radio button is selected
        addChangeListenersToRadios();

        // Initialise Spinner
        foodQualitySpinner = (Spinner) findViewById(R.id.foodQualitySpinner);

        // Add Listener to Spinner - gets the selected option to update tip array
        addItemSelectedListenerToSpinner();

        // Initialise TextViews - displays the tip amount and final bill - used TextViews to prevent
        // user from accidentally changing values
        finalTipUpdateTextView = (TextView) findViewById(R.id.finalTipTextView);
        finalBillUpdateTextView = (TextView) findViewById(R.id.finalBillUpdateTextView);

        // Initialise Button - used to switch to split bill view
        splitBillButton = (Button) findViewById(R.id.splitBillButton);

    }

    // change listener TextWatch used by tipAmountEditText
    // only onTextChange is needed
    private TextWatcher tipUpdateListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // listener method - not needed
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            // try catch to prevent numberFormatException and NullPointerException

            try {
                //check if the EditText value is null
                if (tipAmountEditText.getText().toString().equals(null)) {
                    tipAmount = 0.0;    //set tip amount to 0.0
                    updateTipAndFinalBill();    // call update method
                } else {
                    updateTipAndFinalBill();    // otherwise call method with text from Edit Text

                }
            }catch (NumberFormatException | NullPointerException e){
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // listener method - not needed
        }
    };

    // change listener TextWatch used by billBeforeTipEditText
    // again only onTextChange is needed
    private TextWatcher billBeforeTipListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // listener method - not needed
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            try {
                // before calling update method check that the tip value is not null or 0
                if (tipAmountEditText.getText().toString().equals(null)) {

                } else {
                    billBeforeTip = Double.parseDouble(s.toString());
                    updateTipAndFinalBill();
                }

            } catch(NumberFormatException | NullPointerException e){
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // listener method - not needed
        }
    };

    // Update te tip amount and add tip to bill to get the final bill amount

    // Update method for tip and final bill
    private void updateTipAndFinalBill(){

        // Get Tip Amount from EditText and parse to double

        double tipAmount = Double.parseDouble(tipAmountEditText.getText().toString());

        // if the tip entered into the editText is >= 1 - ie 100% or more
        // divide the tip amount by 100 to get a percentage
        // ASSUMPTION - "normally you would not be tipping over the value of the final bill"
        if (tipAmount >= 1){
            tipAmount = tipAmount / 100;
        }
        // calculate final bill
        double finalBill = billBeforeTip + (billBeforeTip * tipAmount);

        // calculate tip amount
        double tip = finalBill - billBeforeTip;

        // set the final bill to string and add to message
        // message added to bundle to calculate split bill
        finalBillMessage = Double.toString(finalBill);

        // set the finalbill and tip TextViews to display to user
        finalTipUpdateTextView.setText(String.format("%.02f", tip));
        finalBillUpdateTextView.setText(String.format("%.02f", finalBill));

      }

    // Called when a device changes in some way. Used to save state information that you would like
    // to be made available
    protected void onSaveInstanceState(Bundle outState){

        super.onSaveInstanceState(outState);

        outState.putDouble(TOTAL_BILL, finalBill);
        outState.putDouble(CURRENT_TIP, tipAmount);
        outState.putDouble(BILL_WITHOUT_TIP, billBeforeTip);

    }

    // if user is adjusting tip by using the seekbar the onProgressChangedd method is used. This
    // will take the progress (value between 0 - 100) and convert it into a decimal to calculate the
    // tip amount and final bill
    private SeekBar.OnSeekBarChangeListener tipSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            // Sets the tip amount to match the progress of the seekbar
            // seekbar progress is an integer value - to use as a percentage multiple the progress
            // by 0.01
            tipAmount = (tipSeekBar.getProgress()) *0.01 ;

            // update the tip EditText to show the tip as a percentage rather than a decimal value
            // will display tip in EditText as e.g. 10.0 for 10% tip
            // kept this way incase user wants to enter tip as 0.1 for 10% tip

            tipAmountEditText.setText(String.format("%.02f", tipAmount * 100));

            // call method to update the tip amount and final bill
            updateTipAndFinalBill();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // listener method - not needed
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // listener method - not needed
        }
    };
    // method that sets the criteria of each aspect of the visit and adds a pecentage of the tip to
    // the final percentage

    // a bad visit i.e. no checkboxes checked and service and food where "bad" minimum tip will be 0%
    // this means the final bill cannot be less than the original bill
    private void setUpCheckBoxes(){

        friendlyCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1){

                // if the friendly checkbox is ticked - add 5% to tip
                // uses the ternary operator rather than if statements
                // cleaner code this way
                // works along the lines of if checked set value to 5 else 0
                checklistValues[0] = (friendlyCheckBox.isChecked())? 5 :0;

                // call method to update the checklist array and then update tip and final bill
                setTipFromServerChecklist();
                updateTipAndFinalBill();
            }
        });

        specialsCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1){

                checklistValues[1] = (specialsCheckBox.isChecked())? 1 :0;

                setTipFromServerChecklist();
                updateTipAndFinalBill();
            }
        });

        drinksCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1){

                checklistValues[2] = (drinksCheckBox.isChecked())? 3 : 0;

                setTipFromServerChecklist();
                updateTipAndFinalBill();
            }
        });

    }

    private void setTipFromServerChecklist(){

        int checklistTotal = 0;

        // loop through the checklist and add the values of each element to the tip percentage
        for (int item : checklistValues){

            checklistTotal += item;
        }

        tipAmountEditText.setText(Integer.toString(checklistTotal) + ".00");

    }

    private void addChangeListenersToRadios(){

        availableRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                // checks what radio button is selected - using radio group so only one can be
                // selected at a time - depending on which is check changes tip % accordingly
                checklistValues[3] = (availableBadRadio.isChecked()?0:0);
                checklistValues[4] = (availableOKRadio.isChecked()?2:0);
                checklistValues[5] = (availableGoodRadio.isChecked()?5:0);

                setTipFromServerChecklist();
                updateTipAndFinalBill();
            }
        });

    }

    private void addItemSelectedListenerToSpinner(){

        foodQualitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // works same a radio buttons - only 1 option can be selected - tip is reflected
                // by the option selected i.e. good food + 6% tip
                checklistValues[6] = (foodQualitySpinner.getSelectedItem()).equals("Bad")?0:0;
                checklistValues[7] = (foodQualitySpinner.getSelectedItem()).equals("OK")?3:0;
                checklistValues[8] = (foodQualitySpinner.getSelectedItem()).equals("Good")?6:0;

                setTipFromServerChecklist();
                updateTipAndFinalBill();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // implemented method. Not needed
            }
        });
    }
    // method to change activity view. set as an onClick element for the button
    // creates an intent to pass the final bill value between the two activities
    // as a key:value pair - doing the message this way provides the "Back Arrow" on the splitBill
    // activity to navigate back to tipCalc activity.
    public void sendMessage(View view){

        // create an intent object passing in "this" class and the target activity class
        Intent intent = new Intent(this, splitBill.class);
        // add the message to the intent with the finalBill value as a string
        intent.putExtra(MESSAGE, finalBillMessage);
        // call startActivity witht the intent to switch view to splitBill activity
        startActivity(intent);
    }

}
