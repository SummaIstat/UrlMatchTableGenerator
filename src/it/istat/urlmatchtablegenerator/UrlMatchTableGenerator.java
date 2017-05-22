package it.istat.urlmatchtablegenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
* @author  Donato Summa
*/
public class UrlMatchTableGenerator {
	
	static Logger logger = Logger.getLogger(UrlMatchTableGenerator.class);
	
	private static String logFilePath;
	private String urlScorerOutputFilePath;
	private String firmsInfoFilePath;
	private String outputTableFolderPath;
	private static Set<String> problematicFirms = new HashSet<String>();
	private List<TableRow> scoreList = new ArrayList<TableRow>();
	
	public static void main(String[] args) throws IOException {
		
		UrlMatchTableGenerator umtg = new UrlMatchTableGenerator();
        umtg.configure(args);
        
		
        logger.debug("*******************************************************************");
		logger.debug("********************     START EXECUTION       ********************");
		logger.debug("*******************************************************************");
        //String now = Utils.getDateTimeAsString();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date startDateTime = new Date();
        logger.info("Starting datetime = " + dateFormat.format(startDateTime)); //15/12/2014 15:59:48
		
		
        umtg.generateAndPrintMatchTable();    	
		umtg.generateProblemReport();
        
        
        Date endDateTime = new Date();
		logger.info("Started at = " + dateFormat.format(startDateTime)); //15/12/2014 15:59:48
        logger.info("Ending datetime = " + dateFormat.format(endDateTime)); //15/12/2014 15:59:48
        logger.debug("*******************************************************************");
		logger.debug("********************     END EXECUTION         ********************");
		logger.debug("*******************************************************************");
		
	}

	private void generateProblemReport() {
		
		if (problematicFirms.size() != 0){
			logger.info("\n\n\n");
			logger.info("In these cases the printed FirmName and UrlWeHad fields in");
			logger.info("the output file will have the value \"not available/provided\"");
			logger.info("\n\n\n");
			for(String firmId : problematicFirms){
				logger.info("No information available for the firm having id " + firmId);
			}
		}
		
		if (Utils.getProblematicUrls().size() != 0){
			logger.info("\n\n\n");
			logger.info("In these cases the printed domain will be \"**********\"");
			logger.info("\n\n\n");
			for(String url : Utils.getProblematicUrls()){
				logger.info("problem with the url \"" + url + "\" ===> the printed domain will be \"**********\"");
			}
		}
		
	}

	private void generateAndPrintMatchTable() {
		scoreList = getOrderedScoreListFromFile(urlScorerOutputFilePath);
        scoreList = addFirmInfoToScoreList(scoreList, firmsInfoFilePath);
        scoreList = addComputedDomainsAndMatchesToScoreList(scoreList);
        printOtputTableOnFile(scoreList, outputTableFolderPath);   		
	}

	private void configure(String[] args) throws IOException {
		
		if (args.length == 1){
			if (Utils.isAValidFile(args[0])){
				FileInputStream fis = new FileInputStream(args[0]);
				InputStream inputStream = fis;
				Properties props = new Properties();
				props.load(inputStream);
				
				// LOG_FILE_PATH
				configLogFile(props);
				
				// FIRMS_INFO_FILE_PATH
				if(props.getProperty("FIRMS_INFO_FILE_PATH") != null){
					firmsInfoFilePath = props.getProperty("FIRMS_INFO_FILE_PATH");
					if (!Utils.isAValidFile(firmsInfoFilePath)){
			        	logger.error("The FIRMS_INFO_FILE_PATH parameter that you set ( " + firmsInfoFilePath + " ) is not valid");
			        	System.exit(1);
			        }
				}else{
					logger.error("Wrong/No configuration for the parameter FIRMS_INFO_FILE_PATH !");
					System.exit(1);
				}
				
				// URL_SCORER_OUTPUT_FILE_PATH
				if(props.getProperty("URL_SCORER_OUTPUT_FILE_PATH") != null){
					urlScorerOutputFilePath = props.getProperty("URL_SCORER_OUTPUT_FILE_PATH");
					if (!Utils.isAValidFile(urlScorerOutputFilePath)){
			        	logger.error("The URL_SCORER_OUTPUT_FILE_PATH parameter that you set ( " + urlScorerOutputFilePath + " ) is not valid");
			        	System.exit(1);
			        }
				}else{
					logger.error("Wrong/No configuration for the parameter PROVINCES_FILE_PATH !");
					System.exit(1);
				}
				
				// OUTPUT_FOLDER_PATH
				if(props.getProperty("OUTPUT_FOLDER_PATH") != null){
					outputTableFolderPath = props.getProperty("OUTPUT_FOLDER_PATH");
					if (!Utils.isAValidDirectory(outputTableFolderPath)){
			        	logger.error("The OUTPUT_FOLDER_PATH parameter that you set ( " + outputTableFolderPath + " ) is not valid");
			        	System.exit(1);
			        }
				}else{
					logger.error("Wrong/No configuration for the parameter OUTPUT_FOLDER_PATH !");
					System.exit(1);
				}
				
			} else {
				logger.info("usage: java -jar UrlMatchTableGenerator.jar [umtgConf.properties fullpath]");
				System.exit(1);
			}	
		}
		
	}

	private void configLogFile(Properties props) {
		
		if(props.getProperty("LOG_FILE_PATH") != null){
			
			logFilePath = props.getProperty("LOG_FILE_PATH");
			
			RollingFileAppender rfa = new RollingFileAppender();
			rfa.setName("FileLogger");
			rfa.setFile(logFilePath);
			rfa.setAppend(true);
			rfa.activateOptions();
			rfa.setMaxFileSize("20MB");
			rfa.setMaxBackupIndex(30);
			rfa.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"));

			Logger.getRootLogger().addAppender(rfa);
			
		}else{
			logger.error("Wrong/missing configuration for the parameter LOG_FILE_PATH !");
			System.exit(1);
		}
		
	}

	private static List<TableRow> getOrderedScoreListFromFile(String assignScoreFilePath) {
		List<TableRow> scoreList = new ArrayList<TableRow>();		
		try {
			FileInputStream fis = new FileInputStream(assignScoreFilePath);
			InputStream is = fis;
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String strLine;
			String delimiter = "\t";
						
			br.readLine(); // avoid the first line with headers
			while ((strLine = br.readLine()) != null) {
				
				String[] tokens = strLine.split(delimiter);
								
				if(tokens.length <= 5){
					TableRow tr = new TableRow();
					tr.setFirmId(tokens[0]);
					tr.setCodLink(Integer.parseInt(tokens[1].trim()));
					tr.setUrlWeFound(tokens[2]);
					tr.setScoreVector(tokens[3]);
					tr.setScore(Integer.parseInt(tokens[4]));
					scoreList.add(tr);
				}
			}
			br.close();
			is.close();
			return scoreList;
		}catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			System.err.println("Error: " + fnfe.getMessage());
			System.exit(1);
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error: " + e.getMessage());
		}
		return null;
	}
	
	private static List<TableRow> addFirmInfoToScoreList(List<TableRow> scoreList, String firmInformationFilePath){
		Map<String,TableRow> firmInfoMap = new HashMap<String,TableRow>();
		try {
			FileInputStream fis = new FileInputStream(firmInformationFilePath);
			InputStream is = fis;
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String strLine;
			String delimiter = "\t";
						
			br.readLine(); // avoid the first line with headers
			while ((strLine = br.readLine()) != null) {
				
				String[] tokens = strLine.split(delimiter);
								
				if(tokens.length <= 3){
					TableRow tr = new TableRow();
					tr.setFirmId(tokens[0]);
					tr.setFirmName(tokens[1]);
					tr.setUrlWeHad(tokens[2]);
					firmInfoMap.put(tokens[0], tr);
				}
				
			}
			
			br.close();
			is.close();
			
			String firmId;
			TableRow tableRow;
			for (TableRow tr : scoreList) {
				firmId = tr.getFirmId();
				tableRow = firmInfoMap.get(tr.getFirmId().trim());
				if (tableRow != null){
					tr.setFirmName(tableRow.getFirmName());
					tr.setUrlWeHad(tableRow.getUrlWeHad());
				}else{
					//logger.info("No information available for the firm having id " + firmId);
					problematicFirms.add(firmId);
					tr.setFirmName("not available/provided");
					tr.setUrlWeHad("not available/provided");
				}
			}
			return scoreList;
			
		}catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			System.err.println("Error fnfe : " + fnfe.getMessage());
			logger.error("Error fnfe : " + fnfe.getMessage());
			System.exit(1);
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error e : " + e.getMessage());
			logger.error("Error e : " + e.getMessage());
		}
		return null;
	}
	
	private static List<TableRow> addComputedDomainsAndMatchesToScoreList(List<TableRow> scoreList){
		String computedDomain = ""; 
		for (TableRow tr : scoreList) {
			computedDomain = Utils.getDomainFromUrl(tr.getUrlWeHad());
			tr.setCalculatedDomain(computedDomain);
			tr.setMatchStrict(Utils.isMatchStrict(tr.getUrlWeHad(), tr.getUrlWeFound()));
			tr.setMatchOnlyDomain(Utils.isMatchOnlyDomain(tr.getCalculatedDomain(), tr.getUrlWeFound()));
			tr.setMatchOnlyDomainNoExt(Utils.isMatchOnlyDomainNoExt(tr.getCalculatedDomain(), tr.getUrlWeFound()));
		}
		return scoreList;
	}

	private static void printOtputTableOnFile(List<TableRow> scoreList, String outputTableFolderPath){ 
		
		Date dateTime = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String now = formatter.format(dateTime);
        String path = outputTableFolderPath + File.separator + "matchTable_" + now + ".csv";
        
		//boolean primaVolta = true;
		try {
			
			File file = new File(path);
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write("FIRM_ID" + "\t" + "POS_LINK" + "\t" + "FIRM_NAME" + "\t" + "URL_WE_HAD" + "\t" + "URL_WE_FOUND" + "\t" + "SCORE_VECTOR" + "\t" + "SCORE" + "\t" + "CALC_DOMAIN" + "\t" + "MATCH_STRICT" + "\t" + "MATCH_DOMAIN" + "\t" + "MATCH_DOMAIN_NO_EXT");
			bw.newLine();
			for(TableRow tr : scoreList){
				bw.write("" + tr.toString() );	
				bw.newLine();
			}
			bw.flush();
			bw.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}

		
	
}
