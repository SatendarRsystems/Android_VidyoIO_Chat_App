package com.vidyo.io.demo.activities;


import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import com.vidyo.io.demo.R;


/**
 * Summary: Base Activity Component
 * Description: This will handle all the common activity tasks. This activity will be extended in all the activities.
 * used for common methods
 * @author RSI
 * @date 16.08.2018
 */
public class BaseActivity extends AppCompatActivity {
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Show progress dialog
     */
    public void showProgressDialog() {
        dialog = new ProgressDialog(BaseActivity.this);
        dialog.getWindow().setBackgroundDrawable(new
                ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        dialog.setContentView(R.layout.progress_dialog);
    }

    /**
     * Dissmiss progress dialog
     */
    public void dissmissProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * Show snakbar with custom error message
     * @param message  message to show message in snakbar.
     * @param coordinatorLayout is context of coordinator layout.
     */
    public void showSnakbar(String message, CoordinatorLayout coordinatorLayout) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    /**
     * Replace fragments
     * @param fragment  fragment is reference variable.
     */
    public void replaceFragement(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Hide keyboard
     */
    public void hideKeyboard()
    {
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
