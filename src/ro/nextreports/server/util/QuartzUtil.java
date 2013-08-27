/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ro.nextreports.server.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

/**
 * @author Decebal Suiu
 */
public class QuartzUtil {

	public static List<JobDetail> getAllJobDetails(Scheduler scheduler) throws SchedulerException {
		List<JobDetail> jobDetails = new ArrayList<JobDetail>();
		
		String[] groupNames = scheduler.getJobGroupNames();
		for (String jobGroup : groupNames) {
			String[] names = scheduler.getJobNames(jobGroup);
			for (String jobName : names) {
				jobDetails.add(scheduler.getJobDetail(jobName, jobGroup));
			}
		}
		
		return jobDetails;
	}

	public static List<Trigger> getAllTriggers(Scheduler scheduler) throws SchedulerException {
		List<Trigger> triggers = new ArrayList<Trigger>();
		
		String[] groupNames = scheduler.getTriggerGroupNames();
		for (String triggerGroup : groupNames) {
			String[] names = scheduler.getTriggerNames(triggerGroup);
			for (String triggerName : names) {
				triggers.add(scheduler.getTrigger(triggerName, triggerGroup));
			}
		}
		
		return triggers;
	}
    
	@SuppressWarnings("unchecked")
	public static List<String> getRunningJobNames(Scheduler scheduler) throws SchedulerException {
		List<JobExecutionContext> jobs = scheduler.getCurrentlyExecutingJobs();

		List<String> runningJobs = new ArrayList<String>();
		for (JobExecutionContext job : jobs) {
			runningJobs.add(job.getJobDetail().getName());
		}

		return runningJobs;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, JobExecutionContext> getRunningJobs(Scheduler scheduler) throws SchedulerException {
		Map<String, JobExecutionContext> runningJobs = new HashMap<String, JobExecutionContext>();
		List<JobExecutionContext> jobs = scheduler.getCurrentlyExecutingJobs();
		for (JobExecutionContext context : jobs) {
			runningJobs.put(context.getJobDetail().getName(), context);
		}
		
		return runningJobs;		
	}

}
