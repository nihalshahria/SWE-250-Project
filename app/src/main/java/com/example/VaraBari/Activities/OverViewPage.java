package com.example.VaraBari.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.VaraBari.Objects.House;
import com.example.VaraBari.R;

import java.util.ArrayList;

public class OverViewPage extends AppCompatActivity {

    public TextView area, bedRoom, drawingRoom, diningRoom, storeRoom, attachBath, balcony, floorlvl;
    public TextView rent, negotiable, availableFrom, description, address, phoneNo, email, title;
    Toolbar toolbar;
    public ImageSlider imageSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview_page);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Overview");

        Intent intent = getIntent();
        House house = intent.getParcelableExtra("House");

        imageSlider = findViewById(R.id.overview_imageslider);
        title = findViewById(R.id.overview_title);
        area = findViewById(R.id.overview_area);
        bedRoom = findViewById(R.id.overview_bed_room);
        drawingRoom = findViewById(R.id.overview_drawing_room);
        diningRoom = findViewById(R.id.overview_dining_room);
        storeRoom = findViewById(R.id.overview_store_room);
        attachBath = findViewById(R.id.overview_attach_bath);
        balcony = findViewById(R.id.overview_balcony);
        floorlvl = findViewById(R.id.overview_floor_level);
        rent = findViewById(R.id.overview_rent);
        negotiable = findViewById(R.id.overview_negotiable);
        availableFrom = findViewById(R.id.overview_available);
        description = findViewById(R.id.overview_description);
        address = findViewById(R.id.overview_address);
        phoneNo = findViewById(R.id.overview_phone_no);
        email = findViewById(R.id.overview_email);


        ArrayList<SlideModel>images = new ArrayList<>();
        for(String x: house.image){
            images.add(new SlideModel(x, null));
        }
        imageSlider.setImageList(images, ScaleTypes.CENTER_CROP);
        title.setText(house.title);
        area.setText(String.valueOf(house.area));
        bedRoom.setText(String.valueOf(house.bedRoom));
        if(house.drawingRoomAvailable){
            drawingRoom.setText("Yes");
        }else{
            drawingRoom.setText("No");
        }
        if(house.diningRoomAvailable){
            drawingRoom.setText("Yes");
        }else{
            drawingRoom.setText("No");
        }
        if(house.storeRoomAvailable){
            drawingRoom.setText("Yes");
        }else{
            drawingRoom.setText("No");
        }
        attachBath.setText(String.valueOf(house.attachBath));
        balcony.setText(String.valueOf(house.balcony));
        floorlvl.setText(house.floorLevel);
        rent.setText(String.valueOf(house.rent) + " BDT");
        if(house.negotiable){
            negotiable.setText("Yes");
        }else{
            negotiable.setText("No");
        }
        availableFrom.setText(house.availableFrom);
        description.setText(house.description);
        address.setText(house.address);
        phoneNo.setText(house.phoneNo);
        email.setText(house.email);

    }
}