package com.automotive.hhi.mileagetracker.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.automotive.hhi.mileagetracker.KeyContract;
import com.automotive.hhi.mileagetracker.R;
import com.automotive.hhi.mileagetracker.model.data.Car;
import com.automotive.hhi.mileagetracker.presenter.AddCarPresenter;
import com.automotive.hhi.mileagetracker.view.interfaces.AddCarView;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddCarActivity extends AppCompatActivity implements AddCarView {

    private final String LOG_TAG = AddCarActivity.class.getSimpleName();

    @Bind(R.id.add_car_image)
    public ImageView mImage;
    @Bind(R.id.add_car_name)
    public EditText mName;
    @Bind(R.id.add_car_make)
    public EditText mMake;
    @Bind(R.id.add_car_model)
    public EditText mModel;
    @Bind(R.id.add_car_year)
    public EditText mYear;
    @Bind(R.id.add_car_submit)
    public Button mAddCar;
    @Bind(R.id.add_car_input_container)
    public LinearLayout mInputContainer;
    private AddCarPresenter mAddCarPresenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAddCarPresenter = new AddCarPresenter((Car)getIntent().getParcelableExtra(KeyContract.CAR)
                , getIntent().getBooleanExtra(KeyContract.IS_EDIT, false)
                , getContext());
        setContentView(R.layout.activity_add_car);
        ButterKnife.bind(this);
        mAddCarPresenter.attachView(this);
        if(mAddCarPresenter.getIsEdit()){
            mAddCar.setText(getResources().getString(R.string.add_car_edit_button));
        }
    }


    @OnClick(R.id.add_car_submit)
    public void onButtonPressed() {
        if(mAddCarPresenter.validateInput(mInputContainer)){
            Intent returnIntent = new Intent();
            returnIntent.putExtra(KeyContract.CAR, mAddCarPresenter.getCar());
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }

    @OnClick(R.id.add_car_image)
    public void onImageClick(){
        mAddCarPresenter.selectImage();
    }


    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void onDestroy() {
        mAddCarPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void setFields(){
        Log.i(LOG_TAG, "Image URI: " + mAddCarPresenter.getCar().getImage());
        if(mAddCarPresenter.getCar().getImage() != null) {
            Picasso.with(getContext())
                    .load(Uri.parse(mAddCarPresenter.getCar().getImage()))
                    .into(mImage);
        }
        mName.setText(mAddCarPresenter.getCar().getName());
        mMake.setText(mAddCarPresenter.getCar().getMake());
        mModel.setText(mAddCarPresenter.getCar().getModel());
        mYear.setText(String.format("%d", mAddCarPresenter.getCar().getYear()));
    }

    @Override
    public void buildCar(){

        mAddCarPresenter.getCar().setName(mName.getText().toString());
        mAddCarPresenter.getCar().setMake(mMake.getText().toString());
        mAddCarPresenter.getCar().setModel(mModel.getText().toString());
        mAddCarPresenter.getCar().setYear(Integer.valueOf(mYear.getText().toString()));
        if(mAddCarPresenter.getCar().getId() == 0){
            mAddCarPresenter.getCar().setAvgMpg(0.0);
        }
    }

    @Override
    public void selectImage(Intent selectImageIntent){
        Log.i(LOG_TAG, "selecting image");
        startActivityForResult(Intent
                .createChooser(selectImageIntent, "Select Image"), KeyContract.SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.i(LOG_TAG, "returned from gallery");
        if(requestCode == KeyContract.SELECT_IMAGE
                && resultCode == Activity.RESULT_OK
                && data != null
                && data.getData() != null){
            Log.i(LOG_TAG, "got image");
            mAddCarPresenter.saveImage(data.getData());
        }
    }
}