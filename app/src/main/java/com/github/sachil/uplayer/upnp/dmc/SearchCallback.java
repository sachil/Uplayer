package com.github.sachil.uplayer.upnp.dmc;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.contentdirectory.callback.Search;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.SortCriterion;

public class SearchCallback extends Search{

	@SuppressWarnings("rawtypes")
	public SearchCallback(Service service, String containerId,
			String searchCriteria, String filter, long firstResult,
			Long maxResults, SortCriterion[] orderBy) {
		super(service, containerId, searchCriteria, filter, firstResult, maxResults,
				orderBy);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void received(ActionInvocation arg0, DIDLContent arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateStatus(Status arg0) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

}
