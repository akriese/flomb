package de.axolotl.flomb;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.opengl.Visibility;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import org.joda.time.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ListIterator;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    public static String PACKAGE_NAME;

    //region Android ELemente
    public Button btn_addfinal, btn_add, btn_statssets, btn_datepicker, btn_back, btn_settings, btn_resetEntry, btn_updateEntry;
    public EditText edt_place, edt_description, edt_amount, edt_editRow, edt_search, edt_loan_desc, edt_loan_amt;
    public TextView txv_headline, txv_addsumup, txv_sumup11, txv_statssetsumup, txv_statsdisplay;
    public RadioButton rbn_f, rbn_l, rbn_o, rbn_m, rbn_b, rbn_single, rbn_compare, rbn_change;
    public CheckBox cbx_keepdata, cbx_f, cbx_l, cbx_m, cbx_o, cbx_b, cbx_unmapLoan;
    public LinearLayout lnl_description, lnl_dateplace, lnl_cbxfateb, lnl_editRow, lnl_date1, lnl_date2;
    public RelativeLayout rll_add, rll_start, rll_statssets, rll_settings, rll_stats, rll_abo_loan;
    public ScrollView scv_sumup1;
    public Spinner spi_description;
    public Button btn_date1from, btn_date1to, btn_date2from, btn_date2to, btn_loan_to, btn_loan_from;

    //endregion
    //region other Elements and variables
    private int app_state;
    private int amount=0, id, datePickerMode;
    private int overall_all, overall_withoutBig, date_diff1, date_diff2;
    private int[][] summed_subs;
    private String description="Beschreibung", category, subcategory, place;
    private String dateAdd;
    private ArrayList<String> categories, categories_short, food_subcategories, living_subcategories,
            other_subcategories, move_subcategories, big_subcategories;

    private ArrayList<ArrayList<String>> sub_categories;
    private DateTime d1f, d1t, d2f, d2t, dLoanF, dLoanT;
    public ArrayList<ArrayAdapter<CharSequence>> adapter_array;
    public ArrayList<RadioButton> rbn_list;
    public ArrayList<CheckBox> cbx_list;
    DatabaseHelper myDB;
    DatabaseHelperBackup myDBBackup;
    private static final int FLOMB_START=0, FLOMB_ADD=1, FLOMB_SETTINGS=2, FLOMB_STATSETS=3, FLOMB_STATSHOW=4, FLOMB_LOAN=5,
            FLOMB_QUERYSHOW=6, FLOMB_UPDATE=7;

    enum APP_STATE {
        FLOMB_START, FLOMB_ADD, FLOMB_SETTINGS, FLOMB_STATSETS, FLOMB_STATSHOW, FLOMB_LOAN, FLOMB_QUERYSHOW, FLOMB_UPDATE;
    }

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
        lnl_description = findViewById(R.id.lnl_description);
        lnl_dateplace = findViewById(R.id.lnl_dateplace);
        rll_add = findViewById(R.id.rll_add);
        spi_description = findViewById(R.id.spi_description);
        //endregion
        //region stats
        rll_statssets = findViewById(R.id.rll_statssets);
        lnl_cbxfateb = findViewById(R.id.lnl_cbxfateb);
        cbx_f = findViewById(R.id.cbx_f);
        cbx_l = findViewById(R.id.cbx_l);
        cbx_m = findViewById(R.id.cbx_m);
        cbx_o = findViewById(R.id.cbx_o);
        cbx_b = findViewById(R.id.cbx_b);
        rbn_single = findViewById(R.id.rbn_single);
        rbn_compare = findViewById(R.id.rbn_compare);
        rbn_change = findViewById(R.id.rbn_change);
        btn_date1from = findViewById(R.id.btn_date1from);
        btn_date1to = findViewById(R.id.btn_date1to);
        btn_date2from = findViewById(R.id.btn_date2from);
        btn_date2to = findViewById(R.id.btn_date2to);
        txv_statssetsumup = findViewById(R.id.txv_statssetsumup);
        //endregion
        //region settings
        rll_settings = findViewById(R.id.rll_settings);
        lnl_editRow = findViewById(R.id.lnl_editRow);
        btn_resetEntry = findViewById(R.id.btn_resetEntry);
        btn_updateEntry = findViewById(R.id.btn_updateEntry);
        edt_editRow = findViewById(R.id.edt_editRowId);
        edt_search = findViewById(R.id.edt_search);
        btn_loan_from = findViewById(R.id.btn_date_abo_from);
        btn_loan_to = findViewById(R.id.btn_date_abo_to);
        edt_loan_amt = findViewById(R.id.edt_amount_abo);
        edt_loan_desc = findViewById(R.id.edt_desc_abo);
        //endregion
        //region stats
        rll_stats = findViewById(R.id.rll_stats);
        lnl_date1 = findViewById(R.id.lnl_date1);
        lnl_date2 = findViewById(R.id.lnl_date2);
        txv_statsdisplay = findViewById(R.id.txv_statsdisplay);
        //endregion
        //region abo / loan
        rll_abo_loan = findViewById(R.id.rll_abo_loanmapping);
        cbx_unmapLoan = findViewById(R.id.cbx_unmapLoan);
        //endregion
        //endregion

        myDB = new DatabaseHelper(this);
        myDBBackup = new DatabaseHelperBackup(this);

        rll_add.setVisibility(View.INVISIBLE);
        rll_statssets.setVisibility(View.INVISIBLE);
        rll_settings.setVisibility(View.INVISIBLE);
        rll_stats.setVisibility(View.INVISIBLE);
        rll_abo_loan.setVisibility(View.INVISIBLE);
        btn_back.setVisibility(View.INVISIBLE);
        app_state = FLOMB_START;

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

        cbx_list = new ArrayList<>();
        cbx_list.add(cbx_f);
        cbx_list.add(cbx_l);
        cbx_list.add(cbx_o);
        cbx_list.add(cbx_m);
        cbx_list.add(cbx_b);

        spi_description.setAdapter(adapter_array.get(0));
        spi_description.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor e = getSharedPreferences("USER_PREFERENCES_ADD", MODE_PRIVATE).edit();
                e.putInt("SUB", position);
                e.apply();
                subcategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //region datepicker listeners
        datePickerMode = 0;
        btn_datepicker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                datePickerMode = 0;
                showDatePickerDialog();
            }
        });

        btn_date1from.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                datePickerMode = 1;
                showDatePickerDialog();
            }
        });

        btn_date1to.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                datePickerMode = 2;
                showDatePickerDialog();
            }
        });

        btn_date2from.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                datePickerMode = 3;
                showDatePickerDialog();
            }
        });

        btn_date2to.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                datePickerMode = 4;
                showDatePickerDialog();
            }
        });

        btn_loan_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerMode = 1;
                showDatePickerDialog();
            }
        });

        btn_loan_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerMode = 2;
                showDatePickerDialog();
            }
        });
        //endregion

        updateFrontPage();
        PACKAGE_NAME = getApplicationContext().getPackageName();
    }

    public void updateFrontPage(){ //getAllData
        Cursor res = myDB.getPastMonth();
        if (res.getCount() == 0){
            txv_sumup11.setText(R.string.no_data_in_month);
            return;
        }
        StringBuilder builder = buildQueriedData(res);

        txv_sumup11.setText(builder.toString());

        res.close();
    }

    //region Main Movements
    public void goToAddLayout(){
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
                updateInformation();
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
                    showDatePickerDialog();
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
                    addOrUpdate();
                    handled=true;
                }
                return handled;
            }
        });
        //endregion
    }

    public void onAddClick(View view) { //navigates from Main Menu to Add Menu
        goToAddLayout();
        app_state = FLOMB_ADD;
        txv_headline.setText(R.string.add);
        btn_addfinal.setText(R.string.add);
        SharedPreferences prefGetter = getSharedPreferences("USER_PREFERENCES_ADD", MODE_PRIVATE);

        int checked_rbn = prefGetter.getInt("RBN", 0);
        rbn_list.get(checked_rbn).setChecked(true);
        spi_description.setAdapter(adapter_array.get(checked_rbn));
        category = categories.get(checked_rbn);
        spi_description.setSelection(prefGetter.getInt("SUB", 0));
        subcategory = sub_categories.get(checked_rbn).get(prefGetter.getInt("SUB", 0));
        edt_place.setText(prefGetter.getString("PLACE",""));
        if (edt_place.getText().toString().equals("")) cbx_keepdata.setChecked(true);
        dateAdd = prefGetter.getString("DATE", d_to_s(DateTime.now(), "en"));
        amount = prefGetter.getInt("AMOUNT", 0);
        edt_amount.setText(Integer.toString(amount));
    }

    public void onStatssetsClick(View view) { //navigates from Main Menu to Statistic's Settings Menu
        app_state = FLOMB_STATSETS;
        rll_start.setVisibility(View.INVISIBLE);
        rll_statssets.setVisibility(View.VISIBLE);
        btn_back.setVisibility(View.VISIBLE);
        txv_headline.setText(R.string.statistics);
        rbn_single.setChecked(true);
        lnl_date2.setVisibility(View.INVISIBLE);
        btn_date1from.setText(R.string.date1from);
        btn_date1to.setText(R.string.date1to);
        btn_date2from.setText(R.string.date2from);
        btn_date2to.setText(R.string.date2to);

        setDefaultDatesOnPickers();

        txv_statssetsumup.setText(getString(R.string.dates_of) + d_to_s(d1f, "de") + getString(R.string.until) + d_to_s(d1t, "de") + getString(R.string.summarized) + " ("+date_diff1+" Tage)");
    }

    public void onSettingsClick(View view) {
        app_state = FLOMB_SETTINGS;
        rll_start.setVisibility(View.INVISIBLE);
        rll_settings.setVisibility(View.VISIBLE);
        btn_back.setVisibility(View.VISIBLE);
        txv_headline.setText(R.string.settings);
    }

    public void goBack(){
        switch (app_state){
            case FLOMB_STATSHOW:
                app_state = FLOMB_STATSETS;
                rll_stats.setVisibility(View.INVISIBLE);
                btn_back.setVisibility(View.VISIBLE);
                break;
            case FLOMB_UPDATE:
                rll_add.setVisibility(View.INVISIBLE);
                keepData();
            case FLOMB_LOAN:
                rll_abo_loan.setVisibility(View.INVISIBLE);
            case FLOMB_QUERYSHOW:
                app_state = FLOMB_SETTINGS;
                rll_settings.setVisibility(View.VISIBLE);
                rll_stats.setVisibility(View.INVISIBLE);
                txv_headline.setText(R.string.settings);
                break;
            case FLOMB_ADD:
                rll_add.setVisibility(View.INVISIBLE);
                keepData();
            case FLOMB_STATSETS:
                rll_statssets.setVisibility(View.INVISIBLE);
            case FLOMB_SETTINGS:
                rll_settings.setVisibility(View.INVISIBLE);
                rll_start.setVisibility(View.VISIBLE);
                txv_headline.setText(getString(R.string.app_name));
                btn_back.setVisibility(View.INVISIBLE);
                app_state = FLOMB_START;
                updateFrontPage();
                break;
            default: Log.wtf("APP_STATE_ERROR", "app_state set or queried wrongly!");
        }
    }

    public void onBackClick(View view) {
        goBack();
    }

    @Override
    public void onBackPressed() {
        if (app_state == FLOMB_START) {// exit dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Wanna exit, biiitch?")
                    .setCancelable(false)
                    .setPositiveButton("Hell yeah!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("Nope!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else goBack();
    }
    //endregion

    //region Datepicker
    public void setDefaultDatesOnPickers(){
        Calendar c = Calendar.getInstance();
        if (app_state == FLOMB_STATSETS) {
            SharedPreferences p = getSharedPreferences("USER_PREFERENCES_STATS", MODE_PRIVATE);
            String default_date = d_to_s(DateTime.now(), "en");
            d1f = s_to_d(p.getString("D1F", default_date), "en");
            d2f = s_to_d(p.getString("D2F", default_date), "en");
            d1t = s_to_d(p.getString("D1T", default_date), "en");
            d2t = s_to_d(p.getString("D2T", default_date), "en");
            btn_date1from.setText(getString(R.string.from) + d_to_s(d1f, "de"));
            btn_date1to.setText(getString(R.string.to) + d_to_s(d1t, "de"));
            btn_date2from.setText(getString(R.string.from) + d_to_s(d2f, "de"));
            btn_date2to.setText(getString(R.string.to) + d_to_s(d2t, "de"));
            calcDateDiff(d1f,d1t,1);
            calcDateDiff(d2f,d2t,2);
        }
        else if (app_state == FLOMB_LOAN) {
            //TODO choose proper dates
            dLoanF = new DateTime(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH), 13,0,0);
            dLoanT = new DateTime(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH), 13,0,0);
            btn_loan_from.setText(d_to_s(dLoanF, "de"));
            btn_loan_to.setText(d_to_s(dLoanT, "de"));
        }
    }

    public void showDatePickerDialog(){
        DateTime dtAdd = s_to_d(dateAdd, "en");
        int loc_y = dtAdd.getYear();
        int loc_m = dtAdd.getMonthOfYear();
        int loc_d = dtAdd.getDayOfMonth();
        if (app_state == FLOMB_STATSETS){
            switch(datePickerMode){
                case 0: break;
                case 1: loc_y = d1f.getYear(); loc_m = d1f.getMonthOfYear(); loc_d = d1f.getDayOfMonth(); break;
                case 2:
                    if (d1t.isAfter(d1f)) {loc_y = d1t.getYear(); loc_m = d1t.getMonthOfYear(); loc_d = d1t.getDayOfMonth();}
                    else {loc_y = d1f.getYear(); loc_m = d1f.getMonthOfYear(); loc_d = d1f.getDayOfMonth()+1;}
                    break;
                case 3: loc_y = d2f.getYear(); loc_m = d2f.getMonthOfYear(); loc_d = d2f.getDayOfMonth(); break;
                case 4:
                    if (d1t.isAfter(d1f)) {loc_y = d2t.getYear(); loc_m = d2t.getMonthOfYear(); loc_d = d2t.getDayOfMonth();}
                    else {loc_y = d2f.getYear(); loc_m = d2f.getMonthOfYear(); loc_d = d2f.getDayOfMonth()+1;}
                    break;
                default:
            }
        }
        else if (app_state == FLOMB_LOAN) {
            switch (datePickerMode){
                case 1: loc_y = dLoanF.getYear(); loc_m = dLoanF.getMonthOfYear(); loc_d = dLoanF.getDayOfMonth(); break;
                case 2:
                    if (dLoanT.isAfter(dLoanF)) {loc_y = dLoanT.getYear(); loc_m = dLoanT.getMonthOfYear(); loc_d = dLoanT.getDayOfMonth();}
                    else {loc_y = dLoanF.getYear(); loc_m = dLoanF.getMonthOfYear(); loc_d = dLoanF.getDayOfMonth()+1;}
                    break;
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,this, loc_y, loc_m-1, loc_d);
        Toast.makeText(getApplicationContext(), getString(R.string.choose_date), Toast.LENGTH_SHORT).show();
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
        // arg1 = year, arg2 = month, arg3 = day

        if (app_state == FLOMB_ADD || app_state == FLOMB_UPDATE){
            if (datePickerMode == 0) {
                showDateAddMode(arg1, arg2 + 1, arg3);
                //Ort wird fokussiert
                edt_place.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edt_place, InputMethodManager.SHOW_IMPLICIT);
                edt_place.setSelection(edt_place.getText().length());
                updateInformation();
            }
        }
        else if (app_state == FLOMB_STATSETS) {
            SharedPreferences.Editor e = getSharedPreferences("USER_PREFERENCES_STATS", MODE_PRIVATE).edit();
            switch (datePickerMode){
                case 1: d1f = new DateTime(arg1, arg2+1, arg3, 13, 0,0);
                    btn_date1from.setText(getString(R.string.from) + d_to_s(d1f, "de"));
                    calcDateDiff(d1f, d1t, 1);
                    e.putString("D1F", d_to_s(d1f, "en")); break;
                case 2: d1t = new DateTime(arg1, arg2+1, arg3, 13, 0,0);
                    btn_date1to.setText(getString(R.string.to) + d_to_s(d1t, "de"));
                    calcDateDiff(d1f, d1t, 1);
                    e.putString("D1T", d_to_s(d1t, "en")); break;
                case 3: d2f = new DateTime(arg1, arg2+1, arg3, 13, 0,0);
                    btn_date2from.setText(getString(R.string.from) + d_to_s(d2f, "de"));
                    calcDateDiff(d2f, d2t, 2);
                    e.putString("D2F", d_to_s(d2f, "en")); break;
                case 4: d2t = new DateTime(arg1, arg2+1, arg3, 13, 0,0);
                    btn_date2to.setText(getString(R.string.to) + d_to_s(d2t, "de"));
                    calcDateDiff(d2f, d2t, 2);
                    e.putString("D2T", d_to_s(d2t, "en")); break;
            }
            e.apply();
        }
        else if (app_state == FLOMB_LOAN){
            if (datePickerMode == 1) {
                dLoanF = new DateTime(arg1, arg2 + 1, arg3, 13, 0, 0);
                btn_loan_from.setText(d_to_s(dLoanF, "de"));
            }
            else {
                dLoanT = new DateTime(arg1, arg2 + 1, arg3, 13, 0, 0);
                btn_loan_to.setText(d_to_s(dLoanT, "de"));
            }
        }
    }

    //set Button text and sets variables for adding
    private void showDateAddMode(int year, int month, int day) {
        DateTime d = new DateTime(year, month, day, 0, 0, 0);
        btn_datepicker.setText(d_to_s(d, "de"));
        dateAdd = d_to_s(d, "en");
    }

    public void calcDateDiff(DateTime a, DateTime b, int number){
        if (a.isAfter(b)) return;
        Duration dur = new Duration(a,b);
        int diff = (int) dur.getStandardDays()+1;
        if (number==1) date_diff1 = diff;
        else date_diff2 = diff;
    }
    //endregion

    //region Add + children
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

    public void addData(View view){
        addOrUpdate();
    }

    public void addOrUpdate(){
        updateInformation();
        if (checkAddData())
            if (app_state == FLOMB_ADD) insertToDB();
            else if (app_state == FLOMB_UPDATE) updateDB();
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
        boolean isInserted = myDB.insertData(amount, category, subcategory, description, place, dateAdd);
        if (isInserted) Toast.makeText(MainActivity.this,getString(R.string.entry_added),Toast.LENGTH_LONG).show();
        else Toast.makeText(MainActivity.this,getString(R.string.entry_not_added),Toast.LENGTH_LONG).show();
        keepData();
    }

    public void updateDB () {
        boolean isInserted = myDB.updateData(id, amount, category, subcategory, description, place, dateAdd);
        if (isInserted){
            Toast.makeText(MainActivity.this,getString(R.string.entry_updated),Toast.LENGTH_LONG).show();
        }
        else Toast.makeText(MainActivity.this,getString(R.string.entry_not_updated),Toast.LENGTH_LONG).show();
        goBack();
    }

    public void updateInformation(){
        String t = edt_amount.getText().toString();
        if (t.length() == 0) edt_amount.setText("0");
        else if (!(t.charAt(0) == '-' && t.length() == 1))
            amount = Integer.parseInt(edt_amount.getText().toString());
        description = edt_description.getText().toString();
        place = edt_place.getText().toString();

        txv_addsumup.setText(amount+getString(R.string.für)+description+","+getString(R.string.am)+dateAdd+","+getString(R.string.in)+place);
    }

    public void keepData(){
        SharedPreferences placeAndDate = getSharedPreferences("USER_PREFERENCES_ADD", MODE_PRIVATE);
        SharedPreferences.Editor editor = placeAndDate.edit();
        if (cbx_keepdata.isChecked()){
            editor.putString("PLACE",edt_place.getText().toString());
            editor.putInt("RBN", getIndexOfCheckedRbn(rbn_list));
            editor.putInt("SUB", spi_description.getSelectedItemPosition());
            //eingegebenes Datum
            editor.putString("DATE", dateAdd);
            editor.putInt("AMOUNT", amount);
        }
        else {
            editor.putString("PLACE","");
            editor.putInt("RBN", 0);
            editor.putInt("SUB", 0);
            editor.putInt("AMOUNT", 0);
            //heutiges Datum
            editor.putString("DATE", d_to_s(DateTime.now(), "en"));

            edt_description.setText("");
            edt_place.setText("");
            btn_datepicker.setText(getString(R.string.date));
        }
        editor.apply();
        updateFrontPage();
    }
    //endregion add + children

    //region Stats + children
    public void onRbnStatsClick(View view) {
        if (rbn_single.isChecked()){
            lnl_date2.setVisibility(View.INVISIBLE);
            txv_statssetsumup.setText(getString(R.string.dates_of) + d_to_s(d1f, "de") +
                    getString(R.string.until) + d_to_s(d1t, "de")+ getString(R.string.summarized));
        }
        else if (rbn_compare.isChecked()){
            lnl_date2.setVisibility(View.VISIBLE);
            txv_statssetsumup.setText(getString(R.string.dates_of) + d_to_s(d1f , "de")+ " - "+
                    d_to_s(d1t, "de")+ getString(R.string.and) + d_to_s(d2f, "de")+ " - " + d_to_s(d2t, "de") + getString(R.string.compared));
        }
        else if (rbn_change.isChecked()){
            lnl_date2.setVisibility(View.INVISIBLE);
            txv_statssetsumup.setText(getString(R.string.change_from) + d_to_s(d1f, "de") +
                    getString(R.string.until) + d_to_s(d1t, "de") + getString(R.string.showed));
        }
    }

    public void showStats(View view) {
        if (!cbx_f.isChecked() && !cbx_l.isChecked() && !cbx_m.isChecked() && !cbx_o.isChecked() && !cbx_b.isChecked()){
            //nothing
            Toast.makeText(MainActivity.this, R.string.choose_category, Toast.LENGTH_LONG).show();
        }
        else {
            rll_statssets.setVisibility(View.INVISIBLE);
            rll_stats.setVisibility(View.VISIBLE);
            app_state = FLOMB_STATSHOW;
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            analyzeData();
        }
    }

    public void analyzeData(){
        String chosen_cats = "";
        for (int i = 0; i < cbx_list.size(); i++) {
            if (cbx_list.get(i).isChecked()) {
                if (!chosen_cats.equals("")) chosen_cats+=",";
                chosen_cats += "'"+categories.get(i)+"'";
            }
        }
        StringBuilder builderDetails = new StringBuilder();
        StringBuilder builderStats = new StringBuilder();
        ArrayList<ArrayList<String>> content = new ArrayList<>();
        Cursor r = myDB.getQueryData(chosen_cats, d_to_s(d1f, "en"), d_to_s(d1t, "en"));
        if (r.getCount() == 0) return;
        while (r.moveToNext()) {
            ArrayList<String> entry = new ArrayList<>(
                    Arrays.asList(r.getString(0), c_to_e(r.getInt(1)), r.getString(2),
                            r.getString(3), r.getString(4), r.getString(8), r.getString(9))
            );
            content.add(entry);
        }

        //sortiert nach daten
        content.sort(((p1,p2) -> p2.get(6).compareTo(p1.get(6))));

        //TODO schön formatieren
        for (int i = 0; i < content.size(); i++){
            builderDetails.append(content.get(i).toString()+"\n");
        }

        builderStats.append(getString(R.string.dates_of) + d_to_s(d1f, "de") + getString(R.string.until) + d_to_s(d1t, "de") + getString(R.string.summarized)+" ("+date_diff1+" Tage)\n\n");
        r = myDB.getSummaryOfQuery(chosen_cats, d_to_s(d1f, "en"), d_to_s(d1t, "en"));
        while (r.moveToNext()){
            int amt = r.getInt(1);
            builderStats.append(r.getString(0)+": "+c_to_e(amt)+" ("+ c_to_e((amt+0.0)/date_diff1) +" pro Tag)\n");
            Log.wtf("WHILE",r.getString(0)+": "+r.getString(1));
        }

        //Cursor res2 = myDB.getQueryData(str_arr, d2f, d2t);

        StringBuilder builder = new StringBuilder();
        builder.append(builderStats);
        builder.append("\n\n"+builderDetails);
        txv_statsdisplay.setText(builder.toString());
    }
    //endregion

    //region Helper functions
    //date to string
    public String d_to_s(DateTime a, String form) {
        switch (form){
            case "de":
                return ((a.getDayOfMonth()<10) ? "0" : "")+a.getDayOfMonth()+"."+((a.getMonthOfYear()<10) ? "0" : "")+a.getMonthOfYear()+"." + a.getYear();
            case "en":
            default :
                return a.getYear()+"-"+((a.getMonthOfYear()<10) ? "0" : "")+a.getMonthOfYear()+"-" +((a.getDayOfMonth()<10) ? "0" : "")+a.getDayOfMonth() ;
        }
    }

    //string to date
    public DateTime s_to_d(String date, String form){
        DateTime r;
        String[] arr;
        switch (form){
            case "de":
                arr = date.split(".");
                r = new DateTime(Integer.parseInt(arr[2]), Integer.parseInt(arr[1]), Integer.parseInt(arr[0]),0,0);
                break;
            case "en":
            default:
                arr = date.split("-");
                r = new DateTime(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]),0,0);
        }
        return r;
    }

    //getQueriedData
    public StringBuilder buildQueriedData(Cursor c){
        StringBuilder res = new StringBuilder();
        c.moveToPosition(-1);

        while (c.moveToNext()){
            String kurz, kurzOrt;
            String s = c.getString(2);
            kurz = categories_short.get(categories.indexOf(s));

            switch (c.getString(8)){
                case "Berlin": kurzOrt = "B."; break;
                case "Jena": kurzOrt = "J."; break;
                case "unterwegs": kurzOrt = "un."; break;
                default: kurzOrt = c.getString(8); break;
            }

            res.append(c_to_e(c.getInt(1)) +getString(R.string.für) + c.getString(4)
                    +" ("+kurz+", "+ c.getString(3)+")" + getString(R.string.am) + c.getString(9)+" ("+c.getInt(0)+")" + getString(R.string.in) +kurzOrt+"\n");
        }

        res.insert(0, "\n");

        //ermittle #Tage des letzten Monats
        String d1,d2;
        c.moveToFirst();
        d2 = c.getString(9);
        c.moveToLast();
        d1 = c.getString(9);
        Cursor r2 = myDB.getDaysBetween(d1,d2);
        r2.moveToFirst();
        double between = (double) r2.getInt(0) + 1;
        Log.wtf("BETWEEN", ""+between);

        c = myDB.getSummaryOfPastMonth();
        overall_all = overall_withoutBig = 0;
        while (c.moveToNext()){
            int amnt = c.getInt(1);
            String cat = c.getString(0);
            overall_all += amnt; overall_withoutBig += amnt;
            if (cat.equals("Big"))
                overall_withoutBig -= amnt;
            res.insert(0, cat + ": " + c_to_e(amnt) +" ("+c_to_e(amnt/between)+" pro Tag)\n");
        }

        res.insert(0,"Without Big: "+c_to_e(overall_withoutBig)+" ("+c_to_e(overall_withoutBig/between)+" pro Tag)\n\n");
        res.insert(0,"ALL: "+ c_to_e(overall_all) +" ("+c_to_e(overall_all/between)+" pro Tag)\n");

        return res;
    }


    //cents to euros
    public String c_to_e(double cents){
        DecimalFormat df = new DecimalFormat("0.00");
        double val = (double)((int) cents)/100.0;
        return df.format(val) + "€";
    }

    public int getIndexOfCheckedRbn(ArrayList<RadioButton> list){
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isChecked()) return i;
        }
        return 0;
    }

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
    //endregion

    //region Settings + children
    public void resetEntry() {
        int deletedRows, id = getChosenID(0);
        if (id == -1) {
            Toast.makeText(MainActivity.this, R.string.no_or_wrong_id, Toast.LENGTH_LONG).show();
            return;
        }
        deletedRows = myDB.deleteData(id);
        if (deletedRows > 0){
            Toast.makeText(MainActivity.this,R.string.data_del, Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(MainActivity.this,R.string.data_n_del, Toast.LENGTH_LONG).show();
        }
    }

    public void onResetChosenClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.delete_question))
                .setCancelable(true)
                .setTitle("Delete Alert")
                .setPositiveButton("Kill it!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resetEntry();
                    }
                })
                .setNegativeButton("Let if live!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public int getChosenID(int edt) {
        int res = 0;
        String info;
        switch (edt){
            case 0: {
                info = edt_editRow.getText().toString();
                if (info.equals("")) return -1;
                res = Integer.parseInt(info);
                if (res == -1) res = myDB.getLastEntryID();
                else if (res < 0) return -1;
            }
        }
        return res;
    }

    public void setupEntryPage(boolean copy) {
        id = getChosenID(0);
        if (id == -1) {
            Toast.makeText(MainActivity.this, R.string.no_or_wrong_id, Toast.LENGTH_LONG).show();
            return;
        }

        Cursor res = myDB.searchForUpdateEntry(id);
        edt_editRow.setText("");
        if (res.getCount() == 0){
            Toast.makeText(MainActivity.this, getString(R.string.id_not_available),Toast.LENGTH_LONG).show();
            return;
        }
        rll_settings.setVisibility(View.INVISIBLE);
        goToAddLayout();
        if (copy) {
            app_state = FLOMB_ADD;
            txv_headline.setText(R.string.copy_entry);
            btn_addfinal.setText(R.string.copy_entry);
        }
        else {
            app_state = FLOMB_UPDATE;
            txv_headline.setText(R.string.update_entry);
            btn_addfinal.setText(R.string.update_entry);
        }

        res.moveToNext();

        //set rbn
        category = res.getString(2);
        int catIndex = categories.indexOf(category);
        rbn_list.get(catIndex).setChecked(true);

        //set sub_cat
        //set var
        subcategory = res.getString(3);
        spi_description.setAdapter(adapter_array.get(catIndex));
        spi_description.setSelection(sub_categories.get(catIndex).indexOf(subcategory));

        // set date
        dateAdd = res.getString(9);
        btn_datepicker.setText(dateAdd);

        //set Description, place, amount
        description = res.getString(4);
        place = res.getString(8);
        edt_description.setText(description);
        edt_place.setText(place);
        amount = res.getInt(1);

        edt_amount.setText(amount);

        updateInformation();
    }

    public void onUpdateClick(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        setupEntryPage(false);

        cbx_keepdata.setChecked(false);
    }

    public void onCopyClick(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        setupEntryPage(true);
    }

    public StringBuilder rawQuery(String q, boolean raw){
        Cursor r;
        if (raw) r = myDB.doQuery(q);
        else r = myDB.searchQuery(q);

        int col = r.getColumnCount();
        StringBuilder sb = new StringBuilder();
        while (r.moveToNext()){
            String s = "";
            for (int i = 0; i < col; i++){
                s += r.getString(i) + ", ";
            }
            sb.append(s+"\n");
        }
        return sb;
    }

    public void displayQueryResults(StringBuilder res, boolean raw){
        rll_settings.setVisibility(View.INVISIBLE);
        rll_stats.setVisibility(View.VISIBLE);
        txv_headline.setText(R.string.results);

        txv_statsdisplay.setText(res);
        app_state = FLOMB_QUERYSHOW;
    }

    public void onQueryClick(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        StringBuilder res;
        try {
            res = rawQuery(edt_search.getText().toString(),true);
        }
        catch(Exception e){
            Toast.makeText(MainActivity.this, R.string.bad_query, Toast.LENGTH_LONG).show();
            return;
        }
        displayQueryResults(res, true);
    }

    public void onSearchClick(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        StringBuilder res = rawQuery(edt_search.getText().toString(),false);
        displayQueryResults(res, false);
    }

    public void onMapLoanClick(View view) {
        rll_abo_loan.setVisibility(View.VISIBLE);
        rll_settings.setVisibility(View.INVISIBLE);
        app_state = FLOMB_LOAN;
        setDefaultDatesOnPickers();
    }

    public void onCreateAboClick(View view) {
    }

    public void onBackupDBClick(View view) {
        /*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            //not granted, ask for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            //granted, do backup
            Toast.makeText(MainActivity.this, myDB.exportDatabase("flomb.db"), Toast.LENGTH_LONG).show();
        }
        */
    }

    public void mapLoan(View view) {
        String purpose = edt_loan_desc.getText().toString();
        int dec;
        if (!(cbx_unmapLoan.isChecked())) { //Map Loan
            if (edt_loan_amt.getText().toString().equals(""))
                dec = -1;
            else {
                int sum = Integer.parseInt(edt_loan_amt.getText().toString());
                dec = myDB.mapLoanToWorkingHours(d_to_s(dLoanF, "en"), d_to_s(dLoanT, "en"), sum, purpose);
            }
        }
        else // Unmap Loan
            dec = myDB.undoMapLoan(d_to_s(dLoanF, "en"), d_to_s(dLoanT, "en"), purpose);
        if (dec == 0){
            Toast.makeText(MainActivity.this, "Wage mapped to given dates!", Toast.LENGTH_LONG).show();
            edt_loan_amt.setText("0");
        }
        else if (dec == 1)
            Toast.makeText(MainActivity.this, "No entries found to map!", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(MainActivity.this, "Problem with mapping the given data!", Toast.LENGTH_LONG).show();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    //endregion settings + children

    //region TO-DO area
    //TODO 7    abo-func
    //TODO 8    txv: link to update-window --> faster updates
    //TODO 3    use same func for stats, search and front page
    //TODO 6    remember/recommendation func for edt_description (AI style?), AutoCompleteTextView
    //TODO 9    colored lines (for better readabilty)
    //TODO      output layout to (SUB, CAT, ID)
    //TODO 4    write one function for displaying, and use that for every part of the app
    //TODO 2    go to only String and DateTime-Mode (no int gibberish anymore)
    //TODO 5    format output so that it's formed to columns

    //endregion
}