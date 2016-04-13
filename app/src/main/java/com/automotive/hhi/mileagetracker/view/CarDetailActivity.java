package com.automotive.hhi.mileagetracker.view;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.automotive.hhi.mileagetracker.IntentContract;
import com.automotive.hhi.mileagetracker.R;
import com.automotive.hhi.mileagetracker.adapters.FillupAdapter;
import com.automotive.hhi.mileagetracker.model.data.Car;
import com.automotive.hhi.mileagetracker.model.data.Fillup;
import com.automotive.hhi.mileagetracker.model.data.FillupFactory;
import com.automotive.hhi.mileagetracker.presenter.CarDetailPresenter;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CarDetailActivity extends AppCompatActivity implements CarDetailView {

    private final String LOG_TAG = CarDetailActivity.class.getSimpleName();

    @Bind(R.id.car_detail_name)
    public TextView mCarName;
    @Bind(R.id.car_detail_make)
    public TextView mCarMake;
    @Bind(R.id.car_detail_model)
    public TextView mCarModel;
    @Bind(R.id.car_detail_year)
    public TextView mCarYear;
    @Bind(R.id.car_detail_avg_mpg)
    public TextView mAverageMpg;
    @Bind(R.id.car_detail_fillups_rv)
    public RecyclerView mFillupRecyclerView;
    @Bind(R.id.car_detail_add_fillup)
    public Button mAddFillup;
    @Bind(R.id.car_detail_toolbar)
    public Toolbar mToolbar;
    private CarDetailPresenter mCarDetailPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mFillupRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        preparePresenter();

    }

    @OnClick(R.id.car_detail_add_fillup)
    public void onClick(){
        mCarDetailPresenter.launchSelectStation();

    }

    @Override
    public void launchSelectStation(Intent selectStationIntent) {
        startActivityForResult(selectStationIntent, IntentContract.DETAIL_TO_STATION_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == IntentContract.DETAIL_TO_STATION_CODE){
            if(resultCode == RESULT_OK){

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_car_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.car_detail_menu_car_list:
            {
                startActivity(new Intent(getContext(), CarListActivity.class));
            }
            case R.id.action_settings: {
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showFillups(FillupAdapter fillups) {
        Log.i(LOG_TAG, "Showing fillups");
        mFillupRecyclerView.setAdapter(fillups);
        fillups.notifyDataSetChanged();
    }

    @Override
    public void setMileageData(Fillup fillup){

        //mCurrentMileage.setText(String.format("%.1f", fillup.getFillupMileage()));
        //mLastFillupDate.setText(fillup.getReadableDate());
    }


    @Override
    public void showCar(Car car) {
        mAverageMpg.setText(String.format("%.1f", car.getAvgMpg()));
        mCarName.setText(car.getName());
        mCarMake.setText(car.getMake());
        mCarModel.setText(car.getModel());
        mCarYear.setText(String.format("%d", car.getYear()));
    }



    @Override
    public Context getContext() {
        return getApplicationContext();
    }


    private void preparePresenter(){
        mCarDetailPresenter = new CarDetailPresenter(getApplicationContext()
                , getLoaderManager()
                , getIntent().getLongExtra(IntentContract.CAR_ID, 1));
        mCarDetailPresenter.attachView(this);
        mCarDetailPresenter.loadCar();

    }
}
