package com.laststringx.earnings;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IncomeCalculator {
    private static final String GLOBAL_PATH = "D:\\Google Drive Backup\\Payslips";
    private static final List<String> months = Arrays.asList(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    );

    private static final List<String> companiesFolderNames = Arrays.asList("TCS", "Oracle", "Myntra", "MMT");

    public static void main(String[] args) throws InterruptedException {
        getAllSalariesFromPdfs();
    }

    private static void getAllSalariesFromPdfs() throws InterruptedException {
        int totalSalaryRecieved = 0;
        for(String company : companiesFolderNames){
            totalSalaryRecieved += getSalaryData(company);
        }
        System.out.println("total Salary recieved " + totalSalaryRecieved);
    }

    private static int getSalaryData(String company) throws InterruptedException {
        String path = GLOBAL_PATH + "\\" + company;
        System.out.println("Reading company : " + company);
        File directory = new File(path);

        List<String> years = getAllYearsFolderNames(directory);
        int totalCompanySalary = 0;
        for (String year : years){
            int annualSalary = readAllSlips(year, company);
            System.out.println(company + " annual salary for " + year + " " + annualSalary);
            totalCompanySalary += annualSalary;
            printDots();
        }
        System.out.println(company + " total salary " + totalCompanySalary);
        return totalCompanySalary;

    }

    private static void printDots() throws InterruptedException {
        int n = 10;
        while (n-->0){
            System.out.print(".");
            Thread.sleep(1000);
        }
        System.out.println();
    }

    private static int readAllSlips(String year, String company) {
        int totalAnnualSalary = 0;
        for(String month : months){
            try {
                File file = new File(GLOBAL_PATH + "\\" + company + "\\" +year + "\\" + month + ".pdf");
                if(file.exists()) {
                    String salary = readNetPayFromPdf(file);
                    switch (company){
                        case "TCS":
                        case "Oracle":
                        case "Myntra":
                            Pattern pattern = Pattern.compile("\\d{1,3}(,\\d{3})*(\\.\\d+)?");
                            Matcher matcher = pattern.matcher(salary);

                            if (matcher.find()) {
                                // Extract the matched number
                                String numberStr = matcher.group();

                                // Remove commas and decimal part
                                numberStr = numberStr.replace(",", "").split("\\.")[0];

                                // Convert to integer
                                int number = Integer.parseInt(numberStr);
                                totalAnnualSalary += number;
                                System.out.println(month + " " + year +" : " + number);
                            } else {
                                System.out.println("No valid number found in the text.");
                            }
                            break;
                        case "MMT":
                            // Regular expression to find the number pattern
                            pattern = Pattern.compile("\\d{1,3}(,\\d{2,3})*");
                            matcher = pattern.matcher(salary);

                            if (matcher.find()) {
                                // Extract the matched number
                                String numberStr = matcher.group();

                                // Remove commas
                                numberStr = numberStr.replace(",", "");

                                // Convert to integer
                                int number = Integer.parseInt(numberStr);
                                totalAnnualSalary += number;
                                System.out.println(month + " " + year +" : " + number);
                            } else {
                                System.out.println("No valid number found in the text.");
                            }

                    }
                }
            }catch (Exception ignored){
            }
        }
        return totalAnnualSalary;
    }

    private static String readNetPayFromPdf(File file) {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            String[] allLines = text.split("\n");
            for(String line : allLines){
                if(line.contains("Net Pay"))
                {
                    return line;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static List<String > getAllYearsFolderNames(File directory) {
        List<String> list = new ArrayList<>();
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        try {
                            Integer.parseInt(file.getName());
                            list.add(file.getName());
                        }catch (Exception ignored){
                        }
                    }
                }
            }
        }
        return list;
    }

}
