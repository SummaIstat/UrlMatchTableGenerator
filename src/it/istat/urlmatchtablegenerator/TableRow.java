package it.istat.urlmatchtablegenerator;

/**
* @author  Donato Summa
*/
public class TableRow {

	private String firmId;
	private int codLink;
	private String firmName;
	private String urlWeHad;
	private String urlWeFound;
	private String scoreVector;
	private int score;
	private String calculatedDomain;
	private int matchStrict;
	private int matchOnlyDomain;
	private int matchOnlyDomainNoExt;
	
	
	
	public String getFirmId() {
		return firmId;
	}
	public void setFirmId(String firmId) {
		this.firmId = firmId;
	}
	public int getCodLink() {
		return codLink;
	}
	public void setCodLink(int codLink) {
		this.codLink = codLink;
	}
	public String getFirmName() {
		return firmName;
	}
	public void setFirmName(String firmName) {
		this.firmName = firmName;
	}
	public String getUrlWeHad() {
		return urlWeHad;
	}
	public void setUrlWeHad(String urlWeHad) {
		this.urlWeHad = urlWeHad;
	}
	public String getUrlWeFound() {
		return urlWeFound;
	}
	public void setUrlWeFound(String urlWeFound) {
		this.urlWeFound = urlWeFound;
	}
	public String getScoreVector() {
		return scoreVector;
	}
	public void setScoreVector(String scoreVector) {
		this.scoreVector = scoreVector;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getCalculatedDomain() {
		return calculatedDomain;
	}
	public void setCalculatedDomain(String calculatedDomain) {
		this.calculatedDomain = calculatedDomain;
	}
	public int getMatchStrict() {
		return matchStrict;
	}
	public void setMatchStrict(int matchStrict) {
		this.matchStrict = matchStrict;
	}
	public int getMatchOnlyDomain() {
		return matchOnlyDomain;
	}
	public void setMatchOnlyDomain(int matchOnlyDomain) {
		this.matchOnlyDomain = matchOnlyDomain;
	}
	public int getMatchOnlyDomainNoExt() {
		return matchOnlyDomainNoExt;
	}
	public void setMatchOnlyDomainNoExt(int matchOnlyDomainNoExt) {
		this.matchOnlyDomainNoExt = matchOnlyDomainNoExt;
	}
	
	@Override
	public String toString() {
		return "" + firmId + "\t" +
				codLink + "\t" +
				firmName + "\t" +
				urlWeHad + "\t" +
				urlWeFound + "\t" +
				scoreVector + "\t" +
				score + "\t" +
				calculatedDomain + "\t" +
				matchStrict + "\t" +
				matchOnlyDomain + "\t" +
				matchOnlyDomainNoExt;
		
	}
}
