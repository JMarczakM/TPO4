import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class NewsServer {
    private ServerSocket serverSocket;
    private String name;
    private ArrayList<String> news;

    public NewsServer(String name, String[] news) throws IOException {
        this.serverSocket = new ServerSocket(0);
        this.name = name;
        this.news = new ArrayList<>();
        this.news.addAll(List.of(news));
    }

    public String[] getNews(){
        return news.toArray(new String[0]);
    }

    public void addNews(String s){
        news.add(s);
    }
}
