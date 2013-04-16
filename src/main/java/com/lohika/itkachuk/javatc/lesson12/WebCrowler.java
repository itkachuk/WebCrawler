package com.lohika.itkachuk.javatc.lesson12;

import com.lohika.itkachuk.javatc.lesson12.beans.CrowlerProperties;
import com.lohika.itkachuk.javatc.lesson12.beans.CrowlerState;

import com.lohika.itkachuk.javatc.lesson12.beans.CrowlerTaskExecutor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * User: itkachuk
 * Date: 4/10/13 10:33 AM
 */
public class WebCrowler {

    private static CrowlerProperties properties;

    public void setProperties(CrowlerProperties properties) {
        this.properties = properties;
    }

    public static void main( String[] args ) throws InterruptedException {
        System.out.println("----------------------------------------");
        System.out.println("WebCrowler starting...");

        // Initialize Spring context
        ApplicationContext container = new ClassPathXmlApplicationContext("spring/WebCrowlerContext.xml");
        System.out.println("Spring Context initialized");

        // try to use command line arguments
        if (args.length == 3) {
            properties.setStartURL(args[0]);
            properties.setMatchPattern(args[1]);
            properties.setSearchDepth(Integer.valueOf(args[2]));

            System.out.println("Using properties from command line:");
        } else {
            System.out.println("Using properties from \"WebCrowler.properties\" file:");
        }
        if (!validateInputs()) System.exit(1);
        System.out.println("\tStart URL: " + properties.getStartURL());
        System.out.println("\tMatch pattern: " + properties.getMatchPattern());
        System.out.println("\tSearch depth: " + properties.getSearchDepth());
        System.out.println("----------------------------------------");

        // start main job
        ((CrowlerTaskExecutor)container.getBean("crowlerTaskExecutor")).parseWebPage(properties.getStartURL());
    }

    private static boolean validateInputs() {
        if (!properties.getStartURL().startsWith("http://") && !properties.getStartURL().startsWith("https://")) {
            System.out.println("Input error: Start URL parameter should start with the \"http://\" or \"https://\"");
            return false;
        }
        if (properties.getMatchPattern().isEmpty()) {
            System.out.println("Input error: Match pattern parameter can not be empty");
            return false;
        }
        if (properties.getSearchDepth() < 1 || properties.getSearchDepth() > 1000) {
            System.out.println("Input error: allowed range for the Search depth parameter is [1...1000]");
            return false;
        }
        return true;
    }
}
