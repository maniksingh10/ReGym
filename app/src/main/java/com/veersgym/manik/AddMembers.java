package com.veersgym.manik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private CheckBox cb_send_sms;
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
        cb_send_sms = findViewById(R.id.cb_send_sms);

        databaseReference = FirebaseDatabase.getInstance().getReference("All_Members");

        bt_add_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check()){
                    addMember();
                }
            }
        });
    }

    private boolean check() {
        if (et_add_name.getText().toString().isEmpty() || et_add_amount.getText().toString().isEmpty() || et_add_id.getText().toString().isEmpty() || et_add_months.getText().toString().isEmpty()
                || et_add_mobile.getText().toString().isEmpty() || et_add_days.getText().toString().isEmpty() || et_add_mobile.getText().toString().length() != 10) {
            showError();
            return false;
        } else {
            return true;
        }
    }


    private void addMember() {
        String gender = "";
        int selectedId = radioGroup.getCheckedRadioButtonId();
        rb_ = (RadioButton) findViewById(selectedId);
        gender = rb_.getText().toString();
        final String branch = sp_branch.getSelectedItem().toString();
        final String mobile = et_add_mobile.getText().toString();
        final String name = et_add_name.getText().toString();
        final String gymida = et_add_id.getText().toString();
        String key = databaseReference.push().getKey();
        GymMember gymMember = new GymMember(name, gender, branch,
                et_add_emailid.getText().toString().toLowerCase(),
                mobile, "Tra Email", key, Integer.parseInt(gymida)
                , Integer.parseInt(et_add_amount.getText().toString()),  finalFee(),
                System.currentTimeMillis(), Integer.parseInt(et_add_months.getText().toString()));
        databaseReference.child(key).setValue(gymMember);

        if (cb_send_sms.isChecked()) {
            int sms_days = get_days(Integer.parseInt(et_add_months.getText().toString()), Integer.parseInt(et_add_days.getText().toString()));
            if (branch.matches("Veer's Gym")) {
                sendSMS(mobile, "Hey " + name + "," + "\n\nYou are successfully registered.\n" +
                        "Your Membership No. is " + gymida + " your subscription is valid for next " + sms_days +
                        " days.\nGym Timings are\nMonday to Saturday - 5AM-10PM\nSunday - 4PM-10PM"
                        + "\n\nTeam Veer's Gym\nGet Ripped Stay Fit\uD83D\uDCAA");

            } else if (branch.matches("Crossfit Fitness")) {
                sendSMS(mobile, "Hey " + name + "," + "\n\nYou are successfully registered.\n" +
                        "Your Membership No. is " + gymida + " your subscription is valid for next " + sms_days +
                        " days.\nGym Timings are\nMonday to Saturday - 5AM-10PM\nSunday - 4PM-10PM" + "\n\nTeam Crossfit Fitness\uD83D\uDCAA");
            }
        }
        Snackbar snackbar = Snackbar.make(rootview, name + " added with Id " + gymida, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.aadded));
        snackBarView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbar.show();
        clearForm();
    }

    private void clearForm() {
        et_add_name.setText("");
        et_add_amount.setText("");
        et_add_id.setText("");
        et_add_months.setText("");
        et_add_days.setText("");
        et_add_mobile.setText("");
        et_add_emailid.setText("");
        et_add_name.requestFocus();
    }

    private int get_days(int months, int days) {
        return (months * 30) + days;
    }

    private long finalFee() {
        long feefi;
        try {
            feefi = (Integer.parseInt(et_add_months.getText().toString()) * 2592880000L) +
                    (Integer.parseInt(et_add_days.getText().toString()) * 86460000L);
            return feefi +System.currentTimeMillis();
        } catch (Exception e) {
            showError();
            return 0;
        }
    }


    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(msg);
            smsManager.sendMultipartTextMessage(phoneNo, null, parts, null, null);
//            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    private void showError() {
        Snackbar snackbar = Snackbar.make(rootview, "Check Details", Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.error));
        snackBarView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbar.show();
    }
}
