package app;

import criptografia.AES;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import model.Contato;

public class Program {
    
    private static final List<Contato> contatos = new ArrayList<>();

    public static void main(String[] args) throws IOException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException {
        
        contatos.add(new Contato("Bob", "bolabolabolabola"));
        contatos.add(new Contato("Alice", "patopatopatopato"));
        
        ServerSocket principalSocket = new ServerSocket(9998);
        Socket clienteSocket;
        
        do{
            clienteSocket = principalSocket.accept();
            
            DataInputStream dis = new DataInputStream(clienteSocket.getInputStream());
            DataOutputStream dos = new DataOutputStream(clienteSocket.getOutputStream());
            
            String nome = dis.readUTF();
            byte[] destCifrado = new byte[16];
            dis.read(destCifrado);
            Contato rem = new Contato();
            Contato dest = new Contato();
            
            //realizar busca
            for (int i = 0; i < contatos.size(); i++) {
                if (contatos.get(i).getNome().equals(nome)){
                    rem = contatos.get(i);
                    String nomeDest = AES.decifra(destCifrado, rem.getChave());
                    for (int j = 0; j < contatos.size(); j++) {
                        if (contatos.get(j).getNome().equals(nomeDest))
                            dest = contatos.get(j);
                    }
                }
            }
            
            String kSessao = AES.gerarChave();
            byte[] kSessaoRem = AES.cifra(kSessao, rem.getChave());
            byte[] kSessaoDest = AES.cifra(kSessao, dest.getChave());
            
            dos.write(kSessaoRem);
            dos.write(kSessaoDest);
            
        }while(true);
    }
    
}
