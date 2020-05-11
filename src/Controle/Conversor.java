package Controle;

import java.io.File;
import javax.swing.JOptionPane;
import main.Arquivo;

public class Conversor {
    public static void converte(String arq){
        if (verificaArquivo(arq)) {
            JOptionPane.showMessageDialog( null ,
                    " Irei converter o arquivo agora e avisarei quando concluir. Se o arquivo possuir mais de 1mb / 1024kb desconfie que o arquivo possui falsas linhas (em branco). " ,
                    " Informação!" , JOptionPane.INFORMATION_MESSAGE );
            
            String novoLocal = criaLctoGer(arq);
            
            JOptionPane.showMessageDialog( null ,
                    " Arquivo convertido salvo em: " + novoLocal ,
                    " Sucesso!" , JOptionPane.INFORMATION_MESSAGE );
            System.exit(0);
        }
    }
    public static boolean verificaArquivo(String arqui){
        boolean retorno = false;
        String local = arqui;
        if (local.length() <= 5) {
            JOptionPane.showMessageDialog( null ,
                    " O local do arquivo não pode ficar em branco!" ,
                    " Alerta" , JOptionPane.WARNING_MESSAGE );
        }else if (".csv".equals(local.substring(local.length()-4, local.length()))){
            //VERIFICAR SE ARQUIVO EXISTE
            File arq = new File(local);
            
            if (arq.exists() == false) {
                JOptionPane.showMessageDialog( null ,
                    " O arquivo não existe!" ,
                    " Alerta" , JOptionPane.WARNING_MESSAGE );
            }else {
                //ABRE E VERIFICA SE TEM O NÚMERO DE COLUNAS CERTAS
                if (numeroColunas(local) == 8 || numeroColunas(local) == 9){
                    retorno = true;
                }else{
                    JOptionPane.showMessageDialog( null ,
                    " O arquivo não possui o numero de colunas correto!" ,
                    " Alerta" , JOptionPane.WARNING_MESSAGE );
                }
            }
        }else{
            JOptionPane.showMessageDialog( null ,
                    " Deve ser um arquivo CSV!" ,
                    " Alerta" , JOptionPane.WARNING_MESSAGE );
        }
        
        return retorno;
    }
    public static long numeroColunas(String arq){
        long num;
        num = 0;
        
        String textoArquivo;
        textoArquivo = Arquivo.ler(arq);
        String[] linhas = textoArquivo.split("\n");
        
        if (linhas.length > 1 ) {
            String[] cols = linhas[1].split(";");
            if (cols.length == 8 || cols.length == 9) {
                num = cols.length;
            }
        }
        
        return num;
    }
    public static String criaLctoGer(String arq){
        String nomeSalvo = "";
        String[] linhas = Arquivo.ler(arq).replaceAll(";;;;;;;\n", "").split("\n");
        String novoArquivo = "";
        
        //Percorre todas linhas
        for (int i = 0; i < (linhas.length); i++) {
            //SE A LINHA ESTIVER VAZIA
            int qt = linhas[i].length() - linhas[i].replaceAll(";", "").length();
            if (linhas[i].contains(";;;;") == false && (qt == 8 || qt == 7)) {
                String lctosLinha = "";
                //SPLIT COLUNAS
                String[] cols = linhas[i].split(";");
                //SE TIVER 7 COLUNAS
                if (cols.length == 8){
                    //ADICIONA COLUNA VAZIA NA LINHA
                    linhas[i] = ";" + linhas[i];
                    //SPLIT COLUNAS
                    cols = linhas[i].split(";");
                }
                //SE PRIMEIRA COLUN-A FOR NUMERICA
                if (isNumeric(cols[1])){
                    //ADICIONA AA
                    cols[0] = "AA";
                }else{
                    cols[0] = "#" + cols[0];
                }
                //ADICIONA TEXTO AO NOVO ARQUIVO
                lctosLinha = lctosLinha +
                            cols[0] + ";" + //TEMPLATE
                            cols[1] + ";" + //FILIAL
                            cols[2] + ";" + //data
                            cols[3] + ";" + //conta debito
                            cols[4] + ";" + //conta credito
                            cols[5] + ";" + // historico padrao
                            cols[6] + ";" + // historico
                            cols[7] + "\n"; // valor
                            //cols[8]; // Centro Custo

                //SE PRIMEIRA COLUNA FOR NUMERICA
                if (isNumeric(cols[1])){
                    //ADICIONA LCTO DEB
                    lctosLinha = lctosLinha +
                                "XX;" + //TEMPLATE
                                cols[8].substring(0, cols[8].length() -1) + ";" + // Centro Custo (1)
                                "1;" +
                                cols[3] + ";" + //conta debito (2)
                                cols[2] + ";" + //data (3)
                                cols[7] + // valor (4)
                                "\n" ; //proximalinha
                    //ADICIONA LCTO CRED
                    lctosLinha = lctosLinha +
                                "XX;" + //TEMPLATE
                                cols[8].substring(0, cols[8].length() -1) + ";" + // Centro Custo (1)
                                "-1;" +
                                cols[4] + ";" + //conta credito (2)
                                cols[2] + ";" + //data (3)
                                cols[7] + // valor (4)
                                "\n" ; //proximalinha
                }

                //SE FOR ULTIMA LINHA RETORNA SEM O ULTIMO CARACTERE (ENTER)
                lctosLinha = i == (linhas.length -1)? lctosLinha.substring(0, lctosLinha.length() - 1): lctosLinha;
                novoArquivo = novoArquivo + lctosLinha;
            }
        }
        
        nomeSalvo = novoNomeArquivo(arq);
        
        if (Arquivo.salvar(nomeSalvo, novoArquivo) == false){
            nomeSalvo = "[ERRO] " + nomeSalvo;
        }
        
        return nomeSalvo;
    }
    public static boolean isNumeric (String s) {
        try {
            Long.parseLong (s); 
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
    public static String novoNomeArquivo (String arquivo){
        File newfile = new File(arquivo);
        String novoNome;
        novoNome = newfile.getParent()+ "\\GERENCIAL " + newfile.getName();
        return novoNome;
    }
}
