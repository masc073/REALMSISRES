package realm_sisres;

import com.sun.appserv.connectors.internal.api.ConnectorRuntime;
import com.sun.appserv.security.AppservRealm;
import com.sun.enterprise.security.auth.realm.BadRealmException;
import com.sun.enterprise.security.auth.realm.InvalidOperationException;
import com.sun.enterprise.security.auth.realm.NoSuchRealmException;
import com.sun.enterprise.security.auth.realm.NoSuchUserException;
import com.sun.enterprise.security.common.Util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.utilities.BuilderHelper;

public class Realm_sisres extends AppservRealm
{

    private static String JTA_DATA_SOURCE = "jta-data-source";
    private static DataSource dataSource;

    @Override
    protected void init(Properties props) throws BadRealmException, NoSuchRealmException
    {
        super.setProperty(JTA_DATA_SOURCE, props.getProperty(JTA_DATA_SOURCE));
    }

    private Connection getConnection()
    {
        try
        {
            synchronized (this)
            {
                if (dataSource == null)
                {
                    ActiveDescriptor<ConnectorRuntime> cr = (ActiveDescriptor<ConnectorRuntime>) Util.getDefaultHabitat().getBestDescriptor(BuilderHelper.createContractFilter(ConnectorRuntime.class.getName()));
                    ConnectorRuntime connectorRuntime = Util.getDefaultHabitat().getServiceHandle(cr).getService();
                    dataSource = (DataSource) connectorRuntime.lookupNonTxResource(getJtaDataSource(), false);
                }
            }

            return dataSource.getConnection();
        } catch (NamingException | SQLException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public synchronized String getJAASContext()
    {
        return "Realm_Sisres";
    }

    @Override
    public String getAuthType()
    {
        return "jdbc";
    }

    @Override
    public Enumeration getGroupNames(String username) throws InvalidOperationException, NoSuchUserException
    {
        List<String> groupsList = this.getGroupList(username);

        System.out.println("realmgerenciadorcasamento, no groupsList: " + groupsList.get(0));
        return Collections.enumeration(groupsList);
    }

    public List<String> getGroupList(String username)
    {
        System.out.println("no realmgerenciadorcasamento - metodo getGroupList: inicio getGroupList");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<String> groups = new ArrayList<>();

        try
        {
            conn = getConnection();
            System.err.println("Cheguei aqqqqqqqqqqqqui!!");
            stmt = conn.prepareStatement("Select g.nome from sisres.grupo as g, sisres.responsavel as r, where r.email = ? and r.id = g.id ");
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            System.out.println("no realmgerenciadorcasamento - metodo getGroupList: executou query");

            while (rs.next())
            {
                String group = rs.getString(1);
                System.out.println("no realmgerenciadorcasamento - metodo getGroupList: grupo: " + group);

                groups.add(group);
            }
        } catch (SQLException ex)
        {
            System.out.println("no realmgerenciadorcasamento - metodo getGroupList: catch SQLException, causa do erro " + ex.getCause());
            ex.printStackTrace();
        } finally
        {
            close(rs, stmt, conn);
        }

        return groups;
    }

    public boolean authenticateUser(String _username, String _password)
    {
        System.out.println("no realmgerenciadorcasamento - metodo authenticateUser: inicio authenticateUser");

        Encripta encripta = new Encripta();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean result = false;

        try
        {
            conn = getConnection();
            stmt = conn.prepareStatement("Select r.senhadigital , r.numero_numeroAleatorio from sisres.responsavel as r where r.email like ?");
            stmt.setString(1, _username);
            rs = stmt.executeQuery();

            System.out.println("no realmgerenciadorcasamento - metodo authenticateUser: executou query");

            if (rs.next())
            {

                String senhaAtual = rs.getString("senhadigital"); //senha no banco criptografada
                int numeroAleatorio = rs.getInt("numero_numeroAleatorio");
                String senhaDigitada = encripta.encriptar(_password, numeroAleatorio);

                if (senhaDigitada.equals(senhaAtual))
                {
                    System.out.println("no realmgerenciadorcasamento - metodo authenticateUser: SENHAS COMPATIVEIS");
                    result = true;
                } else
                {
                    System.out.println("no realmgerenciadorcasamento - metodo authenticateUser: SENHAS DIFERENTES");
                }

            }
        } catch (SQLException ex)
        {
           ex.printStackTrace();
        } finally
        {
            close(rs, stmt, conn);
        }
        return result;
    }

    private String getJtaDataSource()
    {
        return super.getProperty(JTA_DATA_SOURCE);
    }

    private void close(ResultSet resultSet, Statement statement, Connection connection)
    {
        try
        {
            if (resultSet != null)
            {
                resultSet.close();
            }

            if (statement != null)
            {
                statement.close();
            }

            if (connection != null)
            {
                connection.close();
            }
        } catch (SQLException ex)
        {
            System.out.println("Causa: " + ex.getCause());
            ex.getStackTrace();
        }
    }

}
