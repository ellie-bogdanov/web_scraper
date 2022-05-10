
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

public class Main extends JFrame{
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 800;
    
    

    public Main() {
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setLayout(null);

        MainContent mainContent = new MainContent(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.add(mainContent);
        this.setVisible(true);

    }



    public static void main(String[] args) {
        new Main();
    }   
}