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


public class Update extends ExtendM3Transaction {
  private final MIAPI mi;
  private final DatabaseAPI database;
	private final LoggerAPI logger;
	private final ProgramAPI program;
	private final UtilityAPI utility;
	private final MICallerAPI miCaller;
	
	
	public Update(MIAPI mi, DatabaseAPI database, ProgramAPI program, UtilityAPI utility, MICallerAPI miCaller, LoggerAPI logger) {
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
	private String iYEAR;
	private String iD2PC;
	private String iADSH;
	private String iRGDT;
	private String iRGTM;

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
	iYEAR = mi.inData.get("YEAR") == null ? "" : mi.inData.get("YEAR").trim();
	iD2PC = mi.inData.get("D2PC") == null ? "" : mi.inData.get("D2PC").trim();
	   iADSH = mi.inData.get("ADSH") == null ? "" : mi.inData.get("ADSH").trim();
	   iRGDT = mi.inData.get("RGDT") == null ? "" : mi.inData.get("RGDT").trim();
	   iRGTM = mi.inData.get("RGTM") == null ? "" : mi.inData.get("RGTM").trim();
	   
	   if (!validate()) {
		  return;
		}
		
		logger.debug("Before calling UpdateFFDTBHRecord()");
	
	//If no error then update the record in FFDTBD table
	updateFFDTBDRecord();

  }
  
  
  /**
   * Updates record in the FFDTBD table.
   *
   * @param  null
   * @return void
   */
  private void updateFFDTBDRecord() {
	logger.debug("Before select statement for table FFDTBD");
	
	DBAction query = database.table("FFDTBD")
	.index("00")
	.build();
	
	DBContainer container = query.getContainer();
	container.setInt("FUCONO", currentCompany);
	container.set("FUDIVI", currentDivision);
	container.set("FUDTTB", iDTTB);
	container.setInt("FUYEAR", iYEAR.toInteger());
	container.set("FULMDT", LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")).toInteger());
		container.set("FUCHNO", container.getInt("FUCHNO")+1);
		container.set("FUCHID", program.getUser());
		container.setInt("FURGDT", entryDate);
		container.setInt("FURGTM", entryTime);
		
	   if (iYEAR != ""){
		container.setInt("FUYEAR", iYEAR.toInteger());
	   }
	   if (iD2PC != ""){
		container.setDouble("FUD2PC", iD2PC.toDouble());
	   }
	   if (iADSH != ""){
		container.setDouble("FUADSH", iADSH.toDouble());
	   }
	
	// Insert changed information
	if(!query.insert(container)){
	  mi.error("Record already exists.");
	  return;
	}
	
	logger.debug("After select statement for table FFDTBD");
  }
  
  
  /**
   * Fetch Depreciation plan record from FFDTBH table.
   *
   * @param  null
   * @return boolean
   */
  private boolean fetchFFDTBHDepreciationPlan() {
	logger.debug("Before select statement from table FFDTBH");
	
	DBAction query = database.table("FFDTBH")
	.index("00")
	.build();
	
	DBContainer container = query.getContainer();
		container.setInt("FTCONO", currentCompany);
	container.set("FTDIVI", currentDivision);
		container.set("FTDTTB", iDTTB);
	
	// Read information
	if(!query.read(container)){
	  logger.debug("Return false.");
	  return false;
	} else {
	  logger.debug("Return true.");
	  return true;
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
		} else {
		  if (!fetchFFDTBHDepreciationPlan()) {
			mi.error("Depreciation plan " + iDTTB + " is does not exists in table (FFDTBH) (Please to provide a valid Depreciation plan)");
			  return false;
		  }
		}

		
		if (iYEAR != "") {

			if (!iYEAR.isInteger()) {
				mi.error("Year " + iYEAR + " is invalid");
				return false;
			}
		}
   

		if (iD2PC != "") {

			if (!iD2PC.isDouble()) {
				mi.error("Percent " + iD2PC + " is invalid");
				return false;
			}
		}
		
		
		if (iADSH != "") {

			if (!iADSH.isDouble()) {
				mi.error("Share " + iADSH + " is invalid");
				return false;
			}
		}
		
		logger.debug("After the validate() method.");
		
		return true;
	}
}