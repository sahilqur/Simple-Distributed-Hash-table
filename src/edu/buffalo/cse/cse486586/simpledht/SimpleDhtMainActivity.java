package edu.buffalo.cse.cse486586.simpledht;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SimpleDhtMainActivity extends Activity {
	private Button LDump;
	private Button GDump;
	private Button Test;	
	private TextView txt_view;
	String msg2;
	Cursor resultCursor=null;
	SimpleDhtProvider s=new SimpleDhtProvider(); 	
	public static final Uri CONTENT_URI = Uri.parse("content://edu.buffalo.cse.cse486586.simpledht.provider");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_dht_main);
        
        Test=(Button) findViewById(R.id.button3);
        txt_view=(TextView) findViewById(R.id.textView1);
        
        LDump=(Button) findViewById(R.id.button1);
        GDump=(Button) findViewById(R.id.button2);        
               
        txt_view.setMovementMethod(new ScrollingMovementMethod());
        Test.setOnClickListener(new OnTestClickListener(txt_view, getContentResolver()));
        
        LDump.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {        	
        		resultCursor = getContentResolver().query(SimpleDhtProvider.CONTENT_URI,null,null,null,null);
        		txt_view.setText("");
        		int index_value = resultCursor.getColumnIndex(MyDbHelper.VALUE);
        		int index_key = resultCursor.getColumnIndex(MyDbHelper.KEY);
        		if(resultCursor==null) {
        			txt_view.append("\n No values in AVD");
        		}
        		else if(resultCursor!=null) {
        			while(resultCursor.moveToNext()) {        				
        				txt_view.append("\n"+resultCursor.getString(index_key)+" "+resultCursor.getString(index_value));
        			}
        		}
        		resultCursor.close();
        	}
        	
        });               
        
        GDump.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) { 
        		txt_view.setText("");
        		try {        		
        			for(int i=0;i<OnTestClickListener.TEST_CNT;i++) {
        				String key= "key"+Integer.toString(i);
        				resultCursor = getContentResolver().query(SimpleDhtProvider.CONTENT_URI, null,key, null, null);
        				resultCursor.moveToFirst();        					
        				int keyIndex = resultCursor.getColumnIndex(MyDbHelper.KEY);
        				int valueIndex = resultCursor.getColumnIndex(MyDbHelper.VALUE);					    					  	
        				String returnKey = resultCursor.getString(keyIndex);
        				String returnValue = resultCursor.getString(valueIndex);
        				txt_view.append("\n"+returnKey+" "+returnValue);  	        			      		     			     			        
        				resultCursor.close();
        			}        			
        		}catch(Exception e) {
        			e.printStackTrace();
        		}
        	}
        });                                
                
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_simple_dht_main, menu);
        return true;
    }

}
