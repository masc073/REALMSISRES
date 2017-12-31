package realm_sisres;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Encripta
{

    public int Sorteia()
    {
        Random random = new Random();
        int numeroAleatorio = random.nextInt(20001);
        System.out.println("Numero aleatorio gerado: " + numeroAleatorio);

        return numeroAleatorio;
    }

    private String novaString(String texto, int numeroAleatorio)
    {
        String nova = "";
        for (int i = 0; i < texto.length(); i++)
        {
            char ch = texto.charAt(i);
            int asc = (int) ch + numeroAleatorio;
            nova += Integer.toString(asc);
        }

        return nova;
    }

    public String encriptar(String texto, int numeroAleatorio)
    {
        texto = novaString(texto, numeroAleatorio);

        String senhahexAdmin = "";
        try
        {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
            byte messageDigestSenhaAdmin[] = algorithm.digest(texto.getBytes("UTF-8"));
            StringBuilder hexStringSenhaAdmin = new StringBuilder();

            for (byte b : messageDigestSenhaAdmin)
            {
                hexStringSenhaAdmin.append(String.format("%02X", 0xFF & b));
            }

            senhahexAdmin = hexStringSenhaAdmin.toString();
            System.out.println(senhahexAdmin);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e)
        {
            System.out.println(e.getCause());
        }
        return senhahexAdmin;
    }
}
