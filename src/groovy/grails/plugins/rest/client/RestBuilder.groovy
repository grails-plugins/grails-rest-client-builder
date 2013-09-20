package grails.plugins.rest.client

import static org.springframework.http.MediaType.*

import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.HttpStatusCodeException

import java.security.KeyManagementException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.UnrecoverableKeyException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

import org.apache.http.conn.scheme.PlainSocketFactory
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.scheme.SchemeRegistry
import org.apache.http.conn.ssl.SSLSocketFactory
import org.apache.http.conn.ssl.TrustStrategy
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.impl.conn.PoolingClientConnectionManager


class RestBuilder {

    RestTemplate restTemplate

    RestBuilder() {
        restTemplate = new RestTemplate()
    }

    RestBuilder(ClientHttpRequestFactory factory) {
        restTemplate = new RestTemplate(factory)
    }

    RestBuilder(Map settings, ClientHttpRequestFactory factory) {
        restTemplate = new RestTemplate(factory)
        setup(settings)
    }

    RestBuilder(Map settings) {
        restTemplate = new RestTemplate()
        setup(settings) 
    }

    private setup(Map settings) {
        def proxyHost = System.getProperty("http.proxyHost")
        def proxyPort = System.getProperty("http.proxyPort")

        if (proxyHost && proxyPort) {
            if (settings.proxy == null) {
                settings.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort.toInteger()))
            }
        }

        if (settings.proxy instanceof Map) {
            def ps = settings.proxy.entrySet().iterator().next()
            if (ps.value) {
                def proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ps.key, ps.value.toInteger()))
                settings.proxy = proxy
            }
        }

        restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory(settings))
    }

    /**
     * Issues a GET request and returns the response in the most appropriate type
     * @param url The URL
     * @param url The closure customizer used to customize request attributes
     */
    def get(String url, Closure customizer = null) {
        doRequestInternal(url, customizer, HttpMethod.GET)
    }

     /**
   * Issues a GET request and returns the response in the most appropriate type
   * @param url The URL - URI format
   * @param url The closure customizer used to customize request attributes
   */
    def get(URI url, Closure customizer = null) {
      doRequestInternal(url, customizer, HttpMethod.GET)
    }


    /**
     * Issues a PUT request and returns the response in the most appropriate type
     *
     * @param url The URL
     * @param customizer The clouser customizer
     */
    def put(String url, Closure customizer = null) {
        doRequestInternal(url, customizer, HttpMethod.PUT)
    }

    /**
     * Issues a POST request and returns the response
     * @param url The URL
     * @param customizer (optional) The closure customizer
     */
    def post(String url, Closure customizer = null) {
        doRequestInternal(url, customizer, HttpMethod.POST)
    }

    /**
     * Issues DELETE a request and returns the response

     * @param url The URL
     * @param customizer (optional) The closure customizer
     */
    def delete(String url, Closure customizer = null) {
        doRequestInternal(url, customizer, HttpMethod.DELETE)
    }

    /**
    * TODO: fix for query service - have to use URI
    **/
    protected doRequestInternal(URI url, Closure customizer, HttpMethod method) {

      def requestCustomizer = new RequestCustomizer()
        if (customizer != null) {
          customizer.delegate = requestCustomizer
            customizer.call()
        }

      try {
        def responseEntity
          if(requestCustomizer.getVariables())
            responseEntity = restTemplate.exchange(url, method, requestCustomizer.createEntity(),
                String, requestCustomizer.getVariables())
          else
            responseEntity = restTemplate.exchange(url, method, requestCustomizer.createEntity(), String)

              handleResponse(responseEntity)
      }
      catch (HttpStatusCodeException e) {
        return new ErrorResponse(error:e)
      }
    }

    protected doRequestInternal(String url, Closure customizer, HttpMethod method) {

        def requestCustomizer = new RequestCustomizer()
        if (customizer != null) {
            customizer.delegate = requestCustomizer
            customizer.call()
        }

        try {
            def responseEntity = restTemplate.exchange(url, method, requestCustomizer.createEntity(),
                    String, requestCustomizer.getVariables())
            handleResponse(responseEntity)
        }
        catch (HttpStatusCodeException e) {
            return new ErrorResponse(error:e)
        }
    }

    protected handleResponse(ResponseEntity responseEntity) {
        return new RestResponse(responseEntity: responseEntity)
    }

    public static ClientHttpRequestFactory getFakeSSLClient() {
      return getClientRequestFactory(createClientFakeSSLSocketFactory())
    }

    private static getClientRequestFactory(DefaultHttpClient httpClient) {
      return new HttpComponentsClientHttpRequestFactory(httpClient)
    }

    private static DefaultHttpClient createClientFakeSSLSocketFactory() {
      SchemeRegistry schemeRegistry = new SchemeRegistry()
      schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()))
      schemeRegistry.register(new Scheme("https", 443, getFakeSSLSocketFactory()))

      PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager(schemeRegistry)
      // connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS)
      // connectionManager .setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE)
      return new DefaultHttpClient(connectionManager)
    }

    private static SSLSocketFactory getFakeSSLSocketFactory() {
      SSLSocketFactory sslSocketFactory
      try {
        return new SSLSocketFactory(new TrustStrategy() {
          public boolean isTrusted(final X509Certificate[] chain,
            final String authType) throws CertificateException {
          return true
          } }, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
      } catch (KeyManagementException e) {
        throw new RuntimeException(e)
      } catch (UnrecoverableKeyException e) {
        throw new RuntimeException(e)
      } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e)
      } catch (KeyStoreException e) {
        throw new RuntimeException(e)
      }
    }

}
