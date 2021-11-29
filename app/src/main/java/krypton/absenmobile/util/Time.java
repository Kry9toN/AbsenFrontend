package krypton.absenmobile.util;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import krypton.absenmobile.R;
import krypton.absenmobile.siswa.SiswaMainActivity;

public class Time extends AsyncTask<Void, String, String> {

    private Context context;
    private TextView t;
    private String hasil;

    public Time (Context context, TextView t) {
        this.context = context;
        this.t = t;
    }

    @Override
    protected String doInBackground(Void... voids) {
        while(true) {
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
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
                hasil = simpleDateFormat.format(date);
            } catch (IOException e) {
                e.printStackTrace();
            }
            publishProgress(hasil);
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        t.setText(values[0]);
    }
}
