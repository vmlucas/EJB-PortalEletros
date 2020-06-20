package EJB.Eletros;

import com.eletros.benef.*;

import com.eletros.benef.Beneficio;
import com.eletros.benef.Participante;

import com.eletros.benef.ParticipanteCDElet;

import com.util.DataEletros;

import java.rmi.RemoteException;

import java.util.*;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJBObject;

public interface ParticipanteEJB extends EJBObject {
    Participante BuscarParticipante(String numEletros) throws Exception, 
                                                              RemoteException;

    List BuscarInscricoesParticipante(String nome) throws Exception, 
                                                          RemoteException;Collection buscaJoiasPagas(String numEletros) throws Exception, RemoteException;

    List BuscaInscricoesAtivasParticipante(String nome) throws Exception, 
                                                               RemoteException;
    
    List BuscaInscricoesAtivasPensionista(String nome) throws Exception, 
                                                               RemoteException;

    Collection<DTS> buscaDTSParticipante(Participante part) throws Exception, 
                                                                   RemoteException;

    Beneficio buscaBeneficiosConcedidos(Participante part) throws Exception, 
                                                                  RemoteException;

    List<ParticipanteCDElet> buscaParticipantesMigradosCD(String data) throws Exception, 
                                                                              RemoteException;

    Collection buscaRemuneracoes(Participante part, 
                                 DataEletros dataFim) throws Exception, 
                                                             RemoteException;

    Pensionista BuscarPensionista(String numEletros) throws Exception, RemoteException;

    double buscaSaldoReservaAtualParticipanteCD(String numEletros, String conta) throws Exception,RemoteException;
}
