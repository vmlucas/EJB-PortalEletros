package EJB.Emprestimo;

import com.eletros.PessoaFisica;
import com.eletros.benef.Patrocinadora;
import com.util.DataEletros;

public class ClienteEmprestimo extends PessoaFisica 
{
    private String numEletros;
    private Patrocinadora patrocinadoraAtual;
    private double salarioAtual;
    private DataEletros dataInscricao;
    
    
    public ClienteEmprestimo() { 
    }

    public void setNumEletros(String numEletros) {
        this.numEletros = numEletros;
    }

    public String getNumEletros() {
        return numEletros;
    }

    public void setSalarioAtual(double salarioAtual) {
        this.salarioAtual = salarioAtual;
    }

    public double getSalarioAtual() {
        return salarioAtual;
    }

    public void setDataInscricao(DataEletros dataInscricao) {
        this.dataInscricao = dataInscricao;
    }

    public DataEletros getDataInscricao() {
        return dataInscricao;
    }

    public void setPatrocinadoraAtual(Patrocinadora patrocinadoraAtual) {
        this.patrocinadoraAtual = patrocinadoraAtual;
    }

    public Patrocinadora getPatrocinadoraAtual() {
        return patrocinadoraAtual;
    }
}
