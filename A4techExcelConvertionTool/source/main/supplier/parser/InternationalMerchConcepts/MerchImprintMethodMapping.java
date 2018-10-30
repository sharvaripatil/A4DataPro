package parser.InternationalMerchConcepts;

import java.util.HashMap;
import java.util.Map;

public class MerchImprintMethodMapping {
public static Map<String, String> IMPRINT_METHOD_MAP =new HashMap<String, String>();
	
	static{
		IMPRINT_METHOD_MAP.put("Supplier value","group");
		IMPRINT_METHOD_MAP.put("Laser","Laser Engraved=Laser");
		IMPRINT_METHOD_MAP.put("Screen","Silkscreen=Screen");
		IMPRINT_METHOD_MAP.put("Heat Transfer","Heat Transfer=Heat Transfer");
		IMPRINT_METHOD_MAP.put("Laser in Gold ","Laser Engraved=Laser in Gold");
		IMPRINT_METHOD_MAP.put("Laser in Silver-chrome Etch","Laser Engraved=Laser in Silver-chrome Etch");
		IMPRINT_METHOD_MAP.put("Laser in Silver","Laser Engraved=Laser in Silver");
		IMPRINT_METHOD_MAP.put("Laser in matte Silver","Laser Engraved=Laser");
		IMPRINT_METHOD_MAP.put("Lasers in Gold","Laser Engraved=Lasers in Gold");
		IMPRINT_METHOD_MAP.put("Laser in Matte Silver","Laser Engraved=Laser");
		IMPRINT_METHOD_MAP.put("Sandblast Etch","Etched=Sandblast Etch");
		IMPRINT_METHOD_MAP.put("Reverse Etch","Etched=Reverse Etch");
		IMPRINT_METHOD_MAP.put("Sandblast Etch (Image shows 2 Sandblasting Etch Works)","Etched=Sandblast Etch");
		IMPRINT_METHOD_MAP.put("Laser Etch","Etched=Laser Etch");
		IMPRINT_METHOD_MAP.put("Laser on Base","Laser Engraved=Laser on Base");
		IMPRINT_METHOD_MAP.put("Sandblast","Other=Sandblast");
		IMPRINT_METHOD_MAP.put("Digital Imprint","Printed=Digital Imprint");
		IMPRINT_METHOD_MAP.put("Screen (on handle)","Silkscreen=Screen");
		IMPRINT_METHOD_MAP.put("Screen (on pouch)","Silkscreen=Screen");
		IMPRINT_METHOD_MAP.put("Laser engraved with oxidation","Laser Engraved=Laser engraved with oxidation");
		IMPRINT_METHOD_MAP.put("Laser with oxidation","Laser Engraved=Laser with oxidation");
		IMPRINT_METHOD_MAP.put("Laser in gold","Laser Engraved=Laser in gold");
		IMPRINT_METHOD_MAP.put("oxidation suggested","Other=oxidation suggested");
		IMPRINT_METHOD_MAP.put("Screen (Notepad holder only)","Silkscreen=Screen - Notepad holder only");
		IMPRINT_METHOD_MAP.put("Screen (one location pen or box)","Silkscreen=Screen - (one location, pen or box)");
		IMPRINT_METHOD_MAP.put("Lasers in Silver","Laser Engraved=Lasers in Silver");
		IMPRINT_METHOD_MAP.put("one color logo only","Silkscreen=Screen - one color logo only");
		IMPRINT_METHOD_MAP.put("Screen.","Silkscreen=Screen");
		IMPRINT_METHOD_MAP.put("Full Color Epoxy Dome","Full Color=Full Color Epoxy Dome");
		IMPRINT_METHOD_MAP.put("Label","Printed=Label");
		IMPRINT_METHOD_MAP.put("Deep Laser  (on Back)","Laser Engraved=Deep Laser (on Back)");
		IMPRINT_METHOD_MAP.put("Laser  (on Back)","Laser Engraved=Laser  (on Back)");
		IMPRINT_METHOD_MAP.put("Laser (on Back)","Laser Engraved=Laser (on Back)");
		IMPRINT_METHOD_MAP.put("Laser  (on Front)","Laser Engraved=Laser  (on Front)");
		IMPRINT_METHOD_MAP.put("Screen 1-Color","Silkscreen=Screen 1-Color");
		IMPRINT_METHOD_MAP.put("Full Color Epoxy DomePoly Dome","Full Color=Full Color Epoxy DomePoly Dome");
		IMPRINT_METHOD_MAP.put("Digital Color Insert","Printed=Digital Color Insert");
		IMPRINT_METHOD_MAP.put("Laser Polish*","Laser Engraved=Laser Polish");
		IMPRINT_METHOD_MAP.put("Deep Laser","Laser Engraved=Deep Laser");
		IMPRINT_METHOD_MAP.put("Laser (Back )","Laser Engraved=Laser  (Back)");
		IMPRINT_METHOD_MAP.put("Sticker","Other=Sticker");
		IMPRINT_METHOD_MAP.put("Screen (pen only)","Silkscreen=Screen-pen only");
		IMPRINT_METHOD_MAP.put("Deboss","Debossed=Deboss");
		IMPRINT_METHOD_MAP.put("Screen (1-color only)","Silkscreen=Screen (1-color only)");
		IMPRINT_METHOD_MAP.put("Screen (black only)","Silkscreen=Screen (black only)");
		IMPRINT_METHOD_MAP.put("Laser (in gold)","Laser Engraved=Laser (in gold)");
		IMPRINT_METHOD_MAP.put("Embroidery (Not Included in Pricing)","Embroidered=Embroidery");
		IMPRINT_METHOD_MAP.put("Pen: Laser, Screen  Keychain: Laser","Laser Engraved=Laser,Silkscreen=Screen");
		IMPRINT_METHOD_MAP.put("Screen  (One Side)","Silkscreen=Screen  (One Side)");
		IMPRINT_METHOD_MAP.put("Pen: Laser, Screen. Laser on engraving plate also available. Setup & run charges apply.","Laser Engraved=Laser,Silkscreen=Screen");
		IMPRINT_METHOD_MAP.put("Laser on clip or engraving plate, screen on gift box. Setup & run charges apply.","Laser Engraved=Laser,Silkscreen=Screen");
		IMPRINT_METHOD_MAP.put("Laser on Base included, Sandblast Etch on Globe (additional charges apply)","Laser Engraved=Laser,Etched=Sandblast Etch");
		IMPRINT_METHOD_MAP.put("Screen (Sold Unimprinted)","Silkscreen=Screen");
		IMPRINT_METHOD_MAP.put("Laser Polish (Add $.56(G) Per Unit)","Laser Engraved=Laser Polish");
		       
	}
	public static String getImprintMethodGroup(String imprintMethodValue){
		return IMPRINT_METHOD_MAP.get(imprintMethodValue);
	}
}
