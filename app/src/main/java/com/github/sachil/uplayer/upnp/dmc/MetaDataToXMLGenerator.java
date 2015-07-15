package com.github.sachil.uplayer.upnp.dmc;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.item.MusicTrack;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class MetaDataToXMLGenerator {

	@SuppressWarnings("unused")
	private static final String LOG_TAG = MetaDataToXMLGenerator.class.getSimpleName();
	private static final String NAMESPACE_DIDL_LITE = "urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/";
	private static final String NAMESPACE_UPNP = "urn:schemas-upnp-org:metadata-1-0/upnp/";
	private static final String NAMESPACE_DC = "http://purl.org/dc/elements/1.1/";
	private static final String NAMESPACE_DLNA = "urn:schemas-dlna-org:metadata-1-0/";
	private static final String NAMESPACE_SEC = "http://www.sec.co.kr/";

	public static String metadataToXml(Item item) {
		StringWriter writer = new StringWriter();
		String result = null;
		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(writer);
			serializer.setPrefix("DIDL-Lite", NAMESPACE_DIDL_LITE);
			serializer.setPrefix("upnp", NAMESPACE_UPNP);
			serializer.setPrefix("dc", NAMESPACE_DC);
			serializer.setPrefix("dlna", NAMESPACE_DLNA);
			serializer.setPrefix("sec", NAMESPACE_SEC);
			serializer.startTag(null, "DIDL-Lite");

			serializer.startTag(null, "item");
			serializer.attribute(null, "id", item.getId());
			serializer.attribute(null, "parentID", item.getParentID());
			if (item.isRestricted())
				serializer.attribute("", "restricted", "1");
			else
				serializer.attribute("", "restricted", "0");
			serializer.startTag(NAMESPACE_UPNP, "class");
			serializer.text(item.getClazz().getValue());
			serializer.endTag(NAMESPACE_UPNP, "class");
			serializer.startTag(NAMESPACE_DC, "title");
			serializer.text(item.getTitle());
			serializer.endTag(NAMESPACE_DC, "title");
			serializer.startTag(NAMESPACE_DC, "creator");
			serializer.text(item.getCreator());
			serializer.endTag(NAMESPACE_DC, "creator");
			serializer.startTag(NAMESPACE_UPNP, "artist");
			serializer.text(item.getCreator());
			serializer.endTag(NAMESPACE_UPNP, "artist");

			if (item instanceof MusicTrack) {
				MusicTrack musicTrack = (MusicTrack) item;
				serializer.startTag(NAMESPACE_DC, "date");
				serializer.text(musicTrack.getDate());
				serializer.endTag(NAMESPACE_DC, "date");
				serializer.startTag(NAMESPACE_UPNP, "album");
				serializer.text(musicTrack.getAlbum());
				serializer.endTag(NAMESPACE_UPNP, "album");
				String albumArtUri = null;
				if (item.getFirstProperty(DIDLObject.Property.UPNP.ALBUM_ART_URI.class) != null) {
					albumArtUri = ((URI) item
							.getFirstPropertyValue(DIDLObject.Property.UPNP.ALBUM_ART_URI.class))
							.toString();
					serializer.startTag(NAMESPACE_UPNP, "albumArtURI");
					serializer.text(albumArtUri);
					serializer.endTag(NAMESPACE_UPNP, "albumArtURI");
				}
			}
			serializer.startTag(null, "res");
			serializer.attribute(null, "protocolInfo",
					item.getResources().get(0).getProtocolInfo().toString());
			serializer.attribute(null, "duration", item.getResources().get(0)
					.getDuration());
			serializer.attribute(null, "size", item.getResources().get(0)
					.getSize().toString());
			serializer.text(item.getResources().get(0).getValue());
			serializer.endTag(null, "res");
			serializer.endTag(null, "item");
			serializer.endTag(null, "DIDL-Lite");
			serializer.endDocument();
			result = writer.toString();

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
