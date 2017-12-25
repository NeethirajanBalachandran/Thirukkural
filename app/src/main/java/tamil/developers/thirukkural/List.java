package tamil.developers.thirukkural;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class List extends Activity {

	public static final String TABLE_part = "Part_list";
	static SQLiteDatabase myDB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		String dbName  = this.getFilesDir().getPath() + "/list.db";
	    myDB = openOrCreateDatabase(dbName , Context.MODE_PRIVATE, null);
	    myDB.setVersion(1);
	    myDB.setLocale(Locale.getDefault());
	    
		
		ListView listView = findViewById(R.id.listView1);
		final ArrayList<Map<String, String>> list = buildData();
	    String[] from = { "name", "purpose" };
	    int[] to = {R.id.text1, R.id.text2 }; 
	    SimpleAdapter adapter = new SimpleAdapter(this, list,R.layout.list_layout, from, to){
	            @Override
		        public View getView(int pos, View convertView, ViewGroup parent){
					View v = convertView;
					if(v== null){
						LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						v=vi.inflate(R.layout.list_layout, null);
					}
					Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/vellore.ttf");
					TextView tv = (TextView)v.findViewById(R.id.text1);
					tv.setText(list.get(pos).get("name"));
					tv.setTypeface(tf);
					TextView tvs = (TextView)v.findViewById(R.id.text2);
					tvs.setText(list.get(pos).get("purpose"));
					tvs.setTypeface(tf);
					return v;
		        }
		};
		listView.setAdapter(adapter);
	    listView.setOnItemClickListener(new OnItemClickListener() {
	    @Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				int num = Integer.parseInt(loadFromFile());
		    	if (num == 1)
		    	{
		    		try {
						saveFromFile((position + 1) + "", "select_part.txt");
						saveFromFile((((position + 1) * 10) - 9) + "", "kural.txt");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
		    	}
		    	else if (num == 2)
		    	{
		    		try {
						saveFromFile((position + 1 + 38) + "", "select_part.txt");
						saveFromFile((((position + 1 + 38) * 10) - 9) + "", "kural.txt");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
		    	}
		    	else if (num == 3)
		    	{
		    		try {
						saveFromFile((position + 1 + 108) + "", "select_part.txt");
						saveFromFile((((position + 1 + 108) * 10) - 9) + "", "kural.txt");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
		    	}
		    	Intent openMainList = new Intent(List.this, KuralGame.class);
		        startActivity(openMainList);
		        finish();
			}
		});
	}
	
	private ArrayList<Map<String, String>> buildData() {
	    
		Convert conn = new Convert();
		
		ArrayList<Map<String, String>> list = new ArrayList<>();
	    
	    Cursor c = myDB.rawQuery("SELECT * FROM " + TABLE_part + " Where part = " + loadFromFile() , null);

	    int Column1 = c.getColumnIndex("list_name");
	    int Column2 = c.getColumnIndex("list_number");
	    c.moveToFirst();
	    do
    	{
	    	list.add(putData(conn.convertText(c.getString(Column1)), conn.convertText(getString(R.string.athigaram) + " " + c.getInt(Column2))));
    	}
	    while(c.moveToNext());
	    c.close();
	    return list;
	}
	private HashMap<String, String> putData(String name, String purpose) {
	    HashMap<String, String> item = new HashMap<>();
	    item.put("name", name);
	    item.put("purpose", purpose);
	    return item;
	}
	private void saveFromFile(String data, String file_name) throws FileNotFoundException {
        
        FileOutputStream fos = openFileOutput(file_name, Context.MODE_PRIVATE);
         try {
              fos.write(data.getBytes());
              fos.close();
         } catch (IOException e) {
              e.printStackTrace();
        }
	}
	private String loadFromFile(){
        String line;
        StringBuilder res = new StringBuilder();
        try {
              InputStream in = openFileInput("part.txt");
              if (in != null) {
                    InputStreamReader input = new InputStreamReader(in);
                    BufferedReader buffReader = new BufferedReader(input);
                    while (( line = buffReader.readLine()) != null) {
                          res.append(line);
                    }
                    in.close();
              }
        } catch(Exception e){
             e.printStackTrace();
        }
		return res.toString();
	}
	@Override
    public void onBackPressed(){
    	Intent openMainList = new Intent(List.this, Menu.class);
		startActivity(openMainList);
		finish();
    }
}
