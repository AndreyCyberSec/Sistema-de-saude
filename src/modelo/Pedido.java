package modelo;

import java.util.ArrayList;
import java.util.List;

public class Pedido {

    private int    idPedido;
    private Usuario usuario;      // quem fez o pedido
    private List<Remedio> remedios; // lista de medicamentos solicitados
    private String status;

  //criar um novo pedido
    public Pedido(int id, Usuario usuario) {
        this.idPedido = id;
        this.usuario  = usuario;
        this.remedios = new ArrayList<>();
        this.status   = "Pendente";
    }

    
     // Construtor usado ao carregar pedidos salvos.
     
    public Pedido(int id, Usuario usuario, String status) {
        this.idPedido       = id;
        this.usuario  = usuario;
        this.remedios = new ArrayList<>();
        this.status   = status;
    }

    public void adicionarRemedio(Remedio remedio) {
        remedios.add(remedio);
    }


    public boolean precisaReceita() {
        for (Remedio r : remedios) {
            if (r.isPrecisaReceita()) return true;
        }
        return false;
    }

    public int           getId()       { return idPedido; }
    public Usuario       getUsuario()  { return usuario; }
    public List<Remedio> getRemedios() { return remedios; }
    public String        getStatus()   { return status; }
    public void          setStatus(String status) { this.status = status; }
}