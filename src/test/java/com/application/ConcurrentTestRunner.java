package com.application;

import static com.jayway.restassured.RestAssured.given;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class ConcurrentTestRunner {
	Date startTime;
	Date endTime;
	@SuppressWarnings("unchecked")
	List<Long> getLatencies = Collections.synchronizedList(new ArrayList());
	@SuppressWarnings("unchecked")
	List<Long> postLatencies = Collections.synchronizedList(new ArrayList());


	@Test
	public final void runConcurrentGetRequest() throws InterruptedException {
		ExecutorService exec = Executors.newFixedThreadPool(10);
		System.out.println("Rest GET Details :");
		for (int i = 0; i < 10; i++) {
			exec.execute(new Runnable() {
				@Override
				public void run() {
					startTime = new Date();
					TestRestGetApi_ReturnsStatusOk();
					getLatencies.add(getTimeDifference(startTime));
				}
			});
		}
		exec.shutdown();
		exec.awaitTermination(50, TimeUnit.SECONDS);
		calculatePercentile(getLatencies);
	}

	private void TestRestGetApi_ReturnsStatusOk() {
		given().header("X-Surya-Email-Id", "suhas.hns@gmail.com").when()
				.get("http://surya-interview.appspot.com/message").then().statusCode(200)
				.body(CoreMatchers.containsString("suhas.hns@gmail.com"));
	}

	@Test
	public final void runConcurrentPostRequest() throws InterruptedException {
		ExecutorService exec = Executors.newFixedThreadPool(10);
		System.out.println("Rest POST Details :");

		for (int i = 0; i < 10; i++) {
			exec.execute(new Runnable() {

				@Override
				public void run() {
					startTime = new Date();
					TestRestPostApi_ReturnsStatusOk();
				    postLatencies.add(getTimeDifference(startTime));
				}
			});
		}
		exec.shutdown();
		exec.awaitTermination(50, TimeUnit.SECONDS);
		calculatePercentile(postLatencies);
	}

	public final void calculatePercentile(List<Long> latencies) throws InterruptedException {
		System.out.println("10th Percentile "+getPercentile(latencies, 10));
		System.out.println("50th Percentile "+getPercentile(latencies, 50));
		System.out.println("90th Percentile "+getPercentile(latencies, 90));
		System.out.println("95th Percentile "+getPercentile(latencies, 95));
		System.out.println("99th Percentile "+getPercentile(latencies, 99));
		
		DescriptiveStatistics stats = new DescriptiveStatistics();

	    for (int i = 0; i < latencies.size(); i++) {
	        stats.addValue(latencies.get(i));
	    }

	    // Compute mean and standard deviation
	    double mean = stats.getMean();
	    double standardDeviation = stats.getStandardDeviation();
	    System.out.println("List "+latencies);
	    System.out.println("mean "+mean);
	    System.out.println("standardDeviation "+standardDeviation);
	}
	
	private void TestRestPostApi_ReturnsStatusOk() {
		Map<String, String> request = new HashMap<>();
		request.put("emailId", "suhas.hns@gmail.com");
		request.put("uuid", "53d30386-a97c-4ac8-9b3f-bfb2eca4ddd0");

		given().contentType("application/json").body(request).when().post("http://surya-interview.appspot.com/message")
				.then().statusCode(200).body(CoreMatchers.containsString("Success"));
	}
	
	public static long getPercentile(List<Long> latencies, double Percentile)
    {
        int index = (int)Math.ceil(((double)Percentile / (double)100) * (double)latencies.size());
		return  latencies.get(index-1);
    }
	
	private Long getTimeDifference(Date startTime) {
		endTime = new Date();
		return endTime.getTime()- startTime.getTime();
	}
}