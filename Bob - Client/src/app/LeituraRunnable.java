package app;

import criptografia.AES;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class LeituraRunnable
implements Runnable {
    private final Socket cliente;
    private final String kSessao;
   
    public LeituraRunnable( Socket cliente, String kSessao ){
        this.cliente = cliente;
        this.kSessao = kSessao;
    }
    
    @Override
    public void run(){
        DataInputStream dis;
        try{
            dis = new DataInputStream(cliente.getInputStream());
            do{
                String remetente = dis.readUTF();
                String msg = AES.decifra(AES.base64Decoder(dis.readUTF().getBytes()), kSessao);
                System.out.println(remetente+": "+msg);
            }while(true);
        }
        catch (IOException ex){
            System.out.println("Ocorreu um erro com a conexao");
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(LeituraRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(LeituraRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(LeituraRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(LeituraRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(LeituraRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
