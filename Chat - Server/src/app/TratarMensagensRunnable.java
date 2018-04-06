package app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import model.Cliente;



public class TratarMensagensRunnable implements Runnable {
    private final Cliente cliente;
    private final List<Cliente> clientes;

    
    public TratarMensagensRunnable(Cliente cliente, List<Cliente> clientes){
        this.cliente = cliente;
        this.clientes = clientes;
    }
    
    @Override
    public void run(){
        DataInputStream dis;
        try{
            dis = new DataInputStream(cliente.getSocket().getInputStream());
            do{
                String dest = dis.readUTF();
                String msg = dis.readUTF();
                  
                for(Cliente c : clientes){
                    if (c.getNome().equals(dest)){
                        DataOutputStream dos = new DataOutputStream(c.getSocket().getOutputStream());
                        dos.writeUTF(cliente.getNome());
                        dos.writeUTF(msg);
                    }           
                }
            }while(true);
            
        }
        catch (IOException ex){
            System.out.println("Ocorreu um erro de conexao com o cliente: " + 
                    cliente.getNome());
            clientes.remove(cliente);
        }
    }
}
