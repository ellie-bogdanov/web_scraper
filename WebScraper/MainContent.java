import javax.swing.*;
import java.awt.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

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

    private static final int AMOUNT_OF_COUNTRIES = 214; 

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

    
    public static final int COUNTRY_X = 300;
    public static final int COUNTRY_Y = 550;
    public static final int COUNTRY_WIDTH = 500;
    public static final int COUNTRY_HEIGHT = 100;
    public static final int COUNTRY_FONTSIZE = 50;

    private boolean isButtonPressed;

    private HashMap<String, ArrayList<String>> countryByIp;

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
        //blacklist of urls im getting, i couldnt see where im getting those, probably hidden in the html page and not removen
        //so if the url im getting is one of those then im ignoring
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
           final Document document = Jsoup.connect(url).get(); //first page with country names
           Element pageTBody = document.select("table tbody").get(0);

           for (Element tr : pageTBody.select("tr")) {
               for (Element td : tr.select("td")) {
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
                    Document redirectDoc = Jsoup.connect(redirectLink).get(); //page with the ip table of specific country
                    Element outer_table = redirectDoc.selectFirst("table");
                    Element outer_tbody = outer_table.selectFirst("tbody");
                    for (Element ip_tr : outer_tbody.select("tr")) {

                        if(ip_tr.attr("class").equals("iptableheader")) {
                            continue;
                        }
                        try {
                            Element firstIp = ip_tr.select("td").get(0); //starting ip
                            Element secondIp = ip_tr.select("td").get(1); //ending ip
                            Matcher matcherFirst = NUMBERS_WITH_DOTS.matcher(firstIp.text()); //regex to check if given value is an actual ip
                            Matcher matcherSecond = NUMBERS_WITH_DOTS.matcher(secondIp.text());
                            if(matcherFirst.find() && matcherSecond.find()){
                                populateHashMap(firstIp.text(), td.text());
                                populateHashMap(secondIp.text(), td.text());
                            }



                            if(this.countryByIp.size() >=  AMOUNT_OF_COUNTRIES){
                                if(this.isButtonPressed) {
                                    checkIp(this.ipText);
                                }
                            }
                            else {
                                double onePercent = AMOUNT_OF_COUNTRIES / 100; //calculating the percanteges for the loading
                                double perecentage = this.countryByIp.size() / onePercent;
                                this.country.setText(String.valueOf("Loading: " + perecentage + "%"));
                                if(perecentage >= 99) {
                                    this.country.setText("");
                                }
                            }

                        } catch (Exception e) {

                        }

                    }
                   } catch (Exception e) {
                   }
                   
               }
               
           }
        } catch (Exception e) {
            
        }
    }
    
    private void checkIp(String ipRecieved) {
     
        boolean isCountryFound = false;
        for (String key : this.countryByIp.keySet()) {                  
            ArrayList<String> currentCountryIpList = this.countryByIp.get(key);
            for(int i = 0; i < currentCountryIpList.size() - 2; i += 2) { //when scrapping im adding all the ip's into one array but i have to take 
                                                                          //them in pairs one for the start and one for the end
                                                                                                         
                this.isButtonPressed = false;
                String firstIp = currentCountryIpList.get(i); 
                String secondIp = currentCountryIpList.get(i + 1);

                String[] firstIpToArr = firstIp.split("\\."); //removing the dots for comparing
                String[] secondIpToArr = secondIp.split("\\.");
                String[] recievedtIpToArr = ipRecieved.split("\\.");

                    boolean flag = true; //if contradiction not found then showing the country to the user i.e country found
                    for(int j = 0; j < recievedtIpToArr.length; j++) {
                        int ipRecievedToNum = Integer.parseInt(recievedtIpToArr[j]);
                        int firstIpToNum = Integer.parseInt(firstIpToArr[j]);
                        int secondIpToNum = Integer.parseInt(secondIpToArr[j]);
                    
                        if(ipRecievedToNum < firstIpToNum || ipRecievedToNum > secondIpToNum) {
                            flag = false;
                            break;
                        }
                    }
                    if(flag) {
                        this.country.setText(key);
                        isCountryFound = true;
                    }
            }

        }
        if(!isCountryFound) {
            this.country.setText("Error: not found");
        }
        

    }
    public MainContent(int width, int height) {
        this.countryByIp = new HashMap<>();
        this.isButtonPressed = false;
        new Thread(() -> { //seperating the scrapping and display into different threads
            webScrapper();
            
        }).start();

        //main window appearence

        this.setBounds(0,0, width, height);
        this.setDoubleBuffered(true);
        this.setBackground(Color.GRAY);
        this.setLayout(null);


        this.country = new JLabel("");
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

