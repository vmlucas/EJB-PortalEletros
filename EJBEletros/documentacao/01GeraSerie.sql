-- Script para gerar o script 04b_CalculaSerie.sql
-- para preencher a tabela CAL_SERIE_CONTRIB
-- Autor: Eduardo Bittencourt
-- Data: 07/11/2002; 14/07/2003; 15/07/2004; 13/09/2005; 31/10/2005; 29/12/2005; 23/01/2006; 15/12/2006;
--       04/01/2007; 06/01/2007; 24/02/2007; 26/09/2007
-- Ambiente: ELET
-- @L:\Migracao\Montello\Full\01GeraSerie.sql

Var Referencia Char(6);
Var nQtdMeses Number;
Begin
  :Referencia := '200804';
  :nQtdMeses := Months_Between(To_Date(:Referencia||'25', 'yyyymmdd'), To_Date('25/07/1994', 'dd/mm/yyyy'));
End;
/

Set pagesize 0;
Set timing off;
Set heading off;
Set feedback off;
Set linesize 75;
Set verify off;
Spool L:\Migracao\Montello\Full\02GeraSerie.sql;
Select '-- Script gerado pelo script GeraSerie.sql' From Dual;
Select '-- para popular a tabela CAL_SERIE_CONTRIB a partir' From Dual;
Select '-- da procedure PR_GERA_SERIE_CONTRIB' From Dual;
Select '-- Mudancas realizadas nesse arquivo nao serao preservadas' From Dual;
Select '-- por se tratar de um arquivo gerado a partir de um spool' From Dual;
Select '-- Autor: Eduardo Bittencourt' From Dual;
Select '-- Data: 29/12/2005' From Dual;
Select '-- Ambiente: ELET' From Dual;
Select '-- NAO EDITE ESTE ARQUIVO. AS MUDANCAS NAO SERAO PRESERVADAS!' From Dual;
Select 'DELETE FROM ERAMOS.CAL_SERIE_CONTRIB;' From Dual;
Select 'EXEC ERAMOS.PR_GERA_SERIE_CONTRIB(' || chr(39) || NU_PTI_ICR || chr(39) ||
       ', ' || chr(39) || :Referencia
            || chr(39) || ', ' || To_Char(:nQtdMeses, '000') || ');'
From  PTI_CAD
Where (DT_PTI_DLG Is Null or DT_PTI_DLG >= To_Date(:Referencia, 'yyyymm') )
And   O_CAD_2.FN_CAD_TRAZ_CD_PLN(PTI_CAD.NU_PTI_ICR) = 1
And Not Exists	(Select 1 From SALDOSMIGCD
		Where Substr( SALDOSMIGCD.NRISCBD, 3, 7 ) = PTI_CAD.NU_PTI_ICR
		And DTOPCMIG <= Last_Day(To_Date( :Referencia, 'yyyymm'))
		)
And Not Exists (Select 1 From BNF_BNF B1
	Where B1.CD_PTI = PTI_CAD.NU_PTI_ICR
	And B1.DT_BNF_ELET <= Last_Day(To_Date(:Referencia, 'yyyymm'))
	And B1.TS_BNF = (Select Max(B2.TS_BNF)
			From BNF_BNF B2
			Where B2.CD_PTI = B1.CD_PTI
			And B2.TS_BNF <= Last_Day(To_Date(:Referencia, 'yyyymm'))
			)
	)
And Not Exists (Select 1 From HIS_ELET_PAT H1
                Where H1.NU_PTI_ICR = PTI_CAD.NU_PTI_ICR
                And H1.DT_HIS_PAT_ADS <= (Last_Day(To_Date(:Referencia, 'yyyymm')) + 1)
                And (H1.DT_HIS_PAT_DEM Is Null Or H1.DT_HIS_PAT_DEM > (Last_Day(To_Date(:Referencia, 'yyyymm'))+1) )
                And H1.CD_PAT In (264011, 264038)
               )
Order By NU_PTI_ICR;
Select 'Commit;' From Dual;
Select 'Analyze Table ERAMOS.CAL_SERIE_CONTRIB Compute Statistics;' From Dual;
Spool off;
Set timing on;
Set heading on;
Set feedback on;
Set Verify On;
