package app;

import criptografia.AES;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class Program {

    public static void main(String[] args) throws IOException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException {
        String kAlice = "patopatopatopato";
        String destinatario;
        String kSessao;
        
        Socket socket;
        socket = new Socket("127.0.0.1", 9999);
        
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        
        //se identifica para o servidor de chat
        dos.writeUTF("Alice");
        
        //recebe e decifra a kSessao
        destinatario = dis.readUTF();
        byte[] dataBytes = AES.base64Decoder(dis.readUTF().getBytes());
        kSessao = AES.decifra(dataBytes, kAlice);
        
        //Gerar numero para o nonce
        Random random = new Random();
        int nonce = 1000 + random.nextInt(8000);
        
        //Cifra e envia o Nonce
        dos.writeUTF(destinatario);
        dos.writeUTF(AES.byteToString(AES.base64Encoder(AES.cifra(Integer.toString(nonce), kSessao))));
        
        dis.readUTF();
        dataBytes = AES.base64Decoder(dis.readUTF().getBytes());
        int nonceResposta = Integer.parseInt(AES.decifra(dataBytes, kSessao));
        
        if (nonce+1 == nonceResposta){
            dos.writeUTF(destinatario);
            dos.writeUTF(AES.byteToString(AES.base64Encoder(AES.cifra("ok", kSessao))));
            System.out.println("Conexão estabeleciada com "+destinatario);
            new Thread(new LeituraRunnable(socket,kSessao)).start();
        
            Scanner scanner;
            scanner = new Scanner(System.in);
            String msg;
            do{
                msg = scanner.nextLine();
                dos.writeUTF(destinatario);
                dos.writeUTF(AES.byteToString(AES.base64Encoder(AES.cifra(msg, kSessao))));
            }while(true);
        }else{
            System.out.println("Conexão negada");
        }   
    }
}
