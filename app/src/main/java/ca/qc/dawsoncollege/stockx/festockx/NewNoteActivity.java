package ca.qc.dawsoncollege.stockx.festockx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.app.Activity.RESULT_CANCELED;

public class NewNoteActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.example.android.wordlistsql.REPLY";
    private EditText newNoteET;

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
