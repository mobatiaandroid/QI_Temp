package com.qi.Common;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.qi.R;

import java.util.HashMap;
import java.util.Map;

public class AddData extends AppCompatActivity {

    EditText Question,Op1,Op2,Op3,Op4;
    Button Upload;
    Spinner Category,CorrectOp,Question_no;
    Context mContext;
    Activity activity;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference;
    CollectionReference collectionReference;
    String [] CATEGORY = {"Science","Physics","Facts","Movie","Technology","Programming"};
    String [] CORRECT = {"Option 1","Option 2","Option 3","Option 4"};
    String [] QUES_NO = {"1","2","3","4","5","6","7","8","9","10"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        mContext = this;
        activity = this;

        IniUI();
    }

    private void IniUI() {
        Category = findViewById(R.id.category);
        Question = findViewById(R.id.Question_edt);
        Question_no = findViewById(R.id.Question_no);
        Op1 = findViewById(R.id.opt_1);
        Op2 = findViewById(R.id.opt_2);
        Op3 = findViewById(R.id.opt_3);
        Op4 = findViewById(R.id.opt_4);
        CorrectOp = findViewById(R.id.Correct_opt);
        Upload = findViewById(R.id.enter_Data);

        ArrayAdapter adapter = new ArrayAdapter(mContext,android.R.layout.simple_list_item_1,CATEGORY);
        Category.setAdapter(adapter);

        ArrayAdapter Correctadapter = new ArrayAdapter(mContext,android.R.layout.simple_list_item_1,CORRECT);
        CorrectOp.setAdapter(Correctadapter);

        ArrayAdapter Quesdapter = new ArrayAdapter(mContext,android.R.layout.simple_list_item_1,QUES_NO);
        Question_no.setAdapter(Quesdapter);

       Upload.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               UploadData();
           }
       });


    }

    private void UploadData() {
        Map<String,Object> map = new HashMap<>();
        map.put("Question",Question.getText().toString());
        map.put("Option 1",Op1.getText().toString());
        map.put("Option 2",Op2.getText().toString());
        map.put("Option 3",Op3.getText().toString());
        map.put("Option 4",Op4.getText().toString());
        map.put("Correct Option",CorrectOp.getSelectedItem().toString());



        db.collection("Quiz")
                .document(Category.getSelectedItem().toString())
                .collection("Questions")
                .document(Question_no.getSelectedItem().toString())
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
        Toast.makeText(mContext, "Data Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("DATAAAAA: "+e.getMessage());
                                Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
