/**
 * 
 */
package com.magik.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.Vibrator;

import com.magik.activities.InterfaceManager;
import com.magik.db.PersistenceManager;
import com.magik.services.tasks.SendPDFTask;

/**
 * @author Kelvin Guerrero
 * 
 */
public class SyncService extends Service {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		final PersistenceManager manager = new PersistenceManager(
				getApplicationContext());
		final String[] unsynced = manager.getUnsyncedPaths();
		final InterfaceManager interfaceManager = InterfaceManager
				.getInstance();
		Thread t = new Thread() {
			@Override
			public void run() {
				while (interfaceManager.ismSync()) {
					try {
						if(connected())
						{
							Object[] params = new Object[2];
							Object[] params2 = new Object[2];
							String[] pals = new String[] { "Kelvin", "Tita",
									"Julio" };
							String recs = "";
							File f = null;
							for (String path : unsynced) {
								f = new File(path);
								params[0] = f;
								params[1] = WebServiceConnection.RECOMEND_FILE;
								params2[0] = f;
								params2[1] = WebServiceConnection.PALABRAS_FILE;
								if (manager.getDocumentType(path).equals(
										PersistenceManager.PDF)) {
									SendPDFTask task = new SendPDFTask();
									SendPDFTask task2 = new SendPDFTask();
									task.execute(params);
									task2.execute(params2);
									recs = task.get();
									System.out.println(recs);
									pals = task2.get().split(";");
									System.out.println(Arrays.deepToString(pals));
									manager.setSynced(path);
									if (pals.length > 0) {
										if (!manager.isFileInTable(path)) {
											manager.createDocument(path,
													PersistenceManager.PDF,
													manager.getDocumentTitle(path));

										}
										ArrayList<String> guardar = new ArrayList<String>();
										for (String palabra : pals) {
											if (!manager.isPKInTable(palabra, path)) {
												guardar.add(palabra);
											}
										}
										String[] g = new String[guardar.size()];
										g = guardar.toArray(g);
										guardar = null;
										manager.savePalabrasClave(path, g);
										g = null;
										ArrayList<String> recomms = manager
												.recomendar(path);
										String[] recsPDF = new String[recomms
												.size()];
										for (int i = 0; i < recsPDF.length; i++) {
											recsPDF[i] = recomms.get(i);
										}
										manager.saveRecommendations(path, recsPDF);
										System.out.println(Arrays
												.deepToString(recsPDF));
										Vibrator vibrator = (Vibrator) SyncService.this
												.getSystemService(VIBRATOR_SERVICE);
										vibrator.vibrate(300);
										pals = null;
										recomms = null;
									}
									task.cancel(true);
									task2.cancel(true);
									task = null;
									task2 = null;
								}

							}
							sleep(600000);

						}
											} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}

			
		};
		t.start();
		
		

		return super.onStartCommand(intent, flags, startId);
	}
	
	private boolean connected() 
	{
		boolean connected = false;
    	ConnectivityManager cm =
    	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
    	        connected = true;
    	    }
    	    System.out.println(connected);
        return connected;
	}

}
