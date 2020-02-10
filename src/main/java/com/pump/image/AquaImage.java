package com.pump.image;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.pump.awt.Dimension2D;

/**
 * https://developer.apple.com/documentation/appkit/nsimage/
 * <p>
 * The built-in constants below exclude the 78ish images that begin with
 * "touchBar", because Apple's documentation mentions: <blockquote>Touch Bar
 * template images are exclusively for use in NSTouchBarItem objects and not in
 * onscreen windows.</blockquote>
 */
public class AquaImage implements Serializable {
	private static final long serialVersionUID = 1L;

	/** All known AquaImages. */
	private static final Map<String, AquaImage> knownImages = new HashMap<String, AquaImage>();

	/** A Quick Look template image. Available in OS X v10.5 and later. */
	public static final AquaImage QuickLookTemplate = get("QuickLookTemplate",
			"A Quick Look template image.",
			"Available in OS X v10.5 and later.");

	/** A Bluetooth template image. Available in OS X v10.5 and later. */
	public static final AquaImage BluetoothTemplate = get("BluetoothTemplate",
			"A Bluetooth template image.", "Available in OS X v10.5 and later.");

	/** An iChat Theater template image. Available in OS X v10.5 and later. */
	public static final AquaImage IChatTheaterTemplate = get(
			"IChatTheaterTemplate", "An iChat Theater template image.",
			"Available in OS X v10.5 and later.");

	/** A slideshow template image. Available in OS X v10.5 and later. */
	public static final AquaImage SlideshowTemplate = get("SlideshowTemplate",
			"A slideshow template image.", "Available in OS X v10.5 and later.");

	/** An action menu template image. Available in OS X v10.5 and later. */
	public static final AquaImage ActionTemplate = get("ActionTemplate",
			"An action menu template image.",
			"Available in OS X v10.5 and later.");

	/** A badge for a "smart" item. Available in OS X v10.5 and later. */
	public static final AquaImage SmartBadgeTemplate = get(
			"SmartBadgeTemplate", "A badge for a \"smart\" item.",
			"Available in OS X v10.5 and later.");

	/** An icon view mode template image. Available in OS X v10.5 and later. */
	public static final AquaImage IconViewTemplate = get("IconViewTemplate",
			"An icon view mode template image.",
			"Available in OS X v10.5 and later.");

	/** A list view mode template image. Available in OS X v10.5 and later. */
	public static final AquaImage ListViewTemplate = get("ListViewTemplate",
			"A list view mode template image.",
			"Available in OS X v10.5 and later.");

	/** A column view mode template image. Available in OS X v10.5 and later. */
	public static final AquaImage ColumnViewTemplate = get(
			"ColumnViewTemplate", "A column view mode template image.",
			"Available in OS X v10.5 and later.");

	/**
	 * A cover flow view mode template image. Available in OS X v10.5 and later.
	 */
	public static final AquaImage FlowViewTemplate = get("FlowViewTemplate",
			"A cover flow view mode template image.",
			"Available in OS X v10.5 and later.");

	/** A share view template image. Available in OS X v10.8 and later. */
	public static final AquaImage ShareTemplate = get("ShareTemplate",
			"A share view template image.",
			"Available in OS X v10.8 and later.");

	/** A path button template image. Available in OS X v10.5 and later. */
	public static final AquaImage PathTemplate = get("PathTemplate",
			"A path button template image.",
			"Available in OS X v10.5 and later.");

	/**
	 * An invalid data template image. Place this icon to the right of any
	 * fields containing invalid data. You can use this image to implement a
	 * borderless button. Available in OS X v10.5 and later.
	 */
	public static final AquaImage InvalidDataFreestandingTemplate = get(
			"InvalidDataFreestandingTemplate",
			"An invalid data template image. Place this icon to the right of any fields containing invalid data. You can use this image to implement a borderless button.",
			"Available in OS X v10.5 and later.");

	/**
	 * A locked lock template image. Use to indicate locked content. Available
	 * in OS X v10.5 and later.
	 */
	public static final AquaImage LockLockedTemplate = get(
			"LockLockedTemplate",
			"A locked lock template image. Use to indicate locked content.",
			"Available in OS X v10.5 and later.");

	/**
	 * An unlocked lock template image. Use to indicate modifiable content that
	 * can be locked. Available in OS X v10.5 and later.
	 */
	public static final AquaImage LockUnlockedTemplate = get(
			"LockUnlockedTemplate",
			"An unlocked lock template image. Use to indicate modifiable content that can be locked.",
			"Available in OS X v10.5 and later.");

	/** A \"go forward\" template image. Available in OS X v10.5 and later. */
	public static final AquaImage GoRightTemplate = get("GoRightTemplate",
			"A \"go forward\" template image.",
			"Available in OS X v10.5 and later.");

	/** A \"go back\" template image. Available in OS X v10.5 and later. */
	public static final AquaImage GoLeftTemplate = get("GoLeftTemplate",
			"A \"go back\" template image.",
			"Available in OS X v10.5 and later.");

	/**
	 * A generic right-facing triangle template image. Available in OS X v10.5
	 * and later.
	 */
	public static final AquaImage RightFacingTriangleTemplate = get(
			"RightFacingTriangleTemplate",
			"A generic right-facing triangle template image.",
			"Available in OS X v10.5 and later.");

	/**
	 * A generic left-facing triangle template image. Available in OS X v10.5
	 * and later.
	 */
	public static final AquaImage LeftFacingTriangleTemplate = get(
			"LeftFacingTriangleTemplate",
			"A generic left-facing triangle template image.",
			"Available in OS X v10.5 and later.");

	/** An add item template image. Available in OS X v10.5 and later. */
	public static final AquaImage AddTemplate = get("AddTemplate",
			"An add item template image.", "Available in OS X v10.5 and later.");

	/** A remove item template image. Available in OS X v10.5 and later. */
	public static final AquaImage RemoveTemplate = get("RemoveTemplate",
			"A remove item template image.",
			"Available in OS X v10.5 and later.");

	/**
	 * A reveal contents template image. You can use this image to implement a
	 * borderless button. Available in OS X v10.5 and later.
	 */
	public static final AquaImage RevealFreestandingTemplate = get(
			"RevealFreestandingTemplate",
			"A reveal contents template image. You can use this image to implement a borderless button.",
			"Available in OS X v10.5 and later.");

	/**
	 * A link template image. You can use this image to implement a borderless
	 * button. Available in OS X v10.5 and later.
	 */
	public static final AquaImage FollowLinkFreestandingTemplate = get(
			"FollowLinkFreestandingTemplate",
			"A link template image. You can use this image to implement a borderless button.",
			"Available in OS X v10.5 and later.");

	/**
	 * An enter full-screen mode template image. Available in OS X v10.5 and
	 * later.
	 */
	public static final AquaImage EnterFullScreenTemplate = get(
			"EnterFullScreenTemplate",
			"An enter full-screen mode template image.",
			"Available in OS X v10.5 and later.");

	/**
	 * An exit full-screen mode template image. Available in OS X v10.5 and
	 * later.
	 */
	public static final AquaImage ExitFullScreenTemplate = get(
			"ExitFullScreenTemplate",
			"An exit full-screen mode template image.",
			"Available in OS X v10.5 and later.");

	/**
	 * A stop progress button template image. Available in OS X v10.5 and later.
	 */
	public static final AquaImage StopProgressTemplate = get(
			"StopProgressTemplate", "A stop progress button template image.",
			"Available in OS X v10.5 and later.");

	/**
	 * A stop progress template image. You can use this image to implement a
	 * borderless button. Available in OS X v10.5 and later.
	 */
	public static final AquaImage StopProgressFreestandingTemplate = get(
			"StopProgressFreestandingTemplate",
			"A stop progress template image. You can use this image to implement a borderless button.",
			"Available in OS X v10.5 and later.");

	/** A refresh template image. Available in OS X v10.5 and later. */
	public static final AquaImage RefreshTemplate = get("RefreshTemplate",
			"A refresh template image.", "Available in OS X v10.5 and later.");

	/**
	 * A refresh template image. You can use this image to implement a
	 * borderless button. Available in OS X v10.5 and later.
	 */
	public static final AquaImage RefreshFreestandingTemplate = get(
			"RefreshFreestandingTemplate",
			"A refresh template image. You can use this image to implement a borderless button.",
			"Available in OS X v10.5 and later.");

	/** A Bonjour icon. Available in OS X v10.5 and later. */
	public static final AquaImage Bonjour = get("Bonjour", "A Bonjour icon.",
			"Available in OS X v10.5 and later.");

	/** A Dot Mac icon. Available in OS X v10.5 and later. */
	public static final AquaImage DotMac = get("DotMac", "A Dot Mac icon.",
			"Available in OS X v10.5 and later.");

	/** A computer icon. Available in OS X v10.5 and later. */
	public static final AquaImage Computer = get("Computer",
			"A computer icon.", "Available in OS X v10.5 and later.");

	/** A burnable folder icon. Available in OS X v10.5 and later. */
	public static final AquaImage FolderBurnable = get("FolderBurnable",
			"A burnable folder icon.", "Available in OS X v10.5 and later.");

	/** A smart folder icon. Available in OS X v10.5 and later. */
	public static final AquaImage FolderSmart = get("FolderSmart",
			"A smart folder icon.", "Available in OS X v10.5 and later.");

	/** A network icon. Available in OS X v10.5 and later. */
	public static final AquaImage Network = get("Network", "A network icon.",
			"Available in OS X v10.5 and later.");

	/**
	 * A drag image for multiple items. Available in OS X v10.5 and later. *?
	 * public static final AquaImage MultipleDocuments =
	 * get("MultipleDocuments",
	 * "A drag image for multiple items. Available in OS X v10.5 and later.");
	 * 
	 * /** User account toolbar icon. Use in a preferences window only.
	 */
	public static final AquaImage UserAccounts = get("UserAccounts",
			"User account toolbar icon. Use in a preferences window only.",
			null);

	/**
	 * General preferences toolbar icon. Use in a preferences window only.
	 * Available in OS X v10.5 and later.
	 */
	public static final AquaImage PreferencesGeneral = get(
			"PreferencesGeneral",
			"General preferences toolbar icon. Use in a preferences window only.",
			"Available in OS X v10.5 and later.");

	/**
	 * Advanced preferences toolbar icon. Use in a preferences window only.
	 * Available in OS X v10.5 and later.
	 */
	public static final AquaImage Advanced = get("Advanced",
			"Advanced preferences toolbar icon for the preferences window.",
			"Available in OS X v10.5 and later.");

	/**
	 * Advanced preferences toolbar icon. Use in a preferences window only.
	 * Available in OS X v10.5 and later.
	 */
	public static final AquaImage ApplicationIcon = get("ApplicationIcon",
			"The app's icon.", "Available in OS X v10.6 and later.");

	/** An information toolbar icon. Available in OS X v10.5 and later. */
	public static final AquaImage Info = get("Info",
			"An information toolbar icon.",
			"Available in OS X v10.5 and later.");

	/** A font panel toolbar icon. Available in OS X v10.5 and later. */
	public static final AquaImage FontPanel = get("FontPanel",
			"A font panel toolbar icon.", "Available in OS X v10.5 and later.");

	public static final AquaImage GoBackTemplate = get("GoBackTemplate",
			"A \"Go Back\" template image.",
			"Available in OS X v10.12 and later.");

	public static final AquaImage GoForwardTemplate = get("GoForwardTemplate",
			"A \"Go Forward\" template image.",
			"Available in OS X v10.12 and later.");

	/** A color panel toolbar icon. Available in OS X v10.5 and later. */
	public static final AquaImage ColorPanel = get("ColorPanel",
			"A color panel toolbar icon.", "Available in OS X v10.5 and later.");

	/** Permissions for a single user. Available in OS X v10.5 and later. */
	public static final AquaImage User = get("User",
			"Permissions for a single user.",
			"Available in OS X v10.5 and later.");

	/** Permissions for a group of users. Available in OS X v10.5 and later. */
	public static final AquaImage UserGroup = get("UserGroup",
			"Permissions for a group of users.",
			"Available in OS X v10.5 and later.");

	/** Permissions for all users. Available in OS X v10.5 and later. */
	public static final AquaImage Everyone = get("Everyone",
			"Permissions for all users.", "Available in OS X v10.5 and later.");

	/** Permissions for guests. Available in OS X v10.6 and later. */
	public static final AquaImage UserGuest = get("UserGuest",
			"Permissions for guests.", "Available in OS X v10.6 and later.");

	/** A folder image. Available in OS X v10.6 and later. */
	public static final AquaImage Folder = get("Folder", "A folder image.",
			"Available in OS X v10.6 and later.");

	/** An image of the empty trash can. Available in OS X v10.6 and later. */
	public static final AquaImage TrashEmpty = get("TrashEmpty",
			"An image of the empty trash can.",
			"Available in OS X v10.6 and later.");

	/** An image of the full trash can. Available in OS X v10.6 and later. */
	public static final AquaImage TrashFull = get("TrashFull",
			"An image of the full trash can.",
			"Available in OS X v10.6 and later.");

	/** Home image suitable for a template. Available in OS X v10.6 and later. */
	public static final AquaImage HomeTemplate = get("HomeTemplate",
			"Home image suitable for a template.",
			"Available in OS X v10.6 and later.");

	/**
	 * Bookmarks image suitable for a template. Available in OS X v10.6 and
	 * later.
	 */
	public static final AquaImage BookmarksTemplate = get("BookmarksTemplate",
			"Bookmarks image suitable for a template.",
			"Available in OS X v10.6 and later.");

	/** Caution Image. Available in OS X v10.6 and later. */
	public static final AquaImage Caution = get("Caution", "A caution image.",
			"Available in OS X v10.6 and later.");

	/**
	 * Small green indicator, similar to iChat's available image. Available in
	 * OS X v10.6 and later.
	 */
	public static final AquaImage StatusAvailable = get("StatusAvailable",
			"Small green indicator, similar to iChat's available image.",
			"Available in OS X v10.6 and later.");

	/**
	 * Small yellow indicator, similar to iChat's idle image. Available in OS X
	 * v10.6 and later.
	 */
	public static final AquaImage StatusPartiallyAvailable = get(
			"StatusPartiallyAvailable",
			"Small yellow indicator, similar to iChat's idle image.",
			"Available in OS X v10.6 and later.");

	/**
	 * Small red indicator, similar to iChat's unavailable image. Available in
	 * OS X v10.6 and later.
	 */
	public static final AquaImage StatusUnavailable = get("StatusUnavailable",
			"Small red indicator, similar to iChat's unavailable image.",
			"Available in OS X v10.6 and later.");

	/** Small clear indicator. Available in OS X v10.6 and later. */
	public static final AquaImage StatusNone = get("StatusNone",
			"Small clear indicator.", "Available in OS X v10.6 and later.");

	/**
	 * A check mark. Drawing these outside of menus is discouraged. Available in
	 * OS X v10.6 and later.
	 */
	public static final AquaImage MenuOnStateTemplate = get(
			"MenuOnStateTemplate",
			"A check mark. Drawing these outside of menus is discouraged.",
			"Available in OS X v10.6 and later.");

	/**
	 * A horizontal dash. Drawing these outside of menus is discouraged.
	 * Available in OS X v10.6 and later.
	 */
	public static final AquaImage MenuMixedStateTemplate = get(
			"MenuMixedStateTemplate",
			"A horizontal dash. Drawing these outside of menus is discouraged.",
			"Available in OS X v10.6 and later.");

	/**
	 * MobileMe logo. Note that this is preferred to using the NSImageNameDotMac
	 * image, although that image is not expected to be deprecated. Available in
	 * OS X v10.6 and later.
	 */
	public static final AquaImage MobileMe = get(
			"MobileMe",
			"MobileMe logo. Use of this image is discouraged; instead, use networkName.",
			"Available in OS X v10.6 and later.");

	public static final AquaImage MultipleDocuments = get(
			"MultipleDocuments",
			"A drag image for multiple items. You can use this icon as the drag image when dragging multiple items. You should not use this image for any other intended purpose.",
			"Available in OS X v10.5 and later.");

	// these were all identified programmatically in AquaImageDemo:

	public static final AquaImage Accounts = get("Accounts");
	public static final AquaImage Action = get("Action");
	public static final AquaImage Add = get("Add");
	public static final AquaImage Bluetooth = get("Bluetooth");
	public static final AquaImage Color = get("Color");
	public static final AquaImage Font = get("Font");
	public static final AquaImage Group = get("Group");
	public static final AquaImage Link = get("Link");
	public static final AquaImage Path = get("Path");
	public static final AquaImage Refresh = get("Refresh");
	public static final AquaImage Remove = get("Remove");
	public static final AquaImage Slideshow = get("Slideshow");
	public static final AquaImage Stop = get("Stop");
	public static final AquaImage Actions = get("Actions");
	public static final AquaImage Bookmark = get("Bookmark");
	public static final AquaImage Bookmarks = get("Bookmarks");
	public static final AquaImage Bug = get("Bug");
	public static final AquaImage Burning = get("Burning");
	public static final AquaImage Cancel = get("Cancel");
	public static final AquaImage Disclosed = get("Disclosed");
	public static final AquaImage Effect = get("Effect");
	public static final AquaImage Erase = get("Erase");
	public static final AquaImage Home = get("Home");
	public static final AquaImage Pause = get("Pause");
	public static final AquaImage Person = get("Person");
	public static final AquaImage Photograph = get("Photograph");
	public static final AquaImage Play = get("Play");
	public static final AquaImage Print = get("Print");
	public static final AquaImage Reload = get("Reload");
	public static final AquaImage Rewind = get("Rewind");
	public static final AquaImage Script = get("Script");
	public static final AquaImage Security = get("Security");
	public static final AquaImage Share = get("Share");
	public static final AquaImage Snapback = get("Snapback");
	public static final AquaImage Switch = get("Switch");
	public static final AquaImage AccountsTemplate = get("AccountsTemplate");
	public static final AquaImage ActionsTemplate = get("ActionsTemplate");
	public static final AquaImage AddBoomark = get("AddBookmark");
	public static final AquaImage AdvancedPreferences = get("AdvancedPreferences");
	public static final AquaImage AdvancedTemplate = get("AdvancedTemplate");
	public static final AquaImage BonjourTemplate = get("BonjourTemplate");
	public static final AquaImage BookmarkLock = get("BookmarkLock");
	public static final AquaImage BookmarkTemplate = get("BookmarkTemplate");
	public static final AquaImage BugTemplate = get("BugTemplate");
	public static final AquaImage BurningTemplate = get("BurningTemplate");
	public static final AquaImage CancelTemplate = get("CancelTemplate");
	public static final AquaImage CautionTemplate = get("CautionTemplate");
	public static final AquaImage ColorTemplate = get("ColorTemplate");
	public static final AquaImage ColumnView = get("ColumnView");
	public static final AquaImage ComputerTemplate = get("ComputerTemplate");
	public static final AquaImage DisclosedTemplate = get("DisclosedTemplate");
	public static final AquaImage DisclosedAlternate = get("DisclosedAlternate");
	public static final AquaImage EffectTemplate = get("EffectTemplate");
	public static final AquaImage EraseTemplate = get("EraseTemplate");
	public static final AquaImage EveryoneTemplate = get("EveryoneTemplate");
	public static final AquaImage FlowView = get("FlowView");
	public static final AquaImage FolderTemplate = get("FolderTemplate");
	public static final AquaImage FontTemplate = get("FontTemplate");
	public static final AquaImage GeneralPreferences = get("GeneralPreferences");
	public static final AquaImage GoLeft = get("GoLeft");
	public static final AquaImage GoRight = get("GoRight");
	public static final AquaImage GoBack = get("GoBack");
	public static final AquaImage GoForward = get("GoForward");
	public static final AquaImage GroupTemplate = get("GroupTemplate");
	public static final AquaImage IconBurning = get("IconBurning");
	public static final AquaImage IconComputer = get("IconComputer");
	public static final AquaImage IconGroup = get("IconGroup");
	public static final AquaImage IconLocked = get("IconLocked");
	public static final AquaImage IconUnlocked = get("IconUnlocked");
	public static final AquaImage IconUser = get("IconUser");
	public static final AquaImage IconView = get("IconView");
	public static final AquaImage IconClipboard = get("IconClipboard");
	public static final AquaImage IconDesktop = get("IconDesktop");
	public static final AquaImage IconFinder = get("IconFinder");
	public static final AquaImage IconGrid = get("IconGrid");
	public static final AquaImage IconHelp = get("IconHelp");
	public static final AquaImage IconOwner = get("IconOwner");
	public static final AquaImage IconShortcut = get("IconShortcut");
	public static final AquaImage IconTrash = get("IconTrash");
	public static final AquaImage InfoTemplate = get("InfoTemplate");
	public static final AquaImage InvalidData = get("InvalidData");
	public static final AquaImage LinkTemplate = get("LinkTemplate");
	public static final AquaImage LinkButton = get("LinkButton");
	public static final AquaImage ListView = get("ListView");
	public static final AquaImage LockLocked = get("LockLocked");
	public static final AquaImage LockUnlocked = get("LockUnlocked");
	public static final AquaImage MultipleItems = get("MultipleItems");
	public static final AquaImage NetworkTemplate = get("NetworkTemplate");
	public static final AquaImage PathIndicator = get("PathIndicator");
	public static final AquaImage PauseTemplate = get("PauseTemplate");
	public static final AquaImage PersonTemplate = get("PersonTemplate");
	public static final AquaImage PersonAnonymous = get("PersonAnonymous");
	public static final AquaImage PersonUnknown = get("PersonUnknown");
	public static final AquaImage PhotographTemplate = get("PhotographTemplate");
	public static final AquaImage PlayTemplate = get("PlayTemplate");
	public static final AquaImage PrintTemplate = get("PrintTemplate");
	public static final AquaImage QuickLook = get("QuickLook");
	public static final AquaImage RefreshFreestanding = get("RefreshFreestanding");
	public static final AquaImage ReloadTemplate = get("ReloadTemplate");
	public static final AquaImage RevealFreestanding = get("RevealFreestanding");
	public static final AquaImage RewindTemplate = get("RewindTemplate");
	public static final AquaImage ScriptTemplate = get("ScriptTemplate");
	public static final AquaImage SecurityTemplate = get("SecurityTemplate");
	public static final AquaImage SmartBadge = get("SmartBadge");
	public static final AquaImage SnapbackTemplate = get("SnapbackTemplate");
	public static final AquaImage StopProgress = get("StopProgress");
	public static final AquaImage StopTemplate = get("StopTemplate");
	public static final AquaImage SynchronizeTemplate = get("SynchronizeTemplate");
	public static final AquaImage SynchronizeConflict = get("SynchronizeConflict");
	public static final AquaImage SynchronizeStart = get("SynchronizeStart");
	public static final AquaImage Synchronize = get("Synchronize");
	public static final AquaImage TheaterStart = get("TheaterStart");
	public static final AquaImage UserTemplate = get("UserTemplate");
	public static final AquaImage ViewList = get("ViewList");
	public static final AquaImage ViewColumns = get("ViewColumns");
	public static final AquaImage ViewGroups = get("ViewGroups");
	public static final AquaImage ViewIcons = get("ViewIcons");
	public static final AquaImage KEXT = get("KEXT");

	private static AquaImage get(String name, String description,
			String availability) {
		AquaImage img = knownImages.get(name);
		if (img == null) {
			img = new AquaImage(name, description, availability);
			knownImages.put(name, img);
		}
		return img;
	}

	public static AquaImage get(String name) {
		return get(name, null, null);
	}

	String name, description, availability;
	transient Image image;
	transient BufferedImage bufferedImage;

	private AquaImage(String name, String description, String availability) {
		Objects.requireNonNull(name);
		this.name = name;
		this.description = description;
		this.availability = availability;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AquaImage))
			return false;
		AquaImage t = (AquaImage) obj;
		return t.name.equals(name);
	}

	@Override
	public String toString() {
		if (description == null) {
			return "AquaImage[ \"" + name + "\"]";
		}
		return "AquaImage[ \"" + name + "\", \"" + description + "\", \""
				+ availability + "\"]";
	}

	public synchronized Image getImage() {
		if (image == null) {
			String n = "NSImage://NS" + name;
			image = Toolkit.getDefaultToolkit().getImage(n);
		}
		return image;
	}

	private static Class cImageClass;
	private static Field creatorField;
	private static Object creator;
	private static Method createImageFromNameMethod;

	public Image getImage(Dimension maxConstrainingSize) {
		Image img = getImage();
		Dimension originalSize = ImageSize.get(img);
		Dimension newSize = Dimension2D.scaleProportionally(originalSize,
				maxConstrainingSize);

		String nsName = "NS" + name;

		try {
			if (cImageClass == null) {
				cImageClass = Class.forName("sun.lwawt.macosx.CImage");
				creatorField = cImageClass.getDeclaredField("creator");
				creatorField.setAccessible(true);
				creator = creatorField.get(null);
				createImageFromNameMethod = creator.getClass().getMethod(
						"createImageFromName", String.class, Integer.TYPE,
						Integer.TYPE);
			}

			return (Image) createImageFromNameMethod.invoke(creator, nsName,
					newSize.width, newSize.height);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public synchronized BufferedImage getBufferedImage() {
		if (bufferedImage == null) {
			bufferedImage = ImageLoader.createImage(getImage());
		}
		return bufferedImage;
	}

	public synchronized BufferedImage getBufferedImage(
			Dimension maxConstrainingSize) {
		return ImageLoader.createImage(getImage(maxConstrainingSize));
	}

	public String getName() {
		return name;
	}

	public String getAvailability() {
		return availability;
	}

	public String getDescription() {
		return description;
	}
}