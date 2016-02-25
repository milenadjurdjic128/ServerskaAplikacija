import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class NitServerTCP extends Thread{

	public NitServerTCP(){
		//prazan konstruktor
	}
	
	Socket komunikacioniSoketServer = null; //osluskujuci soket za operaciju
	Socket komunikacioniSoketServerOperatori = null; //osluskujuci soket za operatore
	LinkedList<NitServerTCP> klijenti = null; //konektovani klijenti
	
	//konstruktor sa inicijalizacijom
	public NitServerTCP(LinkedList<NitServerTCP> klijenti, Socket komunikacioniSoketServer){
		this.komunikacioniSoketServer = komunikacioniSoketServer;
		this.klijenti = klijenti;
	}
	
	PrintStream izlazniTokServeraKontrolni = null;
	BufferedReader ulazniTokSeveraKontrolni = null;
	PrintStream izlazniTokServeraOperatori = null;
	BufferedReader ulazniTokSeveraOperatori = null;
	ServerSocket osluskujuciSoket2 = null;
	String operatoriKlijenta = "";
	
	public void run() {
		try {
			//otvaranje ulaznog i izlaznog toka
			ulazniTokSeveraKontrolni = new BufferedReader(new InputStreamReader(komunikacioniSoketServer.getInputStream()));
			izlazniTokServeraKontrolni = new PrintStream(komunikacioniSoketServer.getOutputStream());
			String operacijaKlijenta = ulazniTokSeveraKontrolni.readLine();
			
			osluskujuciSoket2 = new ServerSocket(3128);
			
			while(true) {
				komunikacioniSoketServerOperatori = osluskujuciSoket2.accept(); 
				
				ulazniTokSeveraOperatori = new BufferedReader(new InputStreamReader(komunikacioniSoketServerOperatori.getInputStream()));
				izlazniTokServeraOperatori = new PrintStream(komunikacioniSoketServerOperatori.getOutputStream());
				operatoriKlijenta = ulazniTokSeveraOperatori.readLine();
				
				System.out.println("da li su stigli brojevi" + operatoriKlijenta);
				if(operatoriKlijenta.contains("  ")) {
					izlazniTokServeraOperatori.println("Izmedju 2 broja mora da postoji tacno jedan razmak.");
				}
				//razdvajamo String koji smo dobili sa operatorima po kriterijumu razmaka
				String[] razdvojeniBrojevi = operatoriKlijenta.split(" ");
				System.out.println("Duzina liste" + klijenti.size());
				
				//novi niz koji ce sadrzati double brojeve sa kojima cemo vrsiti operacije
				double[] operatori = new double[razdvojeniBrojevi.length];
				
				//pretvaramo String niz sa brojevima u prave brojeve parsiranjem kako bismo mogli da vrsimo odgovarajuce operacije sa njima
				for (int i = 0; i < razdvojeniBrojevi.length; i++) {
					operatori[i] = Double.parseDouble(razdvojeniBrojevi[i]);
				}
				
				//ako nije uneo dva operatora nema smisla racunati
				if(operatori.length <= 1) {
					izlazniTokServeraOperatori.println("Unesite bar dva broja!");
				}
				
				double rezultatServera = 0;
				
				if (operacijaKlijenta.equals("sabiranje")) {
					for (int i = 0; i < operatori.length; i++) {
						rezultatServera = rezultatServera + operatori[i];
					}
				}
			
				if (operacijaKlijenta.equals("oduzimanje")) {
					rezultatServera = operatori[0];
					for (int i = 1; i < operatori.length; i++) {
						rezultatServera = rezultatServera - operatori[i];
					}
				}
			
				if (operacijaKlijenta.equals("mnozenje")) {
					rezultatServera = 1;
					for (int i = 0; i < operatori.length; i++) {
						rezultatServera = rezultatServera * operatori[i];
					}
				}
			
				if (operacijaKlijenta.equals("deljenje")) {
					rezultatServera = operatori[0];
					for (int i = 1; i < operatori.length; i++) {
						if(operatori[i] != 0) {
						rezultatServera = rezultatServera / operatori[i];
						} else {
							izlazniTokServeraOperatori.println("Ne moze se deliti sa nulom!");
							break;
						}
					}
				}
				//saljem klijentu rezultat racunanja
				izlazniTokServeraOperatori.println(rezultatServera);
			}	
			
		} catch (NumberFormatException n) {
			izlazniTokServeraOperatori.println("Uneli ste tekst! Izbrisite razmake sa pocetka i kraja!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
