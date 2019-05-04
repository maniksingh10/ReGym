package com.veersgym.manik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddMembers extends AppCompatActivity {

    private EditText et_add_name, et_add_amount, et_add_id, et_add_months, et_add_days, et_add_mobile, et_add_emailid;
    private Button bt_add_member;
    private DatabaseReference databaseReference;
    private TextView tv_dateshow;
    private Spinner sp_branch;
    private View rootview;
    private RadioGroup radioGroup;
    private RadioButton rb_;
    private String date = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members);
        rootview = findViewById(R.id.rootview);
        et_add_name = findViewById(R.id.et_name);
        et_add_amount = findViewById(R.id.et_amount);
        et_add_id = findViewById(R.id.et_id);
        et_add_months = findViewById(R.id.et_months);
        et_add_days = findViewById(R.id.et_days);
        et_add_mobile = findViewById(R.id.et_mobile);
        et_add_emailid = findViewById(R.id.et_email);
        bt_add_member = findViewById(R.id.bt_add_member);
        tv_dateshow = findViewById(R.id.tv_date);
        tv_dateshow.setText(date);
        sp_branch = findViewById(R.id.spinner);
        radioGroup = findViewById(R.id.radioGroup);

        databaseReference = FirebaseDatabase.getInstance().getReference("All_Members");

        bt_add_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check()) {
                    addMember();
                }
            }
        });
    }

    private boolean check() {
        if (et_add_name.getText().toString().isEmpty() || et_add_amount.getText().toString().isEmpty() || et_add_id.getText().toString().isEmpty() || et_add_months.getText().toString().isEmpty()
                || et_add_mobile.getText().toString().isEmpty() || et_add_days.getText().toString().isEmpty()) {
            showError();
            return false;

        } else {

            return true;
        }

    }

    private void showError() {

        Snackbar snackbar = Snackbar.make(rootview, "Check Details", Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.error));
        snackBarView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbar.show();
    }


    private void addMember() {
        String gender = "";
        int selectedId = radioGroup.getCheckedRadioButtonId();
        rb_ = (RadioButton) findViewById(selectedId);
        gender = rb_.getText().toString();

        String key = databaseReference.push().getKey();
        GymMember gymMember = new GymMember(et_add_name.getText().toString().toLowerCase(), gender, sp_branch.getSelectedItem().toString(),
                et_add_emailid.getText().toString().toLowerCase(),
                et_add_mobile.getText().toString(), "Tra Email", key, Integer.parseInt(et_add_id.getText().toString())
                , Integer.parseInt(et_add_amount.getText().toString()), System.currentTimeMillis()+finalFee(),
                System.currentTimeMillis(), Integer.parseInt(et_add_months.getText().toString()));
        databaseReference.child(key).setValue(gymMember).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
            }
        });

    }

    private long finalFee(){
        long feefi;

        try {
            feefi = Integer.parseInt(et_add_months.getText().toString()) *2592880000L + Integer.parseInt(et_add_days.getText().toString()) * 86400000 ;
            return feefi;
        }catch (Exception e){
            showError();
            return 0;
        }

    }



}
