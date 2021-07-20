
package com.example.VaraBari.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.VaraBari.Objects.House;
import com.example.VaraBari.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class NewAdForm extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView[] imageView = new ImageView[5];
    public EditText title, description, address, area, bedroom, attachBath, floorLvl;
    public EditText availableFrom, rent, email, phoneNo, balcony;
    public CheckBox drawingRoom, diningRoom, storeRoom, negotiable;
    public CardView submit;
    public LinearLayoutCompat linearLayoutCompat;
    public ProgressDialog progressDialog;

    private int uploadCount = 0;
    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    //    public ArrayList<Integer> PICK_IMAGE_REQUEST_CODE = new ArrayList<>();
    public ArrayList<Uri> imageUri = new ArrayList<Uri>();

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private StorageTask imageUploadTask;
    private String uuid;
    private String key;
    private House house = new House();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ad_form);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        storageReference = FirebaseStorage.getInstance().getReference("House_Images");
        uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        key = UUID.randomUUID().toString();
        databaseReference = FirebaseDatabase.getInstance().getReference("Houses");

        // Hooks
        imageView[0] = (ImageView) findViewById(R.id.new_ad_form_house_image1);
        imageView[1] = (ImageView) findViewById(R.id.new_ad_form_house_image2);
        imageView[2] = (ImageView) findViewById(R.id.new_ad_form_house_image3);
        imageView[3] = (ImageView) findViewById(R.id.new_ad_form_house_image4);
        imageView[4] = (ImageView) findViewById(R.id.new_ad_form_house_image5);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Information uploading");

        linearLayoutCompat = findViewById(R.id.new_ad_form_imagepicker);
        title = (EditText) findViewById(R.id.new_ad_form_title);
        description = (EditText) findViewById(R.id.new_ad_form_imageDescription);
        address = (EditText) findViewById(R.id.new_ad_form_address);
        area = (EditText) findViewById(R.id.new_ad_form_area);
        bedroom = (EditText) findViewById(R.id.new_ad_form_bed_room);
        attachBath = (EditText) findViewById(R.id.new_ad_form_attach_bath);
        balcony = (EditText) findViewById(R.id.new_ad_form_balcony);
        floorLvl = (EditText) findViewById(R.id.new_ad_form_floor_level);
        availableFrom = (EditText) findViewById(R.id.new_ad_form_available_date);
        rent = (EditText) findViewById(R.id.new_ad_form_rent);
        email = (EditText) findViewById(R.id.new_ad_form_email);
        phoneNo = (EditText) findViewById(R.id.new_ad_form_phone_no);

        drawingRoom = (CheckBox) findViewById(R.id.new_ad_form_drawing_room);
        diningRoom = (CheckBox) findViewById(R.id.new_ad_form_dining_room);
        storeRoom = (CheckBox) findViewById(R.id.new_ad_form_store_room);
        negotiable = (CheckBox) findViewById(R.id.new_ad_form_negotiate);

        submit = (CardView) findViewById(R.id.new_ad_form_submit);
        ////////////////////////////////////////////////////////////////

        // Datepicker
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        availableFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        NewAdForm.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String date = day + "/" + month + "/" + year;
                        availableFrom.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        ////////////////////////////////////////////////////////////////

        // Publish new Ad
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                publishNewAd();
            }
        });
    }

    private void publishNewAd() {
        String _title, _description, _address, _area, _bedroom, _attachBath, _floorLvl, _availableFrom, _rent, _email, _phoneNo, _balcony;
        house.title = title.getText().toString();
        house.description = description.getText().toString();
        house.address = address.getText().toString();
        house.email = email.getText().toString();
        house.phoneNo = phoneNo.getText().toString();
        _area = area.getText().toString();
        _bedroom = bedroom.getText().toString();
        _attachBath = attachBath.getText().toString();
        _floorLvl = floorLvl.getText().toString();
        _rent = rent.getText().toString();
        _balcony = balcony.getText().toString();
        if (TextUtils.isEmpty(house.title)) {
            title.setError("Title is required");
            title.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(house.description)) {
            description.setError("Full name is required");
            description.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(_title)) {
            title.setError("Full name is required");
            title.requestFocus();
            return;
        }
//        house.title = title.getText().toString();
//        house.area = Double.parseDouble(area.getText().toString());
//        house.bedRoom = Integer.parseInt(bedroom.getText().toString());
//        house.attachBath = Integer.parseInt(attachBath.getText().toString());
//        house.floorLevel = floorLvl.getText().toString();
//        house.rent = Integer.parseInt(rent.getText().toString());
        house.drawingRoomAvailable = drawingRoom.isChecked();
        house.diningRoomAvailable = diningRoom.isChecked();
        house.storeRoomAvailable = storeRoom.isChecked();
        _negotiable = negotiable.isChecked();
        _availableFrom = availableFrom.getText().toString();

        for (uploadCount = 0; uploadCount < imageUri.size(); uploadCount++) {
            Uri uploadUri = imageUri.get(uploadCount);
            StorageReference fileReference = storageReference.child(key).child((uploadCount) + "."
                    + getFileExtension(imageUri.get(uploadCount)));
            fileReference.putFile(uploadUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = String.valueOf(uri);
                            house.image.add(url);
                        }
                    });
                }
            });
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                databaseReference.child(uuid).child(key)
                        .setValue(house).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(NewAdForm.this, "New Ad Published", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(NewAdForm.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(NewAdForm.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, 12000);

    }


    // Image Picker
    public void openFileChooser(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data.getClipData() != null && data.getClipData().getItemCount() == 5) {
                    int countClipdata = data.getClipData().getItemCount();
                    int currentImageSelected = 0;
                    while (currentImageSelected < countClipdata) {
                        Uri uri = data.getClipData().getItemAt(currentImageSelected).getUri();
                        Picasso.get().load(uri).into(imageView[currentImageSelected]);
                        imageUri.add(uri);
                        currentImageSelected = currentImageSelected + 1;
                    }
                } else {
                    Toast.makeText(this, "Please select five images", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    ////////////////////////////////////////////////////////////////

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }


}