import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Admin {
	ByteBuffer inBuf = ByteBuffer.allocateDirect(1024);
	CharBuffer cbuf = null;
	SocketChannel channel = null;
	Charset charset  = Charset.forName("ISO-8859-2");

	public static void main(String[] args) throws IOException, InterruptedException {
		Admin admin = new Admin();
		admin.start();
	}

	public void start() throws IOException, InterruptedException {
		AdminUi adminUi = new AdminUi(this);
		adminUi.start();

		String server = "localhost"; // adres hosta serwera
		int port = 12345; // numer portu

		try {
			// Utworzenie kanału
			channel = SocketChannel.open();

			// Ustalenie trybu nieblokującego
			channel.configureBlocking(false);

			// połączenie kanału
			channel.connect(new InetSocketAddress(server, port));

			System.out.print("Klient: łączę się z serwerem ...");

			while (!channel.finishConnect()) {
				// ew. pokazywanie czasu łączenia (np. pasek postępu)
				// lub wykonywanie jakichś innych (krótkotrwałych) działań
			}

		} catch(UnknownHostException exc) {
			System.err.println("Uknown host " + server);
			// ...
		} catch(Exception exc) {
			exc.printStackTrace();
			// ...
		}

		System.out.println("\nKlient: jestem połączony z serwerem ...");


		Scanner scanner = new Scanner(System.in);

		// Alokowanie bufora bajtowego
		// allocateDirect pozwala na wykorzystanie mechanizmów sprzętowych
		// do przyspieszenia operacji we/wy
		// Uwaga: taki bufor powinien być alokowany jednokrotnie
		// i wielokrotnie wykorzystywany w operacjach we/wy



		System.out.println("Klinet: wysyłam - Hi");
		// "Powitanie" do serwera
		channel.write(charset.encode("Hi\n"));

		// pętla czytania
		while (true) {

			//cbuf = CharBuffer.wrap("coś" + "\n");

			inBuf.clear();	// opróżnienie bufora wejściowego
			int readBytes = channel.read(inBuf); // czytanie nieblokujące
			// natychmiast zwraca liczbę
			// przeczytanych bajtów

			// System.out.println("readBytes =  " + readBytes);

			if (readBytes == 0) {                              // jeszcze nie ma danych
				//System.out.println("zero bajtów");

				// jakieś (krótkotrwałe) działania np. info o upływającym czasie


				continue;

			}
			else if (readBytes == -1) { // kanał zamknięty po stronie serwera
				// dalsze czytanie niemożlwe
				// ...
				break;
			}
			else {		// dane dostępne w buforze
				//System.out.println("coś jest od serwera");

				inBuf.flip();	// przestawienie bufora

				// pobranie danych z bufora
				// ew. decyzje o tym czy mamy komplet danych - wtedy break
				// czy też mamy jeszcze coś do odebrania z serwera - kontynuacja
				cbuf = charset.decode(inBuf);

				String odSerwera = cbuf.toString();

//				clientUi.printToTextLog("Serwer właśnie odpisał ... " + odSerwera);
				System.out.println("Klient: serwer właśnie odpisał ... " + odSerwera);
				adminUi.printToTextLog(odSerwera);
				cbuf.clear();

				if (odSerwera.equals("Bye")) break;
			}

//			// Teraz klient pisze do serwera poprzez Scanner
//			String input = scanner.nextLine();
//			cbuf = CharBuffer.wrap(input + "\n");
//			ByteBuffer outBuf = charset.encode(cbuf);
//			channel.write(outBuf);
//
//			System.out.println("Klient: piszę " + input);
		}

		scanner.close();
	}

	public void printToServer(String s) throws IOException {
		String input = s;
		cbuf = CharBuffer.wrap(input + "\n");
		ByteBuffer outBuf = charset.encode(cbuf);
		channel.write(outBuf);
	}
}