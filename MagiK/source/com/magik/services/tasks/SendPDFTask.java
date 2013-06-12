package com.magik.services.tasks;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import com.magik.services.WebServiceConnection;

import android.os.AsyncTask;

/**
 * @author User
 *
 */
public class SendPDFTask extends AsyncTask<Object, Void, String> {

	private boolean finished;
	
	
	public SendPDFTask()
	{
		finished = false;
	}
	
	
	@Override
	protected String doInBackground(Object... params) {
		String resp = null;
		try 
		{
			File file = (File)params[0];
			//Comentar cuando le pasen el código de los servicios.
			String url = "http://chimpaweb.com/K/prueba.php";
			HttpPost httpPost = new HttpPost(url);
			MultipartEntity entity = new MultipartEntity();
			entity.addPart("title", new StringBody(file.getName(), Charset.forName("UTF-8")));
			FileBody body = new FileBody(file);
			entity.addPart("file",body);
			
			httpPost.setEntity(entity);			
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(httpPost);
			Header[] headers = response.getAllHeaders();
			resp = String.valueOf(response.getStatusLine().getStatusCode());
			response.getEntity().consumeContent();
			for(Header head:headers)
			{
				resp.concat(";"+head.getValue());
			}
			//TODO
			/*
			 * Descomentar cuando le pasen el código de los servicios.
			connection = WebServiceConnection.darInctancia();			
			String[] nParams = {"base64", "interes", "archivo"};
			Object[] p = new Object[3];
			byte[] data = FileUtils.readFileToByteArray(file);
			String base64 = Base64.encodeToString(data, Base64.DEFAULT);
			p[0] = base64;
			String interes = "LECTURA";
			p[1] = interes;
			String[] campos = file.getName().split(".");
			String archivo = campos[campos.length-1];
			p[2] = archivo;
			
			resp = (String) connection.accederServicio((String)params[1], nParams, p);			
			*/
			
			url = null;
			file = null;
			httpPost = null;
			entity = null;
			body = null;
			client = null;
			response = null;
			headers = null;
			/*
			nParams = null;
			p = null;
			data = null;
			base64 = null;
			interes = null;
			campos = null;
			archivo = null;
			p = null;
			*/
			System.gc();
			
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return resp;
	}
	
	@Override
	protected void onPostExecute(String result) {
		finished = true;
		super.onPostExecute(result);
	}
	 
	
	public boolean isRunning()
	{
		return this.getStatus() == AsyncTask.Status.RUNNING;
	}
	
	public boolean isPending()
	{
		return this.getStatus() == AsyncTask.Status.PENDING;
	}
	
	public boolean isFinished()
	{
		return finished;
	}
	
}
