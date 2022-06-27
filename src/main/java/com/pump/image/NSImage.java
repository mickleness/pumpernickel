/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.image;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.pump.awt.Dimension2D;

/**
 * This class catalogs a set of images available only on Mac.
 * <p>
 * If you are not scaling these images: these are already accessible on Mac by
 * calling:
 * <code>Toolkit.getDefaultToolkit().getImage("NSImage://NSComputer")</code>
 * <p>
 * The scaling logic uses reflection to help guarantee a high-resolution image.
 * <p>
 * The list of constants is based on Apple's documentation here: <br>
 * <a href="https://developer.apple.com/documentation/appkit/nsimage/">https://
 * developer.apple.com/documentation/appkit/nsimage/</a>
 * <p>
 * (The built-in constants below exclude the 78ish images that begin with
 * "touchBar", because Apple's documentation mentions: "Touch Bar template
 * images are exclusively for use in NSTouchBarItem objects and not in onscreen
 * windows.")
 * 
 * @see com.pump.icon.AquaIcon
 */
public class NSImage implements Serializable {
	private static final long serialVersionUID = 1L;

	/** All known NSImages. */
	private static final Map<String, NSImage> knownImages = new LinkedHashMap<String, NSImage>();

	/**
	 * Return all the IDs this runtime session has referred to, including all
	 * the static fields listed in this class.
	 */
	public static Collection<String> getIDs() {
		return Collections.unmodifiableSet(knownImages.keySet());
	}

	// folders:

	// other:

	/** A Quick Look template image. Available in OS X v10.5 and later. */
	public static final NSImage QuickLookTemplate = get("QuickLookTemplate",
			"A Quick Look template image.",
			"Available in OS X v10.5 and later.");

	/** A Bluetooth template image. Available in OS X v10.5 and later. */
	public static final NSImage BluetoothTemplate = get("BluetoothTemplate",
			"A Bluetooth template image.", "Available in OS X v10.5 and later.");

	/** An iChat Theater template image. Available in OS X v10.5 and later. */
	public static final NSImage IChatTheaterTemplate = get(
			"IChatTheaterTemplate", "An iChat Theater template image.",
			"Available in OS X v10.5 and later.");

	/** A slideshow template image. Available in OS X v10.5 and later. */
	public static final NSImage SlideshowTemplate = get("SlideshowTemplate",
			"A slideshow template image.", "Available in OS X v10.5 and later.");

	/** An action menu template image. Available in OS X v10.5 and later. */
	public static final NSImage ActionTemplate = get("ActionTemplate",
			"An action menu template image.",
			"Available in OS X v10.5 and later.");

	/** A badge for a "smart" item. Available in OS X v10.5 and later. */
	public static final NSImage SmartBadgeTemplate = get("SmartBadgeTemplate",
			"A badge for a \"smart\" item.",
			"Available in OS X v10.5 and later.");

	/** An icon view mode template image. Available in OS X v10.5 and later. */
	public static final NSImage IconViewTemplate = get("IconViewTemplate",
			"An icon view mode template image.",
			"Available in OS X v10.5 and later.");

	/** A list view mode template image. Available in OS X v10.5 and later. */
	public static final NSImage ListViewTemplate = get("ListViewTemplate",
			"A list view mode template image.",
			"Available in OS X v10.5 and later.");

	/** A column view mode template image. Available in OS X v10.5 and later. */
	public static final NSImage ColumnViewTemplate = get("ColumnViewTemplate",
			"A column view mode template image.",
			"Available in OS X v10.5 and later.");

	/**
	 * A cover flow view mode template image. Available in OS X v10.5 and later.
	 */
	public static final NSImage FlowViewTemplate = get("FlowViewTemplate",
			"A cover flow view mode template image.",
			"Available in OS X v10.5 and later.");

	/** A share view template image. Available in OS X v10.8 and later. */
	public static final NSImage ShareTemplate = get("ShareTemplate",
			"A share view template image.",
			"Available in OS X v10.8 and later.");

	/** A path button template image. Available in OS X v10.5 and later. */
	public static final NSImage PathTemplate = get("PathTemplate",
			"A path button template image.",
			"Available in OS X v10.5 and later.");

	/**
	 * An invalid data template image. Place this icon to the right of any
	 * fields containing invalid data. You can use this image to implement a
	 * borderless button. Available in OS X v10.5 and later.
	 */
	public static final NSImage InvalidDataFreestandingTemplate = get(
			"InvalidDataFreestandingTemplate",
			"An invalid data template image. Place this icon to the right of any fields containing invalid data. You can use this image to implement a borderless button.",
			"Available in OS X v10.5 and later.");

	/**
	 * A locked lock template image. Use to indicate locked content. Available
	 * in OS X v10.5 and later.
	 */
	public static final NSImage LockLockedTemplate = get("LockLockedTemplate",
			"A locked lock template image. Use to indicate locked content.",
			"Available in OS X v10.5 and later.");

	/**
	 * An unlocked lock template image. Use to indicate modifiable content that
	 * can be locked. Available in OS X v10.5 and later.
	 */
	public static final NSImage LockUnlockedTemplate = get(
			"LockUnlockedTemplate",
			"An unlocked lock template image. Use to indicate modifiable content that can be locked.",
			"Available in OS X v10.5 and later.");

	/** A \"go forward\" template image. Available in OS X v10.5 and later. */
	public static final NSImage GoRightTemplate = get("GoRightTemplate",
			"A \"go forward\" template image.",
			"Available in OS X v10.5 and later.");

	/** A \"go back\" template image. Available in OS X v10.5 and later. */
	public static final NSImage GoLeftTemplate = get("GoLeftTemplate",
			"A \"go back\" template image.",
			"Available in OS X v10.5 and later.");

	/**
	 * A generic right-facing triangle template image. Available in OS X v10.5
	 * and later.
	 */
	public static final NSImage RightFacingTriangleTemplate = get(
			"RightFacingTriangleTemplate",
			"A generic right-facing triangle template image.",
			"Available in OS X v10.5 and later.");

	/**
	 * A generic left-facing triangle template image. Available in OS X v10.5
	 * and later.
	 */
	public static final NSImage LeftFacingTriangleTemplate = get(
			"LeftFacingTriangleTemplate",
			"A generic left-facing triangle template image.",
			"Available in OS X v10.5 and later.");

	/** An add item template image. Available in OS X v10.5 and later. */
	public static final NSImage AddTemplate = get("AddTemplate",
			"An add item template image.", "Available in OS X v10.5 and later.");

	/** A remove item template image. Available in OS X v10.5 and later. */
	public static final NSImage RemoveTemplate = get("RemoveTemplate",
			"A remove item template image.",
			"Available in OS X v10.5 and later.");

	/**
	 * A reveal contents template image. You can use this image to implement a
	 * borderless button. Available in OS X v10.5 and later.
	 */
	public static final NSImage RevealFreestandingTemplate = get(
			"RevealFreestandingTemplate",
			"A reveal contents template image. You can use this image to implement a borderless button.",
			"Available in OS X v10.5 and later.");

	/**
	 * A link template image. You can use this image to implement a borderless
	 * button. Available in OS X v10.5 and later.
	 */
	public static final NSImage FollowLinkFreestandingTemplate = get(
			"FollowLinkFreestandingTemplate",
			"A link template image. You can use this image to implement a borderless button.",
			"Available in OS X v10.5 and later.");

	/**
	 * An enter full-screen mode template image. Available in OS X v10.5 and
	 * later.
	 */
	public static final NSImage EnterFullScreenTemplate = get(
			"EnterFullScreenTemplate",
			"An enter full-screen mode template image.",
			"Available in OS X v10.5 and later.");

	/**
	 * An exit full-screen mode template image. Available in OS X v10.5 and
	 * later.
	 */
	public static final NSImage ExitFullScreenTemplate = get(
			"ExitFullScreenTemplate",
			"An exit full-screen mode template image.",
			"Available in OS X v10.5 and later.");

	/**
	 * A stop progress button template image. Available in OS X v10.5 and later.
	 */
	public static final NSImage StopProgressTemplate = get(
			"StopProgressTemplate", "A stop progress button template image.",
			"Available in OS X v10.5 and later.");

	/**
	 * A stop progress template image. You can use this image to implement a
	 * borderless button. Available in OS X v10.5 and later.
	 */
	public static final NSImage StopProgressFreestandingTemplate = get(
			"StopProgressFreestandingTemplate",
			"A stop progress template image. You can use this image to implement a borderless button.",
			"Available in OS X v10.5 and later.");

	/** A refresh template image. Available in OS X v10.5 and later. */
	public static final NSImage RefreshTemplate = get("RefreshTemplate",
			"A refresh template image.", "Available in OS X v10.5 and later.");

	/**
	 * A refresh template image. You can use this image to implement a
	 * borderless button. Available in OS X v10.5 and later.
	 */
	public static final NSImage RefreshFreestandingTemplate = get(
			"RefreshFreestandingTemplate",
			"A refresh template image. You can use this image to implement a borderless button.",
			"Available in OS X v10.5 and later.");

	/** A Bonjour icon. Available in OS X v10.5 and later. */
	public static final NSImage Bonjour = get("Bonjour", "A Bonjour icon.",
			"Available in OS X v10.5 and later.");

	/** A Dot Mac icon. Available in OS X v10.5 and later. */
	public static final NSImage DotMac = get("DotMac", "A Dot Mac icon.",
			"Available in OS X v10.5 and later.");

	/** A computer icon. Available in OS X v10.5 and later. */
	public static final NSImage Computer = get("Computer", "A computer icon.",
			"Available in OS X v10.5 and later.");

	/** A burnable folder icon. Available in OS X v10.5 and later. */
	public static final NSImage FolderBurnable = get("FolderBurnable",
			"A burnable folder icon.", "Available in OS X v10.5 and later.");

	/** A smart folder icon. Available in OS X v10.5 and later. */
	public static final NSImage FolderSmart = get("FolderSmart",
			"A smart folder icon.", "Available in OS X v10.5 and later.");

	/** A network icon. Available in OS X v10.5 and later. */
	public static final NSImage Network = get("Network", "A network icon.",
			"Available in OS X v10.5 and later.");

	/**
	 * User account toolbar icon. Use in a preferences window only.
	 */
	public static final NSImage UserAccounts = get("UserAccounts",
			"User account toolbar icon. Use in a preferences window only.",
			null);

	/**
	 * General preferences toolbar icon. Use in a preferences window only.
	 * Available in OS X v10.5 and later.
	 */
	public static final NSImage PreferencesGeneral = get(
			"PreferencesGeneral",
			"General preferences toolbar icon. Use in a preferences window only.",
			"Available in OS X v10.5 and later.");

	/**
	 * Advanced preferences toolbar icon. Use in a preferences window only.
	 * Available in OS X v10.5 and later.
	 */
	public static final NSImage Advanced = get("Advanced",
			"Advanced preferences toolbar icon for the preferences window.",
			"Available in OS X v10.5 and later.");

	/**
	 * Advanced preferences toolbar icon. Use in a preferences window only.
	 * Available in OS X v10.5 and later.
	 */
	public static final NSImage ApplicationIcon = get("ApplicationIcon",
			"The app's icon.", "Available in OS X v10.6 and later.");

	/** An information toolbar icon. Available in OS X v10.5 and later. */
	public static final NSImage Info = get("Info",
			"An information toolbar icon.",
			"Available in OS X v10.5 and later.");

	/** A font panel toolbar icon. Available in OS X v10.5 and later. */
	public static final NSImage FontPanel = get("FontPanel",
			"A font panel toolbar icon.", "Available in OS X v10.5 and later.");

	public static final NSImage GoBackTemplate = get("GoBackTemplate",
			"A \"Go Back\" template image.",
			"Available in OS X v10.12 and later.");

	public static final NSImage GoForwardTemplate = get("GoForwardTemplate",
			"A \"Go Forward\" template image.",
			"Available in OS X v10.12 and later.");

	/** A color panel toolbar icon. Available in OS X v10.5 and later. */
	public static final NSImage ColorPanel = get("ColorPanel",
			"A color panel toolbar icon.", "Available in OS X v10.5 and later.");

	/** Permissions for a single user. Available in OS X v10.5 and later. */
	public static final NSImage User = get("User",
			"Permissions for a single user.",
			"Available in OS X v10.5 and later.");

	/** Permissions for a group of users. Available in OS X v10.5 and later. */
	public static final NSImage UserGroup = get("UserGroup",
			"Permissions for a group of users.",
			"Available in OS X v10.5 and later.");

	/** Permissions for all users. Available in OS X v10.5 and later. */
	public static final NSImage Everyone = get("Everyone",
			"Permissions for all users.", "Available in OS X v10.5 and later.");

	/** Permissions for guests. Available in OS X v10.6 and later. */
	public static final NSImage UserGuest = get("UserGuest",
			"Permissions for guests.", "Available in OS X v10.6 and later.");

	/** A folder image. Available in OS X v10.6 and later. */
	public static final NSImage Folder = get("Folder", "A folder image.",
			"Available in OS X v10.6 and later.");

	/** An image of the empty trash can. Available in OS X v10.6 and later. */
	public static final NSImage TrashEmpty = get("TrashEmpty",
			"An image of the empty trash can.",
			"Available in OS X v10.6 and later.");

	/** An image of the full trash can. Available in OS X v10.6 and later. */
	public static final NSImage TrashFull = get("TrashFull",
			"An image of the full trash can.",
			"Available in OS X v10.6 and later.");

	/** Home image suitable for a template. Available in OS X v10.6 and later. */
	public static final NSImage HomeTemplate = get("HomeTemplate",
			"Home image suitable for a template.",
			"Available in OS X v10.6 and later.");

	/**
	 * Bookmarks image suitable for a template. Available in OS X v10.6 and
	 * later.
	 */
	public static final NSImage BookmarksTemplate = get("BookmarksTemplate",
			"Bookmarks image suitable for a template.",
			"Available in OS X v10.6 and later.");

	/** Caution Image. Available in OS X v10.6 and later. */
	public static final NSImage Caution = get("Caution", "A caution image.",
			"Available in OS X v10.6 and later.");

	/**
	 * Small green indicator, similar to iChat's available image. Available in
	 * OS X v10.6 and later.
	 */
	public static final NSImage StatusAvailable = get("StatusAvailable",
			"Small green indicator, similar to iChat's available image.",
			"Available in OS X v10.6 and later.");

	/**
	 * Small yellow indicator, similar to iChat's idle image. Available in OS X
	 * v10.6 and later.
	 */
	public static final NSImage StatusPartiallyAvailable = get(
			"StatusPartiallyAvailable",
			"Small yellow indicator, similar to iChat's idle image.",
			"Available in OS X v10.6 and later.");

	/**
	 * Small red indicator, similar to iChat's unavailable image. Available in
	 * OS X v10.6 and later.
	 */
	public static final NSImage StatusUnavailable = get("StatusUnavailable",
			"Small red indicator, similar to iChat's unavailable image.",
			"Available in OS X v10.6 and later.");

	/** Small clear indicator. Available in OS X v10.6 and later. */
	public static final NSImage StatusNone = get("StatusNone",
			"Small clear indicator.", "Available in OS X v10.6 and later.");

	/**
	 * A check mark. Drawing these outside of menus is discouraged. Available in
	 * OS X v10.6 and later.
	 */
	public static final NSImage MenuOnStateTemplate = get(
			"MenuOnStateTemplate",
			"A check mark. Drawing these outside of menus is discouraged.",
			"Available in OS X v10.6 and later.");

	/**
	 * A horizontal dash. Drawing these outside of menus is discouraged.
	 * Available in OS X v10.6 and later.
	 */
	public static final NSImage MenuMixedStateTemplate = get(
			"MenuMixedStateTemplate",
			"A horizontal dash. Drawing these outside of menus is discouraged.",
			"Available in OS X v10.6 and later.");

	/**
	 * MobileMe logo. Note that this is preferred to using the NSImageNameDotMac
	 * image, although that image is not expected to be deprecated. Available in
	 * OS X v10.6 and later.
	 */
	public static final NSImage MobileMe = get(
			"MobileMe",
			"MobileMe logo. Use of this image is discouraged; instead, use networkName.",
			"Available in OS X v10.6 and later.");

	public static final NSImage MultipleDocuments = get(
			"MultipleDocuments",
			"A drag image for multiple items. You can use this icon as the drag image when dragging multiple items. You should not use this image for any other intended purpose.",
			"Available in OS X v10.5 and later.");

	// these were all identified programmatically in NSImageDemo:

	public static final NSImage Accounts = get("Accounts");
	public static final NSImage Action = get("Action");
	public static final NSImage Add = get("Add");
	public static final NSImage Bluetooth = get("Bluetooth");
	public static final NSImage Color = get("Color");
	public static final NSImage Font = get("Font");
	public static final NSImage Group = get("Group");
	public static final NSImage Link = get("Link");
	public static final NSImage Path = get("Path");
	public static final NSImage Refresh = get("Refresh");
	public static final NSImage Remove = get("Remove");
	public static final NSImage Slideshow = get("Slideshow");
	public static final NSImage Stop = get("Stop");
	public static final NSImage Actions = get("Actions");
	public static final NSImage Bookmark = get("Bookmark");
	public static final NSImage Bookmarks = get("Bookmarks");
	public static final NSImage Bug = get("Bug");
	public static final NSImage Burning = get("Burning");
	public static final NSImage Cancel = get("Cancel");
	public static final NSImage Disclosed = get("Disclosed");
	public static final NSImage Effect = get("Effect");
	public static final NSImage Erase = get("Erase");
	public static final NSImage Home = get("Home");
	public static final NSImage Pause = get("Pause");
	public static final NSImage Person = get("Person");
	public static final NSImage Photograph = get("Photograph");
	public static final NSImage Play = get("Play");
	public static final NSImage Print = get("Print");
	public static final NSImage Reload = get("Reload");
	public static final NSImage Rewind = get("Rewind");
	public static final NSImage Script = get("Script");
	public static final NSImage Security = get("Security");
	public static final NSImage Share = get("Share");
	public static final NSImage Snapback = get("Snapback");
	public static final NSImage Switch = get("Switch");
	public static final NSImage AccountsTemplate = get("AccountsTemplate");
	public static final NSImage ActionsTemplate = get("ActionsTemplate");
	public static final NSImage AddBoomark = get("AddBookmark");
	public static final NSImage AdvancedPreferences = get("AdvancedPreferences");
	public static final NSImage AdvancedTemplate = get("AdvancedTemplate");
	public static final NSImage BonjourTemplate = get("BonjourTemplate");
	public static final NSImage BookmarkLock = get("BookmarkLock");
	public static final NSImage BookmarkTemplate = get("BookmarkTemplate");
	public static final NSImage BugTemplate = get("BugTemplate");
	public static final NSImage BurningTemplate = get("BurningTemplate");
	public static final NSImage CancelTemplate = get("CancelTemplate");
	public static final NSImage CautionTemplate = get("CautionTemplate");
	public static final NSImage ColorTemplate = get("ColorTemplate");
	public static final NSImage ColumnView = get("ColumnView");
	public static final NSImage ComputerTemplate = get("ComputerTemplate");
	public static final NSImage DisclosedTemplate = get("DisclosedTemplate");
	public static final NSImage DisclosedAlternate = get("DisclosedAlternate");
	public static final NSImage EffectTemplate = get("EffectTemplate");
	public static final NSImage EraseTemplate = get("EraseTemplate");
	public static final NSImage EveryoneTemplate = get("EveryoneTemplate");
	public static final NSImage FlowView = get("FlowView");
	public static final NSImage FolderTemplate = get("FolderTemplate");
	public static final NSImage FontTemplate = get("FontTemplate");
	public static final NSImage GeneralPreferences = get("GeneralPreferences");
	public static final NSImage GoLeft = get("GoLeft");
	public static final NSImage GoRight = get("GoRight");
	public static final NSImage GoBack = get("GoBack");
	public static final NSImage GoForward = get("GoForward");
	public static final NSImage GroupTemplate = get("GroupTemplate");
	public static final NSImage IconBurning = get("IconBurning");
	public static final NSImage IconComputer = get("IconComputer");
	public static final NSImage IconGroup = get("IconGroup");
	public static final NSImage IconLocked = get("IconLocked");
	public static final NSImage IconUnlocked = get("IconUnlocked");
	public static final NSImage IconUser = get("IconUser");
	public static final NSImage IconView = get("IconView");
	public static final NSImage IconClipboard = get("IconClipboard");
	public static final NSImage IconDesktop = get("IconDesktop");
	public static final NSImage IconFinder = get("IconFinder");
	public static final NSImage IconGrid = get("IconGrid");
	public static final NSImage IconHelp = get("IconHelp");
	public static final NSImage IconOwner = get("IconOwner");
	public static final NSImage IconShortcut = get("IconShortcut");
	public static final NSImage IconTrash = get("IconTrash");
	public static final NSImage InfoTemplate = get("InfoTemplate");
	public static final NSImage InvalidData = get("InvalidData");
	public static final NSImage LinkTemplate = get("LinkTemplate");
	public static final NSImage LinkButton = get("LinkButton");
	public static final NSImage ListView = get("ListView");
	public static final NSImage LockLocked = get("LockLocked");
	public static final NSImage LockUnlocked = get("LockUnlocked");
	public static final NSImage MultipleItems = get("MultipleItems");
	public static final NSImage NetworkTemplate = get("NetworkTemplate");
	public static final NSImage PathIndicator = get("PathIndicator");
	public static final NSImage PauseTemplate = get("PauseTemplate");
	public static final NSImage PersonTemplate = get("PersonTemplate");
	public static final NSImage PersonAnonymous = get("PersonAnonymous");
	public static final NSImage PersonUnknown = get("PersonUnknown");
	public static final NSImage PhotographTemplate = get("PhotographTemplate");
	public static final NSImage PlayTemplate = get("PlayTemplate");
	public static final NSImage PrintTemplate = get("PrintTemplate");
	public static final NSImage QuickLook = get("QuickLook");
	public static final NSImage RefreshFreestanding = get("RefreshFreestanding");
	public static final NSImage ReloadTemplate = get("ReloadTemplate");
	public static final NSImage RevealFreestanding = get("RevealFreestanding");
	public static final NSImage RewindTemplate = get("RewindTemplate");
	public static final NSImage ScriptTemplate = get("ScriptTemplate");
	public static final NSImage SecurityTemplate = get("SecurityTemplate");
	public static final NSImage SmartBadge = get("SmartBadge");
	public static final NSImage SnapbackTemplate = get("SnapbackTemplate");
	public static final NSImage StopProgress = get("StopProgress");
	public static final NSImage StopTemplate = get("StopTemplate");
	public static final NSImage SynchronizeTemplate = get("SynchronizeTemplate");
	public static final NSImage SynchronizeConflict = get("SynchronizeConflict");
	public static final NSImage SynchronizeStart = get("SynchronizeStart");
	public static final NSImage Synchronize = get("Synchronize");
	public static final NSImage TheaterStart = get("TheaterStart");
	public static final NSImage UserTemplate = get("UserTemplate");
	public static final NSImage ViewList = get("ViewList");
	public static final NSImage ViewColumns = get("ViewColumns");
	public static final NSImage ViewGroups = get("ViewGroups");
	public static final NSImage ViewIcons = get("ViewIcons");
	public static final NSImage KEXT = get("KEXT");

	private static NSImage get(String name, String description,
			String availability) {
		NSImage img = knownImages.get(name);
		if (img == null) {
			img = new NSImage(name, description, availability);
			knownImages.put(name, img);
		}
		return img;
	}

	/**
	 * Return a cached NSImage based on the name.
	 * 
	 * @param the
	 *            unique NSImage name, such as "Computer" or "TrashFull". This
	 *            class lists dozens of fields that include supported names, but
	 *            you can pass any value here. If you know of a special name (or
	 *            if one becomes available in future Mac OS releases) you can
	 *            request it here.
	 * 
	 * @return a cached NSImage based on the name.
	 */
	public static NSImage get(String name) {
		return get(name, null, null);
	}

	String name, description, availability;
	transient Image image;
	transient BufferedImage bufferedImage;

	private NSImage(String name, String description, String availability) {
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
		if (!(obj instanceof NSImage))
			return false;
		NSImage t = (NSImage) obj;
		return t.name.equals(name);
	}

	@Override
	public String toString() {
		if (description == null) {
			return "NSImage[ \"" + name + "\"]";
		}
		return "NSImage[ \"" + name + "\", \"" + description + "\", \""
				+ availability + "\"]";
	}

	/**
	 * Return a cached Image from the Toolkit.
	 * <p>
	 * This is equivalent to calling: <br>
	 * <code>Toolkit.getDefaultToolkit().getImage("NSImage://NS" + getName())</code>
	 */
	public synchronized Image getImage() {
		if (image == null) {
			String n = "NSImage://NS" + getName();
			image = Toolkit.getDefaultToolkit().getImage(n);
		}
		return image;
	}

	private static Class cImageClass;
	private static Field creatorField;
	private static Object creator;
	private static Method createImageFromNameMethod;

	/**
	 * This returns an Image that is scaled to a given size.
	 * <p>
	 * This uses reflection, so it may be more brittle in future JDKs.
	 * 
	 * @param maxConstrainingSize
	 *            the maximum image size to return. The image will be scaled
	 *            (proportionally) to fit inside these bounds.
	 */
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

	/**
	 * Return a cached BufferedImage based on {@link #getImage()}.
	 */
	public synchronized BufferedImage getBufferedImage() {
		if (bufferedImage == null) {
			bufferedImage = ImageLoader.createImage(getImage());
		}
		return bufferedImage;
	}

	/**
	 * Return a new BufferedImage based on {@link #getImage(Dimension)}
	 * 
	 * @param maxConstrainingSize
	 *            the maximum image size to return. The image will be scaled
	 *            (proportionally) to fit inside these bounds.
	 */
	public synchronized BufferedImage getBufferedImage(
			Dimension maxConstrainingSize) {
		return ImageLoader.createImage(getImage(maxConstrainingSize));
	}

	/**
	 * Return the unique name of this image, such as "Computer" or "TrashFull"
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return an optional String describing when this icon was first released,
	 * such as "Available in OS X v10.5 and later."
	 */
	public String getAvailability() {
		return availability;
	}

	/**
	 * Return an optional String describing this icon, such as
	 * "A Bluetooth template image."
	 */
	public String getDescription() {
		return description;
	}
}