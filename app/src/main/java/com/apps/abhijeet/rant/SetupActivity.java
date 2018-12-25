package com.apps.abhijeet.rant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;

public class SetupActivity extends AppCompatActivity {

    private EditText UserName, FullName, Country;
    private Button SaveInformationbuttion;
    private CircularImageView ProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;

    String CurrentUserID;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        UserName = (EditText) findViewById(R.id.setup_username);
        FullName = (EditText) findViewById(R.id.setup_fullname);
        Country = (EditText) findViewById(R.id.setup_country);
        SaveInformationbuttion = (Button) findViewById(R.id.setup_save);
        ProfileImage = (CircularImageView) findViewById(R.id.setup_profile_image);
        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUserID);

        SaveInformationbuttion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveAccountSetupInformation();
            }
        });
    }

    private void SaveAccountSetupInformation()
    {
        String username = UserName.getText().toString();
        String fullname = FullName.getText().toString();
        String country = Country.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(SetupActivity.this,"Username is empty",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(fullname))
        {
            Toast.makeText(SetupActivity.this,"fullname is empty",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(country))
        {
            Toast.makeText(SetupActivity.this,"country is empty",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Saving data");
            loadingBar.setMessage("Please wait while we are saving your data");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("username",username);
            userMap.put("fullname",fullname);
            userMap.put("country",country);
            userMap.put("status","Alive and banging");
            userMap.put("Gender","Male");
            userMap.put("dob","none");
            userMap.put("relationship status","In a relationship");

            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isComplete())
                    {
                        sendUserToMainActivity();
                        Toast.makeText(SetupActivity.this,"Your account is created successfully",Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                    else
                    {
                        String message = task.getException().getMessage();

                        Toast.makeText(SetupActivity.this,"sorry cause:"+message,Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void sendUserToMainActivity()
    {
        Intent MainActivityIntent = new Intent(SetupActivity.this, MainActivity.class);
        MainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainActivityIntent);
        finish();
    }
}
