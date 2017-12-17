package com.example.khalessi.imageopen;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {

    private boolean permission_write_external_granted = false;  // Flag

    private final int REQUEST_CODE = 4711;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Prüfe ob Schreibrechte in externem Speicher vorhanden
        int check = ContextCompat.checkSelfPermission
                (this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(check != PackageManager.PERMISSION_GRANTED) {
            // Rechte anfordern
            ActivityCompat.requestPermissions(this,
                    new String [] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);
            return;
        }
        else {
            // beabsichtigte Operation durchführen; Flag setzen
            permission_write_external_granted = true;
        }
    }

    /**
     * Callback-Routine nimmt Ergebnis der Anforderung von Rechten entgegen.
     * @param requestCode Der Request-code bei der Anforderung.
     * @param permissions Die angeforderten Rechte.
     * @param grantResults Sind Rechte gewährt worden?
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Hier wird nur das erste Recht überprüft und es wird angenommen,
        // dass es sich wirklich um das angefragte Recht handelt.
        if(requestCode == REQUEST_CODE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permission_write_external_granted = true;
        }
        else {
            Log.d("Dateien","Leider können wir so nicht weiter arbeiten!");
            finish();
        }

    }

    /**
     * Test-Routine zum Schreiben von einem Stückchen Text
     * @param text  Der Text, der geschrieben werden soll.
     * @param datei File-Objekt der Datei, die genutzt werden soll.
     */
    public void textSchreiben(String text, File datei){
        try {
            FileOutputStream fo = new FileOutputStream(datei);
            PrintWriter pw = new PrintWriter(fo);
            pw.print(text);
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ereignisbehandlung, ruft Methode zum Schreiben auf.
     * Hier wird entschieden, nach wo geschrieben werden soll.
     */
    public void onSchreibenClick(View view) {
        if(permission_write_external_granted) {
            File fileOut;
            fileOut = new File(getExternalFilesDir(null), "datei.txt");
            textSchreiben("gutenMorgen", fileOut);
        }
    }


    /**
     * Ereignisbehandlung Button-Click.
     * Es soll ermittelt werden, welche Dateien im angegeben Verzeichnis vorkommen.
     * Ausgabe erfolgt testhalber in einer Textview.
     * @param view
     */
    public void onBilderClick(View view) {
        if(permission_write_external_granted) {

            // Zugriff auf Verzeichnis
            File bilder = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);

            // Liste der enthaltenen Dateien
            File [] files = bilder.listFiles();

            // falls es Dateien gibt, Dateinamen in TextView ausgeben.
            if(files.length > 0) {
                TextView tv = (TextView) findViewById(R.id.textView);

                for (File f : files) {
                    tv.setText(tv.getText() + f.getName() + "\n");
                }
            }
        }
    }
}
