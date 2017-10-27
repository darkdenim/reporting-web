package darkdenim;

import org.apache.http.HttpInetConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sun.net.www.http.HttpCaptureInputStream;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FxIndexTracker {

    private final AppConfig config;

    private int[] threadCount = {0};

    private static final Logger logger = LoggerFactory.getLogger(FxIndexTracker.class);
    private ApplicationContext applicationContext;

    private Map<String, String> indexUrlMap;

    public FxIndexTracker() {
        applicationContext = new ClassPathXmlApplicationContext("historicalPriceContext.xml");
        config = (AppConfig) applicationContext.getBean("appConfig");

        indexUrlMap = new HashMap<>();
        indexUrlMap.put("GBP_I", "http://stooq.com/q/?s=gbp_i");
        indexUrlMap.put("USD_I", "http://stooq.com/q/?s=usd_i");
        indexUrlMap.put("JPY_I", "http://stooq.com/q/?s=jpy_i");
        indexUrlMap.put("EUR_I", "http://stooq.com/q/?s=eur_i");
        indexUrlMap.put("CHF_I", "http://stooq.com/q/?s=chf_i");

//        indexUrlMap.put("GBPUSD", "http://stooq.com/q/?s=GBPUSD");
//        indexUrlMap.put("GBPEUR", "http://stooq.com/q/?s=GBPEUR");
//        indexUrlMap.put("GBPCHF", "http://stooq.com/q/?s=GBPCHF");
//        indexUrlMap.put("GBPJPY", "http://stooq.com/q/?s=GBPJPY");
//
//        indexUrlMap.put("EURUSD", "http://stooq.com/q/?s=EURUSD");
//        indexUrlMap.put("EURGBP", "http://stooq.com/q/?s=EURGBP");
//        indexUrlMap.put("EURJPY", "http://stooq.com/q/?s=EURJPY");
//
//        indexUrlMap.put("USDGBP", "http://stooq.com/q/?s=USDGBP");
//        indexUrlMap.put("USDEUR", "http://stooq.com/q/?s=USDEUR");
//        indexUrlMap.put("USDCHF", "http://stooq.com/q/?s=USDCHF");
//        indexUrlMap.put("USDJPY", "http://stooq.com/q/?s=USDJPY");
//
//        indexUrlMap.put("CHFUSD", "http://stooq.com/q/?s=CHFUSD");
//        indexUrlMap.put("CHFGBP", "http://stooq.com/q/?s=CHFGBP");
//        indexUrlMap.put("CHFJPY", "http://stooq.com/q/?s=CHFJPY");
    }

    public void dataImport() {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(config.getProxyUrl(), config.getProxyPort()));
        Authenticator authenticator = new Authenticator() {

            public PasswordAuthentication getPasswordAuthentication() {
                return (new PasswordAuthentication(config.getProxyUsername(), config.getProxyPassword().toCharArray()));
            }
        };
        Authenticator.setDefault(authenticator);

        Map<String, Double> weakMap = new HashMap<>();
        Map<String, Double> strongMap = new HashMap<>();

        for (String name : indexUrlMap.keySet()) {
            try {
                URLConnection con = new URL(indexUrlMap.get(name)).openConnection(proxy);
                InputStream is = con.getInputStream();
                String fileName = name+".html";
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName));
                byte[] b = new byte[1024];
                while(is.read(b) != -1) {
                    out.write(b);
                }
                out.flush();
                out.close();
                is.close();
                logger.debug("all done");

                String key = "<title>"+name;
                Double val = null;
                BufferedReader br = new BufferedReader(new FileReader(fileName));
                String line;
                while((line = br.readLine()) != null) {
                    if (line.contains(key)) {
                        String s = line.substring(line.indexOf(key)+key.length(), line.indexOf("%")).replaceAll("\\(", "").trim();
                        val = Double.parseDouble(s);
                        if (val < 0) {
                            if (weakMap.isEmpty()) {
                                weakMap.put(name, val);
                            } else {
                                String k = weakMap.keySet().iterator().next();
                                if (weakMap.get(k) > val) {
                                    weakMap.clear();
                                    weakMap.put(name, val);
                                }
                            }
                        } else {
                            if (strongMap.isEmpty()) {
                                strongMap.put(name, val);
                            } else {
                                String k = strongMap.keySet().iterator().next();
                                if (strongMap.get(k) < val) {
                                    strongMap.clear();
                                    strongMap.put(name, val);
                                }
                            }
                        }
                        break;
                    }
                }
                br.close();
                logger.debug(name + ": " + val);
                //<title>GBP_I (+1.96%)

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String code;
        if (!weakMap.isEmpty()) {
            code = weakMap.keySet().iterator().next();
            logger.debug("Weakest: " + code + ": " + weakMap.get(code));
        }
        if (!strongMap.isEmpty()) {
            code = strongMap.keySet().iterator().next();
            logger.debug("Strongest: " + code + ": " + strongMap.get(code));
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting Tracker...");
        FxIndexTracker fxIndexTracker = new FxIndexTracker();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                fxIndexTracker.dataImport();
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 1000, 1000*60*30);

//        System.exit(0);
    }

}
