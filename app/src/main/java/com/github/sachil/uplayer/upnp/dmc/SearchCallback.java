package com.github.sachil.uplayer.upnp.dmc;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.contentdirectory.callback.Search;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.SortCriterion;

public class SearchCallback extends Search{
	
	public SearchCallback(Service service, String containerId,
			String searchCriteria, String filter, long firstResult,
			Long maxResults, SortCriterion[] orderBy) {
		super(service, containerId, searchCriteria, filter, firstResult, maxResults,
				orderBy);
	}

	
	@Override
	public void received(ActionInvocation arg0, DIDLContent arg1) {
	}

	@Override
	public void updateStatus(Status arg0) {
	}

	
	@Override
	public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
	}

}
