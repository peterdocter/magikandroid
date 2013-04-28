package com.contolers.magik.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.data.bd.PersistenceManager;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class FileModificationService extends Service {
	// private MyFileObserver fileOb;
	private PersistenceManager pm;
	private static final int MAX_FO = 100;
	private List<MyFileObserver> fileOb_list = new ArrayList<MyFileObserver>();
	@Override
	public void onCreate() {
		pm = new PersistenceManager(this.getApplicationContext());
		if (!EnvironmentUtilsStatic.is_external_storage_available()) {
			Toast.makeText(FileModificationService.this,
					"SDCARD is not available!", Toast.LENGTH_SHORT).show();
			return;
		}
		crearObservadores();
	}

	public void crearObservadores() {
		File sdcard = new File(Environment.getExternalStorageDirectory().getPath()+"/Download");		
		fileOb_list.clear();
		num_of_fos = 0;
		createFileObs(sdcard);		
	}

	int num_of_fos = 0;

	private void createFileObs(File f) {
		MyFileObserver aFileOb = new MyFileObserver(f.getName(),
				f.getAbsolutePath(), this, f.isDirectory());
		String ext;
		if (num_of_fos > MAX_FO) {
			return;
		}
		if (!f.isDirectory()) {
			String filename = f.getName();
			if(filename.contains("."))
			{
				String[] campos = filename.split("\\.");
				ext = campos[campos.length - 1];
				if (ext.contains("pdf")) {
					guardarDocumentoBase(f.getAbsolutePath(), filename);
					fileOb_list.add(aFileOb);
					num_of_fos++;
					campos = null;
				}
			}
			
			ext = null;
			
			filename = null;
//			System.gc();
		} else if (f.getAbsolutePath().contains("Download")) {
			fileOb_list.add(aFileOb);
			num_of_fos++;
		}
		aFileOb = null;
//		System.gc();
		// fileOb = new MyFileObserver( f.getName() ,f.getAbsolutePath( ) ,this
		// );
		// MyFileObserver aFileOb = new MyFileObserver( f.getAbsolutePath( ) );
		// fileOb_list.add( aFileOb );
		
		try {
			if (f.isDirectory()) {
				for (File currentFile : f.listFiles()) {
					createFileObs(currentFile);
				}
			}
		} catch (Exception e) {
			Log.e("Error", e.toString());
		}
	}

	private void guardarDocumentoBase(String url, String filename) {
		try {
			if (!pm.isFileInTable(url)) {
				pm.createDocument(url, PersistenceManager.PDF, filename);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (ProcessMonitor.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	private void sendLocationBroadcast(Intent intent) {
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	public void starServicioProcess() {
		Intent intent = new Intent("action");
		intent.putExtra("StartServicioProcess", "StartServicioProcess");
		sendLocationBroadcast(intent);
	}

	public void stopServiceProcess() {
		Intent intent = new Intent("action");
		intent.putExtra("StopServicioProcess", "StopServicioProcess");
		sendLocationBroadcast(intent);
	}

	@Override
	public void onStart(Intent intent, int startid) {
		// fileOb.startWatching( );
		int i = 0;
		for (i = 0; i < fileOb_list.size(); ++i) {
			fileOb_list.get(i).startWatching();
		}
		Toast.makeText(this.getApplicationContext(),
				"start monitoring file modification" + " " + i,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroy() {
		// fileOb.stopWatching( );
		for (int i = 0; i < fileOb_list.size(); ++i) {
			fileOb_list.get(i).stopWatching();
		}
		Toast.makeText(this.getApplicationContext(),
				"stop monitoring file modification", Toast.LENGTH_SHORT).show();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
