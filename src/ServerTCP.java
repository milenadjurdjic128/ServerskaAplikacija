import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;


public class ServerTCP {

	//prazan konstruktor
	public ServerTCP() {	
	}

	public static void main (String[] args) {
		//postavljamo da maksimalno 3 klijenta mogu da koriste istovremeno
		LinkedList<NitServerTCP> klijenti = new LinkedList<NitServerTCP>();

		//inicijalizacija klijentskog i serverskog soketa
		Socket klijentskiSoket = null;
		ServerSocket osluskujuciSoket = null;
		
		try {
			//zauzimamo portove
			osluskujuciSoket = new ServerSocket(1908); //za operaciju

			while(true) {

				//otvaramo konekciju i cekamo klijenta
				klijentskiSoket = osluskujuciSoket.accept(); 
				
				//ako ima mesta dodeljujemo tom klijentu novu nit
				klijenti.add(new NitServerTCP(klijenti, klijentskiSoket));
				//pokrecemo run metodu
				klijenti.getLast().start();


			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
