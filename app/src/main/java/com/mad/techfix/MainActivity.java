package com.mad.techfix;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.mad.techfix.ui.parts.PartsManagerFragment;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PartsManagerFragment())
                .commit();
    }
}