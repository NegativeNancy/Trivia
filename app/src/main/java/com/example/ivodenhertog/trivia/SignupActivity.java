package com.example.ivodenhertog.trivia;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private EditText usernameView;
    private AutoCompleteTextView emailView;
    private EditText passwordView;
    private EditText confirmPasswordView;

    private View progressView;
    private View submitFormView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        // Find all the views in this activity
        progressView = findViewById(R.id.submit_progress);
        submitFormView = findViewById(R.id.submit_form);
        usernameView = findViewById(R.id.signupUsername);
        emailView = findViewById(R.id.signupEmail);
        passwordView = findViewById(R.id.signupPassword);
        confirmPasswordView = findViewById(R.id.signupConfirmPassword);
    }

    /**
     * Get info of user.
     **/
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    /**
     * User currentUser to sign in the user.
     **/
    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Intent main = new Intent(SignupActivity.this, MainActivity.class);
            main.putExtra("currentUser", currentUser);
            startActivity(main);
            finish();
        }
    }

    /**
     * Send user to login activity.
     */
    public void loginClicked(View view) {
        Intent login = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(login);
        finish();
    }

    /**
     * Submit user input and create account.
     * Use this new account to log user in.
     */
    public void submitClicked(View view) {
        Boolean cancel = false;

        // Gather all required information.
        String username = usernameView.getText().toString();
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        String confirmPassword = confirmPasswordView.getText().toString();

        View focusView = null;

        if (username.length() < 3) {
            usernameView.setError("username needs to be at least 3 characters");
            focusView = usernameView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
        }

        // Check for a valid password.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        // Check if both passwords are the same
        if (!TextUtils.isEmpty(confirmPassword) && !isPasswordSame(password, confirmPassword)) {
            passwordView.setError(getString(R.string.error_passwords_different));
            confirmPasswordView.setError(getString(R.string.error_passwords_different));
            focusView = confirmPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            createUser();
        }
    }

    /**
     * Create the user.
     */
    private void createUser() {

        final String username = usernameView.getText().toString();
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            Log.d(TAG, "onComplete: username = " + username);

                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();

                            if (user != null) {
                                user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");

                                                Log.d(TAG, "onComplete: username = " + user.getDisplayName());
                                                Log.d(TAG, "onComplete: email = " + user.getEmail());
                                            }
                                        }
                                    });
                            }

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        submitFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        submitFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                submitFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Check if the email is valid.
     * @param email this is the users email address.
     * @return return true or false based on result
     */
    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    /**
     * Check if password is long enough.
     * @param password contains the users password.
     * @return return true or false based on result
     */
    private boolean isPasswordValid(String password) {
        return password.length() > 8;
    }

    /**
     * Check if passwords are the same.
     * @param password contains the users password.
     * @param confirmPassword contains the users confirmation password.
     * @return return true or false based on result
     */
    private boolean isPasswordSame(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
}
