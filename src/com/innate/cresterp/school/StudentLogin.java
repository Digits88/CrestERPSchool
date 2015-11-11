/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innate.cresterp.school;

import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.MultipartRequest;
import com.codename1.io.NetworkEvent;
import com.codename1.io.NetworkManager;
import com.codename1.ui.Display;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Image;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.innate.cresterp.homework.Homework;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author user
 */
public class StudentLogin {

    private final List<Homework> studNames = new ArrayList<Homework>();
    private String token;
    private static String URL = "http://localhost/social-network-svr";
    private String username;

    public List<Homework> findLoginDetails() {
        // convert the object to a JSON document
        /*
                studNames.clear();
        NetworkManager networkManager = NetworkManager.getInstance();
        networkManager.start();
        networkManager.addErrorListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                NetworkEvent n = (NetworkEvent) evt;
                n.getError().printStackTrace();
                System.out.println(n.getError());
            }
        });

        ConnectionRequest request;
        request = new ConnectionRequest() {

            int chr;
            StringBuffer sb = new StringBuffer();
            String response = "";

            @Override
            protected void readResponse(InputStream input) throws IOException {

                JSONParser parser = new JSONParser();
                Hashtable hm = parser.parse(new InputStreamReader(input));

                Vector vector = new Vector();
                vector = (Vector) hm.get("homework");

                if (vector.size() > 0) {
                    for (int i = 0; i < vector.size(); i++) {

                        Hashtable h = (Hashtable) vector.get(i);
                        Homework homework = new Homework();
                        homework.setUsername(h.get("username").toString());
                        homework.setPassword(h.get("password").toString());
                        studNames.add(homework);

                    }
                } else {
                    Homework homework = new Homework();
                    homework.setUsername("Sorry...");
                    homework.setDescription("No messages today");
                    studNames.add(homework);
                }
            }

            @Override
            protected void handleException(Exception err) {
                System.err.println(err);
                Homework homework = new Homework();
                homework.setUsername("Ooops...");
                homework.setDescription("Please check your internet connection ");
                studNames.add(homework);

            }

        };

        String URL = "http://localhost/school/verify.php";
        request.setUrl(URL);
        request.setPost(true);

        networkManager.addToQueueAndWait(request);

*/
        return studNames;

                }

        
    private ConnectionRequest sendRequest(Object[] params) throws IOException {
        boolean isMultipart = false;
        int plen = params.length;
        for (int i = 0; i < params.length; i += 2) {
            if (params[i + 1] instanceof Image) {
                isMultipart = true;
                break;
            }
        }
        ConnectionRequest req = isMultipart ? new MultipartRequest() : new ConnectionRequest();
        req.setUrl(URL + "/index.php");
        req.setPost(true);
        req.setHttpMethod("POST");
        req.addArgument("-action", "friends_api");

        for (int i = 0; i < plen; i += 2) {
            if (isMultipart && params[i + 1] instanceof Image) {
                Image img = (Image) params[i + 1];
                EncodedImage enc = null;
                if (img instanceof EncodedImage) {
                    enc = (EncodedImage) img;
                } else {
                    enc = EncodedImage.createFromImage(img, false);
                }
                ((MultipartRequest) req).addData((String) params[i], enc.getImageData(), "image/png");

            } else {
                //req.addArgumentNoEncoding(Util.encodeUrl((String)params[i]), Util.encodeUrl((String)params[i+1]));
                req.addArgumentNoEncoding((String) params[i], (String) params[i + 1]);
            }
        }
        NetworkManager.getInstance().addToQueueAndWait(req);
        return req;
    }

    public void login(String username, String password) throws IOException {
        Map res = getResponse(new String[]{
            "-do", "login",
            "username", username,
            "password", password
        });

        int code = (int) (double) res.get("code");
        if (code != 200) {
            throw new IOException((String) res.get("message"));
        } else {
            token = (String) res.get("token");
            this.username = username;
            if (token == null) {
                throw new IOException("No token received after login");
            }
        }
    }

    private Map getResponse(Object[] params) throws IOException {
        ConnectionRequest req = sendRequest(params);
        boolean result = false;
        if (req.getResponseCode() == 200) {
            System.out.println(new String(req.getResponseData(), "UTF-8"));
            System.out.println(new String(req.getResponseData(), "UTF-8"));
            Map out = new HashMap();
            IOException[] err = new IOException[1];
            Display.getInstance().invokeAndBlock(() -> {
                JSONParser p = new JSONParser();
                try (InputStreamReader r = new InputStreamReader(new ByteArrayInputStream(req.getResponseData()))) {
                    out.putAll(p.parseJSON(r));
                } catch (IOException ex) {
                    System.out.println("Failed to parse JSON ");
                    err[0] = ex;
                }
            });

            if (out != null) {
                return out;
            } else {
                throw err[0];
            }
        } else {
            throw new IOException("Request failed with response " + req.getResponseCode());
        }
    }

    void logout() throws IOException {
        Map res = getResponse(new String[]{
            "-do" , "logout",
            "token", token
        });
        
        int code = (int)(double)res.get("code");
        if (code != 200) {
            throw new IOException((String)res.get("message"));
        } else {
            token = null;
            username = null;
        }
    }
}
