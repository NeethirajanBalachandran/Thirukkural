package tamil.developers.thirukkural;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Random;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import tamil.developers.thirukkural.DragDropManager.DropZoneListener;

public class KuralGame extends Activity implements OnTouchListener {

    private AdView mAdView;
    static SQLiteDatabase myDB;
	static SQLiteDatabase myDB2;
	static SQLiteDatabase myDB3;
	public static final String TABLE_Kutal = "kutal_list";
	public static final String TABLE_Result = "kutal_Result";
	public static final String TABLE_part = "Part_list";
	public static final String TABLE_Name_subpart = "sub_Part_list";   
	
	Convert conn;
	Typeface tf;
	Handler handler;
	
	private PopupWindow pw;
	private PopupWindow pwover;
	View layout;
	View layoutover;
	
	public String my_kural_check[];
	public boolean is_there_any_work[] = {false,false,false,false,false,false,false};
	public int dropla_button[] = {0,0,0,0,0,0,0}; // button entha drop box la irruku
	public int position[] = {3,4,1,7,6,5,2}; // word entha button la irruku
	public int[] position_1[] = {{2,7,4,6,3,5,1},{4,2,3,1,7,5,6},{7,5,3,6,2,1,4},{3,7,5,4,2,1,6},{1,5,7,6,3,4,2},{2,4,6,3,1,5,7},{6,2,4,3,5,7,1},{4,5,3,2,1,6,7},{3,2,5,1,6,7,4},{2,6,4,7,1,5,3}};
	public int[] text_id = {R.id.textView1,R.id.textView2,R.id.textView3,R.id.textView4,R.id.textView5,R.id.textView6,R.id.textView7};
	public int[] button_id = {R.id.button1,R.id.button2,R.id.button3,R.id.button4,R.id.button5,R.id.button6,R.id.button7};
	int kuralID;
	public int star_count = 5;
	int delay = 6;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kural_game);

		MobileAds.initialize(this, "ca-app-pub-1233786554019860~9205333431");

		mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
        
		conn = new Convert();
		tf = Typeface.createFromAsset(this.getAssets(),"fonts/vellore.ttf");
		handler = new Handler();
		
		DragDropManager.getInstance().init(this);
		Button btn;
		for (int i =0; i<7; i++){
			findViewById(text_id[i]).setOnTouchListener(this);
			findViewById(button_id[i]).setOnTouchListener(this);
			btn = findViewById(button_id[i]);
			btn.setTypeface(tf);
			DragDropManager.getInstance().addDropZone(findViewById(button_id[i]), dropZoneListener1);
		}
		btn = findViewById(R.id.help);
		btn.setTypeface(tf);

		databaseCreation();
		load_kural_info();
		display_kural();
		
		myDB3.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_Result + " (kural_id INT(4), star INT(2), timetaken INT(6));");
		Cursor c = myDB3.rawQuery("SELECT * FROM " + TABLE_Result + " Where kural_id = " + kuralID, null);
		init_complete();
		if (c.getCount() > 0)
		{
			ImageView star = findViewById(R.id.complete);
			star.setVisibility(View.VISIBLE);
			c.moveToFirst();
			int Column_star = c.getColumnIndex("star");
			int star_count = c.getInt(Column_star);
			if (star_count > 0) {
				star = findViewById(R.id.star1);
				star.setVisibility(View.VISIBLE);
			}
			if (star_count > 1) {
				star = findViewById(R.id.star2);
				star.setVisibility(View.VISIBLE);
			}
			if (star_count > 2) {
				star = findViewById(R.id.star3);
				star.setVisibility(View.VISIBLE);
			}
			if (star_count > 3) {
				star = findViewById(R.id.star4);
				star.setVisibility(View.VISIBLE);
			}
			if (star_count > 4) {
				star = findViewById(R.id.star5);
				star.setVisibility(View.VISIBLE);
			}
		}
		c.close();
	}
	public void init_complete(){
		ImageView star = findViewById(R.id.complete);
		star.setVisibility(View.GONE);
		star = findViewById(R.id.star1);
		star.setVisibility(View.GONE);
		star = findViewById(R.id.star2);
		star.setVisibility(View.GONE);
		star = findViewById(R.id.star3);
		star.setVisibility(View.GONE);
		star = findViewById(R.id.star4);
		star.setVisibility(View.GONE);
		star = findViewById(R.id.star5);
		star.setVisibility(View.GONE);
	}
    DropZoneListener dropZoneListener1 = new DropZoneListener()
    {
        @Override
        public void OnDropped(View zone, Object item)
        {
        	String Result = Button_Text(Integer.parseInt(item.toString()));
        	int from_button_num = get_button_num(Integer.parseInt(item.toString()));
        	Button to = (Button)zone;
        	int to_button_num = get_button_num(to.getId());
        	if (Result.equals("Button"))
        	{
        		Button from = findViewById(Integer.parseInt(item.toString()));
        		String st_1 = (String) from.getText();
        		String st_2 = (String) to.getText();
        		to.setText(st_1);
        		to.setTypeface(tf);
        		from.setText(st_2);
        		from.setTypeface(tf);
        		boolean bool_1 = is_there_any_work[from_button_num - 1];
        		boolean bool_2 = is_there_any_work[to_button_num - 1];
        		is_there_any_work[from_button_num - 1] = bool_2;
        		is_there_any_work[to_button_num - 1] = bool_1;

        		int num_1 = dropla_button[from_button_num-1];
        		int num_2 = dropla_button[to_button_num-1];
        		
        		dropla_button[from_button_num-1] = num_2;
        		dropla_button[to_button_num-1] = num_1;
        	}
        	if (Result.equals("Text"))
        	{
        		if (!is_there_any_work[to_button_num - 1])
        		{
	                TextView from = findViewById(Integer.parseInt(item+""));
	                int text = get_textview_num(from.getId());
	                dropla_button[to_button_num-1] = text;
	                to.setText(from.getText());
	        		to.setTypeface(tf);
	                from.setVisibility(View.GONE);
	                is_there_any_work[to_button_num - 1] = true;
        		}
        	}
        	int count = 0;
        	for (int i = 0 ; i < 7 ; i ++)
        	{
        		if (is_there_any_work[i])
        		{
        			count = count + 1;
        		}
        	}
        	Button btn = findViewById(R.id.submit);
        	btn.setText(conn.convertText(getString(R.string.check)));
    		btn.setTypeface(tf);
    		if (count == 7)
    		{
    			btn.setVisibility(View.VISIBLE);
    		}
    		else
    		{
    			btn.setVisibility(View.GONE);
    		}
        	
        }
        @Override
        public void OnDragZoneLeft(View zone, Object item)
        {
        	            
        }
        @Override
        public void OnDragZoneEntered(View zone, Object item)
        {
        	
        }
    };
	@Override
    public boolean onTouch(View v, MotionEvent event)
    {
    	DragDropManager.getInstance().startDragging(v, v.getId());
        return false;
    }
    public String Button_Text(int id){
    	switch (id)
        {
	        case R.id.button1:		return "Button";
	        case R.id.button2:		return "Button";
	        case R.id.button3:		return "Button";
	        case R.id.button4:		return "Button";
	        case R.id.button5:		return "Button";
	        case R.id.button6:		return "Button";
	        case R.id.button7:		return "Button";
	        case R.id.textView1:	return "Text";
	        case R.id.textView2:	return "Text";
	        case R.id.textView3:	return "Text";
	        case R.id.textView4:	return "Text";
	        case R.id.textView5:	return "Text";
	        case R.id.textView6:	return "Text";
	        case R.id.textView7:	return "Text";
	        default:			return "";
        }
    }
    public int get_button_num(int id){
    	switch (id)
        {
	        case R.id.button1:		return 1;
	        case R.id.button2:		return 2;
	        case R.id.button3:		return 3;
	        case R.id.button4:		return 4;
	        case R.id.button5:		return 5;
	        case R.id.button6:		return 6;
	        case R.id.button7:		return 7;
	        default:			return 0;
        }
    }
    public int get_textview_num(int id){
    	switch (id)
        {
	        case R.id.textView1:		return 1;
	        case R.id.textView2:		return 2;
	        case R.id.textView3:		return 3;
	        case R.id.textView4:		return 4;
	        case R.id.textView5:		return 5;
	        case R.id.textView6:		return 6;
	        case R.id.textView7:		return 7;
	        default:			return 0;
        }
    }
	private void databaseCreation() {
		//database creation start
		String dbName  = this.getFilesDir().getPath()+"/kural_data.db";
		String dbName2  = this.getFilesDir().getPath()+"/list.db";
		String dbName3  = this.getFilesDir().getPath()+"/scoreAndResult.db";
	    myDB = openOrCreateDatabase(dbName , Context.MODE_PRIVATE, null);
	    myDB.setVersion(1);
	    myDB.setLocale(Locale.getDefault());
	    
	    myDB2 = openOrCreateDatabase(dbName2 , Context.MODE_PRIVATE, null);
	    myDB2.setVersion(1);
	    myDB2.setLocale(Locale.getDefault());
	    
	    myDB3 = openOrCreateDatabase(dbName3 , Context.MODE_PRIVATE, null);
	    myDB3.setVersion(1);
	    myDB3.setLocale(Locale.getDefault());
	    //database creation end
	}
    private void load_kural_info() {
		int id;
		String result = loadFromFile("kural.txt");
		if (result.equals("") || result.equals(null))
		{
			id = 1;
		}
		else
		{
			id = Integer.parseInt(loadFromFile("kural.txt"));
		}
		int partID;
		if (id == 1) partID = id /10;
		else partID = (id -1) /10;
		partID = partID +1;
		//set athigaram name
		TextView txt = findViewById(R.id.part_txt);
		if (partID < 39){
			txt.setText(conn.convertText(getString(this.getResources().getIdentifier("select_part1", "string", getPackageName()))) + " -- ");
		} else if (partID > 38 && partID < 109){
			txt.setText(conn.convertText(getString(this.getResources().getIdentifier("select_part2", "string", getPackageName()))) + " -- ");
		} else {
			txt.setText(conn.convertText(getString(this.getResources().getIdentifier("select_part3", "string", getPackageName()))) + " -- ");
		}
		txt.setTypeface(tf);
		txt.setTextSize(16);
		
		//set part name
		Cursor c1 = myDB2.rawQuery("SELECT * FROM " + TABLE_part + " Where list_number = " + partID , null);
		c1.moveToFirst();
		int Column_part_name = c1.getColumnIndex("list_name");
		int Column_subpart_id = c1.getColumnIndex("subpart");
		txt = findViewById(R.id.athigaram_txt);
		txt.setText(conn.convertText(c1.getString(Column_part_name))+ " -- ");
		txt.setTypeface(tf);
		txt.setTextSize(16);

		//set sub part name
		Cursor c2 = myDB2.rawQuery("SELECT * FROM " + TABLE_Name_subpart + " Where sub_part_number = " + c1.getInt(Column_subpart_id) , null);
		c2.moveToFirst();
		int Column_subpart_name = c2.getColumnIndex("sub_part_name");
		txt = findViewById(R.id.subpart_txt);
		txt.setText(conn.convertText(c2.getString(Column_subpart_name)) + " -- ");
		txt.setTypeface(tf);
		txt.setTextSize(16);
		c1.close();
		c2.close();
		//set kural number
		txt = findViewById(R.id.kural_num_txt);
		txt.setText(conn.convertText(getString(R.string.kural)+ " " + id));
		txt.setTypeface(tf);
		txt.setTextSize(16);
		kuralID = id;
	}
    private void display_kural() {
		int load_kural_id;
		if (loadFromFile("kural.txt") != null) {
			load_kural_id = Integer.parseInt(loadFromFile("kural.txt"));
		}
		else {
			load_kural_id = 1;
		}
		Cursor c = myDB.rawQuery("SELECT * FROM " + TABLE_Kutal + " Where id = " + load_kural_id , null);
		int Column1 = c.getColumnIndex("kural_text");
		c.moveToFirst();
		String kural = c.getString(Column1);
		String kural_split[] = kural.split(":");
		c.close();
		my_kural_check = kural.split(":");
       
		int rand = createRandomNumber();
		position = position_1[rand-1];
		
		TextView subject;
		
		subject = findViewById(R.id.textView1);
		subject.setText(conn.convertText(kural_split[position[0]-1]));
		subject.setTypeface(tf);
		subject = findViewById(R.id.textView2);
		subject.setText(conn.convertText(kural_split[position[1]-1]));
		subject.setTypeface(tf);
		subject = findViewById(R.id.textView3);
		subject.setText(conn.convertText(kural_split[position[2]-1]));
		subject.setTypeface(tf);
		subject = findViewById(R.id.textView4);
		subject.setText(conn.convertText(kural_split[position[3]-1]));
		subject.setTypeface(tf);
		subject = findViewById(R.id.textView5);
		subject.setText(conn.convertText(kural_split[position[4]-1]));
		subject.setTypeface(tf);
		subject = findViewById(R.id.textView6);
		subject.setText(conn.convertText(kural_split[position[5]-1]));
		subject.setTypeface(tf);
		subject = findViewById(R.id.textView7);
		subject.setText(conn.convertText(kural_split[position[6]-1]));
		subject.setTypeface(tf);
    }
    private int createRandomNumber() {
		int min = 1;
		int max = 10;
		Random r = new Random();
		return r.nextInt(max - min + 1) + min;
	}
    private String loadFromFile(String file_name){
        String line;
        StringBuilder res = new StringBuilder();
        try {
		  InputStream in = openFileInput(file_name);
		  if (in != null) {
		    InputStreamReader input = new InputStreamReader(in);
		    BufferedReader buffReader = new BufferedReader(input);
		    while ((line = buffReader.readLine()) != null) {
		          res.append(line);
		    }
		    in.close();
		  }
        } catch(Exception e){
        	e.printStackTrace();
        }
		return res.toString();
	}
    public void checkAnswerClick(View v) {
    	String kural = "";
    	for (int i = 0; i < 7 ; i ++)
    	{
    		Button btn_check = findViewById(this.getResources().getIdentifier("button" + (i+1), "id", getPackageName()));
    		if (kural.equals("")) kural = btn_check.getText().toString();
    		else kural += "::" + btn_check.getText().toString();
    	}
    	Cursor c = myDB.rawQuery("SELECT * FROM " + TABLE_Kutal + " Where id = " + kuralID , null);
    	int Column1 = c.getColumnIndex("kural_text");
    	c.moveToFirst();
    	String kural_1 = c.getString(Column1);
    	String[] kuralsplit = kural.split("::");
    	String[] kural_1split = kural_1.split(":");
    	c.close();
    	Button btn;
    	int count = 0;
    	for (int i = 0; i < 7 ; i ++)
		{
			btn = findViewById(this.getResources().getIdentifier("button" + (i+1), "id", getPackageName()));
    		if (kuralsplit[i].equals(conn.convertText(kural_1split[i])))
    		{
    			btn.setTextColor(getResources().getColor(R.color.green));
    			count = count + 1;
    		}
    		else
    		{
    			btn.setTextColor(getResources().getColor(R.color.red));
    		}
		}
    	if (count == 7)
    	{
			finishPopupWindow();
    	}
    	else
    	{
    		if (star_count > 0)
    		{
    			star_count = star_count - 1;
    		}
    	}
    }
	private void finishPopupWindow() {

    	//myDB.execSQL("Drop TABLE IF EXISTS " + TABLE_Result + " ;");
		myDB3.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_Result + " (kural_id INT(4), star INT(2), timetaken INT(6));");
		Cursor c = myDB3.rawQuery("SELECT * FROM " + TABLE_Result + " Where kural_id = " + kuralID , null);
		
		if (c.getCount() > 0)
		{
			myDB3.execSQL("UPDATE " + TABLE_Result + " SET star = " + star_count
			   	     + " Where kural_id = " + kuralID + ";");
		}
		else
		{
			myDB3.execSQL("INSERT INTO " + TABLE_Result + " (kural_id, star, timetaken)"
			   	     + " VALUES (" + kuralID + ", " + star_count + ", " + "0" + ");");
		}
		c.close();
    	try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
        	LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	View layout = inflater.inflate(R.layout.finish_box, null, false);
        	int measuredWidth;
        	int measuredHeight;
        	WindowManager w = getWindowManager();
        	Display d = w.getDefaultDisplay();
			measuredWidth = d.getWidth();
			measuredHeight = d.getHeight();
        	pw = new PopupWindow(layout, (int) (measuredWidth * 0.85), (int) (measuredHeight * 0.50), true);
        	
            // display the popup in the center
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
            Button btn =  layout.findViewById(R.id.Next);
            btn.setOnClickListener(next_level);
            btn.setText(conn.convertText(getString(R.string.btn_next)));
            btn.setTypeface(tf);
            btn =  layout.findViewById(R.id.MainMenu);
            btn.setOnClickListener(main_menu);
            btn.setText(conn.convertText(getString(R.string.btn_mainmenu)));
            btn.setTypeface(tf);
            btn =  layout.findViewById(R.id.Retry);
            btn.setOnClickListener(retry);
            btn.setText(conn.convertText(getString(R.string.btn_retry)));
            btn.setTypeface(tf);
            
            ImageView img = layout.findViewById(R.id.imageView1);
            if (star_count > 0) img.setVisibility(View.VISIBLE);
            else img.setVisibility(View.GONE);
            img = layout.findViewById(R.id.imageView2);
            if (star_count > 1) img.setVisibility(View.VISIBLE);
            else img.setVisibility(View.GONE);
            img = layout.findViewById(R.id.imageView3);
            if (star_count > 2) img.setVisibility(View.VISIBLE);
            else img.setVisibility(View.GONE);
            img = layout.findViewById(R.id.imageView4);
            if (star_count > 3) img.setVisibility(View.VISIBLE);
            else img.setVisibility(View.GONE);
            img = layout.findViewById(R.id.imageView5);
            if (star_count > 4) img.setVisibility(View.VISIBLE);
            else img.setVisibility(View.GONE);
            img = layout.findViewById(R.id.imageView6);
            if (star_count == 0) img.setVisibility(View.GONE);
            else img.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private OnClickListener main_menu = new OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
            Intent openMainList = new Intent(getBaseContext(), Menu.class);
            startActivity(openMainList);
            finish();
        }
    };	
    private OnClickListener retry = new OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
            Intent openMainList = new Intent(getBaseContext(), KuralGame.class);
            startActivity(openMainList);
            finish();
        }
    };		
    private OnClickListener next_level = new OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
            int num = kuralID;
            if (num != 1330)
    		{
    			num = num + 1;
    		}
    		try {
    			saveFromFile(num + "", "kural.txt");
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    		}
    		go();
        }
    };
    public void prevClick(View view){
		int num = kuralID;
		if (num != 1)
		{
			num = num - 1;
		}
		try {
			saveFromFile(num + "", "kural.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		go();
	}
	public void nextClick(View view){
		int num = kuralID;
		if (num != 1330)
		{
			num = num + 1;
		}
		try {
			saveFromFile(num + "", "kural.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		go();
	}
    public void go(){
		Intent openMainList = new Intent(getBaseContext(), KuralGame.class);
        startActivity(openMainList);
        finish();
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
	public void helpClick(View view){
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     	layout = inflater.inflate(R.layout.help_box, null, false);
     	layoutover = inflater.inflate(R.layout.kural_help_box, null, false);
     	int measuredWidth;
     	int measuredHeight;
     	WindowManager w = getWindowManager();
     	Display d = w.getDefaultDisplay();
		measuredWidth = d.getWidth();
		measuredHeight = d.getHeight();
     	pw = new PopupWindow(layout, (int) (measuredWidth * 0.85), (int) (measuredHeight * 0.50), true);
     	pwover = new PopupWindow(layoutover, (int) (measuredWidth * 0.85), (int) (measuredHeight * 0.50), true);
         // display the popup in the center
     	pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
     	Button btn =  layout.findViewById(R.id.ok);
     	btn.setOnClickListener(ok);
     	btn.setText(conn.convertText(getString(R.string.btn_ok)));
        btn.setTypeface(tf);
		btn =  layout.findViewById(R.id.cancel);
		btn.setOnClickListener(cancel);
		btn.setText(conn.convertText(getString(R.string.btn_no)));
        btn.setTypeface(tf);
        TextView txt1 = layout.findViewById(R.id.textView1);
        txt1.setText(conn.convertText(getString(R.string.help_msg)));
		txt1.setTypeface(tf);
		
		int load_kural_id;
		if (loadFromFile("kural.txt") != null)
		{
			load_kural_id = kuralID;
		}
		else
		{
			load_kural_id = 1;
		}
		Cursor c = myDB.rawQuery("SELECT * FROM " + TABLE_Kutal + " Where id = " + load_kural_id , null);
		int Column1 = c.getColumnIndex("kural_text");
		c.moveToFirst();
		String kural = c.getString(Column1);
		String kural_split[] = kural.split(":");
		c.close();
		TextView txt = layoutover.findViewById(R.id.textView1);
		txt.setText(conn.convertText(kural_split[0] + " " + kural_split[1] + " " + kural_split[2] + " " + kural_split[3]));
		txt.setTypeface(tf);
		txt = layoutover.findViewById(R.id.textView2);
		txt.setText(conn.convertText(kural_split[4] + " " + kural_split[5] + " " + kural_split[6]));
		txt.setTypeface(tf);
	}
    private OnClickListener ok = new OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
            pwover.showAtLocation(layoutover, Gravity.CENTER, 0, 0);
            delay = 6;
  			for (int i = 0 ; i < 6 ; i ++)
            {
	            handler = new Handler();
	            handler.postDelayed(new Runnable() {
	              @Override
	              public void run() {
	            	  	//Do something after 1000ms
	            	  	TextView txt = layoutover.findViewById(R.id.time);
	          			txt.setText((delay - 1));
	          			delay = delay - 1;
	          			if (delay == 0)
	          			{
	          				pwover.dismiss();
	          				TextView txtv = findViewById(R.id.help);
	          				txtv.setVisibility(View.GONE);
	          				star_count = star_count - 1;
	          			}
	              }
	            }, 1000 * i);
            }
        }
    };
    private OnClickListener cancel = new OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
        }
    };
	@Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
    @Override
    public void onBackPressed(){
    	Intent openMainList = new Intent(KuralGame.this, List.class);
		startActivity(openMainList);
		finish();
    }
}
