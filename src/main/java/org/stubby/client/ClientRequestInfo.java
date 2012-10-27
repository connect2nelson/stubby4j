/*
HTTP stub server written in Java with embedded Jetty

Copyright (C) 2012 Alexander Zagniotov, Isa Goksu and Eric Mrak

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.stubby.client;

public final class ClientRequestInfo {

   private final String method;
   private final String uri;
   private final String host;
   private final String postBody;
   private final String base64encodedCredentials;
   private final int clientPort;

   public ClientRequestInfo(final String method,
                            final String uri,
                            final String host,
                            final int clientPort) {
      this(method, uri, host, clientPort, null, null);
   }

   public ClientRequestInfo(final String method,
                            final String uri,
                            final String host,
                            final int clientPort,
                            final String postBody) {
      this(method, uri, host, clientPort, postBody, null);
   }

   public ClientRequestInfo(final String method,
                            final String uri,
                            final String host,
                            final int clientPort,
                            final String postBody,
                            final String base64encodedCredentials) {
      this.method = method;
      this.uri = uri;
      this.host = host;
      this.clientPort = clientPort;
      this.postBody = postBody;
      this.base64encodedCredentials = base64encodedCredentials;
   }

   public String getMethod() {
      return method;
   }

   public String getUri() {
      return uri;
   }

   public String getHost() {
      return host;
   }

   public String getPostBody() {
      return postBody;
   }

   public String getBase64encodedCredentials() {
      return base64encodedCredentials;
   }

   public int getClientPort() {
      return clientPort;
   }
}
