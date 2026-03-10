package edu.escuelaing.framework;

import java.net.*;
import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * HTTP/1.1 Web Server based on the professor's base implementation.
 *
 * MDN conventions (https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Overview):
 *
 *   HTTP/1.1 200 OK
 *   Date: Sat, 09 Oct 2010 14:28:02 GMT
 *   Server: JavaWebFramework/1.0
 *   Last-Modified: Tue, 01 Dec 2009 20:18:22 GMT
 *   Content-Length: 29769
 *   Content-Type: text/html
 *
 * Dispatch order:
 *   1. Registered REST routes via RouteRegistry (lambda functions)
 *   2. Static files from the configured webroot folder
 *   3. Hello World for any unrecognized path
 */
public class HttpServer {

    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        Socket clientSocket = null;
        boolean running = true;

        System.out.println("Server running on http://localhost:35000");

        while (running) {
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            final Socket socket = clientSocket;
            new Thread(() -> {
                try {
                    handleClient(socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        serverSocket.close();
    }

    private static void handleClient(Socket clientSocket) throws Exception {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        OutputStream rawOut = clientSocket.getOutputStream();

        String inputLine;
        boolean isFirstLine = true;
        String reqpath = "";
        String struripath = "";
        String method = "";

        while ((inputLine = in.readLine()) != null) {
            System.out.println("Received: " + inputLine);

            if (isFirstLine) {
                String[] flTokens      = inputLine.split(" ");
                method                 = flTokens[0];
                struripath             = flTokens[1];
                String protocolversion = flTokens[2];

                URI uripath = new URI(struripath);
                reqpath = uripath.getPath();

                System.out.println("Method: "  + method);
                System.out.println("Path: "    + reqpath);
                System.out.println("Protocol: "+ protocolversion);

                isFirstLine = false;
            }

            if (!in.ready()) {
                break;
            }
        }

        Request req = new Request(method, struripath);
        Response res = new Response();

        byte[] body;
        String headers;
        String now = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);

        Route route = RouteRegistry.getRoute(reqpath);
        if (route != null) {
            String result = route.handle(req, res);
            body    = result.getBytes("UTF-8");
            headers = buildHeaders(res.getStatus(), res.getContentType(), body, now);

        } else {
            byte[] staticFile = StaticFileHandler.getFile(reqpath);
            if (staticFile != null) {
                body    = staticFile;
                String contentType = StaticFileHandler.getContentType(reqpath);
                headers = buildHeaders(200, contentType, body, now);

            } else {
                String helloBody = "<!DOCTYPE html>"
                        + "<html>"
                        + "<head><meta charset=\"UTF-8\"><title>Hello</title></head>"
                        + "<body><h1>Hello World!</h1></body>"
                        + "</html>";
                body    = helloBody.getBytes("UTF-8");
                headers = buildHeaders(200, "text/html", body, now);
            }
        }

        rawOut.write(headers.getBytes("UTF-8"));
        rawOut.write(body);
        rawOut.flush();

        out.close();
        in.close();
        clientSocket.close();
    }

    /**
     * Builds a proper HTTP/1.1 response header following MDN conventions:
     *
     * HTTP/1.1 200 OK
     * Date: Sat, 09 Oct 2010 14:28:02 GMT
     * Server: JavaWebFramework/1.0
     * Last-Modified: Tue, 01 Dec 2009 20:18:22 GMT
     * Content-Length: 29769
     * Content-Type: text/html
     */
    private static String buildHeaders(int status, String contentType,
                                       byte[] body, String date) {
        String statusText;
        if (status == 200) {
            statusText = "OK";
        } else if (status == 404) {
            statusText = "Not Found";
        } else {
            statusText = "Internal Server Error";
        }

        return "HTTP/1.1 " + status + " " + statusText + "\r\n"
                + "Date: "           + date         + "\r\n"
                + "Server: JavaWebFramework/1.0\r\n"
                + "Last-Modified: "  + date         + "\r\n"
                + "Content-Length: " + body.length  + "\r\n"
                + "Content-Type: "   + contentType  + "\r\n"
                + "Connection: close\r\n"
                + "\r\n";
    }
}