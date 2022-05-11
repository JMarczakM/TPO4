import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewsServer {
    private HashMap<String, ArrayList<String>> news;

    public NewsServer(HashMap<String, ArrayList<String>> news) throws IOException {
        this.news = news;
    }

    public NewsServer() {
        this.news = new HashMap<>();
    }

    public String[] getNews(){
        return new String[]{"todo","placeholder"};
    }

    public void addNews(String s){

    }

    public void deleteNews(String s){

    }
}
