package com.github.sachil.uplayer.upnp.dms;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.model.types.csv.CSV;
import org.fourthline.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.SortCriterion;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

import android.util.Log;

public class ContentDirectoryService extends AbstractContentDirectoryService {

	private static final String LOG_TAG = ContentDirectoryService.class
			.getSimpleName();

	@Override
	public BrowseResult browse(String objectId, BrowseFlag browseFlag,
			String filter, long firstResult, long maxResult,
			SortCriterion[] sortCriterion) throws ContentDirectoryException {
		// TODO Auto-generated method stub
		try {
			DIDLContent content = new DIDLContent();
			ContentNode node = ContentTree.getContentNode(objectId, true);
			Log.i(LOG_TAG, "The browse object id is:" + objectId);
			if (node == null)
				return new BrowseResult("", 0, 0);
			if (node.isItem()) {
				content.addItem(node.getItem());
				Log.i(LOG_TAG, "Get the item:" + node.getItem().getTitle());
				return new BrowseResult(new DIDLParser().generate(content), 1,
						1);
			} else {
				if (browseFlag == BrowseFlag.METADATA) {
					content.addContainer(node.getContainer());
					Log.i(LOG_TAG, "Get meta data of container:"
							+ node.getContainer().getTitle());
					return new BrowseResult(new DIDLParser().generate(content),
							1, 1);
				} else {
					for (Container container : node.getContainer()
							.getContainers()) {
						content.addContainer(container);
						Log.i(LOG_TAG,
								"Get child container:" + container.getTitle());
					}
					for (Item item : node.getContainer().getItems()) {
						content.addItem(item);
						Log.i(LOG_TAG, "Get child item:" + item.getTitle());
					}
					return new BrowseResult(new DIDLParser().generate(content),
							node.getContainer().getChildCount(), node
									.getContainer().getChildCount());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(LOG_TAG, "Generate browse result failed!");
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("serial")
	@Override
	@UpnpAction(out = @UpnpOutputArgument(name = "SearchCaps"))
	public CSV<String> getSearchCapabilities() {
		// TODO Auto-generated method stub
		CSV<String> capabilities = new CSV<String>() {
		};
		capabilities.add("dc:title");
		capabilities.add("dc:creator");
		capabilities.add("upnp:class");
		capabilities.add("upnp:album");
		return capabilities;
	}

	@Override
	@UpnpAction(out = @UpnpOutputArgument(name = "SortCaps"))
	public CSV<String> getSortCapabilities() {
		// TODO Auto-generated method stub
		return super.getSortCapabilities();
	}

	@Override
	public BrowseResult search(String containerId, String searchCriteria,
			String filter, long firstResult, long maxResults,
			SortCriterion[] orderBy) throws ContentDirectoryException {
		// TODO Auto-generated method stub
		Log.e(LOG_TAG, "containerId:" + containerId + ",searchCriteria:"
				+ searchCriteria + ",filter:" + filter + ",firstResult:"
				+ firstResult + ",maxResults:" + maxResults + ",orderBy:"
				+ orderBy);
		return super.search(containerId, searchCriteria, filter, firstResult,
				maxResults, orderBy);
	}

}
