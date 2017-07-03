package com.application;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

public class TestRunner {
	public static void main(String[] args) {

		HttpClient httpclient = HttpClients.createDefault();
		Date startTime;
		Date endTime;
	    try {
	    	
	      	
	      HttpGet httpGetRequest = new HttpGet("http://surya-interview.appspot.com/message");
	      httpGetRequest.addHeader("X-Surya-Email-Id","suhas.hns@gmail.com");
	      startTime = new Date();
	      HttpResponse httpResponse = httpclient.execute(httpGetRequest);
	      endTime = new Date();
	      
	      HttpEntity entity = httpResponse.getEntity();
	      if(httpResponse.getStatusLine().toString().contains("200"))
	      {
	    	  System.out.println(getTimeDifference(startTime, endTime));
	      }
	      

	     byte[] buffer = new byte[1024];
	      if (entity != null) {
	        InputStream inputStream = entity.getContent();
	        try {
	          int bytesRead = 0;
	          BufferedInputStream bis = new BufferedInputStream(inputStream);
	          while ((bytesRead = bis.read(buffer)) != -1) {
	            String chunk = new String(buffer, 0, bytesRead);
	            System.out.println(chunk);
	          }
	        } catch (Exception e) {
	          e.printStackTrace();
	        } finally {
	          try { inputStream.close(); } catch (Exception ignore) {}
	        }
	      }
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    } 
	  }

	private static Long getTimeDifference(Date startTime, Date endTime) {
		// TODO Auto-generated method stub
		return  endTime.getTime() - startTime.getTime();
	}
}
