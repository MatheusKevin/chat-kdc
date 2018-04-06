package app;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import model.Cliente;


public class Program {

    private static final List<Cliente> clientes = 
            Collections.synchronizedList(new ArrayList<>());
    
    public static void main(String[] args) throws IOException {
        
        ExecutorService pool;
        pool = Executors.newCachedThreadPool();
        
        ServerSocket principalSocket;
        principalSocket = new ServerSocket(9999);
        
        do
        {
            Socket clienteSocket;
            clienteSocket = principalSocket.accept();
            DataInputStream dis = new DataInputStream(clienteSocket.getInputStream());
            String nome = dis.readUTF();
            Cliente cliente = new Cliente(nome, clienteSocket);
            clientes.add(cliente);
            
            pool.submit(new TratarMensagensRunnable(cliente, 
                    clientes));
        }
        while(true);
    }
}