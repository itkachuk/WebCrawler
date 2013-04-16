package com.lohika.itkachuk.javatc.lesson12.beans;

import org.jsoup.nodes.Element;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: itkachuk
 * Date: 4/10/13 11:10 AM
 */
public class CrowlerState {

    private ConcurrentHashMap<String, String> visitedURLs = new ConcurrentHashMap<String, String>();

    private boolean isMatchFound = false;

    public ConcurrentHashMap<String, String> getVisitedURLs() {
        return visitedURLs;
    }

    public void setVisitedURLs(ConcurrentHashMap<String, String> visitedURLs) {
        this.visitedURLs = visitedURLs;
    }

    public synchronized boolean isMatchFound() {
        return isMatchFound;
    }

    public synchronized void setMatchFound(boolean matchFound) {
        isMatchFound = matchFound;
    }
}
