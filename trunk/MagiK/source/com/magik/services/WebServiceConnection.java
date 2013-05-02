package com.magik.services;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Manages the SQLite database in the phone.
 * @author Kelvin Guerrero
 * 
 */

public class WebServiceConnection
{
	// -----------------------------------------------------------------------------------------------
		// CONSTANTES
		// -----------------------------------------------------------------------------------------------

		/**
		 * La URL donde está corriendo el servicio.
		 */
		//TODO Cambiar el namespace del servidor.
		private static final String NAME_SPACE = "http://services.scs.com/";

		/**
		 * La URL donde se encuentra el archivo WSDL
		 */
		//TODO Cambiar la url del WSDL.
		private static final String URL = "http://157.253.236.190:8080/SeguColServices/SCService?wsdl";

		/**
		 * Instancia de la clase para se usada en la aplicación.
		 */
		private static WebServiceConnection instancia;

		/*
		 * MÉTODOS DEL SERVICIO WEB Las siguientes constantes son los nombres de los
		 * método que se encuentran desplegados para consumir en el servicio web.
		 */
		public static final String PALABRAS_URL = "getPalabrasClaveUrl";
		
		public static final String PALABRAS_FILE = "getPalabrasClaveArchivo";

		public static final String RECOMEND_URL = "getRecomendacionesUrl";
		
		public static final String RECOMEND_FILE = "getRecomendacionesArchivo";

		/*
		 * RESPUESTAS Las siguientes cadenas representan las cadenas que responde el
		 * servidor.
		 */

		public static final String REGISTRO_EXITOSO = "El incidente fue agregado correctamente.";

		public static final String DAR_INC_ERROR = "Error al solicitar los incidentes.";

		/**
		 * Inicializa la clase
		 */
		public WebServiceConnection() {
		}

		/**
		 * Retorna la instancia de la clase.
		 * 
		 * @return
		 */
		public static WebServiceConnection darInctancia() {
			return instancia = instancia == null ? new WebServiceConnection()
					: instancia;
		}

		/**
		 * Accede al servicio web a través del método que llega por parámetro.
		 * 
		 * @param metodo
		 *            Nombre del método llamado por el usuario
		 * @param nombresParams
		 *            Nombres de los parámetros que el usuario ingresa
		 * @param params
		 *            Valor de los parámetros.
		 * @return Un objeto que retorna el servidor, puede cambiar dependiendo del
		 *         método que se llame.
		 * @throws Exception
		 *             Si hay un error de conexión con el servicio.
		 */
		public Object accederServicio(String metodo, String[] nombresParams,
				Object[] params) throws Exception {
			Object object;
			String sAction = NAME_SPACE + metodo;
			SoapObject soap = new SoapObject(NAME_SPACE, metodo);
			for (int i = 0; i < nombresParams.length; i++) {
				soap.addProperty(nombresParams[i], params[i]);
			}

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.setOutputSoapObject(soap);
			HttpTransportSE http = new HttpTransportSE(URL);
			try {
				http.call(sAction, envelope);
				SoapPrimitive results = (SoapPrimitive) envelope.getResponse();
				object = processResponse(results, metodo);
			} catch (IOException e) {
				throw new Exception("Conexion Error: " + e.getLocalizedMessage());
			} catch (XmlPullParserException e) {
				throw new Exception("Parsing Error: " + e.getLocalizedMessage());
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("Error: " + e.getLocalizedMessage());

			}
			System.gc();
			return object;
		}

		/**
		 * Procesa la respuesta del servidor dependiendo del método que se llame.
		 * 
		 * @param body
		 *            El objeto de respuesta
		 * @param methodName
		 *            El nombre del método llamado por el usuario.
		 * @return Un objeto que retorna el servidor, puede cambiar dependiendo del
		 *         método que se llame.
		 */
		private Object processResponse(SoapPrimitive body, String methodName) {

			Object result = null;
			if (methodName.equals(PALABRAS_URL)) {
				result = body.toString();
			}
			else if (methodName.equals(PALABRAS_FILE)) {
				result = body.toString();
			}
			else if (methodName.equals(RECOMEND_URL)) {
				result = body.toString();
			}
			if (methodName.equals(RECOMEND_FILE)) {
				result = body.toString();
			}			
			System.gc();
			return result;
		}
}
