Configuration
----------------------------------------------------------------------------------------------------
For Tomcat:
- in Tomcat server.xml use org.apache.coyote.http11.Http11NioProtocol: 

  <Connector port="8080" protocol="org.apache.coyote.http11.Http11NioProtocol"
    maxThreads="150" connectionTimeout="20000" redirectPort="8443"/>

  
For Glassfish:
- in web.xml of demo-comet replace "org.wings.comet.TomcatCometWingServlet" by "org.wings.comet.GlassfishCometWingServlet" twice
- set property cometSupport=true in domain.xml: 

  <http-listener acceptor-threads="1" address="0.0.0.0" blocking-enabled="false"
    default-virtual-server="server" enabled="true" family="inet"
    id="http-listener-1" port="8080" security-enabled="false"
    server-name="" xpowered-by="true">
    ...
    <property name="cometSupport" value="true"/>
  </http-listener>


For Jetty:
- in web.xml of demo-comet replace "org.wings.comet.TomcatCometWingServlet" by "org.wings.comet.JettyCometWingServlet" twice


For JBoss (tested with JBoss 4.2.3 GA):
- in server/default/deploy/jboss-web.deployer/server.xml use org.apache.coyote.http11.Http11NioProtocol:
   
  <Connector port="8080" address="${jboss.bind.address}" maxThreads="250" maxHttpHeaderSize="8192"
    emptySessionPath="true" protocol="org.apache.coyote.http11.Http11NioProtocol" enableLookups="false"
    redirectPort="8443" acceptCount="100" connectionTimeout="20000" disableUploadTimeout="true" />
    
- in server/default/deploy/jboss-web.deployer/conf/web.xml comment out the filter and filter-mapping for CommonHeadersFilter


Running the examples
----------------------------------------------------------------------------------------------------
Point your browser to the following URL's:
  http://localhost:8080/comet/Chat/
  http://localhost:8080/comet/ProgressBar/
  