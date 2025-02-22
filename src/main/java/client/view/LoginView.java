package client.view;

import client.interface_adapter.login.LoginController;
import client.interface_adapter.login.LoginState;
import client.interface_adapter.login.LoginViewModel;
import client.interface_adapter.signup.SignupController;
import client.use_case.high_contrast.HighContrastOutputData;
import client.view.components.buttons.loginSignupButton;
import client.view.components.labels.InputFieldJLabel;
import client.view.components.textfields.CustomJPasswordField;
import client.view.components.textfields.CustomUsernameJTextField;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import utils.InputUtils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static client.view.MainView.messagesColor;

public class LoginView extends JPanel implements ActionListener, PropertyChangeListener {
    public static final String VIEW_NAME = "log in";
    //private JPanel LoginMain;
    JPanel topPanel = new JPanel();
    JPanel buttonPanel = new JPanel();

    InputFieldJLabel enterUsernameLabel = new InputFieldJLabel();
    InputFieldJLabel enterPasswordLabel = new InputFieldJLabel();
    InputFieldJLabel repeatPasswordLabel = new InputFieldJLabel();

    CustomUsernameJTextField usernameField = new CustomUsernameJTextField();
    CustomJPasswordField passwordField = new CustomJPasswordField();
    loginSignupButton signupButton = new loginSignupButton();
    loginSignupButton loginButton = new loginSignupButton();
    loginSignupButton exitButton = new loginSignupButton();
    

//    private final StringBuilder passwordBuilder = new StringBuilder();

    LoginController loginController;

    SignupController signupController;

    LoginViewModel loginViewModel = new LoginViewModel();

    private LoginState loginState;

    private boolean HC = false;

    public LoginView(LoginController controller, SignupController signupController, LoginViewModel loginViewModel, HighContrastOutputData outputData) {

        HC = outputData.getHighContrast();
        this.loginController = controller;
        this.signupController = signupController;
        initComponents(HC);
        
        loginViewModel.addPropertyChangeListener(this);

        usernameField.addKeyListener(new KeyAdapter() {
            private final StringBuilder usernameBuilder = new StringBuilder();
            private boolean deleteFirstChar = false;

            @Override
            public void keyTyped(KeyEvent e) {
                final char typedChar = e.getKeyChar();
                if (!InputUtils.isValidUsernameChar(typedChar)) {
                    if (InputUtils.isBackspace(typedChar)) {
                        if (usernameField.getCaretPosition() != 0 || deleteFirstChar) {
                            usernameBuilder.deleteCharAt(usernameField.getCaretPosition());
                            deleteFirstChar = false;
                        } else {
                            e.consume();
                            return;
                        }
                    } else if (InputUtils.isDelete(typedChar)) {
                        if (usernameField.getCaretPosition() < usernameBuilder.length()) {
                            usernameBuilder.deleteCharAt(usernameField.getCaretPosition());
                        } else {
                            e.consume();
                            return;
                        }
                    } else {
                        e.consume();
                        return;
                    }
                } else if (usernameBuilder.length() == 20) {
                    e.consume();
                    return;
                } else {
                    usernameBuilder.insert(usernameField.getCaretPosition(), typedChar);
                }
                loginState = loginViewModel.getState();
                loginState.setUsername(usernameBuilder.toString());
                loginViewModel.setState(loginState);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                final char typedChar = e.getKeyChar();
                if (typedChar == '\b' && usernameField.getCaretPosition() == 1) {
                    deleteFirstChar = true;
                }
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {
            private final StringBuilder passwordBuilder = new StringBuilder();

            private boolean deleteFirstChar = false;

            @Override
            public void keyTyped(KeyEvent e) {
                final char typedChar = e.getKeyChar();
                if (!InputUtils.isValidPasswordChar(typedChar)) {
                    if (InputUtils.isBackspace(typedChar)) {
                        if (passwordField.getCaretPosition() != 0 || deleteFirstChar) {
                            passwordBuilder.deleteCharAt(passwordField.getCaretPosition());
                            deleteFirstChar = false;
                        } else {
                            e.consume();
                            return;
                        }
                    } else if (InputUtils.isDelete(typedChar)) {
                        if (passwordField.getCaretPosition() < passwordBuilder.length()) {
                            passwordBuilder.deleteCharAt(passwordField.getCaretPosition());
                        } else {
                            e.consume();
                            return;
                        }
                    } else {
                        e.consume();
                        return;
                    }
                } else if (passwordBuilder.length() == 100) {
                    e.consume();
                    return;
                } else {
                    passwordBuilder.insert(passwordField.getCaretPosition(), typedChar);
                }
                loginState = loginViewModel.getState();
                loginState.setPassword(passwordBuilder.toString());
                loginViewModel.setState(loginState);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                final char typedChar = e.getKeyChar();
                if (typedChar == '\b' && passwordField.getCaretPosition() == 1) {
                    deleteFirstChar = true;
                }
            }
        });

        signupButton.addActionListener(e -> {
            controller.executeSignup();
            System.out.println("Sign up");
        });

        loginButton.addActionListener(e -> {
            loginState = loginViewModel.getState();
            controller.execute(loginState.getUsername(), loginState.getPassword());
            System.out.println("Log in");
        });

        exitButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);

            // Process the user's choice
            if (result == JOptionPane.YES_OPTION) {
                //JOptionPane.showMessageDialog(null, "You clicked Yes!");
                System.exit(0);
            }
        });

    }

    private void initComponents(boolean HC) {


        //======== Main Window ========

        Font titleFont = new Font("Helvetica", Font.BOLD, 24);

        setEnabled(true);

        setBorder(new TitledBorder(new LineBorder(new Color(0x111111)), LoginViewModel.TITLE_LABEL, TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, titleFont, new Color(0x111111)));

        setLayout(new GridLayoutManager(7, 2, new Insets(0, 15, 20, 15), -1, -1));

        //---- signupButton----
        signupButton.setEnabled(true);
        signupButton.setHideActionText(false);
        signupButton.setText(LoginViewModel.SIGNUP_BUTTON_LABEL);
        signupButton.setToolTipText(LoginViewModel.SIGNUP_BUTTON_TOOLTIPS);


        //---- loginButton ----
        loginButton.setEnabled(true);
        loginButton.setHideActionText(false);
        loginButton.setText(LoginViewModel.LOGIN_BUTTON_LABEL);
        loginButton.setToolTipText(LoginViewModel.LOGIN_BUTTON_TOOLTIPS);

        //---- exitButton ----
        exitButton.setEnabled(true);
        exitButton.setHideActionText(false);
        exitButton.setText(LoginViewModel.EXIT_BUTTON_LABEL);
        exitButton.setToolTipText(LoginViewModel.EXIT_BUTTON_TOOLTIPS);


        //---- enterUsernameLabel ----
        enterUsernameLabel.setText(LoginViewModel.USERNAME_LABEL);


        //---- enterPasswordLabel ----
        enterPasswordLabel.setText(LoginViewModel.PASSWORD_LABEL);

        //======== topPanel========
        {
            topPanel.setBackground(messagesColor);
            topPanel.setPreferredSize(new Dimension(600, 300));
            topPanel.setLayout(new GridBagLayout());
            topPanel.add(enterUsernameLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            topPanel.add(enterPasswordLabel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            topPanel.add(repeatPasswordLabel, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            topPanel.add(usernameField, new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            topPanel.add(passwordField, new GridBagConstraints(1, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(topPanel, new GridConstraints(1, 1, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(2, 1), null, null));
        //======== buttonPanel ========
        {
            buttonPanel.setBackground(messagesColor);
            buttonPanel.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));

            buttonPanel.add(signupButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));

            buttonPanel.add(loginButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));

            buttonPanel.add(exitButton, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        }
        setBackground(messagesColor);
        add(buttonPanel, new GridConstraints(4, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(2, 1), null, null));
    }


    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        LoginState state = (LoginState) evt.getNewValue();
//        setFields(state);
        if(state.getPasswordError() != null) {
            JOptionPane.showMessageDialog(this, state.getPasswordError());
        }
        else if(state.getUsernameError() != null) {
            JOptionPane.showMessageDialog(this, state.getUsernameError());
        }

    }

//    private void setFields(LoginState state) {
//        usernameField.setText(state.getUsername());
//    }
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}