package com.pump.icon;

import java.awt.Dimension;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Icon;

import com.pump.awt.Dimension2D;

public class AquaIcon {
	public static final String SELECTOR_abdg = "abdg";
	public static final String SELECTOR_acct = "acct";
	public static final String SELECTOR_acts = "acts";
	public static final String SELECTOR_afps = "afps";
	public static final String SELECTOR_aird = "aird";
	public static final String SELECTOR_amnu = "amnu";
	public static final String SELECTOR_appe = "appe";
	public static final String SELECTOR_appr = "appr";
	public static final String SELECTOR_apps = "apps";
	public static final String SELECTOR_arng = "arng";
	public static final String SELECTOR_asnd = "asnd";
	public static final String SELECTOR_asup = "asup";
	public static final String SELECTOR_atlk = "atlk";
	public static final String SELECTOR_atzn = "atzn";
	public static final String SELECTOR_baro = "baro";
	public static final String SELECTOR_bfld = "bfld";
	public static final String SELECTOR_bmrk = "bmrk";
	public static final String SELECTOR_bonj = "bonj";
	public static final String SELECTOR_burn = "burn";
	public static final String SELECTOR_caff = "caff";
	public static final String SELECTOR_capl = "capl";
	public static final String SELECTOR_caut = "caut";
	public static final String SELECTOR_cbdg = "cddr";
	public static final String SELECTOR_cddr = "cdev";
	public static final String SELECTOR_clck = "clck";
	public static final String SELECTOR_clpp = "clps";
	public static final String SELECTOR_clpt = "clpt";
	public static final String SELECTOR_clpu = "clpu";
	public static final String SELECTOR_clsp = "clsp";
	public static final String SELECTOR_cmnu = "cmnu";
	public static final String SELECTOR_cnct = "cnct";
	public static final String SELECTOR_cshm = "cshm";
	public static final String SELECTOR_ctrl = "ctrl";
	public static final String SELECTOR_dali = "dali";
	public static final String SELECTOR_desk = "desk";
	public static final String SELECTOR_devf = "devf";
	public static final String SELECTOR_dfil = "dfil";
	public static final String SELECTOR_dict = "dict";
	public static final String SELECTOR_disk = "disk";
	public static final String SELECTOR_dist = "dist";
	public static final String SELECTOR_docs = "docs";
	public static final String SELECTOR_drfb = "drfb";
	public static final String SELECTOR_dsnd = "dsnd";
	public static final String SELECTOR_dsrv = "dsrv";
	public static final String SELECTOR_dvdr = "dvdr";
	public static final String SELECTOR_dvdw = "dvdw";
	public static final String SELECTOR_dwnf = "dwnf";
	public static final String SELECTOR_edsk = "edsk";
	public static final String SELECTOR_edtf = "edtf";
	public static final String SELECTOR_ejec = "ejec";
	public static final String SELECTOR_eras = "eras";
	public static final String SELECTOR_evry = "evry";
	public static final String SELECTOR_extn = "extn";
	public static final String SELECTOR_faro = "faro";
	public static final String SELECTOR_favr = "favr";
	public static final String SELECTOR_favs = "favs";
	public static final String SELECTOR_fldr = "fldr";
	public static final String SELECTOR_flpy = "flpy";
	public static final String SELECTOR_fold = "fold";
	public static final String SELECTOR_font = "font";
	public static final String SELECTOR_fslb = "fslb";
	public static final String SELECTOR_ftps = "ftps";
	public static final String SELECTOR_ftrh = "ftrh";
	public static final String SELECTOR_fvoc = "fvoc";
	public static final String SELECTOR_gfld = "gfld";
	public static final String SELECTOR_glas = "glas";
	public static final String SELECTOR_gmac = "gmac";
	public static final String SELECTOR_gnet = "gnet";
	public static final String SELECTOR_gnpc = "gnpc";
	public static final String SELECTOR_gnrl = "gnrl";
	public static final String SELECTOR_grid = "grid";
	public static final String SELECTOR_grup = "grup";
	public static final String SELECTOR_gtmi = "gtmi";
	public static final String SELECTOR_gurl = "gurl";
	public static final String SELECTOR_gusr = "gusr";
	public static final String SELECTOR_gwin = "gwin";
	public static final String SELECTOR_hdsk = "hdsk";
	public static final String SELECTOR_help = "help";
	public static final String SELECTOR_htgf = "htgf";
	public static final String SELECTOR_htps = "htps";
	public static final String SELECTOR_idsk = "idsk";
	public static final String SELECTOR_ilvn = "ilvn";
	public static final String SELECTOR_immp = "immp";
	public static final String SELECTOR_ipdt = "ipdt";
	public static final String SELECTOR_iphn = "iphn";
	public static final String SELECTOR_isrv = "isrv";
	public static final String SELECTOR_issf = "issf";
	public static final String SELECTOR_itun = "itun";
	public static final String SELECTOR_jipd = "jipd";
	public static final String SELECTOR_jxjt = "jxjt";
	public static final String SELECTOR_kmej = "kmej";
	public static final String SELECTOR_layz = "layz";
	public static final String SELECTOR_lbdg = "lbdg";
	public static final String SELECTOR_lock = "lock";
	public static final String SELECTOR_macn = "macn";
	public static final String SELECTOR_macs = "macs";
	public static final String SELECTOR_mbdg = "mbdg";
	public static final String SELECTOR_mitm = "mitm";
	public static final String SELECTOR_mntd = "mntd";
	public static final String SELECTOR_mpkg = "mpkg";
	public static final String SELECTOR_mymc = "mymc";
	public static final String SELECTOR_nfbg = "nfbg";
	public static final String SELECTOR_nfil = "nfil";
	public static final String SELECTOR_nfld = "nfld";
	public static final String SELECTOR_nldd = "nldd";
	public static final String SELECTOR_note = "note";
	public static final String SELECTOR_nwrt = "nwrt";
	public static final String SELECTOR_nyrc = "nyrc";
	public static final String SELECTOR_ofld = "ofld";
	public static final String SELECTOR_osas = "osas";
	public static final String SELECTOR_osax = "osax";
	public static final String SELECTOR_ownd = "ownd";
	public static final String SELECTOR_papp = "papp";
	public static final String SELECTOR_pbcl = "pbcl";
	public static final String SELECTOR_pfcl = "pfcl";
	public static final String SELECTOR_pfnt = "pfnt";
	public static final String SELECTOR_plug = "plug";
	public static final String SELECTOR_ppdf = "ppdf";
	public static final String SELECTOR_pref = "pref";
	public static final String SELECTOR_prfb = "prfb";
	public static final String SELECTOR_prnt = "prnt";
	public static final String SELECTOR_prof = "prof";
	public static final String SELECTOR_psys = "psys";
	public static final String SELECTOR_pubf = "pubf";
	public static final String SELECTOR_qivv = "qivv";
	public static final String SELECTOR_ques = "ques";
	public static final String SELECTOR_ramd = "ramd";
	public static final String SELECTOR_rapp = "rapp";
	public static final String SELECTOR_rcar = "rcar";
	public static final String SELECTOR_rcnt = "rcnt";
	public static final String SELECTOR_rdoc = "cdoc";
	public static final String SELECTOR_rmov = "rmov";
	public static final String SELECTOR_rofb = "rofb";
	public static final String SELECTOR_root = "root";
	public static final String SELECTOR_rsrv = "rsrv";
	public static final String SELECTOR_sapl = "sapl";
	public static final String SELECTOR_sapp = "sapp";
	public static final String SELECTOR_sbar = "sbar";
	public static final String SELECTOR_sbdg = "sbdg";
	public static final String SELECTOR_scmp = "scmp";
	public static final String SELECTOR_scrp = "scrp";
	public static final String SELECTOR_scsh = "scsh";
	public static final String SELECTOR_sdbp = "sdbp";
	public static final String SELECTOR_sdoc = "sdoc";
	public static final String SELECTOR_sfar = "sfar";
	public static final String SELECTOR_sfld = "sfld";
	public static final String SELECTOR_shdf = "shdf";
	public static final String SELECTOR_shfl = "shfl";
	public static final String SELECTOR_shlb = "shlb";
	public static final String SELECTOR_shna = "shna";
	public static final String SELECTOR_shpt = "shpt";
	public static final String SELECTOR_shro = "shro";
	public static final String SELECTOR_shrt = "shrt";
	public static final String SELECTOR_shrw = "shrw";
	public static final String SELECTOR_shuk = "shuk";
	public static final String SELECTOR_spki = "spki";
	public static final String SELECTOR_srvr = "srvr";
	public static final String SELECTOR_stop = "stop";
	public static final String SELECTOR_strt = "strt";
	public static final String SELECTOR_susr = "susr";
	public static final String SELECTOR_svgz = "svgz";
	public static final String SELECTOR_sync = "sync";
	public static final String SELECTOR_tbav = "tbav";
	public static final String SELECTOR_tbin = "tbin";
	public static final String SELECTOR_tblb = "tblb";
	public static final String SELECTOR_tcus = "tcus";
	public static final String SELECTOR_tdel = "tdel";
	public static final String SELECTOR_tfav = "tfav";
	public static final String SELECTOR_thom = "thom";
	public static final String SELECTOR_trsh = "trsh";
	public static final String SELECTOR_ttro = "ttro";
	public static final String SELECTOR_ubdg = "ubdg";
	public static final String SELECTOR_udsk = "udsk";
	public static final String SELECTOR_ufld = "ufld";
	public static final String SELECTOR_ulck = "ulck";
	public static final String SELECTOR_unfs = "unfs";
	public static final String SELECTOR_unus = "unus";
	public static final String SELECTOR_user = "user";
	public static final String SELECTOR_utxt = "utxt";
	public static final String SELECTOR_visj = "visj";
	public static final String SELECTOR_vxmz = "vxmz";
	public static final String SELECTOR_wfld = "wfld";
	public static final String SELECTOR_writ = "writ";

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

	public static Icon getAquaIcon(String selector,
			Dimension maxConstrainingSize) {
		initialize();

		try {
			Icon icon = (Icon) systemIconConstructor.newInstance(selector);
			if (maxConstrainingSize != null) {
				Dimension d = new Dimension(icon.getIconWidth(),
						icon.getIconHeight());
				Dimension newSize = Dimension2D.scaleProportionally(d,
						maxConstrainingSize);
				icon = new ScaledIcon(icon, newSize.width, newSize.height);
			}
			return icon;
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
