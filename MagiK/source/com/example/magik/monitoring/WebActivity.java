package com.example.magik.monitoring;

import java.util.ArrayList;

import net.sf.andpdf.pdfviewer.R;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.analisys.words.PalabrasClave;
import com.analisys.words.Words;
import com.contolers.magik.data.ControlerData;
import com.contolers.magik.web.WebControlLogStatic;
import com.data.bd.PersistenceManager;
import com.recomendacion.servicio.Servicio;
import com.recomendacion.servicio.WebServiceConnection;

public class WebActivity extends Activity implements OnTouchListener,
		Handler.Callback {
	private WebView objWebView;
	private Button btnAgregar;
	private Button btnPk;
	private Button btnIr;
	private Button btnLenguaje;
	private EditText txtUrl;
	private final Handler handler = new Handler(this);
	private Thread thread;
	private Thread t;
	private String rta = "";
	private boolean sensorProcessGuardar;
	private boolean sensorProcess;
	private String CLASE_DYSPLAY = "MURL";
	private ControlerData data;
	private ControlerDisplay display;
	private PersistenceManager persistenceManager;
	private ArrayList<String> lecturas;
	private WebViewClient client;
	private static final int CLICK_ON_WEBVIEW = 1;
	private static final int CLICK_ON_URL = 2;
	private ArrayList<String> lenguajes;
	private boolean cargoplabras;
	private boolean cargoClaves;
	private boolean cargolenguaje;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cargoClaves = false;
		cargolenguaje = false;
		setContentView(R.layout.activity_web);
		data = new ControlerData();
		data.existDelete(CLASE_DYSPLAY);
		data.crearFile(CLASE_DYSPLAY, "Velocity (X,Y) ;Titulo;Url");
		lecturas = new ArrayList<String>();
		display = new ControlerDisplay();
		objWebView = (WebView) findViewById(R.id.webView);
		objWebView.setOnTouchListener(this);
		client = new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				Toast.makeText(getApplicationContext(),
						"palabras" + txtUrl.getText().toString(),
						Toast.LENGTH_SHORT).show();
				System.out.println(txtUrl.getText().toString());
				t = new Thread() {
					@Override
					public void run() {
						while (sensorProcess) {
							try {
								Log.v("KELVIN", "lecturas " + lecturas.size());
								if (lecturas.size() > 5) {
									// Toast.makeText( getApplicationContext( ),
									// "Analizando pagina" + objWebView.getUrl(
									// ), Toast.LENGTH_SHORT ).show( );
									ArrayList<String> pals = new ArrayList<String>();
									if (!connected()) {
										WebServiceConnection connection = WebServiceConnection
												.darInctancia();
										String[] nombresParams = { "interes",
												"archivo" };
										Object[] params = new Object[2];
										params[0] = objWebView.getUrl();
										params[1] = "LECTURA";
										String string = (String) connection
												.accederServicio(
														WebServiceConnection.PALABRAS_URL,
														nombresParams, params);
										for (String s : string.split(";")) {
											pals.add(s);
										}
										string = null;
										connection = null;
										nombresParams = null;
										params = null;

									} else {
										if (!cargoplabras) {
											Words w = new Words();
											boolean exito = w.startWords(objWebView.getUrl());
											if (exito) {
												if (!cargolenguaje) {
													lenguajes = w.getLenguaje();
													if (lenguajes != null
															&& lenguajes.size() > 0) {
														// // Toast.makeText(
														// getApplicationContext(
														// ), "LenguajeCargado",
														// Toast.LENGTH_SHORT
														// ).show( );
														cargolenguaje = true;
														sensorProcess = false;
													}
												}
												PalabrasClave palabras = PalabrasClave
														.darInstacia();
												pals = palabras.getPalabras();
												cargoplabras = true;
												w = null;
											}
											System.gc();
										}
									}
									if(!pals.isEmpty())
									{
										recomendaciones(pals);
									}
								}

								sleep(30000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				};
				t.start();
				handler.sendEmptyMessage(CLICK_ON_URL);
				return false;
			}
		};
		objWebView.setWebViewClient(client);
		objWebView.getSettings().setJavaScriptEnabled(true);

		txtUrl = (EditText) findViewById(R.id.txtRuta);
		txtUrl.setText("http://m.eltiempo.com");

		sensorProcess = true;
		thread = new Thread() {
			@Override
			public void run() {
				while (sensorProcessGuardar) {
					try {
						Log.v("KELVIN", "Guardo");
						guardar();
						sleep(120000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();

		inicializarBoton();
		inicializarBotonCargar();
		inicializarBotonLenguaje();
		inicializarBotonPk();
	}

	private boolean connected() {
		boolean connected = false;
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
		connected = activeNetworkInfo != null
				&& activeNetworkInfo.isConnected();
		return connected;
	}

	protected void recomendaciones(ArrayList<String> palabras) {
		try {
			String[] pals = new String[palabras.size()];
			for (int i = 0; i < pals.length; i++) {
				pals[i] = palabras.get(i);
			}
			persistenceManager = new PersistenceManager(getApplicationContext());
			if(!persistenceManager.isFileInTable(objWebView.getUrl()))
			{
				persistenceManager.createDocument(objWebView.getUrl(), PersistenceManager.HTML, objWebView.getTitle());
			}
			
			persistenceManager.savePalabrasClave(objWebView.getUrl(), pals);
			ArrayList<String> recomms = persistenceManager.recomendar(objWebView.getUrl());
			String[] recs = new String[recomms.size()];
			for (int i = 0; i < recs.length; i++) {
				recs[i] = recomms.get(i);
			}
			persistenceManager.saveRecommendations(objWebView.getUrl(), recs);
			pals = null;
			recomms = null;
			recs = null;		
			persistenceManager = null;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		System.gc();
	}

	public void actualizarTextoPk(ArrayList<String> texto) {
		TextView text = (TextView) findViewById(R.id.TextViewPk);
		if (texto != null && texto.size() > 0) {
			for (int i = 0; i < texto.size(); i++) {
				text.clearComposingText();
				text.setText(texto.get(i));
			}
		} else {
			text.clearComposingText();
			text.setText("No se tiene cargado las Pk");
		}
	}

	public void actualizarTextoLecturas(ArrayList<String> texto) {
		TextView text = (TextView) findViewById(R.id.TextViewL);
		if (texto != null) {
			text.clearComposingText();
			for (int i = 0; i < texto.size(); i++) {
				text.setText(texto.get(i));
			}
		} else {
			text.clearComposingText();
			text.setText("No se tiene cargado el idioma");
		}
	}

	public void guardar() {
		// data.crearFile( CLASE_DYSPLAY, "TIME;Velocity" );
		data.writeToFile(rta, true, CLASE_DYSPLAY);
		data.writeToFile(rta, true, CLASE_DYSPLAY);
		rta = "";
		lecturas = new ArrayList<String>();
		sensorProcessGuardar = false;
	}

	private void inicializarBoton() {
		btnIr = (Button) findViewById(R.id.btnIr);
		btnIr.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				cargolenguaje = false;
				cargoplabras = false;
				sensorProcess = true;
				sensorProcessGuardar = true;
				lecturas = new ArrayList<String>();
				System.gc();
				cargarRuta();
			}
		});
	}

	private void inicializarBotonCargar() {
		btnAgregar = (Button) findViewById(R.id.button1);
		btnAgregar.setEnabled(false);
		// btnAgregar.setOnClickListener( new View.OnClickListener( )
		// {
		//
		// public void onClick( View v )
		// {
		// Words w = new Words( objWebView.getUrl( ) );
		// }
		// }
		// );
	}

	private void inicializarBotonLenguaje() {
		btnLenguaje = (Button) findViewById(R.id.btncargarlenguaje);
		btnLenguaje.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				actualizarTextoLecturas(lenguajes);
			}
		});
	}

	private void inicializarBotonPk() {
		btnPk = (Button) findViewById(R.id.ButtonPk);
		btnPk.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				PalabrasClave palabrasClave = PalabrasClave.darInstacia();
				actualizarTextoPk(palabrasClave.getPalabras());
			}
		});
	}

	private void cargarRuta() {
		String vRuta = txtUrl.getText().toString();
		objWebView.loadUrl(vRuta);
	}

	/**
	 * Muestra la actividad que permite ver los productoscreados
	 */
	public boolean onTouchEvent(MotionEvent event) {

		// String captura = "";
		// captura = display.velocityTrack( event ) + objWebView.getUrl( );;
		// if( captura != "" )
		// {
		// rta += "\n";
		// rta += captura;
		// }
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == R.id.webView
				&& event.getAction() == MotionEvent.ACTION_DOWN) {
			handler.sendEmptyMessageDelayed(CLICK_ON_WEBVIEW, 500);
		}

		String captura = "";
		captura = display.velocityTrack(event);
		float valorx = 0;
		float valory = 0;
		if (captura != "" && captura != null) {
			String partexy[] = captura.split(":");
			String valores[] = partexy[1].split(";");
			valorx = Float.parseFloat(valores[0]);
			valory = Float.parseFloat(valores[1]);
		}
		int a = (int) valorx;
		int b = (int) valory;

		String rtaX = display.analizarVelocidadCorto(a, "X");
		String tipolecturax = display.darTipoLectura();

		String rtaY = display.analizarVelocidadCorto(b, "Y");
		String tipolecturay = display.darTipoLectura();

		String tipoLectorua = "";

		if (Math.abs(a) > Math.abs(b)) {
			tipoLectorua = tipolecturax;
		} else {
			tipoLectorua = tipolecturay;
		}

		if (captura != "") {
			rta += "\n";
			rta += objWebView.getTitle() + ";" + captura + ";" + rtaX + ";"
					+ rtaY + ";" + objWebView.getUrl();
			if (tipoLectorua.contains("LECTURA")) {
				lecturas.add(rta);
			}

			String textoFrontal = tipoLectorua + ":(" + a + "," + b + ")";
			TextView text = (TextView) findViewById(R.id.textView1);
			String anterior = (String) text.getText();
			if (anterior.length() > 500) {
				anterior = "";
			}
			String cambio = textoFrontal + "\n" + anterior;
			text.setText(cambio);

		}
		return false;
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == CLICK_ON_URL) {
			lecturas = new ArrayList<String>();
			cargolenguaje = false;
			cargoplabras = false;
			sensorProcess = true;
			sensorProcessGuardar = true;
			lenguajes = null;
			Toast.makeText(this, "WebView url" + msg, Toast.LENGTH_SHORT)
					.show();
			handler.removeMessages(CLICK_ON_WEBVIEW);
			return true;
		}
		if (msg.what == CLICK_ON_WEBVIEW) {
			// Toast.makeText( this, "WebView clicked" + msg, Toast.LENGTH_SHORT
			// ).show( );
			return true;
		}
		return false;
	}
}
