package it.istat.urlmatchtablegenerator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

public class Utils {

	static Logger logger = Logger.getLogger(Utils.class);
	private static Set<String> problematicUrls = new HashSet<String>();
	
	public static Set<String> getProblematicUrls(){
		return problematicUrls;
	}
	
	public static String getDateTimeAsString(){
		Date dateTime;
		String now = "";
		DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		dateTime = Calendar.getInstance().getTime();        
        now = formatter.format(dateTime);
        return now;
	}
	
	public static boolean isAValidFile(String filePathString) {
		File f = new File(filePathString);
		if(f.exists() && !f.isDirectory()) { 
			return true;
		}
		return false;
	}
    
    public static boolean isAValidDirectory(String dirPathString) {
		File f = new File(dirPathString);
		if(f.exists() && f.isDirectory()) { 
			return true;
		}
		return false;
	}
    
    public static int isMatchOnlyDomain(String calculatedDomain, String urlWeFound) {
		// se il dominio del sito che abbiamo è contenuto nell'url in esame ritorna 1 ossia valore positivo
		if(urlWeFound.contains(calculatedDomain)){
			return 1;
		}
		return 0;
	}

	public static int isMatchOnlyDomainNoExt(String calculatedDomain, String urlWeFound) {
		// se il dominio SENZA ESTENSIONE del sito che abbiamo è contenuto nell'url in esame ritorna 1 ossia valore positivo
		
		String domainNoExt = calculatedDomain;
		int indexLastDot = calculatedDomain.lastIndexOf(".");
		
		if(indexLastDot != -1){
			domainNoExt = calculatedDomain.substring(0, indexLastDot);
		}
		
		if(urlWeFound.contains(domainNoExt)){
			return 1;
		}
		return 0;
	}

	public static int isMatchStrict(String urlWeHad, String urlWeFound) {
		// quando il sito noto è integralmente contenuto nell'url trovata in esame ritorna 1 ossia valore positivo
		if(urlWeFound.contains(urlWeHad)){
			return 1;
		}
		return 0;
	}


	public static String getDomainFromUrl(String url) {
		URL aURL;
		try {
			aURL = new URL(url);
			String host = aURL.getHost().replace("www.", "");
	        return host;
		} catch (MalformedURLException e) {
			//e.printStackTrace();
			//logger.info("problem with the url \"" + url + "\" ===> the printed domain will be \"**********\"");
			problematicUrls.add(url);
			return "**********";
		}
	}
	
	public static String getHost(String url){
	    if(url == null || url.length() == 0)
	        return "";

	    int doubleslash = url.indexOf("//");
	    if(doubleslash == -1)
	        doubleslash = 0;
	    else
	        doubleslash += 2;

	    int end = url.indexOf('/', doubleslash);
	    end = end >= 0 ? end : url.length();

	    int port = url.indexOf(':', doubleslash);
	    end = (port > 0 && port < end) ? port : end;

	    return url.substring(doubleslash, end);
	}

	public static String getBaseDomain(String url) {
	    String host = getHost(url);

	    int startIndex = 0;
	    int nextIndex = host.indexOf('.');
	    int lastIndex = host.lastIndexOf('.');
	    while (nextIndex < lastIndex) {
	        startIndex = nextIndex + 1;
	        nextIndex = host.indexOf('.', startIndex);
	    }
	    if (startIndex > 0) {
	        return host.substring(startIndex);
	    } else {
	        return host;
	    }
	}
	
	public static String getDomainFromUrl2(String url) {

        URL aURL;
		try {
			aURL = new URL(url);
			String host = aURL.getHost().replace("www.", "");
	        return host;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        return null;
    }

}
