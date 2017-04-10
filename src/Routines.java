package com.intellij.codeInspection;

/**
 * Complex routines such as file handling, etc
 */

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

public class Routines {


    //Holds the result of the last operation
    StringBuilder result = new StringBuilder();

    public Routines() {}

    /*

    This method returns a String representing a file's contents
    Loads the entire file!

    Used privately to load XML files
     */
    public String LoadResource(String fileName)
    {
        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();

    }

    /*
    Can be used to query an XML file for information
     */
    public String XMLReader(String filename) {
        try {

            //Get file from resources folder
            ClassLoader classLoader = getClass().getClassLoader();
            File inputFile = new File(classLoader.getResource(filename).getFile());
            DocumentBuilderFactory dbFactory
                    = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root element :"
                    + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("student");
            System.out.println("----------------------------");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                System.out.println("\nCurrent Element :"
                        + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    System.out.println("Student roll no : "
                            + eElement.getAttribute("rollno"));
                    System.out.println("First Name : "
                            + eElement
                            .getElementsByTagName("firstname")
                            .item(0)
                            .getTextContent());
                    System.out.println("Last Name : "
                            + eElement
                            .getElementsByTagName("lastname")
                            .item(0)
                            .getTextContent());
                    System.out.println("Nick Name : "
                            + eElement
                            .getElementsByTagName("nickname")
                            .item(0)
                            .getTextContent());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.toString();
    }

}
