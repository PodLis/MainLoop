package org.cereos.p2b;

import java.sql.*;
import java.util.ArrayList;

public class DataBase {
    public static class DataBaseInfo {
        public String url;
        public String user;
        public String password;

        DataBaseInfo(String url, String user, String password) {
            this.url = url;
            this.user = user;
            this.password = password;
        }
    }

    private String db_url;
    private String db_user;
    private String db_password;

    DataBase(String url, String user, String password){
        this.db_url = url;
        this.db_user = user;
        this.db_password = password;

        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
        } catch (SQLException E) {
            E.printStackTrace();
        }

        Connection connection = Connect();

        if ( connection == null )
            Log.Out("WARNING! Can't connect to MySQL:\n- url: \"" + this.db_url + "\"");
        else {
            Log.Out("Connected to DB: \"" + this.db_url + "\"!");
            Disconnect(connection);
        }
    }

    DataBase(DataBaseInfo db_info){
        this.db_url = db_info.url;
        this.db_user = db_info.user;
        this.db_password = db_info.password;

        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
        } catch (SQLException E) {
            E.printStackTrace();
        }

        Connection connection = Connect();

        if ( connection == null )
            Log.Out("WARNING! Can't connect to MySQL:\n- url: \"" + this.db_url + "\"");
        else {
            Log.Out("Connected to DB: \"" + this.db_url + "\"!");
            Disconnect(connection);
        }
    }

    private Connection Connect(){
        Connection connection;
        try {
            connection = DriverManager.getConnection(this.db_url, this.db_user, this.db_password);
            return !connection.isClosed() ? connection : null;
        } catch (SQLException E) {
            E.printStackTrace();
        }
        return null;
    }

    private void Disconnect(Connection connection){
        try {
            connection.close();
        } catch (SQLException E) {
            E.printStackTrace();
        }
    }

    public Manager GetManager(){
        Connection connection = Connect();
        return connection != null ? new Manager(connection) : null;
    }

    /*
     *  Requests manager
     */
    public class Manager{
        private Connection connection;
        private ArrayList<PreparedStatement> UnClosedStatements;
        private ArrayList<ResultSet> UnClosedResultSets;

        Manager(Connection connection){
            this.connection = connection;
            this.UnClosedStatements = new ArrayList<>();
            this.UnClosedResultSets = new ArrayList<>();
        }

        public void Free(){
            synchronized (this.UnClosedResultSets) {
                for (int i = 0; i < this.UnClosedResultSets.size(); i++) {
                    ResultSet resultSet = this.UnClosedResultSets.get(i);

                    if (resultSet != null)
                        try {
                            resultSet.close();
                        } catch (SQLException E) {
                            E.printStackTrace();
                        }
                }
            }

            synchronized (this.UnClosedStatements) {
                for (int i = 0; i < this.UnClosedStatements.size(); i++) {
                    PreparedStatement statement = this.UnClosedStatements.get(i);

                    if (statement != null)
                        try {
                            statement.close();
                        } catch (SQLException E) {
                            E.printStackTrace();
                        }
                }
            }

            try {
                this.connection.close();
            } catch (SQLException E) {
                E.printStackTrace();
            }
        }

        private void PushStatement(PreparedStatement statement){
            synchronized (this.UnClosedStatements) {
                this.UnClosedStatements.add(statement);
            }
        }

        private void PushResultSet(ResultSet resultSet){
            synchronized (this.UnClosedResultSets) {
                this.UnClosedResultSets.add(resultSet);
            }
        }

        public ResultSet Request(String sql){
            PreparedStatement statement = null;
            ResultSet result = null;

            try {
                statement = this.connection.prepareStatement(sql);
                PushStatement(statement);

                result = statement.executeQuery();
                PushResultSet(result);
            } catch (SQLException E) {
                E.printStackTrace();
            }

            return result;
        }

        public boolean Execute(String sql){
            PreparedStatement statement = null;
            boolean result = false;

            try {
                statement = this.connection.prepareStatement(sql);
                statement.execute();
                result = true;
            } catch (SQLException E) {
                E.printStackTrace();
            } finally {
                try {
                    if (statement != null)
                        statement.close();
                } catch (Exception E) {
                    E.printStackTrace();
                }
            }

            return result;
        }
    }
}
