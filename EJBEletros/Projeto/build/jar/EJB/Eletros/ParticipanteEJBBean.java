package EJB.Eletros;

import EJB.Eletros.DAO.*;

import EJB.Indices.IndicesEJB;
import EJB.Indices.IndicesEJBHome;

import com.eletros.benef.*;
import com.eletros.benef.Beneficio;
import com.util.DataEletros;

import java.util.Collection;
import java.util.List;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import javax.naming.InitialContext;

import javax.rmi.PortableRemoteObject;


/**
 * Classe que compoe o Session bean Participantes.
 * Session bean responsavel por buscar um participante na base de dados da Eletros e
 * na Marlin.
 */
public class ParticipanteEJBBean implements SessionBean {
    private SessionContext _context;
    
    private IndicesEJB indiceEJB;
    private ParticipanteDAO dataAccess;
        
    
    
    public ParticipanteEJBBean() {
        try{
            
           InitialContext ctx = new InitialContext();
           Object ref = ctx.lookup("IndicesEJB");
           IndicesEJBHome home = (IndicesEJBHome)
                 PortableRemoteObject.narrow(ref,IndicesEJBHome.class);
           indiceEJB = home.create();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        dataAccess = new ParticipanteDAO(indiceEJB);      
        
    }
    
    
    /**
     * M�todo usado para informar ao container EJB que o ejb precisa ser usado.
     */
    public void ejbCreate() 
    {
        
    }

    public void setSessionContext(SessionContext context) throws EJBException {
        _context = context;
    }

    public void ejbRemove() throws EJBException {
    }

    public void ejbActivate() throws EJBException {
    }

    public void ejbPassivate() throws EJBException {
    }


      /**
       *  busca na base um participante(podendo ser instancia de ParticipanteBD,
       *  ParticipanteCDNS, ParticipanteCDElet) referente ao n�mero de 
       *  inscri��o eletros passado como parametro.
       *  Participante contendo dados espec�ficos de cada plano (BD e CDElet),
       *  dependentes e seus respectivos v�nculos e hist�rico de patrocinadoras
       *  
       *  @param numEletros - numero eletros do participante
       *  @return Participante
       *  @throws Exception
       */
    public Participante BuscarParticipante(String numEletros) 
      throws Exception 
    {
        return dataAccess.buscaParticipante( numEletros );
    }

    
     /**
     * Busca uma lista de participantes referentes a um nome( parte ou totalidade). 
     * Poder� conter objetos referentes ao mesmo participante, sendo que em inscri��es
     * eletros distintas. A lista cont�m participantes em qualquer situa��o e ordenada
     * por nome e data de inscri��o.
     *  
     * @param nome
     * @return List<Participante>
     * @throws Exception
     */
    public List BuscarInscricoesParticipante(String nome) throws Exception {
        return dataAccess.buscaInscricoes( nome );
    }


    /**
     * Busca uma lista de joias pagas pelo participante, contendo o valor
     * e a data do pagamento.
     * @param numEletros
     * @return Collection<Joia>
     * @throws Exception
     */
    public Collection buscaJoiasPagas(String numEletros) throws Exception {
        return dataAccess.buscaJoiasPagas( numEletros );
    }


    /**
     * Busca uma lista de participantes ATIVOS referentes a um nome( parte ou totalidade). 
     * Poder� conter objetos referentes ao mesmo participante, sendo que em inscri��es
     * eletros distintas. A lista � ordenada por nome.
     *  
     * @param nome
     * @return List de Participante
     * @throws Exception
     */
    public List BuscaInscricoesAtivasParticipante(String nome) throws Exception {
        return dataAccess.buscaInscricoesAtivasParticipante( nome );
    }
    
    
    public List BuscaInscricoesAtivasPensionista(String nome) throws Exception {
        return dataAccess.buscaInscricoesAtivasPensionista( nome );
    }
    
    
    /**
     * Busca uma lista com as declara��es de tempo de servi�o do participante.
     * Objeto DTS contendo o nome da empresa, data de in�cio e fim na mesma, dentre
     * outras informa��es.
     * 
     * @param part - Participante
     * @return Collection<DTS>
     * @throws Exception
     */
    public Collection<DTS> buscaDTSParticipante(Participante part) throws Exception {
        return dataAccess.buscaDTSParticipante( part );
    }


    /**
     * Busca um objeto do tipo Beneficio contendo uma lista de benef�cios
     * concedidos ao participante tanto pelo INSS quanto pela Eletros.
     * 
     * @param part - Participante
     * @return Beneficio
     * @throws Exception
     */
    public Beneficio buscaBeneficiosConcedidos(Participante part) throws Exception {
        BenefDAO benefDataAccess = new BenefDAO();
        return benefDataAccess.buscaBeneficiosConcedidos( part );
    }


    /**
     * Busca uma lista de participantes que migraram do plano BD para o Plano
     * CD Eletrobrás. A lista pode contem participantes até uma data ou todos 
     * que migraram.
     * 
     * @return List<ParticipanteCDElet>
     * 
     * @throws Exception
     */
    public List<ParticipanteCDElet> buscaParticipantesMigradosCD(String data) throws Exception {
        return dataAccess.buscaParticipantesMigradosCD( data );
    }


    /**
     * Busca uma lista com as 36 �ltimas remunera��es do participante, apartir de 
     * um data DataFim
     */
    public Collection<Double> buscaRemuneracoes(Participante part, DataEletros dataFim) throws Exception{
        return null;
    }

    public Pensionista BuscarPensionista(String numEletros) throws Exception {
        return dataAccess.buscaPensionista( numEletros );
    }

    public double buscaSaldoReservaAtualParticipanteCD(String numEletros, String conta) throws Exception{
        return dataAccess.buscaSaldoReservaAtualParticipanteCD(numEletros, conta);
    }
}
