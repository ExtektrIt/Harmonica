package com.andrey.guryanov.harmonica;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.andrey.guryanov.harmonica.utils.App;

import java.io.File;

public class testFunction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_function);

        Button resetApp = findViewById(R.id.btn_reset_app);
        Button resetState = findViewById(R.id.btn_reset_state);
    }

    public void goBack(View view) {
        onBackPressed();
    }

    public void resetApp(View v) {
        File file = new File(App.getAppDir());
        if (file.delete()) {
            Toast toast = Toast.makeText(this, "ВСЕ данные приложения успешно удалены!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void resetState(View v) {
        File file = new File(App.getPlayerStatePath());
        file.delete();
        Toast toast = Toast.makeText(this, "Сохранённое состояние успешно удалено!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void manualSavePlayLists(View v) {
//        App.getPlayer().savePlayList();
    }

    public void savePlayList(View v) {
        App.getPlayer().savePlayList(App.getPlayer().getCurrentPlayList());
    }

}