package extrato;

public class Lancamento 
{
    private String data;
    private double salario;
    private double valorAplicBasicaParticipante;
    private double valorAplicExtraParticipante;
    private double valorAplicPatrocinadora;
    private double valorCota;
    private double debitos;
    private double quantidadeCotaLiq;
    private double valorTotalAplic;
    
    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setValorAplicBasicaParticipante(double valorAplicBasicaParticipante) {
        this.valorAplicBasicaParticipante = valorAplicBasicaParticipante;
    }

    public double getValorAplicBasicaParticipante() {
        return valorAplicBasicaParticipante;
    }

    public void setValorAplicExtraParticipante(double valorAplicExtraParticipante) {
        this.valorAplicExtraParticipante = valorAplicExtraParticipante;
    }

    public double getValorAplicExtraParticipante() {
        return valorAplicExtraParticipante;
    }

    public void setValorAplicPatrocinadora(double valorAplicPatrocinadora) {
        this.valorAplicPatrocinadora = valorAplicPatrocinadora;
    }

    public double getValorAplicPatrocinadora() {
        return valorAplicPatrocinadora;
    }

    public void setValorCota(double valorCota) {
        this.valorCota = valorCota;
    }

    public double getValorCota() {
        return valorCota;
    }

    

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public double getSalario() {
        return salario;
    }

    public void setQuantidadeCotaLiq(double quantidadeCotaLiq) {
        this.quantidadeCotaLiq = quantidadeCotaLiq;
    }

    public double getQuantidadeCotaLiq() {
        return quantidadeCotaLiq;
    }

    public void setValorTotalAplic(double valorTotalAplic) {
        this.valorTotalAplic = valorTotalAplic;
    }

    public double getValorTotalAplic() {
        return valorTotalAplic;
    }

    public void setDebitos(double debitos) {
        this.debitos = debitos;
    }

    public double getDebitos() {
        return debitos;
    }
}
