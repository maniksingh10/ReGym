package com.veersgym.manik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Members_List extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomRecycle customRecycleAdapter;
    private ArrayList<GymMember> gymMemberArrayList = new ArrayList<>();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("All_Members");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members__list);

        recyclerView = findViewById(R.id.rv_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        addMemberFromDB();

    }

    private void addMemberFromDB(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gymMemberArrayList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    GymMember gymMember= snapshot.getValue(GymMember.class);
                    gymMemberArrayList.add(gymMember);
                }
                customRecycleAdapter = new CustomRecycle(gymMemberArrayList, getApplicationContext());
                recyclerView.setAdapter(customRecycleAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
