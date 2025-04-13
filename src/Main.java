import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException  {
        OZPH ozph = new OZPH();
        new Thread(()->{
            while(true){
                try {
                    TimeUnit.SECONDS.sleep(5);
                    ozph.requestOZPHPrice();
                    Server.setPrice(ozph.getPrice());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                System.out.println(ozph.getPrice());
            }
        }).start();
        new Thread(()->{
            HttpServer server = null;
            try {
                server = HttpServer.create(new InetSocketAddress(9999), 0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            server.createContext("/metrics", new Server());
            server.setExecutor(null);
            server.start();
        }).start();
    }
    static class Server implements HttpHandler{
        static private double price;
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            Headers map = exchange.getResponseHeaders();
            map.add("Content-Type","text/plain; version=0.0.4; charset=utf-8; escaping=underscores");
            String resp = "ozph " + Server.price;
            exchange.sendResponseHeaders(200,resp.length());
            OutputStream os = exchange.getResponseBody();
            os.write(resp.getBytes());
            os.close();
        }
        static public void setPrice(double price){
            Server.price = price;
        }
    }
}
class OZPH{
    private double price;

    public OZPH() throws IOException {
        File file = new File("log.txt");
        file.createNewFile();
    }

    public void requestOZPHPrice() throws Exception{
        URL url = new URL("https://www.tbank.ru/invest/stocks/OZPH/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder responseContent = new StringBuilder();
        String inputLine;

        FileWriter file = new FileWriter(new File("log.txt"),true);

        while((inputLine = reader.readLine()) != null) {
            if (inputLine.contains("стоимость одной акции компании Озон Фармацевтика составляет")){
                String price = inputLine.split(" стоимость одной акции компании Озон Фармацевтика составляет ")[1].split(" ")[0].replace(",", ".");
                this.setPrice(Double.parseDouble(price));
                file.write("ozph: " + price + "\n");
            }
        }
        reader.close();
        file.close();
    }

    public double getPrice(){
        return  this.price;
    }

    public void setPrice(double price){
        this.price = price;
    }
}