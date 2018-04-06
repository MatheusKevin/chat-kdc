package app;

import criptografia.AES;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class Program {

    public static void main(String[] args) throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        String kBob = "bolabolabolabola";
        String destinatario = "Alice";
        String kSessao;
        
        Socket socket;
        socket = new Socket("127.0.0.1", 9998);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());
          
        byte[] text = AES.cifra(destinatario, kBob);
        dos.writeUTF("Bob");
        dos.write(text);

        byte[] kSessaoBob = new byte[32];
        dis.read(kSessaoBob);
        byte[] kSessaoAlice = new byte[32];
        dis.read(kSessaoAlice);
        kSessao = AES.decifra(kSessaoBob, kBob);
        
        socket.close();
        
        socket = new Socket("127.0.0.1", 9999);
        dos = new DataOutputStream(socket.getOutputStream());
        dis = new DataInputStream(socket.getInputStream());
        
        //se identifica para o servidor
        dos.writeUTF("Bob");
        
        //envia a kSessão para a alice
        dos.writeUTF(destinatario);
        dos.writeUTF(AES.byteToString(AES.base64Encoder(kSessaoAlice)));
        
        //recebe o número nonce gerado pela alice e soma +1
        dis.readUTF();
        byte[] dataBytes = AES.base64Decoder(dis.readUTF().getBytes());
        int nonce = Integer.parseInt(AES.decifra(dataBytes, kSessao));
        nonce++;
        
        //envia o novo número nonce para a alice
        dos.writeUTF(destinatario);
        dos.writeUTF(AES.byteToString(AES.base64Encoder(AES.cifra(Integer.toString(nonce), kSessao))));
        
        dis.readUTF();
        String nonceResposta = AES.decifra(AES.base64Decoder(dis.readUTF().getBytes()), kSessao);
        
        if (nonceResposta.equals("ok")){
            System.out.println("Conexão estabelecida com "+destinatario);
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
            System.out.println("Conexão recusada");
        }
    }
}
