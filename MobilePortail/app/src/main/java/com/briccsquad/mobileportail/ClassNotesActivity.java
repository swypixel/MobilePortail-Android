package com.briccsquad.mobileportail;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// TODO: Add tabbed activity for notes and schedule
public class ClassNotesActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_class_notes, menu);

        // Add click listener
        menu.findItem(R.id.action_logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // Just quickly quit
                Intent itt = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(itt);

                return false;
            }
        });

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Extract HTML from string result
        Document doc = Jsoup.parse(getIntent().getStringExtra("reqval"));

        // Get relevant structure
        Element notesTable = doc.selectFirst("#Table1");
        Elements elemList = notesTable.select("tr");

        // Loop through each class and create a new row for it
        // TODO: Maybe be more robust to changes? Then again, it seems static.
        for(int i = 1, l = elemList.size(); i < l; i++){
            Element classData = elemList.get(i);

            // Get full class ID string
            String className = classData.selectFirst("div.text").text();

            // Add a question mark if there was no teacher specified
            if(className.endsWith(":")){
                className += " ?";
            }

            // Get average note
            String classAvg;
            Element elemAvg = classData.selectFirst("a");

            if(elemAvg == null){
                classAvg = "N/A";
            } else {
                classAvg = classData.selectFirst("a").text();
            }

            // Create table row for displaying text on activity
            TableRow tabr = new TableRow(this);
            tabr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT,
                    2));

            tabr.setPadding((int) getResources().getDimension(R.dimen.activity_horizontal_margin),
                    (int) getResources().getDimension(R.dimen.note_special_margin),
                    (int) getResources().getDimension(R.dimen.activity_horizontal_margin),
                    (int) getResources().getDimension(R.dimen.note_special_margin));

            tabr.setBackgroundColor(Color.parseColor((i % 2 == 0) ? "#DDDDDD" : "#EEEEEE"));

            // Create text view for class name
            TextView dispClassName = new TextView(this);
            dispClassName.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3));
            dispClassName.setText(className);

            // Create text view for class average
            TextView dispClassAvg = new TextView(this);
            dispClassAvg.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));
            dispClassAvg.setText(classAvg);
            dispClassAvg.setGravity(Gravity.CENTER);
            if(elemAvg == null) dispClassAvg.setTypeface(null, Typeface.BOLD);

            // Add text to row
            tabr.addView(dispClassName);
            tabr.addView(dispClassAvg);

            // Add table row to layout
            TableLayout tl = findViewById(R.id.table_layout_notes);
            tl.addView(tabr);
        }
    }
}
