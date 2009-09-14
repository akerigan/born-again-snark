package org.torrent.web;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Embedded;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Date: 14.09.2009
 * Time: 22:24:55 (Moscow Standard Time)
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 */
public class TomcatTest {

    private static Log log = LogFactory.getLog(TomcatTest.class);

    private static String getPath() {
        return "/tmp/snark/Catalina";
    }

    public static void main(String[] args) throws LifecycleException {

        // Set the CATALINA_HOME directory
        System.setProperty("catalina.home", getPath());

        // Create an embedded server
        Embedded embedded = new Embedded();

        // Create an engine
        Engine engine = embedded.createEngine();
        engine.setDefaultHost("localhost");

        // Create a default virtual host
        Host host = embedded.createHost("localhost", getPath() + "/webapps");

        engine.addChild(host);

        // Create the ROOT context
        Context context = embedded.createContext("", getPath() + "/webapps/ROOT");
        host.addChild(context);

        // Install the assembled container hierarchy
        embedded.addEngine(engine);

        // Assemble and install a default HTTP connector
        Connector connector = embedded.createConnector("localhost", 8080, false);
        embedded.addConnector(connector);

        embedded.start();
    }
}
