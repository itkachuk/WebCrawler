package com.lohika.itkachuk.javatc.lesson12.beans;

/**
 * Created with IntelliJ IDEA.
 * User: itkachuk
 * Date: 4/10/13 11:10 AM
 */
public class CrowlerProperties {

    private String startURL;
    private String matchPattern;
    private int searchDepth;

    public String getStartURL() {
        return startURL;
    }

    public void setStartURL(String startURL) {
        this.startURL = startURL;
    }

    public String getMatchPattern() {
        return matchPattern;
    }

    public void setMatchPattern(String matchPattern) {
        this.matchPattern = matchPattern;
    }

    public int getSearchDepth() {
        return searchDepth;
    }

    public void setSearchDepth(int searchDepth) {
        this.searchDepth = searchDepth;
    }
}
