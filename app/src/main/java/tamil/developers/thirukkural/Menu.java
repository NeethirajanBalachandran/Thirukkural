package tamil.developers.thirukkural;

import java.io.FileOutputStream;
import java.io.IOException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;

public class Menu extends Activity {

	Convert conn;
	Typeface tf;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		conn = new Convert();
		tf = Typeface.createFromAsset(getAssets(),"fonts/vellore.ttf");		
		Button btn = findViewById(R.id.button1);
		btn.setTypeface(tf);
		btn.setText(conn.convertText(getString(R.string.select_part1)));
		btn = findViewById(R.id.button2);
		btn.setTypeface(tf);
		btn.setText(conn.convertText(getString(R.string.select_part2)));
		btn = findViewById(R.id.button3);
		btn.setTypeface(tf);
		btn.setText(conn.convertText(getString(R.string.select_part3)));
		
		(findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				saveFromFile("1" );
				startActivity(new Intent(Menu.this, List.class));
				finish();
			}
		});

		(findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				saveFromFile("2");
				startActivity(new Intent(Menu.this, List.class));
				finish();
			}
		});

		(findViewById(R.id.button3)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				saveFromFile("3");
				startActivity(new Intent(Menu.this, List.class));
				finish();
			}
		});
	}
	
	private void saveFromFile(String data) {
         try {
			 FileOutputStream fos = openFileOutput("part.txt", Context.MODE_PRIVATE);
			 fos.write(data.getBytes());
			 fos.close();
         } catch (IOException e) {
              e.printStackTrace();
        }
	}
	@Override
	public void onPause() {
	    super.onPause();
	}
	@Override
	public void onResume() {
	    super.onResume();
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
