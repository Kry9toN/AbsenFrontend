package krypton.absenmobile.util.time;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeCheck {

    public static String time() {
        String hasil = null;
        try {
            String url = "https://time.is/Unix";
            Document doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
            String[] tags = new String[]{
                    "div[id=time_section]",
                    "div[id=clock0_bg]"
            };
            Elements elements = doc.select(tags[0]);
            for (int i = 0; i < tags.length; i++) {
                elements = elements.select(tags[i]);
            }
            long jam = Long.parseLong(elements.text() + "000");
            Date date = new Date(jam);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
            hasil = simpleDateFormat.format(date);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hasil;
    }
}
