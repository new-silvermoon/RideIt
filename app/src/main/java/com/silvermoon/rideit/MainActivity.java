package com.silvermoon.rideit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.silvermoon.rideit.model.User;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    Button btnSignin,btnRegister;
    RelativeLayout rlRoot;


    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting up calligraphy font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                                      .setDefaultFontPath("fonts/Arkhip_font.ttf")
                                      .setFontAttrId(R.attr.fontPath)
                                      .build());

        setContentView(R.layout.activity_main);

        btnSignin = (Button)findViewById(R.id.btnSign);
        btnRegister = (Button)findViewById(R.id.btnRegister);
        rlRoot = (RelativeLayout)findViewById(R.id.rlRoot);

        //Initializing Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDatabase = firebaseDatabase.getReference("RideItUsers");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayRegisterDialog();
            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySignInDialog();
            }
        });
    }

    private void displaySignInDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("SIGN IN");
        alertDialog.setMessage("Please use email to sign in");

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View signin_layout = layoutInflater.inflate(R.layout.layout_signin,null);

        alertDialog.setView(signin_layout);

        final MaterialEditText etEmail = (MaterialEditText)signin_layout.findViewById(R.id.etEmail);
        final MaterialEditText etPassword = (MaterialEditText)signin_layout.findViewById(R.id.etPassword);

        alertDialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                signInUserFirebase(etEmail.getText().toString(),etPassword.getText().toString());
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();


    }



    private void displayRegisterDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("REGISTER");
        alertDialog.setMessage("Please use email to register");

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View register_layour = layoutInflater.inflate(R.layout.layout_register,null);

        final MaterialEditText etEmail = (MaterialEditText)register_layour.findViewById(R.id.etEmail);
        final MaterialEditText etPassword = (MaterialEditText)register_layour.findViewById(R.id.etPassword);
        final MaterialEditText etName = (MaterialEditText)register_layour.findViewById(R.id.etName);
        final MaterialEditText etPhone = (MaterialEditText)register_layour.findViewById(R.id.etPhone);

        alertDialog.setView(register_layour);

        alertDialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                registerUserInFirebase(etEmail.getText().toString(),
                        etPassword.getText().toString(),
                        etName.getText().toString(),
                        etPhone.getText().toString());


            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 dialog.dismiss();
            }
        });

        alertDialog.show();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void registerUserInFirebase(final String email,final String password,final String name,final String phone){
        //Validation
        if(TextUtils.isEmpty(email)){
            Snackbar.make(rlRoot,"Please enter valid EmailId",Snackbar.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(phone)){
            Snackbar.make(rlRoot,"Please enter valid phone number",Snackbar.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Snackbar.make(rlRoot,"Please enter valid password",Snackbar.LENGTH_SHORT).show();
            return;
        }
        if(password.length() < 6){
            Snackbar.make(rlRoot,"Password is too short",Snackbar.LENGTH_SHORT).show();
            return;
        }

        //Registering user
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        User user = new User();
                        user.setEmail(email);
                        user.setPassword(password);
                        user.setPhone(phone);
                        user.setName(name);

                        //Saving data in firebasedatabase (User email is the key column)
                        usersDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Snackbar.make(rlRoot,"Registration Successful",Snackbar.LENGTH_SHORT).show();


                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(rlRoot,"Registration Failed",Snackbar.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rlRoot,"Something went wrong. Please try again",Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInUserFirebase(String email, String password) {
        //Validation
        if(TextUtils.isEmpty(email)){
            Snackbar.make(rlRoot,"Please enter valid EmailId",Snackbar.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Snackbar.make(rlRoot,"Please enter valid password",Snackbar.LENGTH_SHORT).show();
            return;
        }
        if(password.length() < 6){
            Snackbar.make(rlRoot,"Password is too short",Snackbar.LENGTH_SHORT).show();
            return;
        }

        final android.app.AlertDialog loadingDialog = new SpotsDialog(MainActivity.this);
        loadingDialog.show();
        btnSignin.setEnabled(false);

        //Signing user
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        loadingDialog.dismiss();
                        btnSignin.setEnabled(true);
                        startActivity(new Intent(MainActivity.this,HomeActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.dismiss();
                        btnSignin.setEnabled(true);
                        Snackbar.make(rlRoot,"Something went wrong. Please try again",Snackbar.LENGTH_SHORT).show();
                    }
                });
    }
}
