package com.github.sachil.uplayer.upnp.dmr;

import org.fourthline.cling.support.connectionmanager.ConnectionManagerService;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.ProtocolInfos;

/**
 * ConnectionManagerService在本程序中显得并不是特别重要，因为本程序采用的传递数据的
 * 方式为：dmc告知dmr uri地址，dmr通过http-get的方式，将uri所对应的实际数据传输至dmr，
 * 所以并不需要特别区分每一个连接。而在其它情景下，ConnectionManagerService就会显得很
 * 重要，例如当程序采用这种方式获取数据时：dmc告知dms采用http-post方式，将数据传输至dmr
 * 时，每个连接的连接标识就会变得很重要。
 * @author 20001962
 *
 */

public class ConnectionService extends ConnectionManagerService{
	
	/**
	 * 向dmr的protocolInfo中添加协议信息，目前只支持http-get的方式，暂且
	 * 只支持MP3。协议信息的格式为（详情可以查阅ConnectionManager2.5.2章节）：
	 * <protocol>’:’ <network>’:’<contentFormat>’:’<additionalInfo>。
	 */
    public ConnectionService() {
        sinkProtocolInfo.add(new ProtocolInfo("http-get:*:audio/mpeg:*"));
    	sinkProtocolInfo.add(new ProtocolInfo("http-get:*:audio/aac:*"));
    	sinkProtocolInfo.add(new ProtocolInfo("http-get:*:audio/x-wav:*"));
    }
    
    public ProtocolInfos getSinkProtocolInfo() {
    	return super.getSinkProtocolInfo();
    }

}
