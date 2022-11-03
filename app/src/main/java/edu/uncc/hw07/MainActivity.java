package edu.uncc.hw07;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener, SignUpFragment.SignUpListener, ForumsFragment.ForumsFragmentListener, CreateForumFragment.CreateForumFragmentListener {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // If there is not a user logged in, go to Login
        if (mAuth.getCurrentUser() == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rootView, new LoginFragment())
                    .commit();
        } // If user is logged in, go to Forums
        else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rootView, new ForumsFragment())
                    .commit();
        }
    }

    /**
     * Go to Create a New Account
     */
    @Override
    public void createNewAccount() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new SignUpFragment(), "Sign Up")
                .addToBackStack(null)
                .commit();
    }

    /**
     * Go back to Login
     */
    @Override
    public void login() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void cancel() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void goToForums() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new ForumsFragment(), "Forums")
                .commit();
    }

    @Override
    public void logout() {
        FirebaseAuth.getInstance().signOut();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new LoginFragment(), "Login")
                .commit();
    }

    @Override
    public void newForum() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new CreateForumFragment(), "Create Forum")
                .addToBackStack(null)
                .commit();
    }
}