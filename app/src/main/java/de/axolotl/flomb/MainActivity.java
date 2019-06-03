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
import android.support.annotation.NonNull;
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ListIterator;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {

    public static String PACKAGE_NAME;

    //region Android ELemente
    public Button btn_addfinal, btn_add, btn_statssets, btn_datepicker, btn_back, btn_settings, btn_resetLastEntry;
    public EditText edt_place, edt_description, edt_amount, edt_deleteRowId;
    public TextView txv_headline, txv_addsumup, txv_sumup11, txv_statssetsumup, txv_statsdisplay;
    public RadioButton rbn_f, rbn_l, rbn_o, rbn_m, rbn_b, rbn_single, rbn_compare, rbn_change;
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
    private int overall_all, overall_withoutBig;
    private int overall_f2=0,overall_a2=0,overall_t2=0,overall_o2=0,overall_b2=0, overall_all2;
    private int[][] summed_subs;
    private int[] summed_cat;
    private String description="Beschreibung", category, subcategory, place;
    private DatePicker datepicker;
    private String dateAdd, dateString, date1fromString, date1toString, date2fromString, date2toString;
    private ArrayList<String> categories, categories_short, food_subcategories, living_subcategories,
            other_subcategories, move_subcategories, big_subcategories;

    private ArrayList<ArrayList<String>> sub_categories;
    private int daysInbetween, date1from, date1to, date2from, date2to, newestDayValue;
    public ArrayList<ArrayAdapter<CharSequence>> adapter_array;
    public ArrayList<RadioButton> rbn_list;
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
            txv_statssetsumup.setText(getString(R.string.dates_of) + date1fromString +
                    getString(R.string.until) + date1toString + getString(R.string.summarized));
        }
        else if (rbn_compare.isChecked()){
            txv_statssetsumup.setText(getString(R.string.dates_of) + date1fromString + " - "+
                    date1toString + getString(R.string.and) + date2fromString + " - " + date2toString + getString(R.string.compared));
        }
        else if (rbn_change.isChecked()){
            txv_statssetsumup.setText(getString(R.string.change_from) + date1fromString +
                    getString(R.string.until) + date1toString + getString(R.string.showed));
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
        rbn_l = findViewById(R.id.rbn_l);
        rbn_o = findViewById(R.id.rbn_o);
        rbn_m = findViewById(R.id.rbn_m);
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

        //sub_categories = new ArrayList<List<String>>();

        categories = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.categories)));
        categories_short = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.categories_short)));
        food_subcategories = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.food_subcategories)));
        living_subcategories = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.living_subcategories)));
        other_subcategories = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.other_subcategories)));
        move_subcategories = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.move_subcategories)));
        big_subcategories = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.big_subcategories)));
        sub_categories = new ArrayList<ArrayList<String>>();
        sub_categories.add(food_subcategories);
        sub_categories.add(living_subcategories);
        sub_categories.add(other_subcategories);
        sub_categories.add(move_subcategories);
        sub_categories.add(big_subcategories);

        summed_subs = new int[5][];
        summed_subs[0] = new int[food_subcategories.size()];
        summed_subs[1] = new int[living_subcategories.size()];
        summed_subs[2] = new int[other_subcategories.size()];
        summed_subs[3] = new int[move_subcategories.size()];
        summed_subs[4] = new int[big_subcategories.size()];
        summed_cat = new int[categories.size()];


        adapter_array = new ArrayList<>();
        adapter_array.add(ArrayAdapter.createFromResource(this,
                R.array.food_subcategories,android.R.layout.simple_spinner_item));
        adapter_array.add(ArrayAdapter.createFromResource(this,
                R.array.living_subcategories,android.R.layout.simple_spinner_item));
        adapter_array.add(ArrayAdapter.createFromResource(this,
                R.array.other_subcategories,android.R.layout.simple_spinner_item));
        adapter_array.add(ArrayAdapter.createFromResource(this,
                R.array.move_subcategories,android.R.layout.simple_spinner_item));
        adapter_array.add(ArrayAdapter.createFromResource(this,
                R.array.big_subcategories,android.R.layout.simple_spinner_item));

        for (int i = 0; i < adapter_array.size(); i++) {
            adapter_array.get(i).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        rbn_list = new ArrayList<>();
        rbn_list.add(rbn_f);
        rbn_list.add(rbn_l);
        rbn_list.add(rbn_o);
        rbn_list.add(rbn_m);
        rbn_list.add(rbn_b);

        spi_description.setAdapter(adapter_array.get(0));
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
                if (actionId == EditorInfo.IME_ACTION_NEXT){
                    //switched zu Datumsauwahl
                    setDate();
                    updateInformation();
                    handled = true;
                }
                return handled;
            }
        });

        edt_place.setOnEditorActionListener(new TextView.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT){
                    edt_amount.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edt_amount,InputMethodManager.SHOW_IMPLICIT);
                    edt_amount.setSelection(edt_amount.getText().length());
                    updateInformation();
                    handled = true;
                }
                if (actionId == EditorInfo.IME_ACTION_DONE){
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
                    updateInformation();
                    if (checkAddData())
                        insertToDB();
                    else Toast.makeText(MainActivity.this,getString(R.string.missing_data),Toast.LENGTH_LONG).show();
                    handled=true;
                }
                return handled;
            }
        });
        //endregion

        SharedPreferences prefGetter = getSharedPreferences("USER_PREFERENCES_ADD", MODE_PRIVATE);
        //TODO benutze diese gets für Èinstellung der Seite
        int checked_rbn = prefGetter.getInt("RBN", 0);
        rbn_list.get(checked_rbn).setChecked(true);
        spi_description.setAdapter(adapter_array.get(checked_rbn));
        category=categories.get(checked_rbn);
        subcategory=sub_categories.get(checked_rbn).get(0);
        edt_place.setText(prefGetter.getString("PLACE",""));
        if (edt_place.getText().toString().equals("")) cbx_keepdata.setChecked(true);
        int y = prefGetter.getInt("YEAR",2017);
        int m = prefGetter.getInt("MONTH",7);
        int d = prefGetter.getInt("DAY",5);
    }

    public void onStatssetsClick(View view) { //navigates from Main Menu to Statistic's Settings Menu
        rll_start.setVisibility(View.INVISIBLE);
        rll_statssets.setVisibility(View.VISIBLE);
        btn_back.setVisibility(View.VISIBLE);
        txv_headline.setText(R.string.statistics);

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
        txv_statssetsumup.setText(getString(R.string.dates_of) + date1fromString +
                getString(R.string.until) + date1toString + getString(R.string.summarized));
    }

    public void onSettingsClick(View view) {
        rll_start.setVisibility(View.INVISIBLE);
        rll_settings.setVisibility(View.VISIBLE);
        btn_back.setVisibility(View.VISIBLE);
        txv_headline.setText(R.string.settings);
    }

    public void goBack(){
        if (rll_add.getVisibility()==View.VISIBLE){
            rll_add.setVisibility(View.INVISIBLE);
            rll_start.setVisibility(View.VISIBLE);
            keepData();
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
            txv_headline.setText(R.string.statistics);
        }
    }

    public void onBackClick(View view) {
        goBack();
    }

    @Override
    public void onBackPressed() {
        //TODO exit dialog
        if (rll_start.getVisibility()==View.VISIBLE)
            {// exit dialog
            }
        else goBack();
    }
    //endregion

    //Datenbank auf SD laden
    public static void backupDatabase() throws IOException {
        //Open your local db as the input stream
        String inFileName = "/data/data/de/axolotl/flomb/databases/flomb.db";
        File dbFile = new File(inFileName);
        FileInputStream fis = new FileInputStream(dbFile);

        String outFileName = Environment.getExternalStorageDirectory() + "/flomb.db";
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
        updateInformation();
        if (checkAddData())
            insertToDB();
        else Toast.makeText(MainActivity.this,getString(R.string.missing_data),Toast.LENGTH_LONG).show();
    }

    public boolean checkAddData(){
        if (edt_description.getText().toString().length() == 0) return false;
        if (edt_place.getText().toString().length() == 0) return false;
        if (amount == 0) return false;
        if (btn_datepicker.getText().toString().equals(getString(R.string.date))) return false;
        return true;
    }

    public void insertToDB () {
        boolean isInserted = myDB.insertData(amount, category, subcategory, description, dateYear, dateMonth, dateDay, place);
        if (isInserted){
            Toast.makeText(MainActivity.this,getString(R.string.entry_added),Toast.LENGTH_LONG).show();
        }
        else Toast.makeText(MainActivity.this,getString(R.string.entry_not_added),Toast.LENGTH_LONG).show();
        keepData();
    }

    public void updateInformation(){
        if (edt_amount.getText().length()==0){
            edt_amount.setText("0");
        }
        amount = Integer.parseInt(edt_amount.getText().toString());
        if (cbx_minus.isChecked()) amount=amount*(-1);
        description = edt_description.getText().toString();
        place = edt_place.getText().toString();

        txv_addsumup.setText(amount+getString(R.string.für)+description+","+getString(R.string.für)+dateAdd+","+getString(R.string.in)+place);
    }

    public void updateFrontPage(){ //getAllData
        Cursor res = myDB.getAllData();

        StringBuilder builder = new StringBuilder();
        //region number reset
        newestDayValue = 0;
        overall_all = 0;

        for (int i = 0; i < summed_subs.length; i++)
            for (int j = 0; j < summed_subs[i].length; j++)
                summed_subs[i][j] = 0;
        //endregion

        DateTimeZone UTC = DateTimeZone.forID("UTC");
        DateTime travelStart= new DateTime(2017,7,5,12,0,0,UTC);

        while (res.moveToNext()){
            String kurz;
            String kurzOrt;
            String s = res.getString(2);
            kurz = categories_short.get(categories.indexOf(s));

            switch (res.getString(8)){
                case "Berlin": kurzOrt = "B."; break;
                case "Jena": kurzOrt = "J."; break;
                case "unterwegs": kurzOrt = "un."; break;
                default: kurzOrt = res.getString(8); break;
            }

            DateTime addDatedt = new DateTime(res.getInt(5),res.getInt(6),res.getInt(7),13,0,0,UTC);
            daysInbetween=Days.daysBetween(travelStart.toLocalDateTime(),addDatedt.toLocalDateTime()).getDays();
            //TODO set maximum count of lines to be shown
            //über ID von einträgen?
            builder.insert(0, res.getInt(1) + getString(R.string.für) + res.getString(4)
                    +" ("+kurz+", "+ res.getString(3)+")" + getString(R.string.am) + res.getInt(7)+"."
                    +res.getInt(6)+". ("+daysInbetween+", "+res.getInt(0)+") " + getString(R.string.in) +kurzOrt+"\n");

            String dataCategory = res.getString(res.getColumnIndex("CATEGORY"));
            String dataSub = res.getString(res.getColumnIndex("SUBCATEGORY"));

            int cat_index = categories.indexOf(dataCategory);
            int sub_index = sub_categories.get(cat_index).indexOf(dataSub);
            summed_subs[cat_index][sub_index] += res.getInt(1);


            if (daysInbetween>newestDayValue){
                newestDayValue=daysInbetween;
            }
        }

        if (newestDayValue==0){
            newestDayValue=1;
        }

        for (int i = 0; i < summed_cat.length; i++)
            summed_cat[i] = IntStream.of(summed_subs[i]).sum();

        overall_all = IntStream.of(summed_cat).sum();
        overall_withoutBig=overall_all-summed_cat[4];

        builder.insert(0,"Without Big: "+overall_withoutBig+" Cents ("+overall_withoutBig/newestDayValue+" pro Tag)\n\n");
        builder.insert(0,"\nALL: "+ overall_all +" Cents ("+overall_all/newestDayValue+" pro Tag)\n");
        for (int i = 0; i < summed_cat.length; i++) {
            builder.insert(0,categories.get(i) + " " + summed_cat[i] +" Cents ("+summed_cat[i]/newestDayValue+" pro Tag)\n");
        }

        builder.append("\n");

        for (int i = 0; i < summed_subs.length; i++) {
            for (int j = 0; j < summed_subs[i].length; j++) {
                builder.append(sub_categories.get(i).get(j) + ": " + summed_subs[i][j] + " Cents\n");
            }
        }

        txv_sumup11.setText(builder.toString());

        res.close();
    }

    public void onRbnGroupClick(View view) {
        int checkedIndex = -1;
        if (rbn_f.isChecked()) {
            checkedIndex = 0;
        }
        else if (rbn_l.isChecked()) {
            checkedIndex = 1;
        }
        else if (rbn_o.isChecked()){
            checkedIndex = 2;
        }
        else if (rbn_m.isChecked()){
            checkedIndex = 3;
        }
        else if (rbn_b.isChecked()){
            checkedIndex = 4;
        }

        spi_description.setAdapter(adapter_array.get(checkedIndex));
        category = categories.get(checkedIndex);
        subcategory = sub_categories.get(checkedIndex).get(0);
        updateInformation();
    }

    public void cbxMinusClick(View view) {
        updateInformation();
    }

    //region Datepicker
    public void onDateClick(View view) {
        setDate();
    }

    @SuppressWarnings("deprecation")
    public void setDate() {
        showDialog(999);
        Toast.makeText(getApplicationContext(), getString(R.string.choose_date), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3);
            //Ort wird fokussiert
            edt_place.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edt_place,InputMethodManager.SHOW_IMPLICIT);
            edt_place.setSelection(edt_place.getText().length());
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
        dateAdd = (day+"."+month+"."+year+" ("+daysInbetween+")");
        dateYear = year;
        dateMonth = month;
        dateDay = day;
    }
    //endregion

    public void onResetChosenClick(View view) {
        Integer deletedRows = myDB.deleteData(edt_deleteRowId.getText().toString());
        if (deletedRows > 0){
            Toast.makeText(MainActivity.this,R.string.data_del, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this,R.string.data_n_del, Toast.LENGTH_LONG).show();
        }
    }

    //region helper functions
    public void keepData(){
        SharedPreferences placeAndDate = getSharedPreferences("USER_PREFERENCES_ADD", MODE_PRIVATE);
        SharedPreferences.Editor editor = placeAndDate.edit();
        if (cbx_keepdata.isChecked()){
            editor.putString("PLACE",edt_place.getText().toString());
            //eingegebenes Datum
            editor.putInt("RBN", getIndexOfCheckedRbn(rbn_list));
            editor.putInt("YEAR",dateYear);
            editor.putInt("MONTH",dateMonth);
            editor.putInt("DAY",dateDay);
        }
        else {
            editor.putString("PLACE","");
            editor.putInt("RBN",0);
            //heutiges Datum
            editor.putInt("YEAR",year);
            editor.putInt("MONTH",month);
            editor.putInt("DAY",day);

            edt_description.setText("");
            edt_place.setText("");
            btn_datepicker.setText(getString(R.string.date));
            cbx_minus.setChecked(false);
        }
        editor.apply();
        updateFrontPage();
    }

    public int getIndexOfCheckedRbn(ArrayList<RadioButton> list){
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isChecked()) return i;
        }
        return 0;
    }

    public void onRbnStatsClick(View view) {
        if (rbn_single.isChecked()){
            sbr_date2from.setVisibility(View.INVISIBLE);
            sbr_date2to.setVisibility(View.INVISIBLE);
            txv_statssetsumup.setText(getString(R.string.dates_of) + date1fromString +
                    getString(R.string.until) + date1toString + getString(R.string.summarized));
        }
        else if (rbn_compare.isChecked()){
            sbr_date2from.setVisibility(View.VISIBLE);
            sbr_date2to.setVisibility(View.VISIBLE);
            txv_statssetsumup.setText(getString(R.string.dates_of) + date1fromString + " - "+
                    date1toString + getString(R.string.and) + date2fromString + " - " + date2toString + getString(R.string.compared));
        }
        else if (rbn_change.isChecked()){
            sbr_date2from.setVisibility(View.INVISIBLE);
            sbr_date2to.setVisibility(View.INVISIBLE);
            txv_statssetsumup.setText(getString(R.string.change_from) + date1fromString +
                    getString(R.string.until) + date1toString + getString(R.string.showed));
        }
    }

    public void showStats(View view) {
        if (!cbx_f.isChecked()&&!cbx_a.isChecked()&&!cbx_t.isChecked()&&!cbx_o.isChecked()&&!cbx_b.isChecked()){
            //nothing
            Toast.makeText(MainActivity.this, R.string.choose_category, Toast.LENGTH_LONG).show();
        }
        else {
            rll_statssets.setVisibility(View.INVISIBLE);
            rll_stats.setVisibility(View.VISIBLE);
            analyzeData();
        }
    }

    public void analyzeData(){
        for (int i = 0; i < summed_cat.length; i++)
            summed_cat[i] = 0;
        overall_all=0;
        overall_f2=0;overall_a2=0;overall_t2=0;overall_o2=0;overall_b2=0;overall_all2=0;
        //TODO hier memberfunction myDB.getQueryData(args) verwenden
        Cursor res = myDB.getAllData();
        StringBuilder builderDetails = new StringBuilder();
        StringBuilder builderDetails2 = new StringBuilder();
        StringBuilder builderStats = new StringBuilder();
        DateTimeZone UTC = DateTimeZone.forID("UTC");
        DateTime travelStart= new DateTime(2017,7,5,12,0,0,UTC);
        //TODO reduzieren!
        //TODO SQL Query mit allen Selections (schneller und schöner)
        /*
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
            //TODO in Schleife
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
            //TODO schleife vllt von oben?
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
        */
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
            Toast.makeText(MainActivity.this, myDB.exportDatabase("flomb.db"), Toast.LENGTH_LONG).show();
        }
    }
    //endregion

    //TODO methode update entry hinzufügen
    //TODO kein exit bei drehen des Handys
    //TODO delete last entry function + button
}