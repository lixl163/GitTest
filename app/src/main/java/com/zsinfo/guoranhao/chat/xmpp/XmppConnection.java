package com.zsinfo.guoranhao.chat.xmpp;

import android.util.Log;

import com.zsinfo.guoranhao.chat.bean.ChatMessageBean;
import com.zsinfo.guoranhao.chat.contacts.Constants;
import com.zsinfo.guoranhao.chat.db.TalkListManager;
import com.zsinfo.guoranhao.chat.utils.ChatUtils;
import com.zsinfo.guoranhao.utils.LogUtils;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;

import java.util.Iterator;

/**
 * xmpp相关
 */
public class XmppConnection {

	public static XMPPConnection connection;
	public static TaxiConnectionListener connectionListener = new TaxiConnectionListener();//给连接设置监听器了，最好放在登录方法里，关闭连接方法里移除监听
	public static MessagePackageListener packageListener = new MessagePackageListener();

	/**
	 * 获取连接
	 * @return
	 */
	public static XMPPConnection getConnection() {
		//是否被创建
		if (connection == null) {
			//创建并连接
			openConnection();
		}
		return connection;
	}

	/**
	 * 打开连接
	 */
	private static void openConnection() {
		try {
			if (null == connection || !connection.isAuthenticated()) {
				XMPPConnection.DEBUG_ENABLED = true;
				//配置文件  参数（服务地地址，端口号，域）
				ConnectionConfiguration conConfig = new ConnectionConfiguration(Constants.XMPP_HOST, Constants.XMPP_PORT, Constants.XMPP_SERVICE_NAME);
				//设置断网重连 默认为true，表示允许自动连接
				conConfig.setReconnectionAllowed(false);
				//设置登录状态 true-为在线
				conConfig.setSendPresence(false);  //false表示状态设为离线，为了取离线消息
				//设置不需要SAS验证
				conConfig.setSASLAuthenticationEnabled(true);
				//开启连接
				connection = new XMPPConnection(conConfig);
				//设置消息监听
				connection.addPacketListener(XmppConnection.packageListener,
						new PacketTypeFilter(org.jivesoftware.smack.packet.Message.class));
				connection.connect();
				if (connection.isConnected()) {
					// 添加连接监听
					connection.addConnectionListener(connectionListener);
				}
				//添加额外配置信息
				configureConnection();
			}
		} catch (XMPPException e) {
			e.printStackTrace();
			Log.e("XMPPConnection日志-----", e.getMessage());
		}
	}

	/**
	 * 关闭连接
	 */
	public static void closeConnection() {
		if(connection != null){
			connection.removeConnectionListener(connectionListener);
			connection.removePacketListener(packageListener);
			connection.disconnect();
			connection = null;
		}
	}

	/**
	 * ASmack在/META-INF缺少一个smack.providers 文件，配置文件
	 * 不然会出现 空指针异常或者是ClassCastExceptions
	 */
	private static void configureConnection() {
		ProviderManager pm = ProviderManager.getInstance();
		pm.addIQProvider("query", "jabber:iq:private",
				new PrivateDataManager.PrivateDataIQProvider());
		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time",
					Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster",
				new RosterExchangeProvider());
		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event",
				new MessageEventProvider());
		// Chat State
		pm.addExtensionProvider("active",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
				new XHTMLExtensionProvider());
		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference",
				new GroupChatInvitation.Provider());
		// Service Discovery # Items
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
				new DiscoverItemsProvider());
		// Service Discovery # Info
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());
		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
				new MUCUserProvider());
		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
				new MUCAdminProvider());
		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
				new MUCOwnerProvider());
		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay",
				new DelayInformationProvider());
		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version",
					Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			// Not sure what's happening here.
		}
		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
				new OfflineMessageRequest.Provider());
		// Offline Message Indicator
		pm.addExtensionProvider("offline",
				"http://jabber.org/protocol/offline",
				new OfflineMessageInfo.Provider());
		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup",
				"http://www.jivesoftware.org/protocol/sharedgroup",
				new SharedGroupsInfo.Provider());
		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses",
				"http://jabber.org/protocol/address",
				new MultipleAddressesProvider());
		pm.addIQProvider("si", "http://jabber.org/protocol/si",
				new StreamInitiationProvider());
		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
				new BytestreamsProvider());
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
		pm.addIQProvider("command", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider());
		pm.addExtensionProvider("malformed-action",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.MalformedActionError());
		pm.addExtensionProvider("bad-locale",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadLocaleError());
		pm.addExtensionProvider("bad-payload",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadPayloadError());
		pm.addExtensionProvider("bad-sessionid",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadSessionIDError());
		pm.addExtensionProvider("session-expired",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.SessionExpiredError());
	}

	/**
	 * 登录(用户名，密码)
	 * 并且更改在线状态
	 */
	public static void login(String name, String pwd) throws XMPPException {
//		try {
			//①xmpp属于连接状态，使用账户密码登录
			connection.login(name, pwd);
			//②离线状态，获取离线消息
			//offlineMessage();
			//③更改在线状态
			Presence presence = new Presence(Presence.Type.available);  //更改在线状态true
			connection.sendPacket(presence);
			LogUtils.e("XmppConnection login-------------", "");
//		} catch (XMPPException e) {
//			e.printStackTrace();  //SASL authentication failed using mechanism DIGEST-MD5: (使用DIGEST-MD5机制进行SASL身份验证失败：)
//			LogUtils.e("XmppConnection login-------------", e.getMessage());
//		}catch (IllegalStateException e){
//			e.printStackTrace();
//			LogUtils.e("XMPPConnection login-----", e.getMessage());
//		}
	}

	/**
	 * 用户注册
	 * @param username
	 * @param password
	 * @return
	 */
	public static int regStatus(String username, String password){
		int regStatus = 0;
		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo(XmppConnection.connection.getServiceName());
		reg.setUsername(username);
		reg.setPassword(password);
		reg.addAttribute("android", "geolo_createUser_android");
		System.out.println("reg:" + reg);
		PacketFilter filter = new AndFilter(new PacketIDFilter(reg
				.getPacketID()), new PacketTypeFilter(IQ.class));
		PacketCollector collector = XmppConnection.connection.createPacketCollector(filter);
		XmppConnection.connection.sendPacket(reg);

		IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
		// Stop queuing results
		collector.cancel();// 停止请求results（是否成功的结果）

		if (result == null) {
			regStatus = 0;
			LogUtils.e("XmppConnection Registration-------------", "服务器没有返回结果");
		} else if (result.getType() == IQ.Type.ERROR) {
			if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
				regStatus = 409;
				LogUtils.e("XmppConnection Registration-------------", "这个账号已经存在");
			} else {
				regStatus = 400;
				LogUtils.e("XmppConnection Registration-------------", "注册失败");
			}
		} else if (result.getType() == IQ.Type.RESULT) {
			regStatus = 200;
			LogUtils.e("XmppConnection Registration-------------", "恭喜你注册成功");
		}
		return regStatus;
	}

	/**
	 * 修改我的头像
	 *
	 * @param image
	 * @return
	 * @throws XMPPException
	 */
	public static boolean updateAvater(byte[] image) throws XMPPException {
		VCard vcard = new VCard();
		vcard.load(XmppConnection.connection);
		vcard.setAvatar(image);
		vcard.save(XmppConnection.connection);
		return true;
	}

	/**
	 * 修改我的昵称
	 *
	 * @param nikName
	 * @return
	 * @throws XMPPException
	 */
	public static boolean updateNickname(String nikName) throws XMPPException {
		VCard vcard = new VCard();
		vcard.load(XmppConnection.connection);
		vcard.setNickName(nikName);
		vcard.save(XmppConnection.connection);
		return true;
	}

	/**
	 * Android实现openfire获取离线消息的基本思路：
	 在用户连接登录openfire之前，先连接一次openfire，
	 并且要配置ConnectionConfiguration，一定要设置SendPresence为false,
	 即将在线状态设置为离线，然后才能接收到离线消息，处理完离线消息之后，
	 记得要通知openfire服务器端，删除接收到的离线消息，并且设置再将状态设置为在线
	 //todo 防止消息监听 和 离线消息在一起冲突，导致消息重复的问题
	 */
	public static void offlineMessage(){
		//打开数据库
		TalkListManager talkListManager = TalkListManager.getInstance();
		OfflineMessageManager offlineManager = new OfflineMessageManager(connection);
		try {
			Iterator<Message> it = offlineManager.getMessages();
			while (it.hasNext()) {
				Message message = it.next();
				//对离线消息进行的处理
				LogUtils.e("XmppConnection OfflineMessageManager-----", message.getBody().toString());
				//开始解析成Bean对象
				ChatMessageBean chatBean = ChatUtils.getMessage(message.getBody().toString());
				//存放数据库
				talkListManager.insertMessage(true, chatBean, "");
				//关闭数据库
			}
			offlineManager.deleteMessages();//删除所有离线消息
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
