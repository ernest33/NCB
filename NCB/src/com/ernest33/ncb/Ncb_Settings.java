package com.ernest33.ncb;

//import com.ernest33.ncb1.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

//import com.ernest33.ncb1.R;

import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import static android.graphics.Bitmap.createBitmap;

public class Ncb_Settings extends Activity
{
	private static final int	REQUEST_CODE	= 1;
	private static final String TAG = "ernest33-act";
	ImageView img;			// Target the Image View control
	SeekBar seekbar;		// Target the transparency seekbar
	private Bitmap bitmap;	// Contain the bitmap to use
	private Toast tt;		// Used to show small message text
	public boolean Isimgload = false;
	public static final String PREFS_NAME = "ernest33_ncb1_prefs";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ncb__settings);
		img = (ImageView) findViewById(R.id.imageView1);
		seekbar = (SeekBar) findViewById(R.id.seekBar1);

		//String fileUrl = "/notifbg.png";
		String file = android.os.Environment.getExternalStorageDirectory().getPath() + "/notifbg.png";
		File f = new File(file);
		if (!f.exists())
		{
			toastMessage("Not exist");
			CreateDummyBitmap();
			try
			{
				apply_image();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			img.setImageBitmap(bitmap);
			Isimgload = false;
		}
		else
		{
			Isimgload = true;
		}
		if(Isimgload == true)
		{
			// <IMAGEVIEW>
			try
			{
				if(bitmap != null)
					bitmap.recycle();
				//File dir = Environment.getExternalStorageDirectory();// + "/notifbg.png";
				//File output = new File(dir, "notifbg.png");
				File output = new File(android.os.Environment.getExternalStorageDirectory().getPath() + "/notifbg.png");
				InputStream stream = getContentResolver().openInputStream(Uri.fromFile(output));
				bitmap = BitmapFactory.decodeStream(stream);
				stream.close();
				img.setImageBitmap(bitmap);
			}
			catch(FileNotFoundException e)
			{
				toastMessage("No default image. Please select one ;-)");
				e.printStackTrace();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			// </IMAGEVIEW>

			// <SEEKBAR>
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 4);
			int trans = settings.getInt("last_alpha", 255);
			seekbar.setProgress(trans);
			createNewImage(trans);
			img.setImageBitmap(bitmap);

			// Init event for seekbar
			InitSeekBarEvent();


			// </SEEKBAR>
		}
		// <BUTTONS>
		Button btsel = (Button)findViewById(R.id.bt_sel);
		btsel.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				select_image(arg0);
			}
		});
		Button btapply = (Button)findViewById(R.id.bt_apply);
		btapply.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				try
				{
					//toastMessage("Saving image");
					apply_image();
					//toastMessage("Image saved !");
				}
				catch (FileNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					//alertbox("FILENOTFOUND",e.getMessage());
					toastMessage("FileNotFoundException !");
					toastMessage(e.getLocalizedMessage());
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					//alertbox("IOEXCEP",e.getMessage());
					toastMessage("IOException !");
				}
			}
		});	
		// </BUTTONS>
	}

	private void CreateDummyBitmap()
	{
		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);

		int w = dm.widthPixels;
		int h = dm.heightPixels;

		Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		bitmap = createBitmap(w, h, conf); // this creates a MUTABLE bitmap
		Canvas canvas = new Canvas(bitmap);
		canvas.drawARGB(255,80,80,80);
	}

	private void InitSeekBarEvent()
	{
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				// TODO Auto-generated method stub
				// not used
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
				// TODO Auto-generated method stub
				// not used
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				// Change image transparency using progress value
				if((progress > 1) && (progress < 255))
				{
					if(Isimgload)
					{
						createNewImage(progress);
						img.setImageBitmap(bitmap);
					}
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ncb__settings, menu);
		return true;
	}
	//...
	public void select_image(View v)
	{
		// First get the vertical and horizontal resolution
		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm); 
		
		int width = dm.widthPixels;
		int height = dm.heightPixels;

		// Launch the intent...
		Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra("crop", true);
		intent.putExtra("scale", true);
		intent.putExtra("outputX", width);
		intent.putExtra("outputY", height);
		intent.putExtra("aspectX", width); // 9
		intent.putExtra("aspectY", height); // 16
		File dir = Environment.getExternalStorageDirectory();
		File output = new File(dir, "notifbg.png");
		//File output = new File(android.os.Environment.getExternalStorageDirectory().getPath() + "/notifbg.png");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
		{
			File output = null;
			try
			{
				if(bitmap != null)
					bitmap.recycle();
				//File dir = Environment.getExternalStorageDirectory();// + "/notifbg.png";
				//File output = new File(dir, "notifbg.png");
				output = new File(android.os.Environment.getExternalStorageDirectory().getPath() + "/notifbg.png");
				InputStream stream = getContentResolver().openInputStream(Uri.fromFile(output));
				bitmap = BitmapFactory.decodeStream(stream);
				stream.close();
	            img.setImageBitmap(bitmap);
				Isimgload = true;
				InitSeekBarEvent();
			}
			catch(FileNotFoundException e)
			{
				e.printStackTrace();
				toastMessage("Notfound\n" + e.getMessage() + "\n" + output.getAbsolutePath());
				Isimgload = false;
			}
			catch(IOException e)
			{
				e.printStackTrace();
				toastMessage("IOTrouble :-)" + e.getMessage());
				Isimgload = false;
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
		else
		{
			Isimgload = false;
		}
	}
	
	public void toastMessage(String message)
	{
		tt = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        tt.show();
	}
	public void apply_image() throws FileNotFoundException, IOException
	{
			//File dir = Environment.getExternalStorageDirectory();// + "/notifbg.png";
			//File imageFile = new File(dir, "notifbg.png");
			//File imageFile = new File(Environment.getExternalStorageDirectory(), "notifbg.png");
			File imageFile = new File(android.os.Environment.getExternalStorageDirectory().getPath() + "/notifbg.png");
			FileOutputStream fileOutputStream = null;
			fileOutputStream = new FileOutputStream(imageFile);
			bitmap.compress(CompressFormat.PNG, 100, fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();
	}
	
    //
    // adjust alpha value of the image
    // @param alphaValue
    //
    private void createNewImage(int alphaValue)
	{
		int m_width = bitmap.getWidth();
		int m_height = bitmap.getHeight();

		// try to use Pixel Array now...
		int[] pixels = new int[m_width * m_height];
		bitmap.getPixels(pixels, 0, m_width, 0, 0, m_width, m_height);
		for (int y = 0; y < m_height; y++)
		{
			for (int x = 0; x < m_width; x++)
			{
				int index = y * m_width + x;
				int a = (pixels[index] >> 24) & 0xff;
				int r = (pixels[index] >> 16) & 0xff;
				int g = (pixels[index] >> 8) & 0xff;
				int b = pixels[index] & 0xff;
				a = alphaValue;
				pixels[index] = (a << 24) | (r << 16) | (g << 8) | b;
			}
		}

		// myImage.setPixels(pixels, 0, m_width, 0, 0, m_width, m_height); seems like setPixel method have some problem
		Bitmap bm = createBitmap(pixels, 0, m_width, m_width, m_height, Bitmap.Config.ARGB_8888);
		pixels = null;
		bitmap = bm;
    }
    
	@Override
    protected void onStop(){
       super.onStop();

      // We need an Editor object to make preference changes.
      // All objects are from android.context.Context
      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 4);
      SharedPreferences.Editor editor = settings.edit();
      editor.putInt("last_alpha", seekbar.getProgress());

      // Commit the edits!
      editor.commit();
    }
}
