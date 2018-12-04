package ca.qc.dawsoncollege.stockx.festockx;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HintsActivity extends MenuActivity {
    FirebaseAuth mAuth;
    List<Hint> hints;
    FirebaseDatabase database;
    private Random random;
    final String TAG = "Hints";

    /**
     * on Create calls the
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hints);
        random = new Random();
        initializeFirebase();
        signIn();
    }

    /**
     * Initializes the necessary variables for firebase
     */
    protected void initializeFirebase(){
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    /**
     * Signs the user in
     * and launches fetchDatabaseInfo() when the user logs in successfully
     * @author Zhi Jie Cao
     */
    protected void signIn(){
        mAuth.signInWithEmailAndPassword("zhijiec99@hotmail.com", "android")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            fetchDatabaseInfo();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("test", "signInWithEmail:failure", task.getException());
                            Toast.makeText(HintsActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Fetches the info from the Database and display
     * @author Zhi Jie Cao
     */
    protected void fetchDatabaseInfo(){
        DatabaseReference ref = database.getReference("hints");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG ,"Children Count: " + dataSnapshot.getChildrenCount());
                hints = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Hint hint = dataSnapshot1.getValue(Hint.class);
                    hints.add(hint);
                }

                displayData(hints.get(random.nextInt(hints.size())));
                findViewById(R.id.hintCard).setOnClickListener(
                    new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                            displayData(hints.get(random.nextInt(hints.size())));
                       }
                   }
                );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    /**
     * Displays the hint object in the activity.
     * @param hint
     */
    protected void displayData(final Hint hint){
        Log.d(TAG, "DISPLAYING: " +  hint.text);
        TextView text = findViewById(R.id.hintText);
        text.setText(hint.text);
        Button btn = findViewById(R.id.sourceBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent website = new Intent(Intent.ACTION_VIEW, Uri.parse(hint.URI));
                startActivity(website);
            }
        });
    }
}
