package com.a4tech.core.excelMapping;

import com.a4tech.excel.service.IExcelParser;
import com.a4tech.supplier.mapper.AccessLineMapping;
import com.a4tech.supplier.mapper.AdspecProductsExcelMapping;
import com.a4tech.supplier.mapper.AlfaMapping;
import com.a4tech.supplier.mapper.ApparelProductsExcelMapping;
import com.a4tech.supplier.mapper.BagMakersMapping;
import com.a4tech.supplier.mapper.BallProMapping;
import com.a4tech.supplier.mapper.BambamProductExcelMapping;
import com.a4tech.supplier.mapper.BayStateMapping;
import com.a4tech.supplier.mapper.BellaCanvas;
import com.a4tech.supplier.mapper.BestDealProductsExcelMapping;
import com.a4tech.supplier.mapper.BloominPromotionsMapper;
import com.a4tech.supplier.mapper.BlueGenerationMapping;
import com.a4tech.supplier.mapper.BrandwearExcelMapping;
import com.a4tech.supplier.mapper.BroberryExcelMapping;
import com.a4tech.supplier.mapper.CbMapping;
import com.a4tech.supplier.mapper.CrystalDExcelMapping;
//import com.a4tech.supplier.mapper.CutterBuckExcelMapping;
import com.a4tech.supplier.mapper.DacassoMapping;
import com.a4tech.supplier.mapper.DigiSpecMapping;
import com.a4tech.supplier.mapper.DouglasBridgeMapper;
import com.a4tech.supplier.mapper.EdwardsGarmentMapping;
import com.a4tech.supplier.mapper.EveanManufacturingCanadaMapping;
import com.a4tech.supplier.mapper.FITSAccessoriesMapping;
import com.a4tech.supplier.mapper.GempirepromotionsMapping;
import com.a4tech.supplier.mapper.GillStudiosMapping;
import com.a4tech.supplier.mapper.GoldBondExcelMapping;
import com.a4tech.supplier.mapper.GoldstarCanadaExcelMapping;
import com.a4tech.supplier.mapper.HarvestIndustrialExcelMapping;
import com.a4tech.supplier.mapper.HeadWearMapping;
import com.a4tech.supplier.mapper.HighCaliberLineMappingRevised;
import com.a4tech.supplier.mapper.InternationlMerchMapping;
import com.a4tech.supplier.mapper.KlProductsExcelMapping;
import com.a4tech.supplier.mapper.MaxplusMapping;
import com.a4tech.supplier.mapper.MilestoneExcelMapping;
import com.a4tech.supplier.mapper.NewProductsExcelMapping;
import com.a4tech.supplier.mapper.PSLMapping;
import com.a4tech.supplier.mapper.PSLcadMapping;
import com.a4tech.supplier.mapper.PelicanGraphicMapping;
import com.a4tech.supplier.mapper.PioneerLLCMapping;
import com.a4tech.supplier.mapper.PrimeLineExcelMapping;
import com.a4tech.supplier.mapper.ProGolfMapping;
import com.a4tech.supplier.mapper.RFGLineProductExcelMapping;
import com.a4tech.supplier.mapper.RadiousMapping;
import com.a4tech.supplier.mapper.RiversEndExcelMapping;
import com.a4tech.supplier.mapper.SageRMKWorldWideMapping;
import com.a4tech.supplier.mapper.SimplifiedsourcingMapping;
import com.a4tech.supplier.mapper.SolidDimensionMapping;
import com.a4tech.supplier.mapper.SportAzxCandMapping;
import com.a4tech.supplier.mapper.SportCanadaExcelMapping;
import com.a4tech.supplier.mapper.SportUSAMapping;
import com.a4tech.supplier.mapper.SportsManBagMapping;
import com.a4tech.supplier.mapper.SunGraphixRevisedMapping;
import com.a4tech.supplier.mapper.SunScopeMapping;
import com.a4tech.supplier.mapper.TeamworkAthleticMapping;
import com.a4tech.supplier.mapper.TekweldMapping;
import com.a4tech.supplier.mapper.TomaxUsaMapping;
import com.a4tech.supplier.mapper.TowelSpecialtiesMapping;
import com.a4tech.supplier.mapper.TwintechMapping;
import com.a4tech.supplier.mapper.WBTIndustriesMapper;
import com.a4tech.supplier.mapper.WholeSaleExcelMapping;
import com.a4tech.supplier.mapper.ZenithExport;

public class SupplierFactory {
	
	private AdspecProductsExcelMapping adspecMapping;
	private KlProductsExcelMapping klMapping;
	private RFGLineProductExcelMapping rfgLineProductExcelMapping;
	private NewProductsExcelMapping newProductsExcelMapping;
	private ApparelProductsExcelMapping apparealExcelMapping;
	private BroberryExcelMapping broberryExcelMapping;
	private BestDealProductsExcelMapping bdProdcutsMapping;
	private RiversEndExcelMapping riversEndExcelMapping;
	private BambamProductExcelMapping bamExcelMapping;
	// private CutterBuckExcelMapping cbExcelMapping;
	private CrystalDExcelMapping cdExcelMapping;
	private GoldstarCanadaExcelMapping goldcanadaExcelMapping;
	private MilestoneExcelMapping milestoneExcelMapping;
	// private HighCaliberLineExcelMapping hcLineExcelMapping;
	private WholeSaleExcelMapping wholeSaleExcelMapping;
	private PrimeLineExcelMapping primeLineExcelMapping;
	private GoldBondExcelMapping goldBandExcelMapping;
	private PSLMapping pslMapping;
	private TomaxUsaMapping tomaxUsaMapping;
	private ProGolfMapping proGolfMapping;
	private DacassoMapping dacassoMapping;
	private SageRMKWorldWideMapping sage80289Mapping;
	private BrandwearExcelMapping brandwearExcelMapping;
	private BallProMapping ballProMapping;
	private PSLcadMapping pslcadMapping;
	private TwintechMapping twintechMapping;
	private AlfaMapping alfaMapping;
	private BellaCanvas bellaCanvasMapping;
	private HarvestIndustrialExcelMapping harvestMapping;
	private HighCaliberLineMappingRevised hcLineExcelMapping;
	private TowelSpecialtiesMapping towelSpecialties;
	private BagMakersMapping bagMakersMapping;
	private GillStudiosMapping gillStudiosMapping;
	private BlueGenerationMapping blueGenerationMapping;
	private FITSAccessoriesMapping fitsAccessoriesMapping;
	private BayStateMapping baysStateMapping;
	private MaxplusMapping maxplusmapping;
	private GempirepromotionsMapping gempiresMapping;
	private EdwardsGarmentMapping edwardsGarmentMapping;
	private TekweldMapping tekweldMapping;
	private InternationlMerchMapping merchMapping;
	private HeadWearMapping headWearMapping;
	private SportCanadaExcelMapping sportMapping;
	private SunScopeMapping sunScopeMapping;
	// private SunGraphixMapping sunGraphixMapping;
	private SunGraphixRevisedMapping sunGraphixMapping;
	private CbMapping cbExeMapping;
	private PelicanGraphicMapping pelicanGraphicMapping;
	private SportsManBagMapping sportsManBagMapping;
	private SimplifiedsourcingMapping simplifiedMapping;
	private SolidDimensionMapping solidDimensionMapping;
	private EveanManufacturingCanadaMapping eveanManufactureMapping;
	private SportUSAMapping sportUSAMapping;
	private SportAzxCandMapping sportAzxCandMapping;
	private DigiSpecMapping digiSpecMapping;
	private DouglasBridgeMapper douglasBridgeMapper;
	private RadiousMapping radiMapping;
	private WBTIndustriesMapper wbtIndustriesMapper;
	private ZenithExport zenithMapping;
	private BloominPromotionsMapper bloominPromotion;
	private PioneerLLCMapping pioneerLLCMapping;
	private TeamworkAthleticMapping teamWorkAthleticMapper;
	private AccessLineMapping accessLineMapping;

	public IExcelParser getExcelParserObject(String asiNumber) {

		if (asiNumber.equals("44620")) {
			return apparealExcelMapping;
		} else if (asiNumber.equals("64905")) {
			return klMapping;
		} else if (asiNumber.equals("32125")) {
			return adspecMapping;
		} else if (asiNumber.equals("82283")) {
			return rfgLineProductExcelMapping;
		} else if (asiNumber.equals("42057")) {
			return broberryExcelMapping;
		} else if (asiNumber.equals("47791")) {
			return bdProdcutsMapping;
		} else if (asiNumber.equals("82588")) {
			return riversEndExcelMapping;
		} else if (asiNumber.equals("47965")) {
			return cbExeMapping/* cbExcelMapping */;// old file
		} else if (asiNumber.equals("47759")) {
			return cdExcelMapping;
		} else if (asiNumber.equals("38228")) {
			return bamExcelMapping;
		} else if (asiNumber.equals("73295")) {
			return goldcanadaExcelMapping;
		} else if (asiNumber.equals("43442")) {
			return hcLineExcelMapping;
		} else if (asiNumber.equals("91284")) {
			return wholeSaleExcelMapping;
		} else if (asiNumber.equals("57653")) {
			return goldBandExcelMapping;
		} else if (asiNumber.equals("79530")) {
			return primeLineExcelMapping;
		} else if (asiNumber.equals("75613")) {
			return pslMapping;
		} else if (asiNumber.equals("71173")) {
			return milestoneExcelMapping;
		} else if (asiNumber.equals("79680")) {
			return proGolfMapping;
		} else if (asiNumber.equals("48125")) {
			return dacassoMapping;
		} else if (asiNumber.equals("91435")) {
			return tomaxUsaMapping;
		} else if (asiNumber.equals("80289")) {
			return sage80289Mapping;
		} else if (asiNumber.equals("41545")) {
			return brandwearExcelMapping;
		} else if (asiNumber.equals("38120")) {
			return ballProMapping;
		} else if (asiNumber.equals("90345")) {
			return pslcadMapping;
		} else if (asiNumber.equals("83140")) {/// *"92357"*/
			return twintechMapping;
		} else if (asiNumber.equals("34042")) {
			return alfaMapping;
		} else if (asiNumber.equals("39590")) {
			return bellaCanvasMapping;
		} else if (asiNumber.equals("61670") || asiNumber.equals("71685") || asiNumber.equals("91584")) {
			return harvestMapping;// TotesFactory
		} else if (asiNumber.equals("91605")) {
			return towelSpecialties;
		} else if (asiNumber.equals("37940")) {
			return bagMakersMapping;
		} else if (asiNumber.equals("56950")) {
			return gillStudiosMapping;
		} else if (asiNumber.equals("40653")) {
			return blueGenerationMapping;
		} else if (asiNumber.equals("71107")) {
			return fitsAccessoriesMapping;
		} else if (asiNumber.equals("55610")) {
			return gempiresMapping;
		} else if (asiNumber.equals("38980") || asiNumber.equals("35730")) {
			return baysStateMapping;
		} else if (asiNumber.equals("69718")) {
			return maxplusmapping;
		} else if (asiNumber.equals("51752")) {
			return edwardsGarmentMapping;
		} else if (asiNumber.equals("90807")) {
			return tekweldMapping;
		} else if (asiNumber.equals("62820")) {
			return merchMapping;
		} else if (asiNumber.equals("60282")) {
			return headWearMapping;
		} else if (asiNumber.equals("30251")) {
			return sportAzxCandMapping;
		} else if (asiNumber.equals("90075")) {
			return sunScopeMapping;
		} else if (asiNumber.equals("90125")) {
			return sunGraphixMapping;
		} else if (asiNumber.equals("76797")) {
			return pelicanGraphicMapping;
		} else if (asiNumber.equals("88877")) {
			return sportsManBagMapping;
		} else if (asiNumber.equals("87326") || asiNumber.equals("91597")) {
			return simplifiedMapping;
		} else if (asiNumber.equals("88156")) {
			return solidDimensionMapping;
		} else if (asiNumber.equals("52841")) {
			return eveanManufactureMapping;
		} else if (asiNumber.equals("30250")) {
			return sportUSAMapping;
		} else if (asiNumber.equals("49716")) {
			return digiSpecMapping;
		} else if (asiNumber.equals("49916")) {
			return radiMapping;
		} else if (asiNumber.equals("50710")) {
			return douglasBridgeMapper;
		} else if (asiNumber.equals("96640")) {
			return wbtIndustriesMapper;
		} else if (asiNumber.equals("79840")) {
			return zenithMapping;
		} else if (asiNumber.equals("40646")) {
			return bloominPromotion;
		} else if (asiNumber.equals("76771")) {
			return pioneerLLCMapping;
		} else if (asiNumber.equals("90673")) {
			return teamWorkAthleticMapper;
		} else if (asiNumber.equals("30458")) {
			return accessLineMapping;
		}
		return null;
	}

	public void setBloominPromotion(BloominPromotionsMapper bloominPromotion) {
		this.bloominPromotion = bloominPromotion;
	}

	public InternationlMerchMapping getMerchMapping() {
		return merchMapping;
	}

	public void setMerchMapping(InternationlMerchMapping merchMapping) {
		this.merchMapping = merchMapping;
	}

	public AdspecProductsExcelMapping getAdspecMapping() {
		return adspecMapping;
	}

	public void setAdspecMapping(AdspecProductsExcelMapping adspecMapping) {
		this.adspecMapping = adspecMapping;
	}

	public KlProductsExcelMapping getKlMapping() {
		return klMapping;
	}

	public void setKlMapping(KlProductsExcelMapping klMapping) {
		this.klMapping = klMapping;
	}

	public RFGLineProductExcelMapping getRfgLineProductExcelMapping() {
		return rfgLineProductExcelMapping;
	}

	public void setRfgLineProductExcelMapping(RFGLineProductExcelMapping rfgLineProductExcelMapping) {
		this.rfgLineProductExcelMapping = rfgLineProductExcelMapping;
	}

	public NewProductsExcelMapping getNewProductsExcelMapping() {
		return newProductsExcelMapping;
	}

	public void setNewProductsExcelMapping(NewProductsExcelMapping newProductsExcelMapping) {
		this.newProductsExcelMapping = newProductsExcelMapping;
	}

	public ApparelProductsExcelMapping getApparealExcelMapping() {
		return apparealExcelMapping;
	}

	public void setApparealExcelMapping(ApparelProductsExcelMapping apparealExcelMapping) {
		this.apparealExcelMapping = apparealExcelMapping;
	}

	public BroberryExcelMapping getBroberryExcelMapping() {
		return broberryExcelMapping;
	}

	public void setBroberryExcelMapping(BroberryExcelMapping broberryExcelMapping) {
		this.broberryExcelMapping = broberryExcelMapping;
	}

	public BestDealProductsExcelMapping getBdProdcutsMapping() {
		return bdProdcutsMapping;
	}

	public void setBdProdcutsMapping(BestDealProductsExcelMapping bdProdcutsMapping) {
		this.bdProdcutsMapping = bdProdcutsMapping;
	}

	public RiversEndExcelMapping getRiversEndExcelMapping() {
		return riversEndExcelMapping;
	}

	public void setRiversEndExcelMapping(RiversEndExcelMapping riversEndExcelMapping) {
		this.riversEndExcelMapping = riversEndExcelMapping;
	}

	public BambamProductExcelMapping getBamExcelMapping() {
		return bamExcelMapping;
	}

	public void setBamExcelMapping(BambamProductExcelMapping bamExcelMapping) {
		this.bamExcelMapping = bamExcelMapping;
	}

	/*
	 * public CutterBuckExcelMapping getCbExcelMapping() { return cbExcelMapping; }
	 * 
	 * public void setCbExcelMapping(CutterBuckExcelMapping cbExcelMapping) {
	 * this.cbExcelMapping = cbExcelMapping; }
	 */

	public CrystalDExcelMapping getCdExcelMapping() {
		return cdExcelMapping;
	}

	public void setCdExcelMapping(CrystalDExcelMapping cdExcelMapping) {
		this.cdExcelMapping = cdExcelMapping;
	}

	public GoldstarCanadaExcelMapping getGoldcanadaExcelMapping() {
		return goldcanadaExcelMapping;
	}

	public void setGoldcanadaExcelMapping(GoldstarCanadaExcelMapping goldcanadaExcelMapping) {
		this.goldcanadaExcelMapping = goldcanadaExcelMapping;
	}

	public HighCaliberLineMappingRevised getHcLineExcelMapping() {
		return hcLineExcelMapping;
	}

	public void setHcLineExcelMapping(HighCaliberLineMappingRevised hcLineExcelMapping) {
		this.hcLineExcelMapping = hcLineExcelMapping;

	}

	public WholeSaleExcelMapping getWholeSaleExcelMapping() {
		return wholeSaleExcelMapping;
	}

	public void setWholeSaleExcelMapping(WholeSaleExcelMapping wholeSaleExcelMapping) {
		this.wholeSaleExcelMapping = wholeSaleExcelMapping;
	}

	public PrimeLineExcelMapping getPrimeLineExcelMapping() {
		return primeLineExcelMapping;
	}

	public void setPrimeLineExcelMapping(PrimeLineExcelMapping primeLineExcelMapping) {
		this.primeLineExcelMapping = primeLineExcelMapping;
	}

	public MilestoneExcelMapping getMilestoneExcelMapping() {
		return milestoneExcelMapping;
	}

	public void setMilestoneExcelMapping(MilestoneExcelMapping milestoneExcelMapping) {
		this.milestoneExcelMapping = milestoneExcelMapping;
	}

	public GoldBondExcelMapping getGoldBandExcelMapping() {
		return goldBandExcelMapping;
	}

	public void setGoldBandExcelMapping(GoldBondExcelMapping goldBandExcelMapping) {
		this.goldBandExcelMapping = goldBandExcelMapping;
	}

	public ProGolfMapping getProGolfMapping() {
		return proGolfMapping;
	}

	public void setProGolfMapping(ProGolfMapping proGolfMapping) {
		this.proGolfMapping = proGolfMapping;
	}

	public PSLMapping getPslMapping() {
		return pslMapping;
	}

	public void setPslMapping(PSLMapping pslMapping) {
		this.pslMapping = pslMapping;
	}

	public DacassoMapping getDacassoMapping() {
		return dacassoMapping;
	}

	public void setDacassoMapping(DacassoMapping dacassoMapping) {
		this.dacassoMapping = dacassoMapping;
	}

	public TomaxUsaMapping getTomaxUsaMapping() {
		return tomaxUsaMapping;
	}

	public void setTomaxUsaMapping(TomaxUsaMapping tomaxUsaMapping) {
		this.tomaxUsaMapping = tomaxUsaMapping;
	}

	public SageRMKWorldWideMapping getSage80289Mapping() {
		return sage80289Mapping;
	}

	public void setSage80289Mapping(SageRMKWorldWideMapping sage80289Mapping) {
		this.sage80289Mapping = sage80289Mapping;
	}

	public BrandwearExcelMapping getBrandwearExcelMapping() {
		return brandwearExcelMapping;
	}

	public void setBrandwearExcelMapping(BrandwearExcelMapping brandwearExcelMapping) {
		this.brandwearExcelMapping = brandwearExcelMapping;
	}

	public BallProMapping getBallProMapping() {
		return ballProMapping;
	}

	public void setBallProMapping(BallProMapping ballProMapping) {
		this.ballProMapping = ballProMapping;
	}

	public PSLcadMapping getPslcadMapping() {
		return pslcadMapping;
	}

	public void setPslcadMapping(PSLcadMapping pslcadMapping) {
		this.pslcadMapping = pslcadMapping;
	}

	public TwintechMapping getTwintechMapping() {
		return twintechMapping;
	}

	public void setTwintechMapping(TwintechMapping twintechMapping) {
		this.twintechMapping = twintechMapping;
	}

	public BellaCanvas getBellaCanvasMapping() {
		return bellaCanvasMapping;
	}

	public void setBellaCanvasMapping(BellaCanvas bellaCanvasMapping) {
		this.bellaCanvasMapping = bellaCanvasMapping;
	}

	public AlfaMapping getAlfaMapping() {
		return alfaMapping;
	}

	public void setAlfaMapping(AlfaMapping alfaMapping) {
		this.alfaMapping = alfaMapping;
	}

	public TowelSpecialtiesMapping getTowelSpecialties() {
		return towelSpecialties;
	}

	public void setTowelSpecialties(TowelSpecialtiesMapping towelSpecialties) {
		this.towelSpecialties = towelSpecialties;
	}

	public BagMakersMapping getBagMakersMapping() {
		return bagMakersMapping;
	}

	public HarvestIndustrialExcelMapping getHarvestMapping() {
		return harvestMapping;
	}

	public void setHarvestMapping(HarvestIndustrialExcelMapping harvestMapping) {
		this.harvestMapping = harvestMapping;
	}

	public void setBagMakersMapping(BagMakersMapping bagMakersMapping) {
		this.bagMakersMapping = bagMakersMapping;
	}

	public void setGillStudiosMapping(GillStudiosMapping gillStudiosMapping) {
		this.gillStudiosMapping = gillStudiosMapping;
	}

	public GillStudiosMapping getGillStudiosMapping() {
		return gillStudiosMapping;
	}

	public BlueGenerationMapping getBlueGenerationMapping() {
		return blueGenerationMapping;
	}

	public void setBlueGenerationMapping(BlueGenerationMapping blueGenerationMapping) {
		this.blueGenerationMapping = blueGenerationMapping;
	}

	public MaxplusMapping getMaxplusmapping() {
		return maxplusmapping;
	}

	public void setMaxplusmapping(MaxplusMapping maxplusmapping) {
		this.maxplusmapping = maxplusmapping;
	}

	public FITSAccessoriesMapping getFitsAccessoriesMapping() {
		return fitsAccessoriesMapping;
	}

	public void setFitsAccessoriesMapping(FITSAccessoriesMapping fitsAccessoriesMapping) {
		this.fitsAccessoriesMapping = fitsAccessoriesMapping;
	}

	public GempirepromotionsMapping getGempiresMapping() {
		return gempiresMapping;
	}

	public void setGempiresMapping(GempirepromotionsMapping gempiresMapping) {
		this.gempiresMapping = gempiresMapping;
	}

	public BayStateMapping getBaysStateMapping() {
		return baysStateMapping;
	}

	public void setBaysStateMapping(BayStateMapping baysStateMapping) {
		this.baysStateMapping = baysStateMapping;
	}

	public EdwardsGarmentMapping getEdwardsGarmentMapping() {
		return edwardsGarmentMapping;
	}

	public void setEdwardsGarmentMapping(EdwardsGarmentMapping edwardsGarmentMapping) {
		this.edwardsGarmentMapping = edwardsGarmentMapping;
	}

	public TekweldMapping getTekweldMapping() {
		return tekweldMapping;
	}

	public void setTekweldMapping(TekweldMapping tekweldMapping) {
		this.tekweldMapping = tekweldMapping;
	}

	public HeadWearMapping getHeadWearMapping() {
		return headWearMapping;
	}

	public void setHeadWearMapping(HeadWearMapping headWearMapping) {
		this.headWearMapping = headWearMapping;
	}

	public SportCanadaExcelMapping getSportMapping() {
		return sportMapping;
	}

	public void setSportMapping(SportCanadaExcelMapping sportMapping) {
		this.sportMapping = sportMapping;
	}

	public SunScopeMapping getSunScopeMapping() {
		return sunScopeMapping;
	}

	public void setSunScopeMapping(SunScopeMapping sunScopeMapping) {
		this.sunScopeMapping = sunScopeMapping;
	}

	public SunGraphixRevisedMapping getSunGraphixMapping() {
		return sunGraphixMapping;
	}

	public void setSunGraphixMapping(SunGraphixRevisedMapping sunGraphixMapping) {
		this.sunGraphixMapping = sunGraphixMapping;
	}

	public CbMapping getCbExeMapping() {
		return cbExeMapping;
	}

	public void setCbExeMapping(CbMapping cbExeMapping) {
		this.cbExeMapping = cbExeMapping;
	}

	public PelicanGraphicMapping getPelicanGraphicMapping() {
		return pelicanGraphicMapping;
	}

	public void setPelicanGraphicMapping(PelicanGraphicMapping pelicanGraphicMapping) {
		this.pelicanGraphicMapping = pelicanGraphicMapping;
	}

	public SimplifiedsourcingMapping getSimplifiedMapping() {
		return simplifiedMapping;
	}

	public void setSimplifiedMapping(SimplifiedsourcingMapping simplifiedMapping) {
		this.simplifiedMapping = simplifiedMapping;
	}

	public SolidDimensionMapping getSolidDimensionMapping() {
		return solidDimensionMapping;
	}

	public void setSolidDimensionMapping(SolidDimensionMapping solidDimensionMapping) {
		this.solidDimensionMapping = solidDimensionMapping;
	}

	public SportsManBagMapping getSportsManBagMapping() {
		return sportsManBagMapping;
	}

	public void setSportsManBagMapping(SportsManBagMapping sportsManBagMapping) {
		this.sportsManBagMapping = sportsManBagMapping;
	}

	public void setEveanManufactureMapping(EveanManufacturingCanadaMapping eveanManufactureMapping) {
		this.eveanManufactureMapping = eveanManufactureMapping;
	}

	public SportUSAMapping getSportUSAMapping() {
		return sportUSAMapping;
	}

	public void setSportUSAMapping(SportUSAMapping sportUSAMapping) {
		this.sportUSAMapping = sportUSAMapping;
	}

	public SportAzxCandMapping getSportAzxCandMapping() {
		return sportAzxCandMapping;
	}

	public void setSportAzxCandMapping(SportAzxCandMapping sportAzxCandMapping) {
		this.sportAzxCandMapping = sportAzxCandMapping;
	}

	public DigiSpecMapping getDigiSpecMapping() {
		return digiSpecMapping;
	}

	public void setDigiSpecMapping(DigiSpecMapping digiSpecMapping) {
		this.digiSpecMapping = digiSpecMapping;
	}

	public void setDouglasBridgeMapper(DouglasBridgeMapper douglasBridgeMapper) {
		this.douglasBridgeMapper = douglasBridgeMapper;
	}

	public RadiousMapping getRadiMapping() {
		return radiMapping;
	}

	public void setRadiMapping(RadiousMapping radiMapping) {
		this.radiMapping = radiMapping;
	}

	public WBTIndustriesMapper getWbtIndustriesMapper() {
		return wbtIndustriesMapper;
	}

	public void setWbtIndustriesMapper(WBTIndustriesMapper wbtIndustriesMapper) {
		this.wbtIndustriesMapper = wbtIndustriesMapper;
	}

	public PioneerLLCMapping getPioneerLLCMapping() {
		return pioneerLLCMapping;
	}

	public ZenithExport getZenithMapping() {
		return zenithMapping;
	}

	public void setZenithMapping(ZenithExport zenithMapping) {
		this.zenithMapping = zenithMapping;
	}

	public void setPioneerLLCMapping(PioneerLLCMapping pioneerLLCMapping) {
		this.pioneerLLCMapping = pioneerLLCMapping;
	}

	public void setTeamWorkAthleticMapper(TeamworkAthleticMapping teamWorkAthleticMapper) {
		this.teamWorkAthleticMapper = teamWorkAthleticMapper;
	}

	public AccessLineMapping getAccessLineMapping() {
		return accessLineMapping;
	}

	public void setAccessLineMapping(AccessLineMapping accessLineMapping) {
		this.accessLineMapping = accessLineMapping;
	}

}
