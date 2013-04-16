package com.lohika.itkachuk.javatc.lesson12.beans;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: itkachuk
 * Date: 4/12/13 2:59 PM
 */
public class CrowlerTaskExecutor {

    private ThreadPoolTaskExecutor taskExecutor;
    private CrowlerState stateBean;
    private CrowlerProperties properties;

    public CrowlerTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public synchronized void parseWebPage(String pageURL) {
        if (isSearchInProgress()) { // check whether we need next search step or not
            try {
                taskExecutor.execute(new WebPageParsingTask(pageURL));
            } catch (TaskRejectedException e) {
                //e.printStackTrace();
                int size = taskExecutor.getThreadPoolExecutor().getQueue().size();
                System.out.println("The working queue is overflow - reached " + size + " size, some tasks were probably omitted.\n" +
                        "Try to avoid very big search depths." );
            }
        }
    }

    public synchronized boolean isSearchInProgress() {
        return (!stateBean.isMatchFound() && !taskExecutor.getThreadPoolExecutor().isTerminating());
    }

    public void setStateBean(CrowlerState stateBean) {
        this.stateBean = stateBean;
    }

    public void setProperties(CrowlerProperties properties) {
        this.properties = properties;
    }

    private class WebPageParsingTask implements Runnable {

        private String pageURL;

        public WebPageParsingTask(String pageURL) {
            this.pageURL = pageURL;
        }

        public void run() {
            // main job
            try {
                Document doc = Jsoup.connect(pageURL).get(); // here, besides the connection, we also validate that it is a valid http URL
                                                             // other types of links, such as e-mail, we are ignoring

                if (!stateBean.getVisitedURLs().containsKey(pageURL)) { // if we already visited this URL - skip it

                    // manage Crowler State
                    try {
                        stateBean.getVisitedURLs().put(pageURL, doc.title()); // store visited URL to the context
                    } catch (Exception e) {
                        //e.printStackTrace(); // Ignore absent titles - for us only URL is important
                    }

                    // Display progress
                    if (isSearchInProgress())
                        System.out.print("\rScanning URL (" + stateBean.getVisitedURLs().size() + "): " + pageURL);

                    // find all HTML document elements that matches to the pattern
                    Elements foundText = doc.getElementsMatchingText(properties.getMatchPattern());

                    if (foundText.size() > 0 && isSearchInProgress()) { // target pattern was found - print results and terminate the program
                        stateBean.setMatchFound(true);
                        System.out.println("\n----------------------------------------");
                        System.out.println("Match pattern \"" + properties.getMatchPattern() + "\" was found " + foundText.size() + " times " +
                                "on the page \"" + pageURL + "\":\n");
                        for (Element element : foundText) {
                            System.out.println("[" + element.nodeName() + "] " + element.text());
                        }
                        System.out.println("----------------------------------------");
                        printVisitedURLs();
                        taskExecutor.shutdown(); // terminate all tasks in the queue immediately and stop all threads
                    } else { // continue to search recursively
                        if (isSearchInProgress() && stateBean.getVisitedURLs().size() < properties.getSearchDepth()) {
                            Elements links = doc.select("a[href]");
                            for (Element link : links) {
                                if (!stateBean.getVisitedURLs().containsKey(link.attr("abs:href"))) { // check visitedURLs to avoid recursive loops
                                    parseWebPage(link.attr("abs:href"));
                                /*    try {
                                        Thread.sleep(10); // let other threads to do the job
                                    } catch (InterruptedException e) {} // ignore interrupted exception        */
                                }
                            }
                        } else if (isSearchInProgress()) {
                            stateBean.setMatchFound(true);
                            System.out.println("\n----------------------------------------");
                            System.out.println("The search depth was reached, target text wasn't found");
                            System.out.println("----------------------------------------");
                            printVisitedURLs();
                            taskExecutor.shutdown(); // terminate all tasks in the queue immediately and stop all threads
                        }
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();  // Ignore not valid URLs
            } catch (IllegalArgumentException e) {
                //e.printStackTrace();  // Ignore not valid URLs
            }
        }
    }

    public void printVisitedURLs() {
        System.out.println("Total count of visited URLs is " + stateBean.getVisitedURLs().size() + ":");
        for (String visitedURL : stateBean.getVisitedURLs().keySet()) {
            System.out.println(visitedURL + " (" + stateBean.getVisitedURLs().get(visitedURL) + ")"); // print "<absURL> (linkText)"
        }
        System.out.println("----------------------------------------");
    }
}
