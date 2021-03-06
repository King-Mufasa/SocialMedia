package com.arabian.lancul.UI.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.arabian.lancul.MainActivity;
import com.arabian.lancul.R;
import com.arabian.lancul.UI.Activity.LoginActivity;
import com.arabian.lancul.UI.Util.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;


public class signinFragment extends Fragment {

    private View view;
    Button signin;
    EditText edt_email, edt_password;
    boolean hide = false;
    ImageView eye;
    String TAG = "Sign in";
    ProgressDialog loading;
    TextView btn_forgot_password_user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_signin, container, false);

        init_view();
        init_actions();

//        test_function();
        return view;
    }

    private void test_function() {
        edt_email.setText("messi@gmail.com");
        edt_password.setText("mufasa");
        login();
    }

    private void init_actions() {
        btn_forgot_password_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isValidEmail(edt_email.getText().toString())){
                    Toasty.error(getContext(), R.string.Error_Login, Toasty.LENGTH_LONG).show();
                }
                else{
                    FirebaseAuth.getInstance().sendPasswordResetEmail(edt_email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toasty.success(getContext(), R.string.toast_send_email, Toasty.LENGTH_LONG).show();
                                        Log.d(TAG, "Email sent.");
                                    }
                                }
                            });
                }
            }
        });
        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowHidePass();
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isValidEmail(edt_email.getText().toString()) || edt_password.getText().toString().equals("")){
                    Toasty.error(getContext(), getActivity().getString(R.string.Error_Login), Toasty.LENGTH_LONG).show();
                }
                else {
                    login();
                }

            }
        });
    }

    private void init_view() {
        signin = view.findViewById(R.id.btn_signin);
        edt_email = view.findViewById(R.id.edt_email);
        edt_password = view.findViewById(R.id.edt_password);
        eye = view.findViewById(R.id.btn_show_pass);
        loading = new ProgressDialog(LoginActivity.getInstance());
        loading.setTitle(LoginActivity.getInstance().getString(R.string.progress_sign_in));
        btn_forgot_password_user = view.findViewById(R.id.btn_forgot_password_user);

    }


    public boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    private void login() {

            loading.show();
            String username = edt_email.getText().toString();
            String password = edt_password.getText().toString();
            FirebaseApp.initializeApp(LoginActivity.getInstance());
            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(LoginActivity.getInstance(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                loading.dismiss();
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Global.my_email = user.getEmail();
                                for(int i = 0; i < Global.array_client.size(); i++){
                                    if(Global.array_client.get(i).getEmail().equals(Global.my_email)){
                                        Global.my_name = Global.array_client.get(i).getName();
                                        Global.my_email = Global.array_client.get(i).getEmail();
                                        Global.my_user_data = Global.array_client.get(i);
                                        break;
                                    }
                                }


                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                                LoginActivity.getInstance().finish();

                            } else {
                                // If sign in fails, display a message to the user.
                                loading.dismiss();
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toasty.error(LoginActivity.getInstance(), "Authentication failed.",
                                        Toasty.LENGTH_SHORT).show();
                            }

                        }
                    });



    }


    public void ShowHidePass() {

        if (hide){
            edt_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            eye.setImageResource(R.drawable.show_pass);
        }
        else{
            edt_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            eye.setImageResource(R.drawable.hide_pass);
        }
        hide = !hide;

    }


}
