package tamil.developers.thirukkural;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;

public class MainActivity extends Activity {
	int width;
	int height;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		width = metrics.widthPixels;
		height = metrics.heightPixels;

		Start();
		ImageView img = findViewById(R.id.logo);
		img.getLayoutParams().height = height/2;
	}
	private void Start() {
		Convert conn = new Convert();
		Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/vellore.ttf");		
		TextView txt = findViewById(R.id.tittle);
		txt.setTypeface(tf);
		txt.setText(conn.convertText(getString(R.string.app_name)));
		txt = findViewById(R.id.subtittle);
		txt.setTypeface(tf);
		txt.setText(conn.convertText(getString(R.string.tilte_tagline)));
		
		try {
			copyDataBase("/kural_data.db");
			copyDataBase("/list.db");
		} catch (IOException e) {
			e.printStackTrace();
		}
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(MainActivity.this, Menu.class));
				finish();
			}
		}, 3000);
	}
	public void copyDataBase(String name) throws IOException {
		try
		{
			InputStream in =getApplicationContext().getAssets().open("databases" + name);
			OutputStream out = new FileOutputStream(this.getFilesDir().getPath() + name);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
    }
	@Override
	public void onBackPressed() {
		//don't exit
	}
}
