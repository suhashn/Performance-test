package com.application;

import static com.jayway.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.hamcrest.CoreMatchers;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

public class ConcurrentTestRunner {
	Date startTime;
	Date endTime;
	List<Long> latencies = Collections.synchronizedList(new ArrayList<>());
	public static Response response;
	public static String jsonString = null;

	@BeforeClass
	public static void setURL() {
		RestAssured.baseURI = "http://surya-interview.appspot.com";
	}

	@BeforeTest
	public void initializeStartTime() throws InterruptedException {
		startTime = new Date();
	}

	// Invokes rest service 100 times with 10 concurrent hits.
	@Test(threadPoolSize = 10, invocationCount = 100, timeOut = 10000)
	public final void runConcurrentRequest() throws InterruptedException {
		response = given().header("X-Surya-Email-Id", "suhas.hns@gmail.com").when().get("/message").then()
				.contentType(ContentType.JSON).extract().response();
		// gets the json string from "get" and passing the same to hit "post".
		jsonString = response.asString();
		latencies.add(getTimeDifference(startTime));
		// validate if the response body contains success or not
		given().contentType("application/json").body(jsonString).when().post("/message").then().statusCode(200)
				.body(CoreMatchers.containsString("Success"));
	}

	@AfterTest
	public void getPerformance() throws InterruptedException {
		// sort the list
		Collections.sort(latencies);
		calculatePerformance(latencies);
	}

	public void calculatePerformance(List<Long> latencies) throws InterruptedException {
		System.out.println("10th Percentile " + getPercentile(latencies, 90));
		System.out.println("50th Percentile " + getPercentile(latencies, 50));
		System.out.println("90th Percentile " + getPercentile(latencies, 10));
		System.out.println("95th Percentile " + getPercentile(latencies, 5));
		System.out.println("99th Percentile " + getPercentile(latencies, 1));

		DescriptiveStatistics stats = new DescriptiveStatistics();

		for (int i = 0; i < latencies.size(); i++) {
			stats.addValue(latencies.get(i));
		}

		double mean = stats.getMean();
		double standardDeviation = stats.getStandardDeviation();
		System.out.println("Mean " + mean);
		System.out.println("Standard Deviation " + standardDeviation);
	}

	// Calculate and return the percentile
	public static long getPercentile(List<Long> latencies, double Percentile) {
		int index = (int) Math
				.ceil(latencies.size() - ((double) Percentile / (double) 100) * (double) latencies.size());
		return latencies.get(index - 1);
	}

	// Calculate and returns response time
	private Long getTimeDifference(Date startTime) {
		endTime = new Date();
		return endTime.getTime() - startTime.getTime();
	}
}