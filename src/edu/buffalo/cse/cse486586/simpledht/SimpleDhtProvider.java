package edu.buffalo.cse.cse486586.simpledht;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.StringTokenizer;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SimpleDhtProvider extends ContentProvider {

    public static final Uri CONTENT_URI = Uri.parse("content://edu.buffalo.cse.cse486586.simpledht.provider/"+MyDbHelper.TABLE_NAME);
    public SQLiteDatabase db;
	static MyDbHelper myDb;
	int port_avd;
	String node_id,pos;
	int suc_pointer,pre_pointer;
	String suc_hash,pre_hash;
	boolean wait=true;
	String Key_found;
	String Value_found;

	@Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {    	
    	db= myDb.getWritableDatabase();    	
    	String key = (String) values.get(MyDbHelper.KEY);
    	String value = (String) values.get(MyDbHelper.VALUE);
    	String keyhash = null;
    	
    	try {
			 keyhash = genHash(key);
		} catch (NoSuchAlgorithmException e) {			
			e.printStackTrace();
		}    	    	
    	
    	
    	
    	if(keyhash.compareTo(node_id) <=0 ) {
    		if(suc_pointer== 11108 && pre_pointer ==11108 && port_avd==5554) {			// only 5554 is present    							
				Log.d("Content Provider","Insert");				
				long rowId = db.insert(MyDbHelper.TABLE_NAME, MyDbHelper.VALUE, values);
				
				if(rowId > 0){
					Uri newuri = ContentUris.withAppendedId(CONTENT_URI, rowId);
					getContext().getContentResolver().notifyChange(uri, null);
					return newuri;
				}
    		}    		    		    		
    		else if(pos.equals("1")){    							
				Log.d("Content Provider","Insert");				
				long rowId = db.insert(MyDbHelper.TABLE_NAME, MyDbHelper.VALUE, values);
				
				if(rowId > 0){
					Uri newuri = ContentUris.withAppendedId(CONTENT_URI, rowId);
					getContext().getContentResolver().notifyChange(uri, null);
					return newuri;
				}
    		}
    		else if(keyhash.compareTo(pre_hash)>0) {    							
				Log.d("Content Provider","Insert");				
				long rowId = db.insert(MyDbHelper.TABLE_NAME, MyDbHelper.VALUE, values);
		
				if(rowId > 0){
					Uri newuri = ContentUris.withAppendedId(CONTENT_URI, rowId);
					getContext().getContentResolver().notifyChange(uri, null);
					return newuri;
				}
    		}
    		else {
				String msg1="i"+" "+key+" "+value;
	    		Thread cli = new Thread(new Client(msg1,suc_pointer));
				cli.start(); 
			}
    	}
    	
    	else if(keyhash.compareTo(node_id)>0) { 
    			if(keyhash.compareTo(pre_hash)>0 && pos.equals("1")) {    						    				    				
    				Log.d("Content Provider","Insert");				
    				long rowId = db.insert(MyDbHelper.TABLE_NAME, MyDbHelper.VALUE, values);
			
    				if(rowId > 0){
    					Uri newuri = ContentUris.withAppendedId(CONTENT_URI, rowId);
    					getContext().getContentResolver().notifyChange(uri, null);
    					return newuri;
    				}
    			}
    			else {
    				String msg1="i"+" "+key+" "+value;
    	    		Thread cli = new Thread(new Client(msg1,suc_pointer));
    				cli.start();   
    			}
    	}    		    	    	    		    	    
        return null;
    }

    
    @Override
    public boolean onCreate() {            	
    	myDb = new MyDbHelper(getContext());
    	db = myDb.getWritableDatabase();
    	
    	TelephonyManager tel = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        port_avd=Integer.parseInt(portStr);
        try {
			node_id=genHash(portStr);
		} catch (NoSuchAlgorithmException e) {			
			e.printStackTrace();
		}
        Thread serv = new Thread(new Server());
        serv.start();
        
        if(portStr.equals("5554")) {
        	suc_pointer=11108;
        	pre_pointer=11108;
        	pre_hash=node_id;
        	suc_hash=node_id;
        	pos="1";        	
        }
        
        else {
        	String msg="I "+port_avd;
        	Thread cli1 = new Thread(new Client(msg,11108));
        	cli1.start();
        }                
                                                                    
        return true;
    }
    
    public class Server implements Runnable {
    	private String msg1=null;
    	public void run() {
    		try {
    			ServerSocket serv=new ServerSocket(10000);
    			while(true) {
    				Socket cl = serv.accept();
    				try {
    					BufferedReader in = new BufferedReader(new InputStreamReader(cl.getInputStream()));
    					msg1=null;
    					msg1 = in.readLine();
    					Thread par=new Thread(new Parser(msg1));
    					par.start();
    					in.close();
    				} catch(Exception e) {
    					e.printStackTrace();
    				}
    				cl.close();
    			}
    		} catch(Exception e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    public class Client implements Runnable {
    	String msg;
    	int send_port;
    	
    	public Client(String m,int send) {
    		msg=m;
    		send_port=send;
    	}    	
    	public void run() {
    		try {
    			Socket socket=new Socket("10.0.2.2",send_port);    			
    				try {
    					BufferedWriter out = new BufferedWriter(new OutputStreamWriter (socket.getOutputStream()));
    					out.write(msg);
    					out.flush();
        				} catch (Exception e) {
        					e.printStackTrace();
        				}    				         		
    			socket.close();    			
            } catch (Exception e) {
            	e.printStackTrace();           	
            }
    	}    	
    }
    
    public class Parser implements Runnable {
    	String msg;
    	String cmd;
    	String req_node;    
    	String pos_string;
    	String pos_send;
    	public Parser(String m) {
    		msg=m;
    	}
    	
    	public void run() {
    		StringTokenizer s=new StringTokenizer(msg," ");
    		cmd=s.nextToken();
    		char token=msg.charAt(0);
    		
    		if(token=='I') {
    			String hash_node=null;
    			req_node=s.nextToken();
    			try {
    				hash_node=genHash(req_node);
    			} catch(Exception e) {
    				e.printStackTrace();
    			}
    			
    			if(suc_pointer== 11108 && pre_pointer ==11108) {		
    				suc_pointer=Integer.parseInt(req_node)*2;
    				pre_pointer=Integer.parseInt(req_node)*2;
    				suc_hash=hash_node;
    				pre_hash=hash_node;
    				if(req_node.equals("5556")) {
    					pos="2";
    					pos_send="1";
    				}
    				if(req_node.equals("5558")) {
    					pos="1";
    					pos_send="2";    						
    				}
    				String msg1="U2 "+pos_send;
    				Thread cli2 = new Thread(new Client(msg1,Integer.parseInt(req_node)*2));
    	        	cli2.start();    				
    			}
    			else {
    				if(Integer.parseInt(req_node)*2==11112) {
    					suc_pointer=suc_pointer;  //suc_pointer will not change
    					pre_pointer=11112;			// pre_pointer will change
    					suc_hash=suc_hash;			// suc_hash will not change
    					pre_hash=hash_node;  		//pre_hash will change
    					pos="2";
    					pos_send="1";
    					String msg1="U3 "+pos_send;
    					Thread cli3 = new Thread(new Client(msg1,11112));  //send update msg to avd 5556
        	        	cli3.start(); 
        	        	pos_send="3";
        	        	msg1="U3 "+pos_send;
    					Thread cli4 = new Thread(new Client(msg1,11116));  //send update msg to avd 5558
        	        	cli4.start();         	        	        	        	
    				}
    				
    				if(Integer.parseInt(req_node)*2==11116) {
    					suc_pointer=11116;     //suc_pointer will change
    					pre_pointer=pre_pointer;			// pre_pointer will not change
    					suc_hash=hash_node;			// suc_hash will change
    					pre_hash=pre_hash;			//pre_hash will remain same
    					pos="2";
    					pos_send="3";
    					String msg1="U3 "+pos_send;
    					Thread cli5 = new Thread(new Client(msg1,11116));		//send update msg to 5558
    					cli5.start();
    					pos_send="1";
        	        	msg1="U3 "+pos_send;
    					Thread cli6 = new Thread(new Client(msg1,11112));  //send update msg to avd 5556
        	        	cli6.start();    					
    				}    			    				
    			}    			
    		}
    		
    		else if(token=='U') {
    			if(msg.charAt(1)=='2') {
    				pos_string=s.nextToken();
    				pos=pos_string;
    				suc_pointer=11108;    // 2 nodes in the ring
    				pre_pointer=11108;
    				try {
						suc_hash=genHash("5554");
					} catch (NoSuchAlgorithmException e) {						
						e.printStackTrace();
					}
    				pre_hash=suc_hash;
    				suc_hash=suc_hash;    				    				    			
    			}
    			
    			if(msg.charAt(1)=='3') {		// 3 nodes in the ring 
    				pos_string=s.nextToken();
    				if(pos_string.equals("1")) {
    					pos="1";
    					suc_pointer=11108;
    					pre_pointer=11116;
    					try {
    						suc_hash=genHash("5554");
    					} catch (NoSuchAlgorithmException e) {						
    						e.printStackTrace();
    					}
    					try {
    						pre_hash=genHash("5558");
    					} catch (NoSuchAlgorithmException e) {						
    						e.printStackTrace();
    					}
    				}    				
    				if(pos_string.equals("3")) {
    					pos="3";
    					suc_pointer=11112;
    					pre_pointer=11108;
    					try {
    						suc_hash=genHash("5556");
    					} catch (NoSuchAlgorithmException e) {						
    						e.printStackTrace();
    					}
    					try {
    						pre_hash=genHash("5554");
    					} catch (NoSuchAlgorithmException e) {						
    						e.printStackTrace();
    					}
    				}
    			}
    		}
    		
    		else if(token=='i') {
    			String key=s.nextToken();
    			String value=s.nextToken();
    			ContentValues cv = new ContentValues();
    			cv.put(MyDbHelper.KEY,key);
    			cv.put(MyDbHelper.VALUE,value);
    			Uri newUri = insert(SimpleDhtProvider.CONTENT_URI,cv);
    		}
    		
    		else if(token=='Q') {
    			String K=s.nextToken();
    			String add=s.nextToken();
    			int add1=Integer.parseInt(add);
    			String keyhash=null;
    			Cursor c=null;
    			try {
    				 keyhash = genHash(K);
    			} catch (NoSuchAlgorithmException e) {			
    				e.printStackTrace();
    			}    	    	    	    	    			
    			if(keyhash.compareTo(node_id)<=0 && keyhash.compareTo(pre_hash)>0) {
    				c= db.rawQuery("select * from "+MyDbHelper.TABLE_NAME+" where key like '"+K+"'", null);
    				int ind=c.getColumnIndex(MyDbHelper.VALUE);
    				if(c!=null) {
    					while(c.moveToNext()) {
    						String val=c.getString(ind);
    						String msg5="S"+" "+K+" "+val;
    						Thread cli8 = new Thread(new Client(msg5,add1));
    	    				cli8.start();  
    					}
    					
    				}
    				c.close();
    			}
    			
    			else if(keyhash.compareTo(node_id)<=0 && pos.equals("1"))  {    				
    				c= db.rawQuery("select * from "+MyDbHelper.TABLE_NAME+" where key like '"+K+"'", null);
    				int ind=c.getColumnIndex(MyDbHelper.VALUE);
    				if(c!=null) {
    					while(c.moveToNext()) {
    						String val=c.getString(ind);
    						String msg5="S"+" "+K+" "+val;
    						Thread cli8 = new Thread(new Client(msg5,add1));
    	    				cli8.start();  
    					}
    					
    				}
    				c.close();
    			}
    			
    			else if(keyhash.compareTo(pre_hash)>0 && pos.equals("1"))  {    				
    				c= db.rawQuery("select * from "+MyDbHelper.TABLE_NAME+" where key like '"+K+"'", null);
    				int ind=c.getColumnIndex(MyDbHelper.VALUE);
    				if(c!=null) {
    					while(c.moveToNext()) {
    						String val=c.getString(ind);
    						String msg5="S"+" "+K+" "+val;
    						Thread cli8 = new Thread(new Client(msg5,add1));
    	    				cli8.start();  
    					}
    					
    				}
    				c.close();
    			}
    			else {
    				String msg5="Q"+" "+K+" "+add;
    				Thread cli9 = new Thread(new Client(msg5,suc_pointer));
    				cli9.start(); 
    			}
    			     			
    		}    		
    		else if(token=='S') {
    			Key_found=s.nextToken();
    			Value_found=s.nextToken();
    			wait=false;
    		}
    		
    	}
    }
    
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,String sortOrder) {
		Cursor c;
		int ret=port_avd*2;		
		String keyhash=null;
		if(selection==null) {
			c= db.rawQuery("select * from "+MyDbHelper.TABLE_NAME, null);
			return c;
		}
		try {
			 keyhash = genHash(selection);
		} catch (NoSuchAlgorithmException e) {			
			e.printStackTrace();
		}    
							
			if(keyhash.compareTo(node_id)<=0 && keyhash.compareTo(pre_hash)>0) {
				c= db.rawQuery("select * from "+MyDbHelper.TABLE_NAME+" where key like '"+selection+"'", null);
				return c;
			}
			else if(keyhash.compareTo(pre_hash)>0 && pos.equals("1")) {
				c= db.rawQuery("select * from "+MyDbHelper.TABLE_NAME+" where key like '"+selection+"'", null);
				return c;
			}	
			else if(keyhash.compareTo(node_id)<=0 && pos.equals("1")) {
				c= db.rawQuery("select * from "+MyDbHelper.TABLE_NAME+" where key like '"+selection+"'", null);
				return c;
			}
			else if(suc_pointer== 11108 && pre_pointer ==11108 && port_avd==5554) {
				c= db.rawQuery("select * from "+MyDbHelper.TABLE_NAME+" where key like '"+selection+"'", null);
				return c;
			}
			else  {				
				String msg1="Q"+" "+selection+" "+ret;
	    		Thread cli7 = new Thread(new Client(msg1,suc_pointer));
				cli7.start();
				while(wait==true) {
					/* wait for the query to return*/
				}	
				wait=true;
				MatrixCursor m = new MatrixCursor(new String[] {MyDbHelper.KEY,MyDbHelper.VALUE});
				m.newRow().add(Key_found).add(Value_found);				
				return m;
			}		    	
	}

   
    private String genHash(String input) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] sha1Hash = sha1.digest(input.getBytes());
        Formatter formatter = new Formatter();
        for (byte b : sha1Hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}	
}
