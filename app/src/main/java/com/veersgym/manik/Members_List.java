package com.veersgym.manik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Members_List extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomRecycle customRecycleAdapter;
    private ArrayList<GymMember> gymMemberArrayList = new ArrayList<>();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("All_Members");
    private CompoundButton show_due_sw;
    private boolean isDueSend = false;
    private String data = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members__list);
        Intent intent = getIntent();
        data = intent.getStringExtra("KEY");
        show_due_sw = findViewById(R.id.show_overdue);
        recyclerView = findViewById(R.id.rv_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        // addMemberFromDB();

        refreshList();

        show_due_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isDueSend = true;
                    refreshList();
                } else {
                    isDueSend = false;
                    refreshList();
                }
            }
        });

    }

    private void refreshList(){
        if (data.equals("VEERS")) {
            databaseReference.orderByChild("branch").equalTo("Veer's Gym").addValueEventListener(firebaseEventListener);
        } else if (data.equals("CROSSFIT")) {
            databaseReference.orderByChild("branch").equalTo("Crossfit Fitness").addValueEventListener(firebaseEventListener);
        } else {
            databaseReference.addValueEventListener(firebaseEventListener);
        }
    }

    /*
    private void addMemberFromDB(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    */

    private Context context = this;
    private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.RIGHT) {
                GymMember gymMember = gymMemberArrayList.get(viewHolder.getAdapterPosition());
                showAlertDialog(context, gymMember.getName(), gymMember.getId());

                customRecycleAdapter.notifyDataSetChanged();
            }
        }
    };

    public static void showAlertDialog(final Context context, String title, final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Remove " + title);
        builder.setMessage("Are you sure you want to delete this entry?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Member removed", Toast.LENGTH_SHORT).show();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("All_Members");

                Query applesQuery = databaseReference.child(id);

                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setIcon(android.R.drawable.ic_delete);
        dialog.show();

    }

    private ValueEventListener firebaseEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            gymMemberArrayList.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                GymMember gymMember = snapshot.getValue(GymMember.class);
                if(isDueSend){
                    if (days_remain(gymMember.getFeedate()) < 0) {
                        gymMemberArrayList.add(gymMember);
                    }
                }else{
                    gymMemberArrayList.add(gymMember);
                }

            }
            customRecycleAdapter = new CustomRecycle(gymMemberArrayList, getApplicationContext());
            recyclerView.setAdapter(customRecycleAdapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private int days_remain(long feedate) {
        long diff = feedate - System.currentTimeMillis();
        // Calculate difference in days
        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
        return diffDays;
    }

}
