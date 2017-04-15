package com.adobe.aemf.facilities.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aemds.guide.common.GuideContainer;
import com.adobe.aemds.guide.common.GuideNode;
import com.adobe.aemds.guide.common.GuidePanel;

public class FPGuideUtils {

	static Logger logger = LoggerFactory.getLogger(FPGuideUtils.class);
	
	public static List<GuideNode> getAllItems(GuidePanel rootPanel,List<GuideNode> allItems){
		if(rootPanel.getItems() != null && !rootPanel.getItems().isEmpty()){
			List<GuideNode> items = rootPanel.getItems();
			for (GuideNode guideNode : items) {
				if(guideNode instanceof GuidePanel){
					logger.debug("Name and title : "+guideNode.getName()+"::"+guideNode.getTitle());
					allItems.addAll(((GuidePanel)guideNode).getItems());
					getAllItems((GuidePanel)guideNode,allItems);
				}else{
					logger.debug("Name and title : "+guideNode.getName()+"::"+guideNode.getTitle());
					allItems.add(guideNode);
				}
			}
		}
		return allItems;
	}
	
	public static Map<String, String> allItemsNameTitleMap(GuidePanel rootPanel){
		List<GuideNode> allItems = new ArrayList<GuideNode>();
		Map<String, String> nameTitleMap = new HashMap<String, String>();
		List<GuideNode> list = FPGuideUtils.getAllItems(rootPanel,allItems);
		for (GuideNode guideNode : list) {
			if (!(guideNode instanceof GuideContainer)&&!(guideNode instanceof GuidePanel)) {
				nameTitleMap.put(guideNode.getName(),guideNode.getTitle());
			}
		}
		return nameTitleMap;
	}
	
}
