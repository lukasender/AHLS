package at.ahls.analyzer;

import java.util.List;

import at.ahls.controller.MainController;
import at.ahls.controller.usecase.ActivityLogController;
import at.ahls.web.rest.api.jaxb.ActivitiesDto;
import at.ahls.web.rest.api.jaxb.ActivityDto;
import at.ahls.web.rest.api.jaxb.LightDataDto;

public class DummyClassOfAnalyzer {
	
	public DummyClassOfAnalyzer() {
		
	}
	
	public LightDataDto getLightData(){
		ActivityLogController controller = MainController.getInstance().getActivityLogController();
		ActivitiesDto activities = controller.getActivitiesTimeDiff(5000);
		LightDataDto retVal = new LightDataDto();
		
		int bri = 0;
		int ct = 0;
		boolean on = false;
		long transitiontime = 100;
		
		List<ActivityDto> act = activities.getActivity();
		int diff = 0;
		for(int i = 1; i < act.size(); i++){
			int tmp = Integer.parseInt(act.get(i - 1).getData()) - Integer.parseInt(act.get(i).getData());
			if(tmp > 0){
				diff += tmp;
			}
			else{
				diff += ((-1) * tmp);
			}
		}
		
		retVal.setBri(500);
		retVal.setCt(500);
		retVal.setOn(true);
		retVal.setTransitiontime((long)100);
		return retVal;
	}
	
	public int getDiff(){
		ActivityLogController controller = MainController.getInstance().getActivityLogController();
		ActivitiesDto activities = controller.getActivities(1000);
		int min = 1024;
		int max = 0;
		for(ActivityDto act : activities.getActivity()){
			//Get the minimum and maximum of all Data
			int currentData = Integer.parseInt(act.getData());
			if(min > currentData){
				min = currentData;
			}
			else if(max < currentData){
				max = currentData;
			}
		}
		return (max - min);
		//153 bis 500
	}
	
	public boolean isActive(){
		ActivityLogController controller = MainController.getInstance().getActivityLogController();
		ActivitiesDto activities = controller.getActivities(1000);
		int min = 1024;
		int max = 0;
		for(ActivityDto act : activities.getActivity()){
			//Get the minimum and maximum of all Data
			int currentData = Integer.parseInt(act.getData());
			if(min > currentData){
				min = currentData;
			}
			else if(max < currentData){
				max = currentData;
			}
		}
		return (max - min) > 200;
	}
}