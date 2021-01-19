package com.example.favoritelistapp3;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;



import android.text.InputType;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CategoryFragment.OnCategoryInteractionListener {


    FloatingActionButton fab;

    public static final String CATEGORY_OBJECT_KEY = "CATEGORY_VALUE";
    public static final int MAIN_ACTIVITY_REQUEST_CODE = 1000;

    private CategoryFragment mCategoryFragment;
    private boolean isTablet = false;
    private CategoryItemsFragment mCategoryItemsFragment;

    private FrameLayout categoryItemsFragmentContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("List of Order");

        mCategoryFragment = (CategoryFragment) getSupportFragmentManager().findFragmentById(R.id.category_fragment);

        categoryItemsFragmentContainer = findViewById(R.id.category_items_fragment_container);

        isTablet = categoryItemsFragmentContainer != null;


        fab = findViewById(R.id.fab);

        fab.setOnClickListener(view -> {


            displayCreateCategoryDialog();

            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void displayCreateCategoryDialog() {
        String alertTitle = getString(R.string.alert_title);
        String positiveButtonTitle = getString(R.string.positive_button_title);

        EditText categoryEditText = new EditText(this);
        categoryEditText.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(alertTitle);
        alertBuilder.setView(categoryEditText);
        alertBuilder.setPositiveButton(positiveButtonTitle, (dialog, which) -> {

            Category category = new Category(categoryEditText.getText().toString(), new ArrayList<>());
            mCategoryFragment.giveCategoryToManager(category);
            dialog.dismiss();
            displayCategoryItems(category);

        })
                .create().show();


    }

    public void displayCategoryItems(Category category) {
        if (!isTablet) {
            Intent categoryItemsIntent = new Intent(this, CategoryItemsActivity.class);
            categoryItemsIntent.putExtra(CATEGORY_OBJECT_KEY, category);
            startActivityForResult(categoryItemsIntent, MAIN_ACTIVITY_REQUEST_CODE);
        } else {
            if (mCategoryItemsFragment != null){

                getSupportFragmentManager().beginTransaction()
                        .remove(mCategoryItemsFragment)
                        .commit();
                mCategoryItemsFragment = null;

            }
            setTitle("Category: " + category.getName());
            mCategoryItemsFragment = new CategoryItemsFragment().newInstance(category);
            if (mCategoryItemsFragment != null){

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.category_items_fragment_container,mCategoryItemsFragment).addToBackStack(null)
                        .commit();
            }

            fab.setOnClickListener(v -> displayCreateCategoryItemDialog());

        }
    }

    private void displayCreateCategoryItemDialog() {

        final EditText itemEditText = new EditText(this);
        itemEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.alert_title))
                .setView(itemEditText)
                .setPositiveButton(getString(R.string.positive_button_title), (dialog, which) -> {
                    String item = itemEditText.getText().toString();
                    mCategoryItemsFragment.addItemToCategory(item);
                    dialog.dismiss();
                })
                .create()
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MAIN_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if (data != null) {
                mCategoryFragment.saveCategory((Category) data.getSerializableExtra(CATEGORY_OBJECT_KEY));

            }
        }
    }


    @Override
    public void categoryIsTapped(Category category) {
        displayCategoryItems(category);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        setTitle(getString(R.string.title_new_my));

        if (mCategoryItemsFragment.category != null){
            mCategoryFragment.getCategoryManager().saveCategory(mCategoryItemsFragment.category);
        }

        if (mCategoryItemsFragment != null){

            getSupportFragmentManager().beginTransaction()
                    .remove(mCategoryItemsFragment)
                    .commit();
            mCategoryItemsFragment = null;
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCreateCategoryDialog();
            }
        });
    }
}