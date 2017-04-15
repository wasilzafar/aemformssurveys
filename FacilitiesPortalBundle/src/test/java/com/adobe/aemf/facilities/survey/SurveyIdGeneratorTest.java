package com.adobe.aemf.facilities.survey;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Ignore;
import org.junit.Test;

public class SurveyIdGeneratorTest {

	@Ignore
	@Test
	public void test() {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
		  
		  List<Future<String>> resultList = new ArrayList<Future<String>>();
			SurveyIdGenerator sidg = SurveyIdGenerator.getInstance();
		  
		  for (int i=0; i<1000; i++)
		  {

		      CallableThread calculator  = new CallableThread(sidg);
		      Future<String> result = executor.submit(calculator);
		      resultList.add(result);
		  }
		  Set<String> set = new HashSet<String>();
		  for(Future<String> future : resultList)
		  {
	            try 
	            {
	            	set.add(future.get());
	            } 
	            catch (Exception e) 
	            {
	                e.printStackTrace();
	            }
	        }
	        //shut down the executor service now
	        executor.shutdown();
	        System.out.println("Last count	: "+sidg.getPreviousCount());
		
		assertTrue(true);
	}

}
class CallableThread implements Callable<String>{
	AtomicInteger count = new AtomicInteger(0);
	SurveyIdGenerator sidg;
		public CallableThread(SurveyIdGenerator sidg) {
			this.sidg = sidg;
			Thread.currentThread().setName("Thread - "+count.incrementAndGet());
		}
		@Override
		public String call() throws Exception {
			String id = sidg.createID().intern();
			System.out.println("ID      : " +id);
			return id;
		}	
}