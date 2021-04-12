package pollub.ism.lab05;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    private Button save = null;
    private Button read = null;
    private EditText nameSave = null;
    private EditText note = null;
    private Spinner nameRead = null;
    private ArrayList<String> fileNames = null;
    private ArrayAdapter<String> spinnerAdapter = null;

    private final String PREFERENCES_NAME = "Notepad";
    private final String PREFERENCES_KEY = "Saved file names";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        save = (Button) findViewById(R.id.buttonSave);
        read = (Button) findViewById(R.id.buttonRead);
        nameSave = (EditText) findViewById(R.id.editTextTextSaveName);
        note = (EditText) findViewById(R.id.editTextNote);
        nameRead = (Spinner) findViewById(R.id.spinnerNameRead);

        save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveNote();
                    }
                }
        );

        read.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        readNote();
                    }
                }
        );
    }

    @Override
    protected void onPause() {
        saveSharePreferences();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        fileNames = new ArrayList<>();
        spinnerAdapter =  new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fileNames);
        nameRead.setAdapter(spinnerAdapter);

        readSharePreferences();
    }

    private void saveNote(){
        String fileName = nameSave.getText().toString();
        String info = "Saving successful";

        if(!saveToFile(fileName,note)){
            info = "Saving not successful";
        }

        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }

    private void readNote(){
        String fileName= nameRead.getSelectedItem().toString();
        String info = "Reading successful";

        note.getText().clear();

        if(!readFromFile(fileName,note)){
            info = "Reading not successful";
        }

        Toast.makeText(this, info,Toast.LENGTH_SHORT).show();
    }

    private boolean saveToFile(String fileName, EditText editText){
        boolean result = true;
        File directory = getApplicationContext().getExternalFilesDir(null);
        File file = new File(directory + File.separator + fileName);
        BufferedWriter writer = null;

        try{
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(editText.getText().toString());
            editText.setText("");
        }catch(IOException exception){

            result = false;
        }finally {
            try{
                writer.close();
            }catch (IOException exception){
                result = false;
            }
        }

        if(result && !fileNames.contains(fileName)){
            fileNames.add(fileName);
            spinnerAdapter.notifyDataSetChanged();
        }

        return result;
    }

    private boolean readFromFile(String fileName, EditText editText){
        boolean result = true;
        File directory = getApplicationContext().getExternalFilesDir(null);
        File file = new File(directory + File.separator + fileName);
        BufferedReader reader = null;
        if(file.exists()){
            try{
                reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine() + "\n";
                while(line!=null){
                    editText.getText().append(line);
                    line =  reader.readLine();
                }
            }catch (FileNotFoundException exception){
                result = false;
            }catch (IOException exception){
                result = false;
            }finally {
                if(reader!=null){
                    try{
                        reader.close();
                    }catch (IOException exception){
                        result =  false;
                    }
                }
            }
        }
        return result;
    }

    private void saveSharePreferences(){
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();

        editor.putStringSet(PREFERENCES_KEY, new HashSet<String>(fileNames));

        editor.apply();
    }

    private void readSharePreferences() {
        SharedPreferences sharedPreferences =  getSharedPreferences(PREFERENCES_NAME,MODE_PRIVATE);
        Set<String> savedFileNames = sharedPreferences.getStringSet(PREFERENCES_KEY, null);

        if(savedFileNames != null){
            fileNames.clear();
            for(String name : savedFileNames){
                fileNames.add(name);
            }
            spinnerAdapter.notifyDataSetChanged();
        }
    }
}