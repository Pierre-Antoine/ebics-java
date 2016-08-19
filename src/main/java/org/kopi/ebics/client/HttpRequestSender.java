/*
 * Copyright (c) 1990-2012 kopiLeft Development SARL, Bizerte, Tunisia
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package org.kopi.ebics.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.*;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.kopi.ebics.interfaces.ContentFactory;
import org.kopi.ebics.io.InputStreamContentFactory;
import org.kopi.ebics.session.EbicsSession;


/**
 * A simple HTTP request sender and receiver.
 * The send returns a HTTP code that should be analyzed
 * before proceeding ebics request response parse.
 *
 * @author hachani
 *
 */
public class HttpRequestSender {

  /**
   * Constructs a new <code>HttpRequestSender</code> with a
   * given ebics session.
   * @param session the ebics session
   */
  public HttpRequestSender(EbicsSession session) {
    this.session = session;
  }

  /**
   * Sends the request contained in the <code>ContentFactory</code>.
   * The <code>ContentFactory</code> will deliver the request as
   * an <code>InputStream</code>.
   *
   * @param request the ebics request
   * @return the HTTP return code
   */
  public final int send(ContentFactory request) throws IOException {
    HttpClient			httpClient;
    String                      proxyConfiguration;
    HttpPost			method;
    HttpEntity requestEntity;
    InputStream			input;
    HttpResponse response;
    RequestConfig config = RequestConfig.copy(RequestConfig.DEFAULT).setSocketTimeout(100000).build();
    httpClient = HttpClientBuilder.create().build();
    proxyConfiguration = session.getConfiguration().getProxyHost();

    if (proxyConfiguration != null && !proxyConfiguration.equals("")) {
      String			proxyHost;
      int			proxyPort;

      proxyHost = session.getConfiguration().getProxyHost().trim();
      proxyPort = Integer.parseInt(session.getConfiguration().getProxyPort().trim());
      if (!session.getConfiguration().getProxyUser().equals("")) {
        String				user;
        String				pwd;
        NTCredentials credentials;
        AuthScope authscope;
        HttpHost proxy = new HttpHost(proxyHost,proxyPort);

        user = session.getConfiguration().getProxyUser().trim();
        session.getConfiguration().getLogger().info("User pour le proxy : " + user);
        pwd = session.getConfiguration().getProxyPassword().trim();
        session.getConfiguration().getLogger().info("Mdp pour le proxy : " + pwd);
        credentials = new NTCredentials(user, pwd,"localhost","AD");
        authscope = new AuthScope(proxyHost,proxyPort);

        config = RequestConfig.custom().setProxy(proxy).setSocketTimeout(100000).setConnectTimeout(100000).build();

        CredentialsProvider cp = new BasicCredentialsProvider();
        cp.setCredentials(authscope,credentials);

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.useSystemProperties();
        clientBuilder.setProxy(proxy);
        clientBuilder.setDefaultCredentialsProvider(cp);
        clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());

        httpClient = clientBuilder.build();
      }
    }
    input = request.getContent();
    method = new HttpPost(session.getUser().getPartner().getBank().getURL().toString());

    requestEntity = EntityBuilder.create().setStream(input).build();
    method.setEntity(requestEntity);
    method.setHeader("Content-type", "text/xml; charset=ISO-8859-1");
    method.setConfig(config);

    response = httpClient.execute(method);
    this.response = new InputStreamContentFactory(response.getEntity().getContent());

    return response.getStatusLine().getStatusCode();
  }

  /**
   * Returns the content factory of the response body
   * @return the content factory of the response.
   */
  public ContentFactory getResponseBody() {
    return response;
  }

  //////////////////////////////////////////////////////////////////
  // DATA MEMBERS
  //////////////////////////////////////////////////////////////////

  private EbicsSession				session;
  private ContentFactory			response;
}
