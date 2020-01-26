package com.pump.icon;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

public class AquaIcon {
	private static Map<String, Icon> iconMap = new HashMap<>();
	private static Map<String, String> descriptionMap = new HashMap<>();

	public static final Icon AliasBadge = initialize("abdg", "aliasBadge");
	public static final Icon AFPServer = initialize("afps", "AFPServer");
	public static final Icon AppleMenuFolder = initialize("amnu",
			"AppleMenuFolder");
	public static final Icon AppearanceFolder = initialize("appr",
			"AppearanceFolder");
	public static final Icon ApplicationsFolder = initialize("apps",
			"ApplicationsFolder");
	public static final Icon KeepArranged = initialize("arng", "KeepArranged");
	public static final Icon SortAscending = initialize("asnd", "SortAscending");
	public static final Icon ApplicationSupportFolder = initialize("asup",
			"ApplicationSupportFolder");
	public static final Icon AppleTalk = initialize("atlk", "AppleTalk");
	public static final Icon AppleTalkZone = initialize("atzn", "AppleTalkZone");
	public static final Icon BackwardArrow = initialize("baro", "BackwardArrow");
	public static final Icon Burning = initialize("burn", "Burning");
	public static final Icon AppleLogo = initialize("capl", "AppleLogo");
	public static final Icon AlertCaution = initialize("caut", "AlertCaution");
	public static final Icon AlertCautionBadge = initialize("cbdg",
			"AlertCautionBadge");
	public static final Icon GenericCDROM = initialize("cddr", "GenericCDROM");
	public static final Icon ClippingPictureType = initialize("clpp",
			"ClippingPictureType");
	public static final Icon ClippingSoundType = initialize("clps",
			"ClippingSoundType");
	public static final Icon ClippingTextType = initialize("clpt",
			"ClippingTextType");
	public static final Icon ClippingUnknownType = initialize("clpu",
			"ClippingUnknownType");
	public static final Icon ContextualMenuItemsFolder = initialize("cmnu",
			"ContextualMenuItemsFolder");
	public static final Icon ConnectTo = initialize("cnct", "ConnectTo");
	public static final Icon ControlPanelFolder = initialize("ctrl",
			"ControlPanelFolder");
	public static final Icon DeleteAlias = initialize("dali", "DeleteAlias");
	public static final Icon Desktop = initialize("desk", "Desktop");
	public static final Icon DocumentsFolder = initialize("docs",
			"DocumentsFolder");
	public static final Icon SortDescending = initialize("dsnd",
			"SortDescending");
	public static final Icon GenericEditionFile = initialize("edtf",
			"GenericEditionFile");
	public static final Icon EjectMedia = initialize("ejec", "EjectMedia");
	public static final Icon ExtensionsFolder = initialize("extn",
			"ExtensionsFolder");
	public static final Icon ForwardArrow = initialize("faro", "ForwardArrow");
	public static final Icon FavoriteItems = initialize("favr", "FavoriteItems");
	public static final Icon FavoritesFolder = initialize("favs",
			"FavoritesFolder");
	public static final Icon GenericFolder = initialize("fldr", "GenericFolder");
	public static final Icon GenericFloppy = initialize("flpy", "GenericFloppy");
	public static final Icon FontsFolder = initialize("font", "FontsFolder");
	public static final Icon FTPServer = initialize("ftps", "FTPServer");
	public static final Icon FullTrash = initialize("ftrh", "FullTrash");
	public static final Icon VoicesFolder = initialize("fvoc", "VoicesFolder");
	public static final Icon GenericNetwork = initialize("gnet",
			"GenericNetwork");
	public static final Icon Grid = initialize("grid", "Grid");
	public static final Icon Group = initialize("grup", "Group");
	public static final Icon GenericURL = initialize("gurl", "GenericURL");
	public static final Icon GuestUser = initialize("gusr", "GuestUser");
	public static final Icon GenericWindow = initialize("gwin", "GenericWindow");
	public static final Icon GenericHardDisk = initialize("hdsk",
			"GenericHardDisk");
	public static final Icon Help = initialize("help", "Help");
	public static final Icon HTTPServer = initialize("htps", "HTTPServer");
	public static final Icon GenericIDisk = initialize("idsk", "GenericIDisk");
	public static final Icon IPFileServer = initialize("isrv", "IPFileServer");
	public static final Icon InternetSearchSitesFolder = initialize("issf",
			"InternetSearchSitesFolder");
	public static final Icon LockedBadge = initialize("lbdg", "LockedBadge");
	public static final Icon Locked = initialize("lock", "Locked");
	public static final Icon SystemFolder = initialize("macs", "SystemFolder");
	public static final Icon Clipboard = initialize("CLIP", "Clipboard");
	public static final Icon MountedBadge = initialize("mbdg", "MountedBadge");
	public static final Icon MountedFolder = initialize("mntd", "MountedFolder");
	public static final Icon NoFiles = initialize("nfil", "NoFiles");
	public static final Icon NoFolder = initialize("nfld", "NoFolder");
	public static final Icon AlertNote = initialize("note", "AlertNote");
	public static final Icon NoWrite = initialize("nwrt", "NoWrite");
	public static final Icon OpenFolder = initialize("ofld", "OpenFolder");
	public static final Icon OwnedFolder = initialize("ownd", "OwnedFolder");
	public static final Icon ProtectedApplicationFolder = initialize("papp",
			"ProtectedApplicationFolder");
	public static final Icon PrinterDescriptionFolder = initialize("ppdf",
			"PrinterDescriptionFolder");
	public static final Icon GenericPreferences = initialize("pref",
			"GenericPreferences");
	public static final Icon PrintMonitorFolder = initialize("prnt",
			"PrintMonitorFolder");
	public static final Icon ColorSyncFolder = initialize("prof",
			"ColorSyncFolder");
	public static final Icon ProtectedSystemFolder = initialize("psys",
			"ProtectedSystemFolder");
	public static final Icon PublicFolder = initialize("pubf", "PublicFolder");
	public static final Icon QuestionMark = initialize("ques", "QuestionMark");
	public static final Icon GenericRAMDisk = initialize("ramd",
			"GenericRAMDisk");
	public static final Icon RecentApplicationsFolder = initialize("rapp",
			"RecentApplicationsFolder");
	public static final Icon RightContainerArrow = initialize("rcar",
			"RightContainerArrow");
	public static final Icon RecentItems = initialize("rcnt", "RecentItems");
	public static final Icon RecentDocumentsFolder = initialize("rdoc",
			"RecentDocumentsFolder");
	public static final Icon GenericRemovableMedia = initialize("rmov",
			"GenericRemovableMedia");
	public static final Icon Computer = initialize("root", "Computer");
	public static final Icon RecentServersFolder = initialize("rsrv",
			"RecentServersFolder");
	public static final Icon AppleMenu = initialize("sapl", "AppleMenu");
	public static final Icon SharedBadge = initialize("sbdg", "SharedBadge");
	public static final Icon AppleScriptBadge = initialize("scrp",
			"AppleScriptBadge");
	public static final Icon GenericStationery = initialize("sdoc",
			"GenericStationery");
	public static final Icon ShutdownItemsFolder = initialize("shdf",
			"ShutdownItemsFolder");
	public static final Icon SharedFolder = initialize("shfl", "SharedFolder");
	public static final Icon GenericSharedLibary = initialize("shlb",
			"GenericSharedLibary");
	public static final Icon SharingPrivsNotApplicable = initialize("shna",
			"SharingPrivsNotApplicable");
	public static final Icon SharingPrivsReadOnly = initialize("shro",
			"SharingPrivsReadOnly");
	public static final Icon Shortcut = initialize("shrt", "Shortcut");
	public static final Icon SharingPrivsReadWrite = initialize("shrw",
			"SharingPrivsReadWrite");
	public static final Icon SharingPrivsUnknown = initialize("shuk",
			"SharingPrivsUnknown");
	public static final Icon SpeakableItemsFolder = initialize("spki",
			"SpeakableItemsFolder");
	public static final Icon GenericFileServer = initialize("srvr",
			"GenericFileServer");
	public static final Icon AlertStop = initialize("stop", "AlertStop");
	public static final Icon StartupItemsFolder = initialize("strt",
			"StartupItemsFolder");
	public static final Icon Owner = initialize("susr", "Owner");
	public static final Icon ToolbarAdvanced = initialize("tbav",
			"ToolbarAdvanced");
	public static final Icon ToolbarInfo = initialize("tbin", "ToolbarInfo");
	public static final Icon ToolbarLabels = initialize("tblb", "ToolbarLabels");
	public static final Icon ToolbarCustomize = initialize("tcus",
			"ToolbarCustomize");
	public static final Icon ToolbarDelete = initialize("tdel", "ToolbarDelete");
	public static final Icon ToolbarFavorites = initialize("tfav",
			"ToolbarFavorites");
	public static final Icon ToolbarHome = initialize("thom", "ToolbarHome");
	public static final Icon Trash = initialize("trsh", "Trash");
	public static final Icon UserIDisk = initialize("udsk", "UserIDisk");
	public static final Icon UserFolder = initialize("ufld", "UserFolder");
	public static final Icon Unlocked = initialize("ulck", "Unlocked");
	public static final Icon UnknownFSObject = initialize("unfs",
			"UnknownFSObject");
	public static final Icon User = initialize("user", "User");
	public static final Icon WorkgroupFolder = initialize("wfld",
			"WorkgroupFolder");
	public static final Icon SharingPrivsWritable = initialize("writ",
			"SharingPrivsWritable");
	public static final Icon GenericControlPanel = initialize("APPC",
			"GenericControlPanel");
	public static final Icon GenericDeskAccessory = initialize("APPD",
			"GenericDeskAccessory");
	public static final Icon GenericApplication = initialize("APPL",
			"GenericApplication");
	public static final Icon Finder = initialize("FNDR", "Finder");
	public static final Icon FontSuitcase = initialize("FFIL", "FontSuitcase");
	public static final Icon GenericFont = initialize("ffil", "GenericFont");
	public static final Icon GenericControlStripModule = initialize("sdev",
			"GenericControlStripModule");
	public static final Icon GenericComponent = initialize("thng",
			"GenericComponent");
	public static final Icon GenericDocument = initialize("docu",
			"GenericDocument");
	public static final Icon GenericExtension = initialize("INIT",
			"GenericExtension");
	public static final Icon GenericFontScaler = initialize("sclr",
			"GenericFontScaler");
	public static final Icon GenericMoverObject = initialize("movr",
			"GenericMoverObject");
	public static final Icon GenericPCCard = initialize("pcmc", "GenericPCCard");
	public static final Icon GenericQueryDocument = initialize("qery",
			"GenericQueryDocument");
	public static final Icon GenericSuitcase = initialize("suit",
			"GenericSuitcase");
	public static final Icon GenericWORM = initialize("worm", "GenericWORM");
	public static final Icon InternationalResources = initialize("ifil",
			"InternationalResources");
	public static final Icon KeyboardLayout = initialize("kfil",
			"KeyboardLayout");
	public static final Icon SoundFile = initialize("sfil", "SoundFile");
	public static final Icon SystemSuitcase = initialize("zsys",
			"SystemSuitcase");
	public static final Icon TrueTypeFont = initialize("tfil", "TrueTypeFont");
	public static final Icon TrueTypeFlatFont = initialize("sfnt",
			"TrueTypeFlatFont");
	public static final Icon TrueTypeMultiFlatFont = initialize("ttcf",
			"TrueTypeMultiFlatFont");
	public static final Icon InternetLocationHTTP = initialize("ilht",
			"InternetLocationHTTP");
	public static final Icon InternetLocationFTP = initialize("ilft",
			"InternetLocationFTP");
	public static final Icon InternetLocationAppleShare = initialize("ilaf",
			"InternetLocationAppleShare");
	public static final Icon InternetLocationAppleTalkZone = initialize("ilat",
			"InternetLocationAppleTalkZone");
	public static final Icon InternetLocationFile = initialize("ilfi",
			"InternetLocationFile");
	public static final Icon InternetLocationMail = initialize("ilma",
			"InternetLocationMail");
	public static final Icon InternetLocationNews = initialize("ilnw",
			"InternetLocationNews");
	public static final Icon InternetLocationNSLNeighborhood = initialize(
			"ilns", "InternetLocationNSLNeighborhood");
	public static final Icon InternetLocationGeneric = initialize("ilge",
			"InternetLocationGeneric");
	public static final Icon DropFolder = initialize("dbox", "DropFolder");
	public static final Icon PrivateFolder = initialize("prvf", "PrivateFolder");
	public static final Icon ControlPanelDisabledFolder = initialize("ctrD",
			"ControlPanelDisabledFolder");
	public static final Icon ExtensionsDisabledFolder = initialize("extD",
			"ExtensionsDisabledFolder");
	public static final Icon ShutdownItemsDisabledFolder = initialize("shdD",
			"ShutdownItemsDisabledFolder");
	public static final Icon StartupItemsDisabledFolder = initialize("strD",
			"StartupItemsDisabledFolder");
	public static final Icon SystemExtensionDisabledFolder = initialize("macD",
			"SystemExtensionDisabledFolder");
	public static final Icon ToolbarApplicationsFolder = initialize("tAps",
			"ToolbarApplicationsFolder");
	public static final Icon ToolbarDocumentsFolder = initialize("tDoc",
			"ToolbarDocumentsFolder");
	public static final Icon ToolbarMovieFolder = initialize("tMov",
			"ToolbarMovieFolder");
	public static final Icon ToolbarMusicFolder = initialize("tMus",
			"ToolbarMusicFolder");
	public static final Icon ToolbarPicturesFolder = initialize("tPic",
			"ToolbarPicturesFolder");
	public static final Icon ToolbarPublicFolder = initialize("tPub",
			"ToolbarPublicFolder");
	public static final Icon ToolbarDesktopFolder = initialize("tDsk",
			"ToolbarDesktopFolder");
	public static final Icon ToolbarDownloadsFolder = initialize("tDwn",
			"ToolbarDownloadsFolder");
	public static final Icon ToolbarLibraryFolder = initialize("tLib",
			"ToolbarLibraryFolder");
	public static final Icon ToolbarUtilitiesFolder = initialize("tUtl",
			"ToolbarUtilitiesFolder");
	public static final Icon ToolbarSitesFolder = initialize("tSts",
			"ToolbarSitesFolder");
	public static final Icon AppleExtrasFolder = initialize("aex ",
			"AppleExtrasFolder");
	public static final Icon AssistantsFolder = initialize("ast ",
			"AssistantsFolder");
	public static final Icon ControlStripModulesFolderIcon = initialize("sdv ",
			"ControlStripModulesFolderIcon");
	public static final Icon HelpFolder = initialize("hlp ", "HelpFolder");
	public static final Icon InternetFolder = initialize("int ",
			"InternetFolder");
	public static final Icon InternetPlugInFolder = initialize("net ",
			"InternetPlugInFolder");
	public static final Icon LocalesFolder = initialize("loc", "LocalesFolder");
	public static final Icon MacOSReadMeFolder = initialize("mor ",
			"MacOSReadMeFolder");
	public static final Icon PreferencesFolder = initialize("prf ",
			"PreferencesFolder");
	public static final Icon PrinterDriverFolder = initialize("prd ",
			"PrinterDriverFolder");
	public static final Icon ScriptingAdditionsFolder = initialize("scr ",
			"ScriptingAdditionsFolder");
	public static final Icon TextEncodingsFolder = initialize("tex ",
			"TextEncodingsFolder");
	public static final Icon UsersFolder = initialize("usr ", "UsersFolder");
	public static final Icon UtilitiesFolder = initialize("uti ",
			"UtilitiesFolder");

	private static Constructor systemIconConstructor;

	private static void initialize() {
		try {
			if (systemIconConstructor == null) {
				Class j1 = com.apple.laf.AquaIcon.class;
				Class j2;
				j2 = Class.forName("com.apple.laf.AquaIcon$SystemIcon");
				systemIconConstructor = j2.getDeclaredConstructor(String.class);
				systemIconConstructor.setAccessible(true);
			}
		} catch (ClassNotFoundException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public static Collection<String> getIDs() {
		return Collections.unmodifiableSet(iconMap.keySet());
	}

	private synchronized static final Icon initialize(String selectorID,
			String description) {
		descriptionMap.put(selectorID, description);
		return get(selectorID);
	}

	public static String getDescription(String selectorID) {
		return descriptionMap.get(selectorID);
	}

	public synchronized static final Icon get(String selectorID) {
		try {
			initialize();
			Icon icon = iconMap.get(selectorID);
			if (icon == null) {
				icon = (Icon) systemIconConstructor.newInstance(selectorID);
				iconMap.put(selectorID, icon);
			}

			// if (maxConstrainingSize != null) {
			// Dimension d = new Dimension(icon.getIconWidth(),
			// icon.getIconHeight());
			// Dimension newSize = Dimension2D.scaleProportionally(d,
			// maxConstrainingSize);
			// icon = new ScaledIcon(icon, newSize.width, newSize.height);
			// }

			return icon;
		} catch (InstantiationException | IllegalAccessException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
