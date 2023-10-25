/**
 * README
 * Name: EXT001MI.Update
 * Standard Tables FFDTBH and FFDTBD Update
 * Description: Update FFDTBH and FFDTBD Tables Data
 * Date	    Changed By      	Description
 * 20230722 Hatem Abdellatif - Update FFDTBH and FFDTBD Tables Data
 */
 
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Add extends ExtendM3Transaction {
  private final MIAPI mi;
  private final DatabaseAPI database;
	private final LoggerAPI logger;
	private final ProgramAPI program;
	private final UtilityAPI utility;
	private final MICallerAPI miCaller;
	
	
	public Add(MIAPI mi, DatabaseAPI database, ProgramAPI program, UtilityAPI utility, MICallerAPI miCaller, LoggerAPI logger) {
    this.mi = mi;
    this.database = database;
    this.program = program;
    this.utility = utility;
    this.miCaller = miCaller;
    this.logger = logger;
  }
  
  /**
	 * Input fields ExtendM3Transaction
	 */
	private String iCONO;
	private String iDIVI;
	private String iDTTB;
	private String iNYDP;
	private String iTX40;
	private String iTX15;
	private String iADME;
	private String iDMTS;
	private String iRGDT;
	private String iRGTM;
	private String iMCRS;
	private String iBNRT;
	private String iMDTP;
	private String iHYAD;
	private String iROLR;
	int currentCompany = (Integer)program.getLDAZD().CONO;
	String currentDivision = program.LDAZD.DIVI.toString();
	int changeNumber = 0;
	int sequence = 0;
	int entryTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger();
  int entryDate = LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")).toInteger();
  
  public void main() {
    iCONO = mi.inData.get("CONO") == null ? "" : mi.inData.get("CONO").trim();
   	iDIVI = mi.inData.get("DIVI") == null ? "" : mi.inData.get("DIVI").trim();
   	iDTTB = mi.inData.get("DTTB") == null ? "" : mi.inData.get("DTTB").trim();
    iNYDP = mi.inData.get("NYDP") == null ? "" : mi.inData.get("NYDP").trim();
   	iTX40 = mi.inData.get("TX40") == null ? "" : mi.inData.get("TX40").trim();
    iTX15 = mi.inData.get("TX15") == null ? "" : mi.inData.get("TX15").trim();
    iADME = mi.inData.get("ADME") == null ? "" : mi.inData.get("ADME").trim();
    iDMTS = mi.inData.get("DMTS") == null ? "" : mi.inData.get("DMTS").trim();
   	iRGDT = mi.inData.get("RGDT") == null ? "" : mi.inData.get("RGDT").trim();
   	iRGTM = mi.inData.get("RGTM") == null ? "" : mi.inData.get("RGTM").trim();
    iMCRS = mi.inData.get("MCRS") == null ? "" : mi.inData.get("MCRS").trim();
    iBNRT = mi.inData.get("BNRT") == null ? "" : mi.inData.get("BNRT").trim();
    iMDTP = mi.inData.get("MDTP") == null ? "" : mi.inData.get("MDTP").trim();
   	iHYAD = mi.inData.get("HYAD") == null ? "" : mi.inData.get("HYAD").trim();
   	iROLR = mi.inData.get("ROLR") == null ? "" : mi.inData.get("ROLR").trim();
   	
   	if (!validate()) {
		  return;
		}
		
		logger.debug("Before calling UpdateFFDTBHRecord()");
		
		//If no error then update the record in FFDTBH table
    UpdateFFDTBHRecord();

  }
  
  
  /**
   * Updates record in the FFDTBH table.
   *
   * @param  null
   * @return void
   */
  private void UpdateFFDTBHRecord() {
    logger.debug("Before select statement for table FFDTBH");
    
    DBAction query = database.table("FFDTBH")
    .index("00")
    .build();
    
    DBContainer container = query.getContainer();
		container.setInt("FTCONO", currentCompany);
    container.set("FTDIVI", currentDivision);
		container.set("FTDTTB", iDTTB);
		container.set("FTLMDT", LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")).toInteger());
		container.set("FTCHNO", container.getInt("FTCHNO")+1);
		container.set("FTCHID", program.getUser());
		container.setInt("FTRGDT", entryDate);
		container.setInt("FTRGTM", entryTime);
		
	   if (iNYDP != ""){
	      container.setInt("FTNYDP", iNYDP.toInteger());
	   }
	   if (iTX40 != ""){
	      container.set("FTTX40", iTX40);
	   }
	   if (iTX15 != ""){
	      container.set("FTTX15", iTX15);
	   }
	   if (iADME != ""){
	      container.setInt("FTADME", iADME.toInteger());
	   }
	   if (iDMTS!= ""){
	      container.set("FTDMTS", iDMTS);
	   }
	   if (iMCRS!= ""){
        container.setInt("FTMCRS", iMCRS.toInteger());
	   }
	   if (iBNRT!= ""){
        container.setDouble("FTBNRT", iBNRT.toDouble());
	   }
	   if (iMDTP!= ""){
	      container.set("FTMDTP", iMDTP);
	   }
	   if (iHYAD!= ""){
        container.setInt("FTHYAD", iHYAD.toInteger());
	   }
	   if (iROLR!= ""){
        container.setInt("FTROLR", iROLR.toInteger());
	   }

    // Insert changed information
    if(!query.insert(container)){
      mi.error("Record already exists.");
      return;
    }
    
    logger.debug("After select statement for table FFDTBH");
  }

  
  /**
	 * validate - Validate input
	 *
	 * @return boolean
	 */
	boolean validate() {
	  
	  logger.debug("Before the validate() method.");

		if (iCONO == "") {
			iCONO = program.LDAZD.CONO.toString();
		} else if (!iCONO.isInteger()) {
			mi.error("Company number " + iCONO + " is invalid");
			return false;
		}
		
		
		if (iDIVI == "") {
			iDIVI = program.LDAZD.DIVI.toString();
		}
		

		if (iDTTB == "") {
			mi.error("Depreciation plan " + iDTTB + " is empty (note: Depreciation plan is required )");
			return false;
		}


		if (iNYDP != "") {
			if (!iNYDP.isInteger()) {
				mi.error("Number of years " + iNYDP + " is invalid");
				return false;
			}
		}

		
		if (iTX40 == "") {
			mi.error("Description " + iTX40 + " is empty (note: Description is required )");
			return false;
		}

	
		if (iTX15 == "") {
			mi.error("Name " + iTX15 + " is empty (note: Name is required )");
			return false;
		}


		if (iADME != "") {
		  
			if (!iADME.isInteger()) {
				mi.error("Distribution method " + iADME + " is invalid");
				return false;
			}
		}
		
		logger.debug("After the validate() method.");
		
		return true;
	} 
}