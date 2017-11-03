package parser.goldbond;

import java.util.HashMap;
import java.util.Map;

public class GoldBondImprintMethodMapping {
	public static Map<String, String> IMPRINT_METHOD_MAP =new HashMap<String, String>();
	
	static{
		IMPRINT_METHOD_MAP.put("Screen printed","Silkscreen=Screen printed");
		IMPRINT_METHOD_MAP.put("Laser Engraved","Laser Engraved=Laser Engraved");
		IMPRINT_METHOD_MAP.put("Heat Transfer","Heat Transfer=Heat Transfer");
		IMPRINT_METHOD_MAP.put("Woven","Other=Woven");
		IMPRINT_METHOD_MAP.put("Screen Print","Silkscreen=Screen Print");
		IMPRINT_METHOD_MAP.put("Faux Etching","Etched=Faux Etching");
		IMPRINT_METHOD_MAP.put("4-color Process","Full Color=4-color Process");
		IMPRINT_METHOD_MAP.put("Pad Print","Pad Print=Pad Print");
		IMPRINT_METHOD_MAP.put("Pad printed","Pad Print=Pad printed");
		IMPRINT_METHOD_MAP.put("Debossed","Debossed");
		IMPRINT_METHOD_MAP.put("N","Other=N");
		IMPRINT_METHOD_MAP.put("A","Other=A");
		IMPRINT_METHOD_MAP.put("Full color imprint with protective dome","Full Color=Full color imprint with protective dome");
		IMPRINT_METHOD_MAP.put("Full color imprint with laminate","Full Color=Full color imprint with laminate");
		IMPRINT_METHOD_MAP.put("Screen/Pad printed","Pad Print=Screen/Pad printed");
		IMPRINT_METHOD_MAP.put("Embroidered","Embroidered");
		IMPRINT_METHOD_MAP.put("Hot Stamped","Hot Stamped");
		IMPRINT_METHOD_MAP.put("Dye sublimation","Sublimation");
		IMPRINT_METHOD_MAP.put("Full color","Full Color");
		IMPRINT_METHOD_MAP.put("Screen printed standard","Silkscreen=Screen printed standard");
		IMPRINT_METHOD_MAP.put("ColorfinityHD","Other=ColorfinityHD");
		IMPRINT_METHOD_MAP.put("Laser engraving","Laser Engraved=Laser engraving");
		IMPRINT_METHOD_MAP.put("Embroidery","Embroidered=Embroidery");
		IMPRINT_METHOD_MAP.put("Four color process print","Full Color=Four color process print");
		IMPRINT_METHOD_MAP.put("Pad Printing","Pad Print=Pad Printing");
		IMPRINT_METHOD_MAP.put("Printed","Printed=Printed");
		IMPRINT_METHOD_MAP.put("4-Color Process Print","Full Color=4-Color Process Print");
		IMPRINT_METHOD_MAP.put("Decal","Other=Decal");
		IMPRINT_METHOD_MAP.put("Offset 4-Color Process","Full Color=Offset 4-Color Process");
		IMPRINT_METHOD_MAP.put("Screen printed standard (Full color optional)","Silkscreen=Screen printed standard (Full color optional)");
		IMPRINT_METHOD_MAP.put("Etched appearance","Etched=Etched appearance");
		IMPRINT_METHOD_MAP.put("Full Color Heat Transfer","Heat Transfer=Full Color Heat Transfer");
		IMPRINT_METHOD_MAP.put("EZGRIP Full color imprint with protective dome","Full Color=EZGRIP Full color imprint with protective dome");
		IMPRINT_METHOD_MAP.put("Pad printed (standard)","Pad Print=Pad printed (standard)");
		IMPRINT_METHOD_MAP.put("Faux Laser","Laser Engraved=Faux Laser");
		IMPRINT_METHOD_MAP.put("Heat transfer on bag and Screen","Heat Transfer=Heat transfer on bag and Screen");
		IMPRINT_METHOD_MAP.put("Pad printed on balance","Pad Print=Pad printed on balance");
		IMPRINT_METHOD_MAP.put("Full color process printing","Full Color=Full color process printing");
		IMPRINT_METHOD_MAP.put("direct imprint","Printed=direct imprint");
		IMPRINT_METHOD_MAP.put("Laser engrave","Laser Engraved=Laser engrave");
		IMPRINT_METHOD_MAP.put("UV Screen Printed","Silkscreen=UV Screen Printed");
		IMPRINT_METHOD_MAP.put("CT","Other=CT");
		IMPRINT_METHOD_MAP.put("SW","Other=SW");
		IMPRINT_METHOD_MAP.put("NSS","Other=NSS");
		IMPRINT_METHOD_MAP.put("LUMA","Other=LUMA");
		IMPRINT_METHOD_MAP.put("BOULDER","Other=BOULDER");
		IMPRINT_METHOD_MAP.put("Full color imprint with protective dome on BHFE","Full Color=Full color imprint with protective dome on BHFE");
		IMPRINT_METHOD_MAP.put("1-color direct imprint","Printed=1-color direct imprint");
		IMPRINT_METHOD_MAP.put("4 color process label","Full Color=4 color process label");
		IMPRINT_METHOD_MAP.put("4 color process direct imprint","Full Color=4 color process direct imprint");
		IMPRINT_METHOD_MAP.put("EZGRIP: Full color imprint with protective dome","Full Color=EZGRIP: Full color imprint with protective dome");
		IMPRINT_METHOD_MAP.put("Screen","Silkscreen=Screen");
		IMPRINT_METHOD_MAP.put("Full-color Digital UV process","Full Color=Full-color Digital UV process");
		IMPRINT_METHOD_MAP.put("Silkscreen","Silkscreen=Silkscreen");
		IMPRINT_METHOD_MAP.put("Laser engraving; Optional: Pad print", "Laser Engraved=Laser engraving, Pad Print");
		IMPRINT_METHOD_MAP.put("Screen/Pad printed; Mints: 4-color process label, 4-color process direct imprint or 1-color direct imprint", "Silkscreen=Screen Print, Pad Print, Full Color");
		IMPRINT_METHOD_MAP.put("Screen/Pad printed. Trail Mix: 4-color process print label or 1-color direct imprint. Mints: 4-color process print label, 4-color direct imprint, 1-color direct imprint", "Silkscreen=Screen Print, Pad Print, Full Color");
		IMPRINT_METHOD_MAP.put("Screen print on ALB, ODIN, SW; 4-color process sublimation on 1033; Laser Engraving on ROLLO, IBOOST-EXEC; Full color imprint with protective dome on PVPTBH & PVPOKR", "Silkscreen=Screen Print, Full Color, Laser Engraved,Other=SW");
		IMPRINT_METHOD_MAP.put("Odin: 1-Color screen printed / Multi-color pad printed / Laser Engraving; EZGRIP: Full color imprint with protective dome", "Silkscreen=Screen Print, Full Color, Laser Engraved, Pad Print");
		IMPRINT_METHOD_MAP.put("Screen/Pad printed. Trail Mix: 4-color process print label or 1-color direct imprint", "Silkscreen=Screen Print, Pad Print, Full Color");
		IMPRINT_METHOD_MAP.put("Screen printed (standard); Faux Etching (optional); or Laser engraving (optional)", "Silkscreen=Screen Print, Etched=Faux Etching, Laser Engraved=Laser engraving");
		IMPRINT_METHOD_MAP.put("Full color stock imprint on SSPSKIT; Screen print on NS, WVP, SW", "Silkscreen=Screen Print, Full Color");
		IMPRINT_METHOD_MAP.put("Laser engraved standard on Black, Blue, and Red; Pad print on gold", "Laser Engraved, Pad Print");
		IMPRINT_METHOD_MAP.put("Screen print on SPIRIT, CT, KWS, F925, 1518PBR, FRUITFUSION28, FC-02", "Silkscreen=Screen Print,Other=CT");
		IMPRINT_METHOD_MAP.put("Pad printed on handle standard; laser engraved available on blade", "Laser Engraved, Pad Print");
		IMPRINT_METHOD_MAP.put("Laser engraving standard for colors (Pad printing standard on silver and white pens only)", " Laser Engraved, Pad Print");
		IMPRINT_METHOD_MAP.put("1-Color screen printed / Multi-color pad printed / Laser Engraving", "Silkscreen=Screen Print, Laser Engraved, Pad Print");
		IMPRINT_METHOD_MAP.put("Screen 1 color", "Silkscreen=Screen Print");
		IMPRINT_METHOD_MAP.put("Screen printed on case only", "Silkscreen=Screen Print");
		IMPRINT_METHOD_MAP.put("Screen printed. Full color dome imprint add $0.40 (G)", "Silkscreen=Screen Print, Full Color");
		IMPRINT_METHOD_MAP.put("Full color heat transfer with 30 day production, 1 or 2 color heat transfer with rush production", "Full Color, Heat Transfer");
		IMPRINT_METHOD_MAP.put("4-color process print (standard); Embroidery or Full Color Dome (optional)", "Full Color, Embroidered");
		IMPRINT_METHOD_MAP.put("Heat transfer on bag and Screen/Pad printed on balance", "Heat Transfer, Silkscreen=Screen Printed, Pad Print");
		IMPRINT_METHOD_MAP.put("Heat transfer on bag, Screen/Pad printed on tees, towel, power device charger, full color imprint on Pringles and mints", "Heat Transfer, Silkscreen=Screen Printed, Pad Print, Full Color");
		IMPRINT_METHOD_MAP.put("Screen printed (standard) / Faux Etching (optional)", "Silkscreen=Screen Print, Etched=Faux Etching");
		IMPRINT_METHOD_MAP.put("4-color process print label or 1-color direct imprint", "Full Color=4-Color Process,Other=Direct Imprint");
		IMPRINT_METHOD_MAP.put("UV Screen Printed", "Silkscreen=UV Screen Printed");
		IMPRINT_METHOD_MAP.put("Screen print on URBANDUFFLE, CT, SW, NSS, LUMA, BOULDER; Full color imprint with protective dome on BHFE", "Other=CT,Other=SW,Other=NSS,Other=LUMA,Other=BOULDER,Full Color =Full color imprint with protective dome on BHFE");
		IMPRINT_METHOD_MAP.put("Kong: 1-Color screen printed / Multi-color pad printed / Laser Engraving; EZGRIP: Full color imprint with protective dome", "Full Color=EZGRIP: Full color imprint with protective dome,Laser Engraved=Laser Engraving,Silkscreen=screen printed");
	}
	
	public static String getImprintMethodValues(String imprintMethodVal){
		return IMPRINT_METHOD_MAP.get(imprintMethodVal);
	}

}
