/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.icon;

import java.awt.Dimension;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.accessibility.AccessibleIcon;
import javax.swing.Icon;

import com.pump.awt.Dimension2D;

/**
 * This class uses reflection to access <code>com.apple.laf.AquaIcons</code> on
 * Macs. If you try to use this class on another platform (or in a radically
 * different JVM that discontinued support for
 * <code>com.apple.laf.AquaIcons</code>): then calling one of the
 * <code>get</code> methods will throw an exception.
 * <p>
 * I'm not an expert on Mac's icons, but my understanding is every icon has a
 * unique 4-letter identifier. (Like "fldr" for "folder".) This class also
 * includes a human-readable name as an optional "description" attribute.
 * <p>
 * Most (but not all) of these icons are scalable to very large (and small)
 * dimensions. However requesting any icon at any size appears to incur a
 * significant one-time performance cost.
 * <p>
 * This class includes a set of predefined icons. (I forget the origins of this
 * list.) Apple appears to unofficially "support" deprecated IDs by redirecting
 * those IDs to generic icons. For example "shdf" appears to refer to
 * "shutdown items folder", which was introduced 1991 in system 7.5. I tried to
 * comment out several of these obsolete icons and only include relevant/modern
 * icons.
 * 
 * @see com.pump.image.NSImage
 */
public class AquaIcon {
	// TODO: we could localize these descriptions

	private static Map<String, Icon> iconMap = new LinkedHashMap<>();
	private static Map<String, String> descriptionMap = new HashMap<>();

	// The following constants are loosely organized into what I thought would
	// be a presentable grouping in the AquaIconDemo. There is not strict
	// rule/logic behind this order, other than "I thought it looked good"

	// I commented out many icons that resolve to the "FNDR" or "docu" icons.

	// folders #1:

	public static final Icon GENERIC_FOLDER = initialize("fldr",
			"generic folder");
	public static final Icon OPEN_FOLDER = initialize("ofld", "open folder");

	// toolbar #1:

	public static final Icon TOOLBAR_APPLICATIONS_FOLDER = initialize("tAps",
			"toolbar applications folder");
	public static final Icon TOOLBAR_DESKTOP_FOLDER = initialize("tDsk",
			"toolbar desktop folder");
	public static final Icon TOOLBAR_DOCUMENTS_FOLDER = initialize("tDoc",
			"toolbar documents folder");
	public static final Icon TOOLBAR_DOWNLOADS_FOLDER = initialize("tDwn",
			"toolbar downloads folder");
	public static final Icon TOOLBAR_LIBRARY_FOLDER = initialize("tLib",
			"toolbar library folder");
	public static final Icon TOOLBAR_MOVIE_FOLDER = initialize("tMov",
			"toolbar movie folder");
	public static final Icon TOOLBAR_MUSIC_FOLDER = initialize("tMus",
			"toolbar music folder");
	public static final Icon TOOLBAR_PICTURES_FOLDER = initialize("tPic",
			"toolbar pictures folder");
	public static final Icon TOOLBAR_PUBLIC_FOLDER = initialize("tPub",
			"toolbar public folder");
	public static final Icon TOOLBAR_SITES_FOLDER = initialize("tSts",
			"toolbar sites folder");
	public static final Icon TOOLBAR_UTILITIES_FOLDER = initialize("tUtl",
			"toolbar utilities folder");

	// folders #2:

	public static final Icon APPLICATIONS_FOLDER = initialize("apps",
			"applications older");
	public static final Icon DESKTOP = initialize("desk", "desktop");
	public static final Icon DOCUMENTS_FOLDER = initialize("docs",
			"documents folder");
	public static final Icon EXTENSIONS_FOLDER = initialize("extn",
			"extensions folder");
	public static final Icon INTERNET_SEARCH_SITES_FOLDER = initialize("issf",
			"internet search sites folder");
	public static final Icon PUBLIC_FOLDER = initialize("pubf", "public folder");
	public static final Icon SPEAKING_ITEMS_FOLDER = initialize("spki",
			"speaking items folder");
	public static final Icon SYSTEM_FOLDER = initialize("macs", "system folder");
	public static final Icon WORKGROUP_FOLDER = initialize("wfld",
			"workgroup folder");
	public static final Icon VOICES_FOLDER = initialize("fvoc", "voices folder");

	// folders that don't include a folder icon:

	public static final Icon COLOR_SYNC_FOLDER = initialize("prof",
			"color sync folder");
	public static final Icon FAVORITES_FOLDER = initialize("favs",
			"favorites folder");
	public static final Icon USER_FOLDER = initialize("ufld", "user folder");

	// public static final Icon APPLE_MENU_FOLDER = initialize("amnu",
	// "Apple menu folder");
	// public static final Icon APPEARANCE_FOLDER = initialize("appr",
	// "appearance folder");
	// public static final Icon APPLICATION_SUPPORT_FOLDER = initialize("asup",
	// "application support folder");
	// public static final Icon CONTEXTUAL_MENU_ITEMS_FOLDER =
	// initialize("cmnu",
	// "contextual menu items folder");
	// public static final Icon CONTROL_PANEL_FOLDER = initialize("ctrl",
	// "control panel folder");
	// public static final Icon FONT_FOLDER = initialize("font",
	// "fonts folder");
	// public static final Icon MOUNTED_FOLDER = initialize("mntd",
	// "mounted folder");
	// public static final Icon NO_FOLDER = initialize("nfld", "no folder");
	// public static final Icon OWNED_FOLDER = initialize("ownd",
	// "owned folder");
	// public static final Icon PROTECTED_APPLICATION_FOLDER =
	// initialize("papp",
	// "protected application folder");
	// public static final Icon PRINTER_DESCRIPTION_FOLDER = initialize("ppdf",
	// "printer description folder");
	// public static final Icon PRINT_MONITOR_FOLDER = initialize("prnt",
	// "print monitor folder");
	// public static final Icon PROTECTED_SYSTEM_FOLDER = initialize("psys",
	// "protected system folder");
	// public static final Icon RECENT_APPLICATIONS_FOLDER = initialize("rapp",
	// "recent applications folder");
	// public static final Icon RECENT_DOCUMENTS_FOLDER = initialize("rdoc",
	// "recent documents folder");
	// public static final Icon RECENT_SERVERS_FOLDER = initialize("rsrv",
	// "recent servers folder");
	// public static final Icon SHUTDOWN_ITEMS_FOLDER = initialize("shdf",
	// "shutdown items folder");
	// public static final Icon SHARED_FOLDER = initialize("shfl",
	// "shared folder");
	// public static final Icon STARTUP_ITEMS_FOLDER = initialize("strt",
	// "startup items folder");
	// public static final Icon DROP_FOLDER = initialize("dbox", "drop folder");
	// public static final Icon PRIVATE_FOLDER = initialize("prvf",
	// "private folder");
	// public static final Icon CONTROL_PANEL_DISABLED_FOLDER =
	// initialize("ctrD",
	// "control panel disabled folder");
	// public static final Icon EXTENSIONS_DISABLED_FOLDER = initialize("extD",
	// "extensions disabled folder");
	// public static final Icon SHUTDOWN_ITEMS_DISABLED_FOLDER = initialize(
	// "shdD", "shutdown items disabled folder");
	// public static final Icon STARTUP_ITEMS_DISABLED_FOLDER =
	// initialize("strD",
	// "startup items disabled folder");
	// public static final Icon SYSTEM_EXTENSIONS_DISABLED_FOLDER = initialize(
	// "macD", "system extensions disabled folder");
	// public static final Icon APPLE_EXTRAS_FOLDER = initialize("aex ",
	// "Apple extras folder");
	// public static final Icon ASSISTANTS_FOLDER = initialize("ast ",
	// "assistants folder");
	// public static final Icon HELP_FOLDER = initialize("hlp ", "help folder");
	// public static final Icon INTERNET_FOLDER = initialize("int ",
	// "internet folder");
	// public static final Icon INTERNET_PLUGIN_FOLDER = initialize("net ",
	// "internet plugin folder");
	// public static final Icon LOCALES_FOLDER = initialize("loc",
	// "locales folder");
	// public static final Icon MAC_OS_README_FOLDER = initialize("mor ",
	// "Mac OS readme folder");
	// public static final Icon PREFERENCES_FOLDER = initialize("prf ",
	// "preferences folder");
	// public static final Icon PRINTER_DRIVER_FOLDER = initialize("prd ",
	// "printer driver folder");
	// public static final Icon SCRIPTING_ADDITIONS_FOLDER = initialize("scr ",
	// "scripting additions folder");
	// public static final Icon TEXT_ENCODINGS_FOLDER = initialize("tex ",
	// "text encodings folder");
	// public static final Icon USERS_FOLDER = initialize("usr ",
	// "users folder");
	// public static final Icon UTILITIES_FOLDER = initialize("uti ",
	// "utilities folder");

	// toolbar #2:

	public static final Icon TOOLBAR_ADVANCED = initialize("tbav",
			"toolbar advanced");
	public static final Icon TOOLBAR_CUSTOMIZE = initialize("tcus",
			"toolbar customize");
	public static final Icon TOOLBAR_DELETE = initialize("tdel",
			"toolbar delete");
	public static final Icon TOOLBAR_FAVORITES = initialize("tfav",
			"toolbar favorites");
	public static final Icon TOOLBAR_HOME = initialize("thom", "toolbar home");
	public static final Icon TOOLBAR_INFO = initialize("tbin", "toolbar info");
	public static final Icon TOOLBAR_LABELS = initialize("tblb",
			"toolbar labels");

	// alert:
	public static final Icon ALERT_CAUTION = initialize("caut", "alert caution");
	public static final Icon ALERT_CAUTION_BADGE = initialize("cbdg",
			"alert caution badge");

	public static final Icon ALERT_NOTE = initialize("note", "alert note");

	public static final Icon ALERT_STOP = initialize("stop", "alert stop");

	// trash:

	public static final Icon FULL_TRASH = initialize("ftrh", "full trash");
	public static final Icon TRASH = initialize("trsh", "trash");

	// media / generics:
	public static final Icon BURNING = initialize("burn", "burning");
	public static final Icon EJECT_MEDIA = initialize("ejec", "eject media");
	public static final Icon GENERIC_CD_ROM = initialize("cddr",
			"generic CD ROM");
	public static final Icon GENERIC_DOCUMENT = initialize("docu",
			"generic document");
	public static final Icon GENERIC_FILE_SERVER = initialize("srvr",
			"generic file server");
	public static final Icon GENERIC_FLOPPY = initialize("flpy",
			"generic floppy");
	public static final Icon GENERIC_HARD_DISK = initialize("hdsk",
			"generic hard disk");
	public static final Icon GENERIC_IDISK = initialize("idsk", "generic iDisk");
	public static final Icon USER_IDISK = initialize("udsk", "user iDisk");
	public static final Icon GENERIC_NETWORK = initialize("gnet",
			"generic network");
	public static final Icon GENERIC_REMOVABLE_MEDIA = initialize("rmov",
			"generic removable media");
	public static final Icon GENERIC_URL = initialize("gurl", "generic URL");

	// users:

	public static final Icon GUEST_USER = initialize("gusr", "guest user");
	public static final Icon USER = initialize("user", "user");
	public static final Icon GROUP = initialize("grup", "group");

	// clipping:

	public static final Icon CLIPPING_PICTURE_TYPE = initialize("clpp",
			"clipping picture type");
	public static final Icon CLIPPING_SOUND_TYPE = initialize("clps",
			"clipping sound type");
	public static final Icon CLIPPING_TEXT_TYPE = initialize("clpt",
			"clipping text type");
	public static final Icon CLIPPING_UNKNOWN_TYPE = initialize("clpu",
			"clipping unknown type");

	// permissions

	public static final Icon LOCKED_BADGE = initialize("lbdg", "locked badge");
	public static final Icon LOCKED = initialize("lock", "locked");
	public static final Icon UNLOCKED = initialize("ulck", "unlocked");
	public static final Icon NO_WRITE = initialize("nwrt", "no write");

	// arrow:

	public static final Icon BACKWARD_ARROW = initialize("baro",
			"backward arrow");
	public static final Icon FORWARD_ARROW = initialize("faro", "forward arrow");

	// other:

	public static final Icon ALIAS_BADGE = initialize("abdg", "alias badge");
	// public static final Icon AFP_SERVER = initialize("afps", "AFP server");
	public static final Icon KEEP_ARRANGED = initialize("arng", "keep arranged");
	// public static final Icon SORT_ASCENDING = initialize("asnd",
	// "sort ascending");
	// public static final Icon APPLETALK = initialize("atlk", "AppleTalk");
	// public static final Icon APPLETALK_ZONE = initialize("atzn",
	// "AppleTalk Zone");
	// public static final Icon APPLE_LOGO = initialize("capl", "Apple logo");
	public static final Icon CONNECT_TO = initialize("cnct", "connect to");
	public static final Icon DELETE_ALIAS = initialize("dali", "delete alias");
	// public static final Icon SORT_DESCENDING = initialize("dsnd",
	// "sort descending");
	// public static final Icon GENERIC_EDITION_FILE = initialize("edtf",
	// "generic edition file");
	public static final Icon FAVORITE_ITEMS = initialize("favr",
			"favorite items");
	// public static final Icon FTP_SERVER = initialize("ftps", "FTP server");
	public static final Icon GRID = initialize("grid", "grid");
	public static final Icon GENERIC_WINDOW = initialize("gwin",
			"generic window");
	public static final Icon HELP = initialize("help", "help");
	// public static final Icon HTTP_SERVER = initialize("htps", "HTTP server");
	// public static final Icon IP_FILE_SERVER = initialize("isrv",
	// "IP file server");
	// public static final Icon CLIPBOARD = initialize("CLIP", "clipboard");
	// public static final Icon MOUNTED_BADGE = initialize("mbdg",
	// "mounted badge");
	// public static final Icon NO_FILES = initialize("nfil", "no files");
	// public static final Icon GENERIC_PREFERENCES = initialize("pref",
	// "generic preferences");
	public static final Icon QUESTION_MARK = initialize("ques", "question mark");
	// public static final Icon GENERIC_RAM_DISK = initialize("ramd",
	// "generic RAM disk");
	public static final Icon RIGHT_CONTAINER_ARROW = initialize("rcar",
			"right container arrow");
	public static final Icon RECENT_ITEMS = initialize("rcnt", "recent items");
	public static final Icon COMPUTER = initialize("root", "computer");
	// public static final Icon APPLE_MENU = initialize("sapl", "Apple menu");
	// public static final Icon SHARED_BADGE = initialize("sbdg",
	// "shared badge");
	// public static final Icon APPLESCRIPT_BADGE = initialize("scrp",
	// "AppleScript badge");
	public static final Icon GENERIC_STATIONARY = initialize("sdoc",
			"generic stationary");
	// public static final Icon GENERIC_SHARED_LIBRARY = initialize("shlb",
	// "generic shared library");
	// public static final Icon SHARING_PRIVS_NOT_APPLICABLE =
	// initialize("shna",
	// "sharing privileges not applicable");
	// public static final Icon SHARING_PRIVS_READ_ONLY = initialize("shro",
	// "sharing privileges read only");
	// public static final Icon SHORTCUT = initialize("shrt", "shortcut");
	// public static final Icon SHARING_PRIVS_READ_WRITE = initialize("shrw",
	// "sharing privileges read write");
	// public static final Icon SHARING_PRIVS_UNKNOWN = initialize("shuk",
	// "sharing privileges unknown");
	// public static final Icon OWNER = initialize("susr", "owner");
	public static final Icon UNKNOWN_FS_OBJECT = initialize("unfs",
			"unknown FS object");
	// public static final Icon SHARING_PRIVS_WRITABLE = initialize("writ",
	// "sharing privileges writable");
	// public static final Icon GENERIC_CONTROL_PANEL = initialize("APPC",
	// "generic control panel");
	// public static final Icon GENERIC_DESK_ACCESSORY = initialize("APPD",
	// "generic desk accessory");
	public static final Icon GENERIC_APPLICATION = initialize("APPL",
			"generic application");
	public static final Icon FINDER = initialize("FNDR", "Finder");
	public static final Icon FONT_SUITCASE = initialize("FFIL", "font suitcase");
	public static final Icon GENERIC_FONT = initialize("ffil", "generic font");
	// public static final Icon CONTROL_CONTROL_STRIP_MODULE =
	// initialize("sdev",
	// "generic control strip module");
	// public static final Icon GENERIC_COMPONENT = initialize("thng",
	// "generic component");
	// public static final Icon GENERIC_EXTENSION = initialize("INIT",
	// "generic extension");
	// public static final Icon GENERIC_FONT_SCALE = initialize("sclr",
	// "generic font scaler");
	// public static final Icon GENERIC_MOVER_OBJECT = initialize("movr",
	// "generic mover object");
	// public static final Icon GENERIC_PC_CARD = initialize("pcmc",
	// "generic PC card");
	// public static final Icon GENERIC_QUERY_DOCUMENT = initialize("qery",
	// "generic query document");
	// public static final Icon GENERIC_SUITCASE = initialize("suit",
	// "generic suitcase");
	// public static final Icon GENERIC_WORM = initialize("worm",
	// "generic WORM");
	// public static final Icon INTERNATIONAL_RESOURCES = initialize("ifil",
	// "international resources");
	// public static final Icon KEYBOARD_LAYOUT = initialize("kfil",
	// "keyboard layout");
	// public static final Icon SOUND_FILE = initialize("sfil", "sound file");
	// public static final Icon SYSTEM_SUITCASE = initialize("zsys",
	// "system suitcsae");
	public static final Icon TRUETYPE_FONT = initialize("tfil", "TrueType font");
	public static final Icon TRUETYPE_FLAT_FONT = initialize("sfnt",
			"TrueType flat font");
	public static final Icon TRUETYPE_MULTI_FLAT_FONT = initialize("ttcf",
			"TrueType multi flat font");
	public static final Icon INTERNET_LOCATION_HTTP = initialize("ilht",
			"internet location HTTP");
	public static final Icon INTERNET_LOCATION_FTP = initialize("ilft",
			"internet location FTP");
	// public static final Icon INTERNET_LOCATION_APPLESHARE =
	// initialize("ilaf",
	// "internet location AppleShare");
	public static final Icon INTERNET_LOCATION_APPLETALK_ZONE = initialize(
			"ilat", "internet location AppleTalk zone");
	public static final Icon INTERNET_LOCATION_FILE = initialize("ilfi",
			"internet location file");
	// public static final Icon INTERNET_LOCATION_MAIL = initialize("ilma",
	// "internet location mail");
	// public static final Icon INTERNET_LOCATION_NEWS = initialize("ilnw",
	// "internet location news");
	public static final Icon INTERNET_LOCATION_NLS_NEIGHBORHOOD = initialize(
			"ilns", "internet location NLS neighborhood");
	// public static final Icon INTERNET_LOCATION_GENERIC = initialize("ilge",
	// "internet location generic");
	public static final Icon CONTROL_STRIP_MODULES_FOLDER_ICON = initialize(
			"sdv ", "control strip modules folder icon");

	@SuppressWarnings("rawtypes")
	private static Constructor systemIconConstructor;

	/**
	 * Initialize the reflection objects needed to instantiate
	 * com.apple.laf.AquaIcons
	 */
	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	private static void initialize() {
		try {
			if (systemIconConstructor == null) {
				Class j2 = Class.forName("com.apple.laf.AquaIcon$SystemIcon");
				systemIconConstructor = j2.getDeclaredConstructor(String.class);
				systemIconConstructor.setAccessible(true);
			}
		} catch (ClassNotFoundException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Return all the IDs this runtime session has referred to, including all
	 * the static fields listed in this class.
	 */
	public static Collection<String> getIDs() {
		return Collections.unmodifiableSet(iconMap.keySet());
	}

	/**
	 * Initialize a com.apple.laf.AquaIcon
	 * 
	 * @param selectorID
	 *            the 4-letter identifier, such as "fldr"
	 * @param description
	 *            an optional human-readable description
	 * @return a newly instantiated com.apple.laf.AquaIcon
	 */
	private synchronized static final Icon initialize(String selectorID,
			String description) {
		descriptionMap.put(selectorID, description);
		Icon icon = get(selectorID);
		return icon;
	}

	/**
	 * Return the description associated with a 4-letter icon identifier.
	 * 
	 * @param selectorID
	 *            the 4-letter identifier, such as "fldr"
	 * @return the human-readable description, such as "generic folder". This
	 *         description may be more "developer-friendly" than
	 *         "user-friendly", but it will always be more readable than the
	 *         4-letter identifier.
	 */
	public static String getDescription(String selectorID) {
		return descriptionMap.get(selectorID);
	}

	/**
	 * Return a cached Icon based on an identifier.
	 * 
	 * @param selectorID
	 *            selectorID the 4-letter identifier, such as "fldr". This class
	 *            lists dozens of fields that include supported IDs, but you can
	 *            pass any value here. If you know of a special ID (or if one
	 *            becomes available in future Mac OS releases) you can request
	 *            it here.
	 * 
	 * @return a cached Icon based on that identifier.
	 */
	public synchronized static final Icon get(String selectorID) {
		try {
			initialize();
			Icon icon = iconMap.get(selectorID);
			if (icon == null) {
				icon = (Icon) systemIconConstructor.newInstance(selectorID);
				String description = descriptionMap.get(selectorID);
				if (description != null && description.trim().length() > 0) {
					AccessibleIcon icon2 = IconUtils.createAccessibleIcon(icon);
					icon2.setAccessibleIconDescription(description);
					icon = (Icon) icon2;
				}
				iconMap.put(selectorID, icon);
			}

			return icon;
		} catch (InstantiationException | IllegalAccessException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Return a ScaledIcon based on an identifier.
	 * 
	 * @param selectorID
	 *            selectorID the 4-letter identifier, such as "fldr". This class
	 *            lists dozens of fields that include supported IDs, but you can
	 *            pass any value here. If you know of a special ID (or if one
	 *            becomes available in future Mac OS releases) you can request
	 *            it here.
	 * @param iconSize
	 *            a Dimension specifying the maximum size the returned icon
	 *            should be. If this is non-null then this method returns a new
	 *            ScaledIcon that refers to the cached Icon returned by
	 *            {@link #get(String)}.
	 *            <p>
	 *            Note not all AquaIcons are guaranteed to scale gracefully.
	 *            Some may only be supported at small resolutions, so requesting
	 *            them at 64x64 may return a large icon that is technically
	 *            64x64 but is pixelated and ugly.
	 *            <p>
	 *            This method will proportionally constrain the scale if needed.
	 *            So if you ask for a 100x75 icon and the original icon has a
	 *            1:1 width/height ratio: this will return an icon that is
	 *            75x75.
	 * 
	 * @return a ScaledIcon based on that identifier that matches the required
	 *         size.
	 */
	public static ScaledIcon get(String selectorID, Dimension iconSize) {
		Icon icon = get(selectorID);

		Dimension d = new Dimension(icon.getIconWidth(), icon.getIconHeight());
		Dimension newSize = Dimension2D.scaleProportionally(d, iconSize);

		return IconUtils.createScaledIcon(icon, newSize.width, newSize.height);
	}
}