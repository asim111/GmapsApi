package com.example.gmapsapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText username,email,password,confpassword;
    TextView logintext;
    Button signupbtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = (EditText)findViewById(R.id.nametext);
        email = (EditText)findViewById(R.id.emailtext);
        password = (EditText) findViewById(R.id.regpasswordtext);
        confpassword = (EditText) findViewById(R.id.confpasswordtext);
        signupbtn = (Button) findViewById(R.id.regcardview);
        logintext = (TextView)findViewById(R.id.logintext);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = username.getText().toString();
                String stringemail = email.getText().toString();
                String pass = password.getText().toString();
                String confirmpass = confpassword.getText().toString();
                if(!TextUtils.isEmpty(name)||!TextUtils.isEmpty(stringemail)||!TextUtils.isEmpty(pass)
                        ||!TextUtils.isEmpty(confirmpass)){
                    if(confirmpass.equals(pass)){
                        firebaseAuth.createUserWithEmailAndPassword(stringemail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                                    String uid = current_user.getUid();
                                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Staff").child(uid);
                                    HashMap usermap = new HashMap();
                                    usermap.put("username",name);
                                    databaseReference.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                                startActivity(intent);
                                            }else {
                                                Toast.makeText(RegisterActivity.this,"there is an error occour while uploading data",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else {
                                    Toast.makeText(RegisterActivity.this,"there is an error occour while registering user",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
        logintext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

}

