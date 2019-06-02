package com.veersgym.manik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button membersList_bt, show_veer_bt, show_crossfit_bt;
    private Button addMember_bt;
    private TextView tv_total_members;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addMember_bt = findViewById(R.id.fab_add_member);
        membersList_bt = findViewById(R.id.bt_main_members_list);
        show_veer_bt = findViewById(R.id.bt_show_veers_members);
        show_crossfit_bt = findViewById(R.id.bt_show_crossfit_members);
        tv_total_members = findViewById(R.id.tv_total_members);

        databaseReference = FirebaseDatabase.getInstance().getReference("All_Members");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               tv_total_members.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addMember_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddMembers.class));

            }
        });

        membersList_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Members_List.class);

                intent.putExtra("KEY", "ALL");
                startActivity(intent);

            }
        });

        show_crossfit_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Members_List.class);

                intent.putExtra("KEY", "CROSSFIT");

                startActivity(intent);
            }
        });

        show_veer_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Members_List.class);

                intent.putExtra("KEY", "VEERS");

                startActivity(intent);
            }
        });
    }
}
