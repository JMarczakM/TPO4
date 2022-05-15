import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;


public class Server {
	private ArrayList<Topic> topics;
	private HashMap<SocketChannel, ArrayList<Topic>> clientsSubscribed;


	public static void main(String[] args) throws IOException, InterruptedException {
		new Server();
	}
	
	Server () throws IOException {
		topics = new ArrayList<>();
		clientsSubscribed = new HashMap<>();
		topics.add(new Topic("Muzyka", "Muzyka gra"));
		topics.add(new Topic("Motoryzacja", "Samochód jeździ"));
	
			// Utworzenie kanału gniazda serwera
			// i związanie go z konkretnym adresem (host+port)
		String host = "localhost";
		int port = 12345;
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.socket().bind(new InetSocketAddress(host, port));

			// Ustalenie trybu nieblokującego
			// dla kanału serwera gniazda
		serverChannel.configureBlocking(false);
		
			// Utworzenie selektora
		Selector selector = Selector.open();
		
			// Rejestracja kanału gniazda serwera u selektora
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		System.out.println("Serwer: czekam ... ");
		
			// Selekcja gotowych operacji do wykonania i ich obsługa
			// w pętli dzialania serwera
		while (true) {
			
					// Selekcja gotowej operacji
			  		// To wywolanie jest blokujące
			  		// Czeka aż selektor powiadomi o gotowości jakiejś operacji na jakimś kanale
			selector.select();
			 
			  		// Teraz jakieś operacje są gotowe do wykonania
			  		// Zbiór kluczy opisuje te operacje (i kanały)
			 Set<SelectionKey> keys = selector.selectedKeys();
			 
			  		// Przeglądamy "gotowe" klucze
			 Iterator<SelectionKey> iter = keys.iterator();
			  
			 while(iter.hasNext()) {  
			    
				  	// pobranie klucza
				 SelectionKey key = iter.next();
			    
				  	// musi być usunięty ze zbioru (nie ma autonatycznego usuwania)
				  	// w przeciwnym razie w kolejnym kroku pętli "obsłużony" klucz
				  	// dostalibyśmy do ponownej obsługi
				 iter.remove();                                                  
			    
			    		// Wykonanie operacji opisywanej przez klucz
				 if (key.isAcceptable()) { // połaczenie klienta gotowe do akceptacji
			      
					 System.out.println("Serwer: ktoś się połączył ..., akceptuję go ... ");
			    		// Uzyskanie kanału do komunikacji z klientem
			    		// accept jest nieblokujące, bo już klient czeka
					 SocketChannel cc = serverChannel.accept();
			      
			    		// Kanał nieblokujący, bo będzie rejestrowany u selektora
					 cc.configureBlocking(false);
			    		
			    		// rejestrujemy kanał komunikacji z klientem
			    		// do monitorowania przez ten sam selektor
					 cc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

					 clientsSubscribed.put(cc,new ArrayList<>(topics));
			      
					 continue;
				  }
			    
				  if (key.isReadable()) {  // któryś z kanałów gotowy do czytania
			      
			    			// Uzyskanie kanału na którym czekają dane do odczytania
					  SocketChannel cc = (SocketChannel) key.channel();
					  
					  serviceRequest(cc); 
			
			    			//obsługa zleceń klienta
			    			// ... 
					  continue;
				  }
				  if (key.isWritable()) {  // któryś z kanałów gotowy do pisania
			    	
					  		// Uzyskanie kanału
//					  SocketChannel cc = (SocketChannel) key.channel();
			      
			    			// pisanie do kanału
			    			// ...
					  continue;
				  } 
				 			  
			 }
		}
	
	}
	
	
		// Strona kodowa do kodowania/dekodowania buforów
	private static Charset charset  = Charset.forName("ISO-8859-2");
	private static final int BSIZE = 1024;

	  	// Bufor bajtowy - do niego są wczytywane dane z kanału
	private ByteBuffer bbuf = ByteBuffer.allocate(BSIZE);

	  	// Tu będzie zlecenie do pezetworzenia
	private StringBuffer reqString = new StringBuffer();
	
	
	private void serviceRequest(SocketChannel sc) {
		if (!sc.isOpen()) return; // jeżeli kanał zamknięty
	 
		System.out.print("Serwer: czytam komunikat od klienta ... ");
			// Odczytanie zlecenia
		reqString.setLength(0);
	    bbuf.clear();
	    
	    try {
	    	readLoop:                    // Czytanie jest nieblokujące
	    	while (true) {               // kontynujemy je dopóki
	    		int n = sc.read(bbuf);   // nie natrafimy na koniec wiersza
	    		if (n > 0) {
	    			bbuf.flip();
	    			CharBuffer cbuf = charset.decode(bbuf);
	    			while(cbuf.hasRemaining()) {
	    				char c = cbuf.get();
	    				//System.out.println(c);
	    				if (c == '\r' || c == '\n') break readLoop;
	    				else {
	    					//System.out.println(c);
	    				    reqString.append(c);
	    				}
	    			}
	    		}
	      }
	    		
		    String cmd = reqString.toString();
		    System.out.println(reqString);
			String[] cmdspited = cmd.split(" ");
		    
		    if (cmd.equals("Hi")) {
		    	sc.write(charset.encode(CharBuffer.wrap("Hi")));
		    } 
		    else if (cmd.equals("Bye")) {           // koniec komunikacji
		    										
		    	sc.write(charset.encode(CharBuffer.wrap("Bye")));
		  	  	System.out.println("Serwer: mówię \"Bye\" do klienta ...\n\n");  
		    	
		  	  	sc.close();                      // - zamknięcie kanału  
		        sc.socket().close();			 // i gniazda
		       
		    } else if(cmd.split(" ")[0].equals("subscribe")){
				for(Topic topic : topics){
					if(topic.getName().equals(cmdspited[1])){
						if(clientsSubscribed.get(sc).contains(topic)) {
							sc.write(charset.encode(CharBuffer.wrap("Already subscribed to "+cmd.split(" ")[1])));
							break;
						}
						clientsSubscribed.get(sc).add(topic);
						sc.write(charset.encode(CharBuffer.wrap("Subscribed to "+cmd.split(" ")[1])));
						sc.write(charset.encode(CharBuffer.wrap(topic.getMessage())));
						break;
					}
				}
			} else if(cmd.split(" ")[0].equals("unsubscribe")){
				for(Topic topic : topics){
					if(topic.getName().equals(cmdspited[1])){
						clientsSubscribed.get(sc).remove(topic);
						sc.write(charset.encode(CharBuffer.wrap("Unsubscribed to "+cmd.split(" ")[1])));
						break;
					}
				}
			} else if(cmd.split(" ")[0].equals("create")){
				StringBuilder stringBuilder = new StringBuilder();
				for (int i=0; i< cmdspited.length-2;i++) {
					stringBuilder.append(cmdspited[i+2]);
				}
				topics.add(new Topic(cmd.split(" ")[1], stringBuilder.toString()));
				sc.write(charset.encode(CharBuffer.wrap("Created "+cmd.split(" ")[1])));
			} else if(cmd.split(" ")[0].equals("deleteTopic")){
				if(topics.removeIf(topic -> topic.getName().equals(cmdspited[1]))){
					sc.write(charset.encode(CharBuffer.wrap("Deleted "+cmd.split(" ")[1])));
				} else {
					sc.write(charset.encode(CharBuffer.wrap("Coudn't find "+cmd.split(" ")[1])));
				}
			} else if(cmd.split(" ")[0].equals("replace")){
				for(Topic topic : topics){
					if(topic.getName().equals(cmdspited[1])){
						StringBuilder stringBuilder = new StringBuilder();
						for (int i=0; i< cmdspited.length-2;i++) {
							stringBuilder.append(cmdspited[i+2]);
						}
						topic.setMessage(stringBuilder.toString());
						sc.write(charset.encode(CharBuffer.wrap("Replaced "+cmd.split(" ")[1])));
						break;
					}
				}
				sendTopicToAll(cmdspited[1]);
			} else if(cmd.split(" ")[0].equals("sendToAll")){
				sendAllToAll();
			} else{
				// echo do Klienta
				sc.write(charset.encode(CharBuffer.wrap(reqString)));
			}


	 
	    } catch (Exception exc) { // przerwane polączenie?
	    	exc.printStackTrace();
	        try { sc.close();
	              sc.socket().close();
	        } catch (Exception e) {}
	    }
	    
	}

	public void sendAllToAll() throws IOException {
		for(SocketChannel socketChannel : clientsSubscribed.keySet()){
			for (Topic topic : clientsSubscribed.get(socketChannel)) {
				socketChannel.write(charset.encode(CharBuffer.wrap(topic.getMessage())));
			}
		}
	}

	public void sendTopicToAll(String topicName) throws IOException {
		for(SocketChannel socketChannel : clientsSubscribed.keySet()){
			for (Topic topic : clientsSubscribed.get(socketChannel)) {
				if(topic.getName().equals(topicName)){
					socketChannel.write(charset.encode(CharBuffer.wrap(topic.getMessage())));
				}
			}
		}
	}


}
