package com.automotive.hhi.mileagetracker.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.automotive.hhi.mileagetracker.KeyContract;
import com.automotive.hhi.mileagetracker.R;
import com.automotive.hhi.mileagetracker.model.data.Car;
import com.automotive.hhi.mileagetracker.model.data.Fillup;
import com.automotive.hhi.mileagetracker.model.data.Station;
import com.automotive.hhi.mileagetracker.presenter.AddFillupPresenter;
import com.automotive.hhi.mileagetracker.view.interfaces.AddFillupView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AddFillupActivity extends AppCompatActivity implements AddFillupView
        , DatePickerDialog.OnDateSetListener {

    private final String LOG_TAG = AddFillupActivity.class.getSimpleName();

    @Bind(R.id.add_fillup_date_container)
    public LinearLayout mDateInputContainer;
    @Bind(R.id.add_fillup_date)
    public TextView mDate;
    @Bind(R.id.add_fillup_station)
    public TextView mStation;
    @Bind(R.id.add_fillup_station_address)
    public TextView mStationAddress;
    @Bind(R.id.add_fillup_fuel_amount)
    public EditText mFuelAmount;
    @Bind(R.id.add_fillup_price)
    public EditText mFuelPrice;
    @Bind(R.id.add_fillup_octane)
    public EditText mOctane;
    @Bind(R.id.add_fillup_current_mileage)
    public EditText mMileage;
    @Bind(R.id.add_fillup_submit)
    public Button mAddFillup;
    @Bind(R.id.add_fillup_layout)
    public LinearLayout mInputContainer;
    @Bind(R.id.add_fillup_station_container)
    public LinearLayout mStationContainer;
    private AddFillupPresenter mAddFillupPresenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAddFillupPresenter =
                new AddFillupPresenter((Fillup) getIntent().getParcelableExtra(KeyContract.FILLUP)
                        , (Car) getIntent().getParcelableExtra(KeyContract.CAR)
                        , (Station) getIntent().getParcelableExtra(KeyContract.STATION)
                        , getIntent().getBooleanExtra(KeyContract.IS_EDIT, false)
                        , getContext());
        setContentView(R.layout.activity_add_fillup);
        ButterKnife.bind(this);
        mAddFillupPresenter.attachView(this);
        if(mAddFillupPresenter.getIsEdit()){
            mAddFillup.setText(getResources().getString(R.string.add_fillup_edit_button));
        } else{
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            mDate.setText(sdf.format(new Date()));
        }
        setStationText();

    }

    @OnClick(R.id.add_fillup_submit)
    public void onButtonPressed() {
        mAddFillupPresenter.checkStation();
        if(mAddFillupPresenter.validateInput(mInputContainer)){
            setResult(RESULT_OK, new Intent());
            finish();
        }
    }

    @OnClick(R.id.add_fillup_station_container)
    public void addFillup(){
        mAddFillupPresenter.launchAddStation();
    }

    @OnClick(R.id.add_fillup_date_container)
    public void showDatePickerDialog(){
        mAddFillupPresenter
                .buildDatePickerFragment()
                .show(getSupportFragmentManager(), "date_picker");
    }

    @Override
    public void launchActivity(Intent getStationIntent, int code){
        startActivityForResult(getStationIntent, code);
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void onDestroy(){
        mAddFillupPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void setFields(){
        mDate.setText(mAddFillupPresenter.getFillup().getReadableDate());

        mFuelAmount.setText(String.format("%.2f", mAddFillupPresenter.getFillup().getGallons()));
        mFuelPrice.setText(String.format("%.2f", mAddFillupPresenter.getFillup().getFuelCost()));
        mOctane.setText(String.format("%d", mAddFillupPresenter.getFillup().getOctane()));
        mMileage.setText(String.format("%.1f", mAddFillupPresenter.getFillup().getFillupMileage()));
    }

    @Override
    public void buildFillup(){
        mAddFillupPresenter.getFillup().setGallons(Double.parseDouble(mFuelAmount.getText().toString()));
        mAddFillupPresenter.getFillup().setFuelCost(Double.parseDouble(mFuelPrice.getText().toString()));
        mAddFillupPresenter.getFillup().setFillupMileage(Double.parseDouble(mMileage.getText().toString()));
        mAddFillupPresenter.getFillup().setOctane(Integer.valueOf(mOctane.getText().toString()));
    }

    private void setStationText(){
        if(mAddFillupPresenter.getStation().getName() != null) {
            mStation.setText(mAddFillupPresenter.getStation().getName());
            mStationAddress.setText(mAddFillupPresenter.getStation().getAddress());
        } else{
            mStation.setText(R.string.add_fillup_no_station_text);
            mStationAddress.setText(R.string.add_fillup_no_station_click_here);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case KeyContract.GET_STATION_CODE: {
                if (resultCode == RESULT_OK) {
                    mAddFillupPresenter.setStation((Station) data.getParcelableExtra(KeyContract.STATION));
                    setStationText();
                }
                break;
            }
        }

    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mAddFillupPresenter.onDateSet(year, monthOfYear, dayOfMonth);
    }
}
