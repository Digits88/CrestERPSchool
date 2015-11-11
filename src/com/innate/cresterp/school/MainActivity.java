package com.innate.cresterp.school;

import com.codename1.capture.Capture;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.Log;
import com.codename1.ui.BrowserComponent;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.ComponentGroup;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.Tabs;
import com.codename1.ui.TextField;
import com.codename1.ui.URLImage;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.MultiList;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.innate.cresterp.homework.Homework;
import com.innate.cresterp.homework.HomeworkService;
import com.innate.cresterp.notifications.Notifications;
import com.innate.cresterp.notifications.NotificationsService;
import com.innate.cresterp.payments.Payments;
import com.innate.cresterp.payments.PaymentsService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

public class MainActivity {

    Homework homework = new Homework();
    HomeworkService homeworkService = new HomeworkService();
    java.util.List<Homework> homeworkDetails = new ArrayList();

    Notifications notifications = new Notifications();
    NotificationsService notificationsService = new NotificationsService();
    java.util.List<Notifications> notificationDetails = new ArrayList();

    Payments payments = new Payments();
    PaymentsService paymentsService = new PaymentsService();
    java.util.List<Payments> paymentDetails = new ArrayList();

    StudentLogin student = new StudentLogin();

    private Form current;
    private Resources theme;

    public void init(Object context) {
        theme = UIManager.initFirstTheme("/theme");

        // Pro users - uncomment this code to get crash reports sent to you automatically
        /*Display.getInstance().addEdtErrorHandler(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
         evt.consume();
         Log.p("Exception in AppName version " + Display.getInstance().getProperty("AppVersion", "Unknown"));
         Log.p("OS " + Display.getInstance().getPlatformName());
         Log.p("Error " + evt.getSource());
         Log.p("Current Form " + Display.getInstance().getCurrent().getName());
         Log.e((Throwable)evt.getSource());
         Log.sendLog();
         }
         });*/
    }

    public void start() {
        if (current != null) {
            current.show();
            return;
        }
        //show the login method of the app
        showLoginForm();
    }

    public void showLoginForm() {
        Form f = new Form("Login");

        Container padding = new Container();
        Style s = new Style();
        s.setPadding(0, 15, 5, 5);
        s.setPaddingUnit(new byte[]{
            Style.UNIT_TYPE_DIPS,
            Style.UNIT_TYPE_DIPS,
            Style.UNIT_TYPE_DIPS,
            Style.UNIT_TYPE_DIPS
        });

        padding.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        padding.addComponent(new Label("Username"));
        TextField usernameField = new TextField();
        TextField passwordField = new TextField();
        passwordField.setConstraint(TextField.PASSWORD);
        padding.addComponent(usernameField);
        padding.addComponent(new Label("Password"));
        padding.addComponent(passwordField);

        Button loginButton = new Button("Login");
        loginButton.addActionListener((e) -> {
            try {
                student.login(usernameField.getText(), passwordField.getText());

            } catch (Exception ex) {
                Dialog.show("Login Failed", ex.getMessage(), "OK", "Cancel");
                return;
            }

            try {
                showStudentInfo();
            } catch (Exception ex) {
                Log.e(ex);
                Dialog.show("Friend Lookup Failed", ex.getMessage(), "OK", "Cancel");
                return;
            }
        });

        padding.addComponent(loginButton);
        f.setLayout(new BorderLayout());
        f.addComponent(BorderLayout.CENTER, padding);
        f.show();
    }

    private void addMenu(Form f) {
        /*
         f.addCommand(new Command("Profile") {

         @Override
           
         public void actionPerformed(ActionEvent evt) {
         showProfile();
         }
                
         });
         f.addCommand(new Command("News") {

         @Override
         public void actionPerformed(ActionEvent evt) {
         showFeed();
         }
            
         });
        
         f.addCommand(new Command("My Friends") {

         @Override
         public void actionPerformed(ActionEvent evt) {
         showFriends();
         }
            
         });
        
         f.addCommand(new Command("Pending Requests") {

         @Override
         public void actionPerformed(ActionEvent evt) {
         showFriendRequests();
         }
            
         });
        
         f.addCommand(new Command("Invite Friends") {

         @Override
         public void actionPerformed(ActionEvent evt) {
         showSendFriendRequestForm();
         }
            
         });
         */
        f.addCommand(new Command("Logout") {

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    student.logout();
                    showLoginForm();
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            }

        });

    }

    public void showStudentInfo() {
        try {
            Form form1 = new Form("Welcome");

            form1.setLayout(new BorderLayout());
            addMenu(form1);

            Tabs t = new Tabs();
            t.addTab("Homework", showHomework());
        //t.addTab("Payment", showPayments());
            //t.addTab("Notifications", showNotifications());

            form1.addComponent(BorderLayout.CENTER, t);
            form1.revalidate();
            form1.show();

            /*
             Map profile = student.getProfile(username);
             Form f = new Form((String)profile.get("screen_name"));
             addMenu(f);
             f.setLayout(new BorderLayout());
             Container padding = new Container();
             padding.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
             Image avatar = defaultAvatarLarge;
             if (profile.get("avatar") != null) {
             String url = (String)profile.get("avatar");
             avatar = URLImage.createToStorage((EncodedImage)avatar, url+"?large", url, URLImage.RESIZE_SCALE_TO_FILL);
             }
             Button avatarBtn = new Button(avatar);
             padding.addComponent(avatarBtn);
            
             ComponentGroup g = new ComponentGroup();
            
             Button screenName = new Button("Screen name: "+(String)profile.get("screen_name"));
             boolean[] editingInProgress = new boolean[1];
             screenName.addActionListener((evt)->{
             if (editingInProgress[0]) {
             return;
             }
             editingInProgress[0] = true;
             if (!username.equals(student.getUsername())) {
             return;
             }
             TextField tf = new TextField();
             tf.setText((String)profile.get("screen_name"));
             tf.addActionListener((e)->{
             Map updates = new HashMap();
             updates.put("screen_name", tf.getText());
             try {
             student.updateProfile(updates);
             screenName.setText("Screen name: "+tf.getText());
             g.replaceAndWait(tf, screenName, null);
             g.revalidate();
             } catch (Exception ex){
             showError (ex.getMessage());
        
             }
             editingInProgress[0] = false;
             });
             tf.setPreferredSize(screenName.getPreferredSize());
                
             g.replaceAndWait(screenName, tf, null);
             g.revalidate();
             tf.requestFocus();
             });
            
             g.addComponent(screenName);
             padding.addComponent(g);
            
             f.addComponent(BorderLayout.CENTER, padding);
            
             avatarBtn.addActionListener((e) -> {
             String photoPath = Capture.capturePhoto();
             if (photoPath == null) {
                    
             }
             */
                    // User cancelled the photo
  /*
             return;
             }
             if (username == null ? student.getUsername() != null : !username.equals(student.getUsername())) {
             return;
             }
             try {
             Image img = Image.createImage(photoPath);
             if (img.getWidth() > 512 || img.getHeight() > 512) {
             img = img.scaledSmallerRatio(512, 512);
             } else if (img.getWidth() < 512 || img.getHeight() < 512) {
             img = img.scaledLargerRatio(512, 512);
             }
             Image replaceImg = img.scaledSmallerRatio(defaultAvatarLarge.getWidth(), defaultAvatarLarge.getHeight());
             avatarBtn.setIcon(replaceImg);

             Map updates = new HashMap();
             updates.put("username", username);
                    
             EncodedImage encImg = null;
             if (img instanceof EncodedImage) {
             encImg = ((EncodedImage)encImg).scaledEncoded(img.getWidth(), img.getHeight());
             } else {
             encImg = EncodedImage.createFromImage(img, true).scaledEncoded(img.getWidth(), img.getHeight());
             }
                    
             updates.put("avatar", encImg);
             student.updateProfile(updates);
             } catch (Exception ex) {
             Log.e(ex);
             }
             });
             */
        } catch (Exception ex) {
            showError(ex.getMessage());
        }

    }

    private Container showHomework() {
        Container homeworkCn = new Container();

        homeworkCn.setLayout(new BorderLayout());

        MultiList list = new MultiList();
        list.setModel(new DefaultListModel(theHomework()));
        list.addActionListener(e -> {
            homework = work.get(list.getSelectedIndex());
            //displayHomework().show();
        });

        homeworkCn.addComponent(BorderLayout.CENTER, list);

        //homeworkCn.addComponent(f);
        return homeworkCn;
    }

    java.util.List<Homework> work = new ArrayList();

    private Vector theHomework() {

        work = homeworkService.findHomework();

        Vector vec = new Vector();
        for (Homework studentHomework : work) {

            Hashtable h = new Hashtable();

            h.put("Line1", studentHomework.getSubject());
            h.put("Line2", studentHomework.getAssignedDate());

            vec.add(h);
        }
        return vec;
    }

    private Form displayHomework() {
        Form f = new Form();
        f.setLayout(new BorderLayout());
        BrowserComponent web = new BrowserComponent();
        web.setPage(homework.createHTML(), "http://localhost");
        web.setPinchToZoomEnabled(true);
        f.addComponent(BorderLayout.CENTER, web);
        return f;
    }

    private Container showPayments() {
        Container paymentsCn = new Container();

        paymentsCn.setLayout(new BorderLayout());

        MultiList list = new MultiList();
        list.setModel(new DefaultListModel(thePayment()));
        list.addActionListener(e -> {
            payments = payment.get(list.getSelectedIndex());
            //displayPaymentDetails().show();
        });

        paymentsCn.addComponent(BorderLayout.CENTER, list);

        //homeworkCn.addComponent(f);
        return paymentsCn;
    }

    java.util.List<Payments> payment = new ArrayList();

    private Vector thePayment() {

        payment = paymentsService.findPayments();

        Vector vec = new Vector();
        for (Payments studentPayment : payment) {

            Hashtable h = new Hashtable();

            h.put("Line1", studentPayment.getUsername());
            h.put("Line2", studentPayment.getBalance());

            vec.add(h);
        }
        return vec;
    }

    private Form displayPaymentDetails() {
        Form f = new Form();
        f.setLayout(new BorderLayout());
        BrowserComponent web = new BrowserComponent();
        web.setPage(payments.createHTML(), "http://localhost");
        web.setPinchToZoomEnabled(true);
        f.addComponent(BorderLayout.CENTER, web);
        return f;
    }

    private Container showNotifications() {
        Container notificationsCn = new Container();

        notificationsCn.setLayout(new BorderLayout());

        MultiList list = new MultiList();
        list.setModel(new DefaultListModel(theNotification()));
        list.addActionListener(e -> {
            notifications = notification.get(list.getSelectedIndex());
            // displayNotification().show();
        });

        notificationsCn.addComponent(BorderLayout.CENTER, list);

        //homeworkCn.addComponent(f);
        return notificationsCn;
    }

    java.util.List<Notifications> notification = new ArrayList();

    private Vector theNotification() {

        notification = notificationsService.findNotifications();

        Vector vec = new Vector();
        for (Notifications myNotification : notification) {

            Hashtable h = new Hashtable();

            h.put("Line1", myNotification.getUsername());
            h.put("Line2", myNotification.getResults());

            vec.add(h);
        }
        return vec;
    }

    private Form displayNotification() {
        Form f = new Form();
        f.setLayout(new BorderLayout());
        BrowserComponent web = new BrowserComponent();
        web.setPage(notifications.createHTML(), "http://localhost");
        web.setPinchToZoomEnabled(true);
        f.addComponent(BorderLayout.CENTER, web);
        return f;
    }

    public void stop() {
        current = Display.getInstance().getCurrent();
    }

    public void destroy() {
    }

    private void showError(String msg) {
        Dialog.show("Failed", msg, "OK", null);
    }

}
