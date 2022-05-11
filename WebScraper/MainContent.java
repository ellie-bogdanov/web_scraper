import javax.swing.*;
import java.awt.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.print.Doc;
import javax.swing.JFrame;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.HashMap;


public class MainContent extends JPanel {
    private JLabel title;
    private JLabel description;
    private JTextField input;
    private JButton search;
    private JLabel country;
    private String ipText;

    public static final int TITLE_X = 250;
    public static final int TITLE_Y = 20;
    public static final int TITLE_WIDTH = 800;
    public static final int TITLE_HEIGHT = 150;
    public static final int TITLE_FONT_SIZE = 100;

    public static final int DESCRIPTION_X = 120;
    public static final int DESCRIPTION_Y = 200;
    public static final int DESCRIPTION_WIDTH = 800;
    public static final int DESCRIPTION_HEIGHT = 100;
    public static final int DESCRIPTION_FONT_SIZE = 50;

    public static final int INPUT_X = 350;
    public static final int INPUT_Y = 330;
    public static final int INPUT_WIDTH = 280;
    public static final int INPUT_HEIGHT = 50;
    public static final int INPUT_FONT_SIZE = 30;

    public static final int SEARCH_X = 440;
    public static final int SEARCH_Y = 430;
    public static final int SEARCH_WIDTH = 100;
    public static final int SEARCH_HEIGHT = 50;

    
    public static final int COUNTRY_X = 440;
    public static final int COUNTRY_Y = 550;
    public static final int COUNTRY_WIDTH = 300;
    public static final int COUNTRY_HEIGHT = 50;
    public static final int COUNTRY_FONTSIZE = 50;

    private boolean isButtonPressed;

    private HashMap<String, ArrayList<String>> countryByIp;

    private ArrayList<String> ips;


    public String getInputText() {
        return input.getText();
    }

    public String getIpText() {
        return ipText;
    }

    private void populateHashMap(String ip, String country) {
        if(this.countryByIp.containsKey(country)){
            ArrayList<String> updatedList = this.countryByIp.get(country);
            updatedList.add(ip);
            this.countryByIp.put(country, updatedList);
        }
        else {
            ArrayList<String> newIpList = new ArrayList<>();
            newIpList.add(ip);
            this.countryByIp.put(country, newIpList);
        }

    }

    public void webScrapper() {
        Pattern NUMBERS_WITH_DOTS = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                                                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                                                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                                                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

        String[] blackList = {"https://www.nirsoft.net/countryip///www.nirsoft.net", "https://www.nirsoft.net/countryip///blog.nirsoft.net",
                                "https://www.nirsoft.net/countryip//search_freeware.html", "https://www.nirsoft.net/countryip//faq.html",
                            "https://www.nirsoft.net/countryip//top_utilities_downloads.html", "https://www.nirsoft.net/countryip//pad",
                            "https://www.nirsoft.net/countryip//contact-new.html", "https://www.nirsoft.net/countryip//about_nirsoft_freeware.html",
                            "https://www.nirsoft.net/countryip/https://www.nirsoft.net/donate.html", "https://www.nirsoft.net/countryip//utils/index.html"
                            , "https://www.nirsoft.net/countryip//password_recovery_tools.html", "https://www.nirsoft.net/countryip//system_tools.html",
                            "https://www.nirsoft.net/countryip//web_browser_tools.html", "https://www.nirsoft.net/countryip//programmer_tools.html",
                            "https://www.nirsoft.net/countryip//network_tools.html", "https://www.nirsoft.net/countryip//outlook_office_software.html",
                            "https://www.nirsoft.net/countryip//x64_download_package.html", "https://www.nirsoft.net/countryip//panel", "https://www.nirsoft.net/countryip//computer_forensic_software.html",
                            "https://www.nirsoft.net/countryip//alpha", "https://www.nirsoft.net/countryip//articles", "https://www.nirsoft.net/countryip/"};
        final String url = "https://www.nirsoft.net/countryip/";
        try {
           final Document document = Jsoup.connect(url).get();
           Element pageTBody = document.select("table tbody").get(0);

           //Document redirectDoc = Jsoup.connect("https://www.nirsoft.net/countryip/dz.html").get();
           //Element outer_table = redirectDoc.selectFirst("table");
           //Element outer_tbody = outer_table.selectFirst("tbody");
           

           

           for (Element tr : pageTBody.select("tr")) {
               for (Element td : tr.select("td")) {
                   String currentCountry = td.text();
                   String redirectLink = "https://www.nirsoft.net/countryip/";
                   
                   if(td.select("a").first() != null) {
                       redirectLink = redirectLink + td.select("a").first().attr("href");
                   }
                   else {
                       continue;
                   }
                   boolean flag = false;
                   for (String string : blackList) {
                       if(redirectLink.equals(string)) {
                           flag = true;
                           break;
                       }
                   }
                   if(flag) {
                       continue;
                   }
                   
                   try {
                    Document redirectDoc = Jsoup.connect(redirectLink).get();
                    Element outer_table = redirectDoc.selectFirst("table");
                    Element outer_tbody = outer_table.selectFirst("tbody");
                    for (Element ip_tr : outer_tbody.select("tr")) {

                        if(ip_tr.attr("class").equals("iptableheader")) {
                            continue;
                        }
                        try {
                            Element firstIp = ip_tr.select("td").get(0);
                            Element secondIp = ip_tr.select("td").get(1);
                            Matcher matcherFirst = NUMBERS_WITH_DOTS.matcher(firstIp.text());
                            Matcher matcherSecond = NUMBERS_WITH_DOTS.matcher(secondIp.text());
                            if(matcherFirst.find() && matcherSecond.find()){
                                populateHashMap(firstIp.text(), td.text());
                                populateHashMap(secondIp.text(), td.text());
                            }



                            
                            if(this.isButtonPressed) {
                                
                                checkIp(this.ipText, currentCountry);
                                
                            }
                            

                        } catch (Exception e) {
                            //TODO: handle exception
                        }

                        /*for (Element ip_td : ip_tr.select("td")) {
                            Matcher matcher = NUMBERS_WITH_DOTS.matcher(ip_td.text()); 
                            if(matcher.find()){
                                //for (String ip : this.ipList) {
                                //    if(ip_td.text().equals(ip)) {
                                //        continue;
                                //    }
                                //}
                                //this.ipList.add(ip_td.text());
                                checkIp("58.147.128.0", ip_td.text());
                            }
                        }*/
                    }
                   } catch (Exception e) {
                   }
                   
               }
               
           }
        } catch (Exception e) {
            
        }
    }
    
    private void checkIp(String ipRecieved, String currentCountry) {
     
            
        for (String key : this.countryByIp.keySet()) {                  
            ArrayList<String> currentCountryIpList = this.countryByIp.get(key);
            for(int i = 0; i < currentCountryIpList.size() - 2; i += 2) {
                this.isButtonPressed = false;
                String firstIp = currentCountryIpList.get(i);
                String secondIp = currentCountryIpList.get(i + 1);

                String[] firstIpToArr = firstIp.split("\\.");
                String[] secondIpToArr = secondIp.split("\\.");
                String[] recievedtIpToArr = ipRecieved.split("\\.");

                    boolean flag = true;
                    for(int j = 0; j < recievedtIpToArr.length; j++) {
                        int ipRecievedToNum = Integer.parseInt(recievedtIpToArr[j]);
                        int firstIpToNum = Integer.parseInt(firstIpToArr[j]);
                        int secondIpToNum = Integer.parseInt(secondIpToArr[j]);
                    
                        System.out.println(ipRecieved);
                        System.out.println(key);
                        if(ipRecievedToNum < firstIpToNum || ipRecievedToNum > secondIpToNum) {
                            flag = false;
                            break;
                        }
                    }
                    if(flag) {
                        this.country.setText(key);
                    }
            }

        }
        

    }
    public MainContent(int width, int height) {
        this.countryByIp = new HashMap<>();
        this.isButtonPressed = false;
        new Thread(() -> {
            webScrapper();
            
        }).start();


        this.setBounds(0,0, width, height);
        this.setDoubleBuffered(true);
        this.setBackground(Color.GRAY);
        this.setLayout(null);


        this.country = new JLabel("ASDAS");
        this.country.setBounds(COUNTRY_X, COUNTRY_Y, COUNTRY_WIDTH, COUNTRY_HEIGHT);
        Font countyFont = new Font("country_font", Font.ITALIC, COUNTRY_FONTSIZE);
        this.country.setFont(countyFont);
        this.add(this.country);

        this.title = new JLabel("IP Locator");
        this.title.setBounds(TITLE_X, TITLE_Y, TITLE_WIDTH, TITLE_HEIGHT);
        Font titleFont = new Font("font", Font.ITALIC, TITLE_FONT_SIZE);
        this.title.setFont(titleFont);
        this.add(title);

        this.description = new JLabel("Enter the IP you want to Locate:");
        this.description.setBounds(DESCRIPTION_X, DESCRIPTION_Y, DESCRIPTION_WIDTH, DESCRIPTION_HEIGHT);
        Font descriptionFont = new Font("desFont", Font.ITALIC, DESCRIPTION_FONT_SIZE);
        this.description.setFont(descriptionFont);
        this.add(description);

        this.input = new JTextField();
        input.setBounds(INPUT_X, INPUT_Y, INPUT_WIDTH, INPUT_HEIGHT);
        Font textFont = new Font("textFont", Font.ITALIC, INPUT_FONT_SIZE);
        this.input.setFont(textFont);
        this.add(input);

        this.search = new JButton("search");
        this.search.setBounds(SEARCH_X, SEARCH_Y, SEARCH_WIDTH, SEARCH_HEIGHT);
        this.add(search);

        search.addActionListener((event) -> {
            this.ipText = this.input.getText();
            this.isButtonPressed = true;
        });

        
    }
}

