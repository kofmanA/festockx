package ca.qc.dawsoncollege.stockx.festockx.SQLite;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ca.qc.dawsoncollege.stockx.festockx.Menu.MenuActivity;
import ca.qc.dawsoncollege.stockx.festockx.R;

public class NewNoteActivity extends MenuActivity {
    public static final String EXTRA_REPLY = "com.example.android.wordlistsql.REPLY";
    private EditText newNoteET;

    /**Summary: Basic activity that sends back information upon close     *
     * @param savedInstanceState
     * @author Simon Guevara-Ponce
     */
    @Override
    public void onCreate(Bundle savedInstanceState ){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        newNoteET = findViewById(R.id.edit_note);
        final Button bttn = findViewById(R.id.button_save);
        bttn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(newNoteET.getText().toString())){
                    setResult(RESULT_CANCELED,replyIntent);
                }
                else{
                    String note = newNoteET.getText().toString();
                    replyIntent.putExtra(EXTRA_REPLY,note);
                    setResult(RESULT_OK,replyIntent);
                }
                finish();
            }
        });

    }

}
