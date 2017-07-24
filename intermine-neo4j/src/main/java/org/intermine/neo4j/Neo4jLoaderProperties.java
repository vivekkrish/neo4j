package org.intermine.neo4j;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.intermine.metadata.InterMineModelParser;
import org.intermine.metadata.Model;
import org.intermine.metadata.ModelParserException;
import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.services.ListService;
import org.intermine.webservice.client.services.QueryService;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

/**
 * Simple storage class with methods to return various InterMine and Neo4j services.
 */
public class Neo4jLoaderProperties {

    public static final String DEFAULT_PROPERTIES_FILE = "neo4jloader.properties";

    private static final Logger LOG = Logger.getLogger(Neo4jLoaderProperties.class);

    String intermineServiceUrl;
    String neo4jUrl;
    String neo4jUser;
    String neo4jPassword;
    String dataModelFilename;
    String extraValueLabel;
    String extraValuePropertyName;
    String intermineApiToken;
    String sequencePgHost;
    String sequencePgDatabase;
    String sequencePgUser;
    String sequencePgPassword;
    String sequencePgTable;
    
    int maxRows;
    int maxSequenceLength;
    int sequencePgPort;

    boolean verbose;
    boolean debug;

    /**
     * Default constructor, loads properties from DEFAULT_PROPERTIES_FILE
     */
    public Neo4jLoaderProperties() throws FileNotFoundException, IOException {
        Properties props = new Properties();
        props.load(getClass().getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_FILE));
        load(props);
    }

    /**
     * Constructor which loads properties from a given filename
     */
    public Neo4jLoaderProperties(String filename) throws FileNotFoundException, IOException {
        Properties props = new Properties();
        props.load(getClass().getClassLoader().getResourceAsStream(filename));
        load(props);
    }

    /**
     * Set the various properties from a Properties object
     */
    void load(Properties props) throws FileNotFoundException, IOException {
        // strings
        intermineServiceUrl = props.getProperty("intermine.service.url");
        neo4jUrl = props.getProperty("neo4j.url");
        neo4jUser = props.getProperty("neo4j.user");
        neo4jPassword = props.getProperty("neo4j.password");
        dataModelFilename = props.getProperty("data.model.file");
        extraValueLabel = props.getProperty("extraValueLabel");
        extraValuePropertyName = props.getProperty("extraValuePropertyName");
        intermineApiToken = props.getProperty("intermine.api.token");
        sequencePgHost = props.getProperty("sequence.pg.host");
        sequencePgDatabase = props.getProperty("sequence.pg.database");
        sequencePgUser = props.getProperty("sequence.pg.user");
        sequencePgPassword = props.getProperty("sequence.pg.password");
        sequencePgTable = props.getProperty("sequence.pg.table");

        // ints
        maxRows = getIntProperty(props, "max.rows");
        maxSequenceLength = getIntProperty(props, "max.sequence.length");
        sequencePgPort = getIntProperty(props, "sequence.pg.port");
        if (sequencePgPort==0) sequencePgPort = 5432; // DEFAULT

        // booleans
        verbose = getBooleanProperty(props, "verbose");
        debug = getBooleanProperty(props, "debug");
    }

    /**
     * Return a Neo4j graph database driver
     */
    public Driver getGraphDatabaseDriver() {
        return GraphDatabase.driver(neo4jUrl, AuthTokens.basic(neo4jUser, neo4jPassword));
    }

    /**
     * Return an InterMine QueryService
     */
    public QueryService getQueryService() {
        return new ServiceFactory(intermineServiceUrl).getQueryService();
    }
    
    /**
     * Return an InterMine ListService
     */
    public ListService getListService() {
        return new ServiceFactory(intermineServiceUrl).getListService();
    }

    /**
     * Return an InterMine ListService with authentication.
     */
    public ListService getListService(String authToken) {
        return new ServiceFactory(intermineServiceUrl, authToken).getListService();
    }


    /**
     * Return an InterMine Model
     */
    public Model getModel() throws FileNotFoundException, ModelParserException {
        return new InterMineModelParser().process(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(dataModelFilename)));
    }

    /**
     * Return maxSequenceLength
     */
    public int getMaxSequenceLength() {
        return maxSequenceLength;
    }

    /**
     * The label of the nodes that are matched with the extra value provided in the
     * LOOKUP constraint is stored in neo4jloader.properties file. This method fetches
     * that node label.
     */
    public String getExtraValueLabel() {
        return extraValueLabel;
    }

    public String getExtraValuePropertyName() {
        return extraValuePropertyName;
    }

    public String getIntermineApiToken() {
        return intermineApiToken;
    }

    public String getIntermineServiceUrl() {
        return intermineServiceUrl;
    }

    public String getSequencePgHost() {
        return sequencePgHost;
    }
    public int getSequencePgPort() {
        return sequencePgPort;
    }
    public String getSequencePgDatabase() {
        return sequencePgDatabase;
    }
    public String getSequencePgUser() {
        return sequencePgUser;
    }
    public String getSequencePgPassword() {
        return sequencePgPassword;
    }
    public String getSequencePgTable() {
        return sequencePgTable;
    }

    /**
     * Extract an int property from the properties file; 0 if property not present.
     */
    int getIntProperty(Properties props, String propName) throws NumberFormatException {
        String propString = props.getProperty(propName);
        if (propString!=null) {
            return Integer.parseInt(propString);
        } else {
            return 0;
        }
    }

    /**
     * Extract a boolean property from the properties file; false if property not present.
     */
    boolean getBooleanProperty(Properties props, String propName) {
        String propString = props.getProperty(propName);
        if (propString!=null) {
            return Boolean.parseBoolean(propString);
        } else {
            return false;
        }
    }


    
}
