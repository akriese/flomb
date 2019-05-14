package de.axolotl.flomb;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public static String PACKAGE_NAME;

    //region Android ELemente
    public Button btn_addfinal, btn_add, btn_statssets, btn_datepicker, btn_back, btn_settings, btn_resetLastEntry;
    public EditText edt_place, edt_description, edt_amount, edt_deleteRowId;
    public TextView txv_headline, txv_addsumup, txv_sumup11, txv_statssetsumup, txv_statsdisplay;
    public RadioButton rbn_f, rbn_a, rbn_t, rbn_o, rbn_b, rbn_single, rbn_compare, rbn_change;
    public CheckBox cbx_keepdata, cbx_minus, cbx_f, cbx_a, cbx_t, cbx_o, cbx_b;
    public LinearLayout lnl_description, lnl_dateplace, lnl_cbxfateb, lnl_deleteRow;
    public RelativeLayout rll_add, rll_start, rll_statssets, rll_settings, rll_stats;
    public ScrollView scv_sumup1;
    public Spinner spi_description;
    public SeekBar sbr_date1from, sbr_date1to, sbr_date2from, sbr_date2to;

    //endregion
    //region other Elements and variables
    private int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE=0;
    private Calendar calendar;
    private int year, month, day, amount=0, dateYear, dateMonth, dateDay;
    private int overall_f=0,overall_a=0,overall_t=0,overall_o=0,overall_b=0, overall_all, overall_withoutBig;
    private int overall_f2=0,overall_a2=0,overall_t2=0,overall_o2=0,overall_b2=0, overall_all2;
    private int overall_Imbiss, overall_GS, overall_ResBar, overall_Mensa,
            overall_otherFood, overall_Flat, overall_Hostel, overall_Hotel,
            overall_AirBnB, overall_otherliving, overall_Train, overall_Bus, overall_Flug,
            overall_MFG, overall_Bike, overall_otherMove, overall_Entry,
            overall_Post, overall_Gift, overall_Clothes, overall_Hygiene, overall_otherOther,
            overall_Big, overall_Wage, overall_Bafoeg;
    private String description="Beschreibung", category, subcategory;
    private DatePicker datepicker;
    private String dateAdd, dateString, date1fromString, date1toString, date2fromString, date2toString;
    private final List<String> categories = Arrays.asList(getResources().getStringArray(R.array.categories)),
                                categories_short = Arrays.asList(getResources().getStringArray(R.array.categories_short)),
                                sub_food = Arrays.asList(getResources().getStringArray(R.array.food_subcategories)),
                                sub_living = Arrays.asList(getResources().getStringArray(R.array.living_subcategories)),
                                sub_other = Arrays.asList(getResources().getStringArray(R.array.other_subcategories)),
                                sub_move = Arrays.asList(getResources().getStringArray(R.array.move_subcategories)),
                                sub_big = Arrays.asList(getResources().getStringArray(R.array.big_subcategories));
    private int daysInbetween, date1from, date1to, date2from, date2to, newestDayValue;
    public ArrayAdapter<CharSequence> adapter_subcategory1, adapter_subcategory2, adapter_subcategory3, adapter_subcategory4, adapter_subcategory5;
    DatabaseHelper myDB;
    DatabaseHelperBackup myDBBackup;

    //endregion
    //region seekbars
    //region date1from activated
    private SeekBar.OnSeekBarChangeListener sbr_date1from_listener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar sbr_date1from, int progress, boolean fromUser) {
                    date1from=progress+1;
                    calculateDates();
                    sbr_date1to.setMax(newestDayValue-progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar sbr_date1from) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar sbr_date1from) {
                }
            };

    private SeekBar.OnSeekBarChangeListener sbr_date1to_listener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar sbr_date1to, int progress, boolean fromUser) {
                    date1to=progress+date1from;
                    calculateDates();
                }

                @Override
                public void onStartTrackingTouch(SeekBar sbr_date1to) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar sbr_date1to) {
                }
            };

    private SeekBar.OnSeekBarChangeListener sbr_date2from_listener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar sbr_date2from, int progress, boolean fromUser) {
                    date2from=progress+1;
                    calculateDates();
                    sbr_date2to.setMax(newestDayValue-progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar sbr_date2from) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar sbr_date2from) {
                }
            };

    private SeekBar.OnSeekBarChangeListener sbr_date2to_listener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar sbr_date2to, int progress, boolean fromUser) {
                    date2to=progress+date2from;
                    calculateDates();
                }

                @Override
                public void onStartTrackingTouch(SeekBar sbr_date2to) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar sbr_date2to) {
                }
            };

    public void calculateDates(){
        DateTimeZone UTC = DateTimeZone.forID("UTC");
        DateTime travelStart= new DateTime(2017,7,5,12,0,0,UTC);

        DateTime date1fromdt = travelStart.plusDays(date1from).plusHours(1);
        DateTime date1todt = travelStart.plusDays(date1to).plusHours(2);
        DateTime date2fromdt = travelStart.plusDays(date2from).plusHours(1);
        DateTime date2todt = travelStart.plusDays(date2to).plusHours(2);
        date1fromString=date1fromdt.getDayOfMonth()+"."+date1fromdt.getMonthOfYear()+"."+date1fromdt.getYear();
        date1toString=date1todt.getDayOfMonth()+"."+date1todt.getMonthOfYear()+"."+date1todt.getYear();
        date2fromString=date2fromdt.getDayOfMonth()+"."+date2fromdt.getMonthOfYear()+"."+date2fromdt.getYear();
        date2toString=date2todt.getDayOfMonth()+"."+date2todt.getMonthOfYear()+"."+date2todt.getYear();
        if (rbn_single.isChecked()){
            txv_statssetsumup.setText("Daten vom "+date1fromString+" bis einschließlich\ndem "+date1toString+" werden zusammengefasst!");
        }
        else if (rbn_compare.isChecked()){
            txv_statssetsumup.setText("Daten vom "+date1fromString+" - "+date1toString+" und\n"+date2fromString+" - "+date2toString+" werden verglichen!");
        }
        else if (rbn_change.isChecked()){
            txv_statssetsumup.setText("Entwicklung vom "+date1fromString+" bis einschließlich\ndem "+date1toString+" angezeigt!");
        }
    }
    //endregion
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region Initialisierung der Android-Elemente
        //region general
        txv_headline = findViewById(R.id.txv_headline);
        btn_add = findViewById(R.id.btn_add);
        btn_statssets = findViewById(R.id.btn_statssets);
        btn_back = findViewById(R.id.btn_back);
        btn_settings = findViewById(R.id.btn_settings);
        rll_start = findViewById(R.id.rll_start);
        scv_sumup1 = findViewById(R.id.scv_sumup1);
        txv_sumup11 = findViewById(R.id.txv_sumup11);
        //endregion
        //region Add
        btn_addfinal = findViewById(R.id.btn_addfinal);
        btn_datepicker = findViewById(R.id.btn_datepcker);
        edt_place = findViewById(R.id.edt_place);
        edt_description = findViewById(R.id.edt_description);
        edt_amount = findViewById(R.id.edt_amount);
        txv_addsumup = findViewById(R.id.txv_addsumup);
        rbn_f = findViewById(R.id.rbn_f);
        rbn_a = findViewById(R.id.rbn_a);
        rbn_t = findViewById(R.id.rbn_t);
        rbn_o = findViewById(R.id.rbn_o);
        rbn_b = findViewById(R.id.rbn_b);
        cbx_keepdata = findViewById(R.id.cbx_keepdata);
        cbx_minus = findViewById(R.id.cbx_minus);
        lnl_description = findViewById(R.id.lnl_description);
        lnl_dateplace = findViewById(R.id.lnl_dateplace);
        rll_add = findViewById(R.id.rll_add);
        spi_description = findViewById(R.id.spi_description);
        //endregion
        //region stats
        rll_statssets = findViewById(R.id.rll_statssets);
        lnl_cbxfateb = findViewById(R.id.lnl_cbxfateb);
        cbx_f = findViewById(R.id.cbx_f);
        cbx_a = findViewById(R.id.cbx_a);
        cbx_t = findViewById(R.id.cbx_t);
        cbx_o = findViewById(R.id.cbx_o);
        cbx_b = findViewById(R.id.cbx_b);
        rbn_single = findViewById(R.id.rbn_single);
        rbn_compare = findViewById(R.id.rbn_compare);
        rbn_change = findViewById(R.id.rbn_change);
        sbr_date1from = findViewById(R.id.sbr_date1from);
        sbr_date1to = findViewById(R.id.sbr_date1to);
        sbr_date2from = findViewById(R.id.sbr_date2from);
        sbr_date2to = findViewById(R.id.sbr_date2to);
        txv_statssetsumup = findViewById(R.id.txv_statssetsumup);
        //endregion
        //region settings
        rll_settings = findViewById(R.id.rll_settings);
        lnl_deleteRow = findViewById(R.id.lnl_deleteRow);
        btn_resetLastEntry = findViewById(R.id.btn_resetLastEntry);
        edt_deleteRowId = findViewById(R.id.edt_deleteRowId);
        //endregion
        //region stats
        rll_stats = findViewById(R.id.rll_stats);
        txv_statsdisplay = findViewById(R.id.txv_statsdisplay);
        //endregion
        //endregion

        myDB = new DatabaseHelper(this);
        myDBBackup = new DatabaseHelperBackup(this);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        rll_add.setVisibility(View.INVISIBLE);
        rll_statssets.setVisibility(View.INVISIBLE);
        rll_settings.setVisibility(View.INVISIBLE);
        rll_stats.setVisibility(View.INVISIBLE);
        btn_back.setVisibility(View.INVISIBLE);

        rbn_f.setChecked(true);

        adapter_subcategory1 = ArrayAdapter.createFromResource(this,
                R.array.food_subcategories,android.R.layout.simple_spinner_item);
        adapter_subcategory1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter_subcategory2 = ArrayAdapter.createFromResource(this,
                R.array.living_subcategories,android.R.layout.simple_spinner_item);
        adapter_subcategory2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter_subcategory3 = ArrayAdapter.createFromResource(this,
                R.array.move_subcategories,android.R.layout.simple_spinner_item);
        adapter_subcategory3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter_subcategory4 = ArrayAdapter.createFromResource(this,
                R.array.other_subcategories,android.R.layout.simple_spinner_item);
        adapter_subcategory4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter_subcategory5 = ArrayAdapter.createFromResource(this,
                R.array.big_subcategories,android.R.layout.simple_spinner_item);
        adapter_subcategory5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spi_description.setAdapter(adapter_subcategory1);
        spi_description.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subcategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        updateFrontPage();
        PACKAGE_NAME = getApplicationContext().getPackageName();
    }

    //region Main Movements
    public void onAddClick(View view) throws IOException { //navigates from Main Menu to Add Menu

        rll_add.setVisibility(View.VISIBLE);
        rll_start.setVisibility(View.INVISIBLE);
        btn_back.setVisibility(View.VISIBLE);

        rbn_f.setChecked(true);
        spi_description.setAdapter(adapter_subcategory1);
        category="Food";
        subcategory="IKS";

        //region EditText Listener
        edt_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt_amount.getText().toString().startsWith("0") && edt_amount.getText().toString().length()!=1){
                    edt_amount.setText(edt_amount.getText().toString().substring(1));
                    edt_amount.setSelection(edt_amount.getText().length());
                }
                if (edt_amount.getText().length()!=0){
                    updateInformation();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_description.setOnEditorActionListener(new TextView.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    edt_place.requestFocus();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edt_place,InputMethodManager.SHOW_IMPLICIT);
                    handled = true;
                }
                return handled;
            }
        });

        edt_place.setOnEditorActionListener(new TextView.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    edt_amount.requestFocus();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edt_amount,InputMethodManager.SHOW_IMPLICIT);
                    edt_amount.setSelection(edt_amount.getText().length());
                    handled = true;
                }
                return handled;
            }
        });

        edt_amount.setOnEditorActionListener(new TextView.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                    amount=Integer.parseInt(edt_amount.getText().toString());
                    updateInformation();
                    handled=true;
                }
                return handled;
            }
        });
        //endregion

        SharedPreferences placeAndDate = getSharedPreferences("USER_PREFERENCES_PLACE_AND_DATE", MODE_PRIVATE);
        placeAndDate.getString("PLACE","0");
        placeAndDate.getInt("YEAR",2017);
        placeAndDate.getInt("MONTH",7);
        placeAndDate.getInt("DAY",5);

    }

    public void onStatssetsClick(View view) { //navigates from Main Menu to Statistic's Settings Menu
        rll_start.setVisibility(View.INVISIBLE);
        rll_statssets.setVisibility(View.VISIBLE);
        btn_back.setVisibility(View.VISIBLE);
        txv_headline.setText("Stats");

        sbr_date1from.setOnSeekBarChangeListener(sbr_date1from_listener);
        sbr_date1to.setOnSeekBarChangeListener(sbr_date1to_listener);
        sbr_date2from.setOnSeekBarChangeListener(sbr_date2from_listener);
        sbr_date2to.setOnSeekBarChangeListener(sbr_date2to_listener);
        Cursor res = myDB.getAllData();
        DateTimeZone UTC = DateTimeZone.forID("UTC");
        DateTime travelStart= new DateTime(2017,7,5,12,0,0,UTC);
        while (res.moveToNext()) {
            DateTime addDatedt = new DateTime(res.getInt(5), res.getInt(6), res.getInt(7), 13, 0, 0, UTC);
            daysInbetween = Days.daysBetween(travelStart.toLocalDateTime(), addDatedt.toLocalDateTime()).getDays();
            if (daysInbetween>newestDayValue){
                newestDayValue=daysInbetween;
            }
        }

        sbr_date1from.setMax(newestDayValue);
        sbr_date1to.setMax(newestDayValue);
        sbr_date2from.setMax(newestDayValue);
        sbr_date2to.setMax(newestDayValue);
        sbr_date1from.setProgress(0);
        sbr_date1to.setProgress(0);
        sbr_date2from.setProgress(0);
        sbr_date2to.setProgress(0);
        txv_statssetsumup.setText("Daten vom "+date1fromString+" bis einschließlich\ndem "+date1toString+" werden zusammengefasst!");
    }

    public void onSettingsClick(View view) {
        rll_start.setVisibility(View.INVISIBLE);
        rll_settings.setVisibility(View.VISIBLE);
        btn_back.setVisibility(View.VISIBLE);
        txv_headline.setText("Settings");
    }

    public void onBackClick(View view) {
        if (rll_add.getVisibility()==View.VISIBLE){
            rll_add.setVisibility(View.INVISIBLE);
            rll_start.setVisibility(View.VISIBLE);
        }
        if (rll_statssets.getVisibility()==View.VISIBLE){
            rll_statssets.setVisibility(View.INVISIBLE);
            rll_start.setVisibility(View.VISIBLE);
        }
        if (rll_settings.getVisibility()==View.VISIBLE){
            rll_settings.setVisibility(View.INVISIBLE);
            rll_start.setVisibility(View.VISIBLE);

            updateFrontPage();
        }
        txv_headline.setText(getString(R.string.app_name));
        btn_back.setVisibility(View.INVISIBLE);
        if (rll_stats.getVisibility()==View.VISIBLE){
            rll_statssets.setVisibility(View.VISIBLE);
            rll_stats.setVisibility(View.INVISIBLE);
            btn_back.setVisibility(View.VISIBLE);
            txv_headline.setText("Stats");
        }



    }
    //endregion

    //Datenbank auf SD laden
    public static void backupDatabase() throws IOException {
        //Open your local db as the input stream
        String inFileName = "/data/data/antonk/fatebDE/databases/fateb.db";
        File dbFile = new File(inFileName);
        FileInputStream fis = new FileInputStream(dbFile);

        String outFileName = Environment.getExternalStorageDirectory() + "/fateb.db";
        //Open the empty db as the output stream
        OutputStream output = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer))>0){
            output.write(buffer, 0, length);
        }
        //Close the streams
        output.flush();
        output.close();
        fis.close();
    }

    public void addData(View view){
        boolean isInserted = myDB.insertData(amount,category,subcategory,edt_description.getText().toString(), dateYear, dateMonth, dateDay, edt_place.getText().toString());
        if (isInserted){
            Toast.makeText(MainActivity.this,getString(R.string.entry_added),Toast.LENGTH_LONG).show();
        }
        else Toast.makeText(MainActivity.this,getString(R.string.entry_not_added),Toast.LENGTH_LONG).show();

        //region Keep Data
        if (cbx_keepdata.isChecked()){
            SharedPreferences placeAndDate = getSharedPreferences("USER_PREFERENCES_PLACE_AND_DATE", MODE_PRIVATE);
            SharedPreferences.Editor editor = placeAndDate.edit();

            editor.putString("PLACE",edt_place.getText().toString());
            editor.putInt("YEAR",year);
            editor.putInt("MONTH",month);
            editor.putInt("DAY",day);

            editor.apply();
        }
        else {
            SharedPreferences placeAndDate = getSharedPreferences("USER_PREFERENCES_PLACE_AND_DATE", MODE_PRIVATE);
            SharedPreferences.Editor editor = placeAndDate.edit();

            editor.putString("PLACE","");
            editor.putInt("YEAR",year); //get current year
            editor.putInt("MONTH",month); //get current month
            editor.putInt("DAY",day); //get current day

            editor.apply();

            edt_description.setText("");
            edt_place.setText("");
            btn_datepicker.setText(getString(R.string.date));
            cbx_minus.setChecked(false);
        }

        updateFrontPage();
        //endregion
    }

    public void updateInformation(){
        if (edt_amount.getText().length()==0){
            edt_amount.setText("0");
        }
        amount=Integer.parseInt(edt_amount.getText().toString());
        if (cbx_minus.isChecked()) amount=amount*(-1);
        String description = edt_description.getText().toString();
        String place = edt_place.getText().toString();

        if (!description.equals("") && amount!=0 && !place.equals("") && !btn_datepicker.getText().toString().equals("Datum")){
            btn_addfinal.setClickable(true);
        } else btn_addfinal.setClickable(false);



        txv_addsumup.setText(amount+getString(R.string.für)+description+","+getString(R.string.für)+dateAdd+","+getString(R.string.in)+place);
    }

    public void updateFrontPage(){ //getAllData
        Cursor res = myDB.getAllData();

        StringBuilder builder = new StringBuilder();
        //region number reset
        newestDayValue =0;
        overall_f=0;
        overall_a=0;
        overall_t=0;
        overall_o=0;
        overall_b=0;
        overall_all=0;
        overall_Imbiss=0; overall_GS=0; overall_ResBar=0; overall_Mensa=0;
        overall_otherFood=0; overall_Flat=0; overall_Hostel=0; overall_Hotel=0;
        overall_AirBnB=0; overall_otherliving=0; overall_Train=0; overall_Bus=0; overall_Flug=0;
        overall_MFG=0; overall_Bike=0; overall_otherMove=0; overall_Entry=0;
        overall_Post=0; overall_Gift=0; overall_Clothes=0; overall_Hygiene=0; overall_otherOther=0;
        overall_Big=0;overall_Wage=0; overall_Bafoeg=0;
        //endregion

        DateTimeZone UTC = DateTimeZone.forID("UTC");
        DateTime travelStart= new DateTime(2017,7,5,12,0,0,UTC);

        while (res.moveToNext()){
            String kurz;
            String kurzSub;
            String kurzOrt;
            String s = res.getString(2);
            if (categories.get(0).equals(s)) {
                kurz = categories_short.get(0);
            } else if (categories.get(1).equals(s)) {
                kurz = categories_short.get(1);
            } else if (categories.get(2).equals(s)) {
                kurz = categories_short.get(2);
            } else if (categories.get(3).equals(s)) {
                kurz = categories_short.get(3);
            } else if (categories.get(4).equals(s)) {
                kurz = categories_short.get(4);
            } else {
                kurz = "ERROR";
            }
            switch (res.getString(3)){
                case "IKS": kurzSub = "IKS"; break;
                case "GS": kurzSub = "GrSt"; break;
                case "ResBar": kurzSub = "Res"; break;
                case "Mensa": kurzSub = "Men"; break;
                case "Flat": kurzSub = "Whn"; break;
                case "Hostel": kurzSub = "Hos"; break;
                case "Hotel": kurzSub = "Hot"; break;
                case "AirBnB": kurzSub = "Air"; break;
                case "Zug": kurzSub = "Zug"; break;
                case "Bus": kurzSub = "Bus"; break;
                case "Flug": kurzSub = "Flug"; break;
                case "MFG": kurzSub = "MFG"; break;
                case "Bike": kurzSub = "Rad"; break;
                case "Entry": kurzSub = "Entr"; break;
                case "Gift": kurzSub = "Pre"; break;
                case "Clothes": kurzSub = "Clo"; break;
                case "Post": kurzSub = "Post"; break;
                case "Hygiene": kurzSub = "Hyg"; break;
                case "Sonstiges": kurzSub = "Son"; break;
                case "Big": kurzSub = "Big"; break;
                case "Wage": kurzSub = "Arb"; break;
                case "Bafoeg": kurzSub = "Baf"; break;
                default: kurzSub= "ERROR"; break;
            }
            switch (res.getString(8)){
                case "Berlin": kurzOrt = "B."; break;
                case "Jena": kurzOrt = "J."; break;
                case "unterwegs": kurzOrt = "un."; break;
                case "unterwegs ": kurzOrt = "un."; break;
                default: kurzOrt = res.getString(8); break;
            }

            DateTime addDatedt = new DateTime(res.getInt(5),res.getInt(6),res.getInt(7),13,0,0,UTC);
            daysInbetween=Days.daysBetween(travelStart.toLocalDateTime(),addDatedt.toLocalDateTime()).getDays();
            builder.insert(0, res.getInt(1)+" für "+res.getString(4)+" ("+kurz+", "+kurzSub+") am "
                    +res.getInt(7)+"."+res.getInt(6)+". ("+daysInbetween+", "+res.getInt(0)+") in "+kurzOrt+"\n");

            String dataCategory = res.getString(res.getColumnIndex("CATEGORY"));
            String dataSub = res.getString(res.getColumnIndex("SUBCATEGORY"));

            switch (dataCategory) {
                case "Food":
                    overall_f += res.getInt(1);
                    switch (dataSub) {
                        case "IKS": overall_Imbiss += res.getInt(1); break;
                        case "GS": overall_GS += res.getInt(1); break;
                        case "ResBar": overall_ResBar += res.getInt(1); break;
                        case "Mensa": overall_Mensa += res.getInt(1); break;
                        case "Sonstiges": overall_otherFood += res.getInt(1); break;
                        default: break;
                    } break;
                case "Living":
                    overall_a += res.getInt(1);
                    switch (dataSub) {
                        case "Flat": overall_Flat += res.getInt(1); break;
                        case "Hostel": overall_Hostel += res.getInt(1); break;
                        case "Hotel": overall_Hotel += res.getInt(1); break;
                        case "AirBnB": overall_AirBnB += res.getInt(1); break;
                        case "Sonstiges": overall_otherliving += res.getInt(1); break;
                        default: break;
                    } break;
                case "Move":
                    overall_t += res.getInt(1);
                    switch (dataSub) {
                        case "Zug": overall_Train += res.getInt(1); break;
                        case "Bus": overall_Bus += res.getInt(1); break;
                        case "Flug": overall_Flug += res.getInt(1); break;
                        case "MFG": overall_MFG += res.getInt(1); break;
                        case "Bike": overall_Bike += res.getInt(1); break;
                        case "Sonstiges": overall_otherMove += res.getInt(1); break;
                        default: break;
                    }break;
                case "Other":
                    overall_o += res.getInt(1);
                    switch (dataSub) {
                        case "Entry": overall_Entry += res.getInt(1); break;
                        case "Post": overall_Post += res.getInt(1); break;
                        case "Gift": overall_Gift += res.getInt(1); break;
                        case "Clothes": overall_Clothes += res.getInt(1); break;
                        case "Hygiene": overall_Hygiene += res.getInt(1); break;
                        case "Sonstiges": overall_otherOther += res.getInt(1); break;
                        default: break;
                    }break;
                case "Big":
                    overall_b += res.getInt(1);
                    switch (dataSub) {
                        case "Big": overall_Big += res.getInt(1); break;
                        case "Wage": overall_Wage += res.getInt(1); break;
                        case "Bafoeg": overall_Bafoeg += res.getInt(1); break;
                        default: break;
                    }break;

                default:
                    overall_b += res.getInt(1); break;
            }
            overall_all += res.getInt(1);
            overall_withoutBig=overall_all-overall_b;

            if (daysInbetween>newestDayValue){
                newestDayValue=daysInbetween;
            }
        }

        if (newestDayValue==0){
            newestDayValue=1;
        }
        builder.insert(0,"Without Big: "+overall_withoutBig+" Cents ("+overall_withoutBig/newestDayValue+" pro Tag)\n\n");
        builder.insert(0,"ALL: "+overall_all+" Cents ("+overall_all/newestDayValue+" pro Tag)\n");
        builder.insert(0,"Big: "+overall_b+" Cents ("+overall_b/newestDayValue+" pro Tag)\n\n");
        builder.insert(0,"Other: "+overall_o+" Cents ("+overall_o/newestDayValue+" pro Tag)\n");
        builder.insert(0,"Move: "+overall_t+" Cents ("+overall_t/newestDayValue+" pro Tag)\n");
        builder.insert(0,"Living: "+overall_a+" Cents ("+overall_a/newestDayValue+" pro Tag)\n");
        builder.insert(0,"Food: "+overall_f+" Cents ("+overall_f/newestDayValue+" pro Tag)\n");

        builder.append("\nImbiss/Kiosk/Späti: "+overall_Imbiss+" Cents\n");
        builder.append("Supermarkt/Laden: "+overall_GS+" Cents\n");
        builder.append("ResBar: "+overall_ResBar+" Cents\n");
        builder.append("Mensa: "+overall_Mensa+" Cents\n");
        builder.append("Sonstiges Essen: "+overall_otherFood+" Cents\n");
        builder.append("Flat: "+overall_Flat+" Cents\n");
        builder.append("Hostel: "+overall_Hostel+" Cents\n");
        builder.append("Hotel: "+overall_Hotel+" Cents\n");
        builder.append("AirBnB: "+overall_AirBnB+" Cents\n");
        builder.append("Sonstige Unterkünfte: "+overall_otherliving+" Cents\n");
        builder.append("Zug: "+overall_Train+" Cents\n");
        builder.append("Bus: "+overall_Bus+" Cents\n");
        builder.append("Flug: "+overall_Flug+" Cents\n");
        builder.append("MFG: "+overall_MFG+" Cents\n");
        builder.append("Bike: "+overall_Bike+" Cents\n");
        builder.append("Sonstige Mittel: "+overall_otherMove+" Cents\n");
        builder.append("Entry: "+overall_Entry+" Cents\n");
        builder.append("Post: "+overall_Post+" Cents\n");
        builder.append("Gift: "+overall_Gift+" Cents\n");
        builder.append("Clothes: "+overall_Clothes+" Cents\n");
        builder.append("Hygiene: "+overall_Hygiene+" Cents\n");
        builder.append("Sonstiges Sonstiges: "+overall_otherOther+" Cents\n");
        builder.append("Big: "+overall_Big+" Cents\n");
        builder.append("Wage: "+overall_Wage+" Cents\n");
        builder.append("Bafoeg: "+overall_Bafoeg+" Cents\n");
        txv_sumup11.setText(builder.toString());

        res.close();
    }

    public void onRbnGroupClick(View view) {
        if (rbn_f.isChecked()) {
            category = "Food";
            subcategory = "IKS";
            spi_description.setAdapter(adapter_subcategory1);
        }
        if (rbn_a.isChecked()) {
            category = "Living";
            subcategory = "Flat";
            spi_description.setAdapter(adapter_subcategory2);
        }
        if (rbn_t.isChecked()){
            category = "Move";
            subcategory = "Zug";
            spi_description.setAdapter(adapter_subcategory3);
        }
        if (rbn_o.isChecked()){
            category = "Other";
            subcategory = "Entry";
            spi_description.setAdapter(adapter_subcategory4);
        }
        if (rbn_b.isChecked()){
            category = "Big";
            subcategory = "Big";
            spi_description.setAdapter(adapter_subcategory5);
        }

        updateInformation();
    }

    public void cbxMinusClick(View view) {
        updateInformation();
    }

    //region Datepicker
    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "Wähle das Datum aus!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3);
            updateInformation();
        }
    };
    private void showDate(int year, int month, int day) {
        btn_datepicker.setText(new StringBuilder().append(day).append(".")
                .append(month).append(".").append(year));
        DateTimeZone UTC = DateTimeZone.forID("UTC");
        DateTime travelStart= new DateTime(2017,7,5,12,0,0,UTC);
        DateTime addDatedt = new DateTime(year,month,day,13,0,0,UTC);
        daysInbetween=Days.daysBetween(travelStart.toLocalDateTime(),addDatedt.toLocalDateTime()).getDays();
        dateAdd=(day+"."+month+"."+year+" ("+daysInbetween+")");
        dateYear=year;
        dateMonth=month;
        dateDay=day;
    }
    //endregion

    public void onResetChosenClick(View view) {
        Integer deletedRows = myDB.deleteData(edt_deleteRowId.getText().toString());
        if (deletedRows > 0){
            Toast.makeText(MainActivity.this,"Data deleted!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this,"Data not deleted!", Toast.LENGTH_LONG).show();
        }
    }

    public void onRbnStatsClick(View view) {
        if (rbn_single.isChecked()){
            sbr_date2from.setVisibility(View.INVISIBLE);
            sbr_date2to.setVisibility(View.INVISIBLE);
            txv_statssetsumup.setText("Daten vom "+date1fromString+" bis einschließlich dem "+date1toString+" werden zusammengefasst!");
        }
        else if (rbn_compare.isChecked()){
            sbr_date2from.setVisibility(View.VISIBLE);
            sbr_date2to.setVisibility(View.VISIBLE);
            txv_statssetsumup.setText("Daten vom "+date1fromString+" - "+date1toString+" und\n"+date2fromString+" - "+date2toString+" werden verglichen!");
        }
        else if (rbn_change.isChecked()){
            sbr_date2from.setVisibility(View.INVISIBLE);
            sbr_date2to.setVisibility(View.INVISIBLE);
            txv_statssetsumup.setText("Entwicklung vom "+date1fromString+" bis einschließlich dem "+date1toString+" angezeigt!");
        }
    }

    public void showStats(View view) {
        if (!cbx_f.isChecked()&&!cbx_a.isChecked()&&!cbx_t.isChecked()&&!cbx_o.isChecked()&&!cbx_b.isChecked()){
            //nothing
            Toast.makeText(MainActivity.this,"Kategorie auswählen!",Toast.LENGTH_LONG).show();
        }
        else {
            rll_statssets.setVisibility(View.INVISIBLE);
            rll_stats.setVisibility(View.VISIBLE);
            analyzeData();
        }
    }

    public void analyzeData(){
        overall_f=0;overall_a=0;overall_t=0;overall_o=0;overall_b=0;overall_all=0;
        overall_f2=0;overall_a2=0;overall_t2=0;overall_o2=0;overall_b2=0;overall_all2=0;
        Cursor res = myDB.getAllData();
        StringBuilder builderDetails = new StringBuilder();
        StringBuilder builderDetails2 = new StringBuilder();
        StringBuilder builderStats = new StringBuilder();
        DateTimeZone UTC = DateTimeZone.forID("UTC");
        DateTime travelStart= new DateTime(2017,7,5,12,0,0,UTC);
        if (rbn_single.isChecked()){
            while (res.moveToNext()) {
                DateTime checkDatedt = new DateTime(res.getInt(5), res.getInt(6), res.getInt(7), 13, 0, 0, UTC);
                daysInbetween = Days.daysBetween(travelStart.toLocalDateTime(), checkDatedt.toLocalDateTime()).getDays();
                if ((daysInbetween >= date1from) && (daysInbetween <= date1to)) {
                    String dataCategory = res.getString(res.getColumnIndex("CATEGORY"));
                    switch (dataCategory) {
                        case "Food":
                            if (cbx_f.isChecked()){
                                overall_f += res.getInt(1);
                                overall_all += res.getInt(1);
                                builderDetails.insert(0,res.getInt(1) + " für " + res.getString(4) + " (" + res.getString(2) + ", " + res.getString(3) + ") am "
                                        + res.getInt(7) + "." + res.getInt(6) + ". (" + daysInbetween + ", " + res.getInt(0) + ") in " + res.getString(8) + "\n");
                            }break;
                        case "Living":
                            if (cbx_a.isChecked()){
                                overall_a += res.getInt(1);
                                overall_all += res.getInt(1);
                                builderDetails.insert(0,res.getInt(1) + " für " + res.getString(4) + " (" + res.getString(2) + ", " + res.getString(3) + ") am "
                                        + res.getInt(7) + "." + res.getInt(6) + ". (" + daysInbetween + ", " + res.getInt(0) + ") in " + res.getString(8) + "\n");
                            }break;
                        case "Move":
                            if (cbx_t.isChecked()){
                                overall_t += res.getInt(1);
                                overall_all += res.getInt(1);
                                builderDetails.insert(0,res.getInt(1) + " für " + res.getString(4) + " (" + res.getString(2) + ", " + res.getString(3) + ") am "
                                        + res.getInt(7) + "." + res.getInt(6) + ". (" + daysInbetween + ", " + res.getInt(0) + ") in " + res.getString(8) + "\n");
                            }break;
                        case "Other":
                            if (cbx_o.isChecked()){
                                overall_o += res.getInt(1);
                                overall_all += res.getInt(1);
                                builderDetails.insert(0,res.getInt(1) + " für " + res.getString(4) + " (" + res.getString(2) + ", " + res.getString(3) + ") am "
                                        + res.getInt(7) + "." + res.getInt(6) + ". (" + daysInbetween + ", " + res.getInt(0) + ") in " + res.getString(8) + "\n");
                            }break;
                        case "Big":
                            if (cbx_b.isChecked()){
                                overall_b += res.getInt(1);
                                overall_all += res.getInt(1);
                                builderDetails.insert(0,res.getInt(1) + " für " + res.getString(4) + " (" + res.getString(2) + ", " + res.getString(3) + ") am "
                                        + res.getInt(7) + "." + res.getInt(6) + ". (" + daysInbetween + ", " + res.getInt(0) + ") in " + res.getString(8) + "\n");
                            }break;

                        default:
                            overall_b += res.getInt(1);
                            break;
                    }
                }
            }
            int zeitspanne = date1to-date1from+1;
            if (overall_all==0){overall_all=1;}
            builderStats.append("Modus: Einzel\n");
            builderStats.append("Zeitraum: "+date1fromString+" bis "+date1toString+" ("+zeitspanne+" Tage)\n");
            if (cbx_f.isChecked()){builderStats.append("\nFood: "+overall_f+" Cent ("+overall_f/zeitspanne+" pro Tag, "+ overall_f*100/overall_all+" Prozent)\n");}
            if (cbx_a.isChecked()){builderStats.append("Living: "+overall_a+" Cent ("+overall_a/zeitspanne+" pro Tag, "+overall_a*100/overall_all+" Prozent)\n");}
            if (cbx_t.isChecked()){builderStats.append("Move: "+overall_t+" Cent ("+overall_t/zeitspanne+" pro Tag, "+overall_t*100/overall_all+" Prozent)\n");}
            if (cbx_o.isChecked()){builderStats.append("Other: "+overall_o+" Cent ("+overall_o/zeitspanne+" pro Tag, "+overall_o*100/overall_all+" Prozent)\n");}
            if (cbx_b.isChecked()){builderStats.append("Big: "+overall_b+" Cent ("+overall_b/zeitspanne+" pro Tag, "+overall_b*100/overall_all+" Prozent)\n\n");}
            builderStats.append("Auswahl addiert: "+overall_all+" Cent ("+overall_all/zeitspanne+" pro Tag)\n");
            builderStats.append("");
        }
        if (rbn_compare.isChecked()){
            while (res.moveToNext()) {
                DateTime checkDatedt = new DateTime(res.getInt(5), res.getInt(6), res.getInt(7), 13, 0, 0, UTC);
                daysInbetween = Days.daysBetween(travelStart.toLocalDateTime(), checkDatedt.toLocalDateTime()).getDays();
                if ((daysInbetween >= date1from) && (daysInbetween <= date1to)) {
                    String dataCategory = res.getString(res.getColumnIndex("CATEGORY"));
                    switch (dataCategory) {
                        case "Food":
                            if (cbx_f.isChecked()){
                                overall_f += res.getInt(1);
                                overall_all += res.getInt(1);
                                builderDetails.insert(0,res.getInt(1) + " für " + res.getString(4) + " (" + res.getString(2) + ", " + res.getString(3) + ") am "
                                        + res.getInt(7) + "." + res.getInt(6) + ". (" + daysInbetween + ", " + res.getInt(0) + ") in " + res.getString(8) + "\n");
                            }break;
                        case "Living":
                            if (cbx_a.isChecked()){
                                overall_a += res.getInt(1);
                                overall_all += res.getInt(1);
                                builderDetails.insert(0,res.getInt(1) + " für " + res.getString(4) + " (" + res.getString(2) + ", " + res.getString(3) + ") am "
                                        + res.getInt(7) + "." + res.getInt(6) + ". (" + daysInbetween + ", " + res.getInt(0) + ") in " + res.getString(8) + "\n");
                            }break;
                        case "Move":
                            if (cbx_t.isChecked()){
                                overall_t += res.getInt(1);
                                overall_all += res.getInt(1);
                                builderDetails.insert(0,res.getInt(1) + " für " + res.getString(4) + " (" + res.getString(2) + ", " + res.getString(3) + ") am "
                                        + res.getInt(7) + "." + res.getInt(6) + ". (" + daysInbetween + ", " + res.getInt(0) + ") in " + res.getString(8) + "\n");
                            }break;
                        case "Other":
                            if (cbx_o.isChecked()){
                                overall_o += res.getInt(1);
                                overall_all += res.getInt(1);
                                builderDetails.insert(0,res.getInt(1) + " für " + res.getString(4) + " (" + res.getString(2) + ", " + res.getString(3) + ") am "
                                        + res.getInt(7) + "." + res.getInt(6) + ". (" + daysInbetween + ", " + res.getInt(0) + ") in " + res.getString(8) + "\n");
                            }break;
                        case "Big":
                            if (cbx_b.isChecked()){
                                overall_b += res.getInt(1);
                                overall_all += res.getInt(1);
                                builderDetails.insert(0,res.getInt(1) + " für " + res.getString(4) + " (" + res.getString(2) + ", " + res.getString(3) + ") am "
                                        + res.getInt(7) + "." + res.getInt(6) + ". (" + daysInbetween + ", " + res.getInt(0) + ") in " + res.getString(8) + "\n");
                            }break;

                        default:
                            overall_b += res.getInt(1);
                            break;
                    }
                }
                if ((daysInbetween >= date2from) && (daysInbetween <= date2to)) {
                    String dataCategory = res.getString(res.getColumnIndex("CATEGORY"));
                    switch (dataCategory) {
                        case "Food":
                            if (cbx_f.isChecked()){
                                overall_f2 += res.getInt(1);
                                overall_all2 += res.getInt(1);
                                builderDetails2.insert(0,res.getInt(1) + " für " + res.getString(4) + " (" + res.getString(2) + ", " + res.getString(3) + ") am "
                                        + res.getInt(7) + "." + res.getInt(6) + ". (" + daysInbetween + ", " + res.getInt(0) + ") in " + res.getString(8) + "\n");
                            }break;
                        case "Living":
                            if (cbx_a.isChecked()){
                                overall_a2 += res.getInt(1);
                                overall_all2 += res.getInt(1);
                                builderDetails2.insert(0,res.getInt(1) + " für " + res.getString(4) + " (" + res.getString(2) + ", " + res.getString(3) + ") am "
                                        + res.getInt(7) + "." + res.getInt(6) + ". (" + daysInbetween + ", " + res.getInt(0) + ") in " + res.getString(8) + "\n");
                            }break;
                        case "Move":
                            if (cbx_t.isChecked()){
                                overall_t2 += res.getInt(1);
                                overall_all2 += res.getInt(1);
                                builderDetails2.insert(0,res.getInt(1) + " für " + res.getString(4) + " (" + res.getString(2) + ", " + res.getString(3) + ") am "
                                        + res.getInt(7) + "." + res.getInt(6) + ". (" + daysInbetween + ", " + res.getInt(0) + ") in " + res.getString(8) + "\n");
                            }break;
                        case "Other":
                            if (cbx_o.isChecked()){
                                overall_o2 += res.getInt(1);
                                overall_all2 += res.getInt(1);
                                builderDetails2.insert(0,res.getInt(1) + " für " + res.getString(4) + " (" + res.getString(2) + ", " + res.getString(3) + ") am "
                                        + res.getInt(7) + "." + res.getInt(6) + ". (" + daysInbetween + ", " + res.getInt(0) + ") in " + res.getString(8) + "\n");
                            }break;
                        case "Big":
                            if (cbx_b.isChecked()){
                                overall_b2 += res.getInt(1);
                                overall_all2 += res.getInt(1);
                                builderDetails2.insert(0,res.getInt(1) + " für " + res.getString(4) + " (" + res.getString(2) + ", " + res.getString(3) + ") am "
                                        + res.getInt(7) + "." + res.getInt(6) + ". (" + daysInbetween + ", " + res.getInt(0) + ") in " + res.getString(8) + "\n");
                            }break;

                        default:
                            overall_b2 += res.getInt(1);
                            break;
                    }
                }
            }
            int zeitspanne = date1to-date1from+1;
            int zeitspanne2 = date2to-date2from+1;
            if (overall_all==0){overall_all=1;}
            if (overall_all2==0){overall_all2=1;}
            builderStats.append("Modus: Vergleich\n");
            builderStats.append("Zeitraum: "+date1fromString+" bis "+date1toString+" ("+zeitspanne+" Tage) und\n"+date2fromString+" bis "+date2toString+" ("+zeitspanne2+" Tage)\n");
            if (cbx_f.isChecked()){builderStats.append("\nFood: "+overall_f+"---"+overall_f2+" Cent ("+overall_f/zeitspanne+"---"+overall_f2/zeitspanne2+" pro Tag, "+ overall_f*100/overall_all+"/"+overall_f2*100/overall_all2+"%)\n");}
            if (cbx_a.isChecked()){builderStats.append("Living: "+overall_a+"---"+overall_a2+" Cent ("+overall_a/zeitspanne+"---"+overall_a2/zeitspanne2+" pro Tag, "+overall_a*100/overall_all+"/"+overall_a2*100/overall_all2+"%)\n");}
            if (cbx_t.isChecked()){builderStats.append("Move: "+overall_t+"---"+overall_t2+" Cent ("+overall_t/zeitspanne+"---"+overall_t2/zeitspanne2+" pro Tag, "+overall_t*100/overall_all+"/"+overall_t2*100/overall_all2+"%)\n");}
            if (cbx_o.isChecked()){builderStats.append("Other: "+overall_o+"---"+overall_o2+" Cent ("+overall_o/zeitspanne+"---"+overall_o2/zeitspanne2+" pro Tag, "+overall_o*100/overall_all+"/"+overall_o2*100/overall_all2+"%)\n");}
            if (cbx_b.isChecked()){builderStats.append("Big: "+overall_b+"---"+overall_b2+" Cent ("+overall_b/zeitspanne+"---"+overall_b2/zeitspanne2+" pro Tag, "+overall_b*100/overall_all+"/"+overall_b2*100/overall_all2+"%)\n\n");}
            builderStats.append("Auswahl addiert: "+overall_all+"---"+overall_all2+" Cent ("+overall_all/zeitspanne+"---"+overall_all2/zeitspanne2+" pro Tag)\n");
            if (cbx_f.isChecked()){builderStats.append("\nFood: "+(overall_f-overall_f2)+" Cent ("+((overall_f/zeitspanne)-(overall_f2/zeitspanne2))+" pro Tag)\n");}
            if (cbx_a.isChecked()){builderStats.append("Living: "+(overall_a-overall_a2)+" Cent ("+((overall_a/zeitspanne)-(overall_a2/zeitspanne2))+" pro Tag)\n");}
            if (cbx_t.isChecked()){builderStats.append("Move: "+(overall_t-overall_t2)+" Cent ("+((overall_t/zeitspanne)-(overall_t2/zeitspanne2))+" pro Tag)\n");}
            if (cbx_o.isChecked()){builderStats.append("Other: "+(overall_o-overall_o2)+" Cent ("+((overall_o/zeitspanne)-(overall_o2/zeitspanne2))+" pro Tag)\n");}
            if (cbx_b.isChecked()){builderStats.append("Big: "+(overall_b-overall_b2)+" Cent ("+((overall_b/zeitspanne)-(overall_b2/zeitspanne2))+" pro Tag)\n");}
            builderStats.append("Auswahl addiert: "+(overall_all-overall_all2)+" Cent ("+((overall_all/zeitspanne)-(overall_all2/zeitspanne2))+" pro Tag)\n");
        }

        StringBuilder builder = new StringBuilder();
        builder.append(builderStats);
        builder.append("\n\n"+builderDetails);
        if (rbn_compare.isChecked()){builder.append("\n\n"+builderDetails2);}
        txv_statsdisplay.setText(builder);
    }

    public void onBackupDBClick(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            //not granted, ask for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            //granted, do backup
            Toast.makeText(MainActivity.this, myDB.exportDatabase("fateb.db"), Toast.LENGTH_LONG).show();
        }
    }
}