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
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
* @author  Donato Summa
*/
public class UrlMatchTableGenerator {
	
	static Logger logger = Logger.getLogger(UrlMatchTableGenerator.class);
	
	private String urlScorerOutputFilePath;
	private String firmsInfoFilePath;
	private String outputTableFolderPath;
	private List<TableRow> scoreList = new ArrayList<TableRow>();
	
	public static void main(String[] args) throws IOException {
		
		logger.debug("*******************************************************************");
		logger.debug("********************     START EXECUTION       ********************");
		logger.debug("*******************************************************************");
        //String now = Utils.getDateTimeAsString();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date startDateTime = new Date();
        logger.info("Starting datetime = " + dateFormat.format(startDateTime)); //15/12/2014 15:59:48
		
		
        UrlMatchTableGenerator umtg = new UrlMatchTableGenerator();
        umtg.configure(args);
		umtg.generateAndPrintMatchTable();    	
        
        
        Date endDateTime = new Date();
		logger.info("Started at = " + dateFormat.format(startDateTime)); //15/12/2014 15:59:48
        logger.info("Ending datetime = " + dateFormat.format(endDateTime)); //15/12/2014 15:59:48
        logger.debug("*******************************************************************");
		logger.debug("********************     END EXECUTION         ********************");
		logger.debug("*******************************************************************");
		
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
				
				// FIRMS_INFO_FILE_PATH
				if(props.getProperty("FIRMS_INFO_FILE_PATH") != null){
					firmsInfoFilePath = props.getProperty("FIRMS_INFO_FILE_PATH");
					if (!Utils.isAValidFile(firmsInfoFilePath)){
			        	System.out.println("The FIRMS_INFO_FILE_PATH parameter that you set ( " + firmsInfoFilePath + " ) is not valid");
			        	System.exit(1);
			        }
				}else{
					System.out.println("Wrong/No configuration for the parameter FIRMS_INFO_FILE_PATH !");
					System.exit(1);
				}
				
				// URL_SCORER_OUTPUT_FILE_PATH
				if(props.getProperty("URL_SCORER_OUTPUT_FILE_PATH") != null){
					urlScorerOutputFilePath = props.getProperty("URL_SCORER_OUTPUT_FILE_PATH");
					if (!Utils.isAValidFile(urlScorerOutputFilePath)){
			        	System.out.println("The URL_SCORER_OUTPUT_FILE_PATH parameter that you set ( " + urlScorerOutputFilePath + " ) is not valid");
			        	System.exit(1);
			        }
				}else{
					System.out.println("Wrong/No configuration for the parameter PROVINCES_FILE_PATH !");
					System.exit(1);
				}
				
				// OUTPUT_FOLDER_PATH
				if(props.getProperty("OUTPUT_FOLDER_PATH") != null){
					outputTableFolderPath = props.getProperty("OUTPUT_FOLDER_PATH");
					if (!Utils.isAValidDirectory(outputTableFolderPath)){
			        	System.out.println("The OUTPUT_FOLDER_PATH parameter that you set ( " + outputTableFolderPath + " ) is not valid");
			        	System.exit(1);
			        }
				}else{
					System.out.println("Wrong/No configuration for the parameter OUTPUT_FOLDER_PATH !");
					System.exit(1);
				}
				
			} else {
				System.out.println("usage: java -jar UrlMatchTableGenerator.jar [umtgConf.properties fullpath]");
				System.exit(1);
			}	
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
			
			for (TableRow tr : scoreList) {
				tr.setFirmName(firmInfoMap.get(tr.getFirmId().trim()).getFirmName());
				tr.setUrlWeHad(firmInfoMap.get(tr.getFirmId().trim()).getUrlWeHad());
			}
			return scoreList;
			
		}catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			System.err.println("Error fnfe : " + fnfe.getMessage());
			System.exit(1);
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error e : " + e.getMessage());
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
