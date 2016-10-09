package weddingmemories.ash.memories;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static weddingmemories.ash.memories.R.id.userid;

public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button submit = (Button) findViewById((R.id.Submit));
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText userid = (EditText)findViewById(R.id.userid);
                EditText password = (EditText)findViewById(R.id.password);
                EditText groupid = (EditText)findViewById(R.id.groupid);

                String sUsername = userid.getText().toString();
                if(userid.getText().toString().length() > 0 && password.getText().toString().length() > 0 && groupid.getText().toString().length() > 0) {
                    Intent returnintent = new Intent();
                    returnintent.putExtra("userid", userid.getText().toString());
                    returnintent.putExtra("password", password.getText().toString());
                    returnintent.putExtra("groupid", groupid.getText().toString());

                    setResult(Activity.RESULT_OK, returnintent);
                    finish();
                } else
                {
                    Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
