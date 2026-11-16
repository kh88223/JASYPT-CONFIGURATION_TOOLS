package com.company.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.FontUIResource;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

public class JasyptConfigurationTools extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_ALGORITHM = "PBEWithMD5AndDES";
    private static final Color WINDOW_BACKGROUND = new Color(244, 247, 252);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color PRIMARY = new Color(37, 99, 235);
    private static final Color PRIMARY_DARK = new Color(29, 78, 216);
    private static final Color PRIMARY_LIGHT = new Color(235, 242, 255);
    private static final Color TEXT_PRIMARY = new Color(30, 41, 59);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER = new Color(203, 213, 225);
    private static final Color INPUT_BACKGROUND = new Color(248, 250, 252);
    private static final Color SUCCESS_BACKGROUND = new Color(236, 253, 245);
    private static final Color SUCCESS_TEXT = new Color(4, 120, 87);
    private static final Color ERROR_BACKGROUND = new Color(254, 242, 242);
    private static final Color ERROR_TEXT = new Color(185, 28, 28);
    private static final String UI_FONT_NAME = findAvailableFont("Malgun Gothic", "맑은 고딕", "Noto Sans CJK KR", "Dialog");
    private static final Font TITLE_FONT = new Font(UI_FONT_NAME, Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font(UI_FONT_NAME, Font.PLAIN, 13);
    private static final Font SECTION_FONT = new Font(UI_FONT_NAME, Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font(UI_FONT_NAME, Font.BOLD, 14);
    private static final Font NORMAL_FONT = new Font(UI_FONT_NAME, Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font(UI_FONT_NAME, Font.BOLD, 14);
    private static final Font TEXT_AREA_FONT = new Font("Monospaced", Font.PLAIN, 15);
    private final JPasswordField passwordField = new JPasswordField();
    private final JTextField algorithmField = new JTextField(DEFAULT_ALGORITHM);
    private final JTextArea inputTextArea = new JTextArea();
    private final JTextArea resultTextArea = new JTextArea();
    private final JToggleButton encryptModeButton = new JToggleButton("Encrypt");
    private final JToggleButton decryptModeButton = new JToggleButton("Decrypt", true);
    private final JCheckBox showPasswordCheckBox = new JCheckBox("Show password");
    private final JCheckBox addEncWrapperCheckBox = new JCheckBox("암호화 결과에 ENC(...) 추가", true);
    private final JButton executeButton = new JButton("Decrypt");
    private final JButton copyButton = new JButton("Copy Result");
    private final JButton useResultButton = new JButton("Use Result as Input");
    private final JButton clearButton = new JButton("Clear");
    private final JLabel inputTitleLabel = new JLabel("Encrypted Text");
    private final JLabel inputHelpLabel = new JLabel("ENC(...) 또는 ENC 내부의 암호문을 입력하세요.");
    private final JLabel resultTitleLabel = new JLabel("Decrypted Result");
    private final JLabel statusLabel = new JLabel("Decrypt mode selected.");
    private final JPanel statusPanel = new JPanel(new BorderLayout());

    private char defaultPasswordEchoCharacter;

    public JasyptConfigurationTools() {
        configureFrame();
        configureComponents();

        setContentPane(createMainPanel());

        registerEvents();
        updateModeUI();

        setLocationRelativeTo(null);

        SwingUtilities.invokeLater(passwordField::requestFocusInWindow);
    }

    private void configureFrame() {
        setTitle("Jasypt Configuration Tools");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 760);
        setMinimumSize(new Dimension(900, 680));
    }

    private void configureComponents() {
        defaultPasswordEchoCharacter = passwordField.getEchoChar();

        configureTextField(passwordField);
        configureTextField(algorithmField);

        configureTextArea(inputTextArea, true);
        configureTextArea(resultTextArea, false);

        configureModeButton(encryptModeButton);
        configureModeButton(decryptModeButton);

        configurePrimaryButton(executeButton, 145);
        configureSecondaryButton(copyButton, 140);
        configureSecondaryButton(useResultButton, 195);
        configureSecondaryButton(clearButton, 110);

        showPasswordCheckBox.setFont(NORMAL_FONT);
        showPasswordCheckBox.setForeground(TEXT_PRIMARY);
        showPasswordCheckBox.setOpaque(false);

        addEncWrapperCheckBox.setFont(NORMAL_FONT);
        addEncWrapperCheckBox.setForeground(TEXT_PRIMARY);
        addEncWrapperCheckBox.setOpaque(false);

        inputTitleLabel.setFont(SECTION_FONT);
        inputTitleLabel.setForeground(TEXT_PRIMARY);

        inputHelpLabel.setFont(SUBTITLE_FONT);
        inputHelpLabel.setForeground(TEXT_SECONDARY);

        resultTitleLabel.setFont(SECTION_FONT);
        resultTitleLabel.setForeground(TEXT_PRIMARY);

        statusLabel.setFont(NORMAL_FONT);
        statusLabel.setForeground(SUCCESS_TEXT);
        statusLabel.setBorder(new EmptyBorder(8, 12, 8, 12));
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 12));
        mainPanel.setBackground(WINDOW_BACKGROUND);
        mainPanel.setBorder( new EmptyBorder(16, 22, 16, 22));
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridBagLayout());

        headerPanel.setOpaque(false);

        GridBagConstraints constraints = new GridBagConstraints();

        JPanel titlePanel = new JPanel(new GridBagLayout());

        titlePanel.setOpaque(false);

        GridBagConstraints titleConstraints = new GridBagConstraints();

        titleConstraints.gridx = 0;
        titleConstraints.weightx = 1.0;
        titleConstraints.fill =
                GridBagConstraints.HORIZONTAL;

        titleConstraints.anchor =
                GridBagConstraints.WEST;

        titleConstraints.insets =
                new Insets(0, 0, 0, 0);

        JLabel titleLabel =
                new JLabel(
                        "Jasypt Configuration Tools");

        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_PRIMARY);

        titleConstraints.gridy = 0;

        titlePanel.add(
                titleLabel,
                titleConstraints);

        JLabel subtitleLabel =
                new JLabel(
                        "Jasypt 1.9.3 문자열 암호화 및 복호화 도구");

        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(TEXT_SECONDARY);

        /*
         * GridBagLayout에서 남은 가로 공간을 전부 사용하도록 합니다.
         * 고정된 JLabel 최대 너비 때문에 ...이 생기는 문제를 방지합니다.
         */
        titleConstraints.gridy = 1;
        titleConstraints.insets =
                new Insets(3, 0, 0, 0);

        titlePanel.add(
                subtitleLabel,
                titleConstraints);

        /*
         * 왼쪽 제목 영역이 남은 가로 공간을 모두 차지하게 합니다.
         */
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.fill =
                GridBagConstraints.HORIZONTAL;

        constraints.anchor =
                GridBagConstraints.WEST;

        constraints.insets =
                new Insets(0, 0, 0, 20);

        headerPanel.add(
                titlePanel,
                constraints);

        /*
         * 오른쪽 버전 배지
         */
        JLabel versionLabel =
                new JLabel(
                        "JASYPT 1.9.3",
                        SwingConstants.CENTER);

        versionLabel.setFont(BUTTON_FONT);
        versionLabel.setForeground(PRIMARY);
        versionLabel.setOpaque(true);
        versionLabel.setBackground(PRIMARY_LIGHT);

        versionLabel.setBorder(
                new EmptyBorder(
                        8,
                        14,
                        8,
                        14));

        versionLabel.setPreferredSize(
                new Dimension(
                        125,
                        38));

        versionLabel.setMinimumSize(
                new Dimension(
                        125,
                        38));

        versionLabel.setMaximumSize(
                new Dimension(
                        125,
                        38));

        constraints.gridx = 1;
        constraints.weightx = 0.0;
        constraints.fill =
                GridBagConstraints.NONE;

        constraints.anchor =
                GridBagConstraints.EAST;

        constraints.insets =
                new Insets(0, 0, 0, 0);

        headerPanel.add(
                versionLabel,
                constraints);

        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel =
                new JPanel(new BorderLayout(0, 12));

        centerPanel.setOpaque(false);

        centerPanel.add(
                createConfigurationCard(),
                BorderLayout.NORTH);

        centerPanel.add(
                createTextAreasPanel(),
                BorderLayout.CENTER);

        return centerPanel;
    }

    private JPanel createConfigurationCard() {
        JPanel card = createCardPanel();

        card.setLayout(
                new BorderLayout(0, 10));

        JPanel topRow =
                new JPanel(new BorderLayout(15, 0));

        topRow.setOpaque(false);

        JPanel operationPanel =
                new JPanel(new FlowLayout(
                        FlowLayout.LEFT,
                        8,
                        0));

        operationPanel.setOpaque(false);

        JLabel operationLabel =
                new JLabel("Operation");

        operationLabel.setFont(LABEL_FONT);
        operationLabel.setForeground(TEXT_PRIMARY);

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(encryptModeButton);
        modeGroup.add(decryptModeButton);

        operationPanel.add(operationLabel);
        operationPanel.add(Box.createHorizontalStrut(5));
        operationPanel.add(encryptModeButton);
        operationPanel.add(decryptModeButton);

        JPanel optionPanel =
                new JPanel(new FlowLayout(
                        FlowLayout.RIGHT,
                        0,
                        0));

        optionPanel.setOpaque(false);
        optionPanel.add(addEncWrapperCheckBox);

        topRow.add(
                operationPanel,
                BorderLayout.WEST);

        topRow.add(
                optionPanel,
                BorderLayout.EAST);

        card.add(topRow, BorderLayout.NORTH);

        JPanel fieldsPanel =
                new JPanel(new GridBagLayout());

        fieldsPanel.setOpaque(false);

        GridBagConstraints constraints =
                new GridBagConstraints();

        constraints.fill =
                GridBagConstraints.HORIZONTAL;

        constraints.anchor =
                GridBagConstraints.WEST;

        /*
         * Password 행
         */
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.weightx = 0;
        constraints.insets =
                new Insets(3, 0, 4, 12);

        JLabel passwordLabel =
                createFieldLabel(
                        "Encryptor Password");

        passwordLabel.setPreferredSize(
                new Dimension(165, 38));

        fieldsPanel.add(
                passwordLabel,
                constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;

        fieldsPanel.add(
                passwordField,
                constraints);

        constraints.gridx = 2;
        constraints.weightx = 0;
        constraints.insets =
                new Insets(3, 8, 4, 0);

        fieldsPanel.add(
                showPasswordCheckBox,
                constraints);

        /*
         * Algorithm 행
         */
        constraints.gridy = 1;
        constraints.gridx = 0;
        constraints.weightx = 0;
        constraints.insets =
                new Insets(4, 0, 3, 12);

        JLabel algorithmLabel =
                createFieldLabel("Algorithm");

        algorithmLabel.setPreferredSize(
                new Dimension(165, 38));

        fieldsPanel.add(
                algorithmLabel,
                constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;

        fieldsPanel.add(
                algorithmField,
                constraints);

        JButton resetButton =
                new JButton("Reset Default");

        configureSecondaryButton(
                resetButton,
                135);

        resetButton.setPreferredSize(
                new Dimension(135, 38));

        resetButton.setMinimumSize(
                new Dimension(135, 38));

        resetButton.addActionListener(event -> {
            algorithmField.setText(
                    DEFAULT_ALGORITHM);

            setStatus(
                    "Default algorithm restored.",
                    false);
        });

        constraints.gridx = 2;
        constraints.weightx = 0;
        constraints.insets =
                new Insets(4, 8, 3, 0);

        fieldsPanel.add(
                resetButton,
                constraints);

        card.add(
                fieldsPanel,
                BorderLayout.CENTER);

        return card;
    }

    private JPanel createTextAreasPanel() {
        /*
         * GridLayout은 입력창과 결과창에 같은 높이를
         * 자동으로 할당합니다.
         *
         * 상단 설정 영역에는 사용하지 않고,
         * 텍스트 영역 두 개에만 적용합니다.
         */
        JPanel panel =
                new JPanel(new GridLayout(
                        2,
                        1,
                        0,
                        10));

        panel.setOpaque(false);

        panel.add(
                createTextCard(
                        inputTitleLabel,
                        inputHelpLabel,
                        inputTextArea));

        JLabel resultHelpLabel =
                new JLabel(
                        "암호화 또는 복호화 결과가 표시됩니다.");

        resultHelpLabel.setFont(SUBTITLE_FONT);
        resultHelpLabel.setForeground(TEXT_SECONDARY);

        panel.add(
                createTextCard(
                        resultTitleLabel,
                        resultHelpLabel,
                        resultTextArea));

        return panel;
    }

    private JPanel createTextCard(
            JLabel titleLabel,
            JLabel helpLabel,
            JTextArea textArea) {

        JPanel card = createCardPanel();

        card.setLayout(
                new BorderLayout(0, 6));

        /*
         * 제목과 설명을 가로 한 줄에 배치하여
         * 세로 공간을 절약합니다.
         */
        JPanel headerPanel =
                new JPanel(new BorderLayout(15, 0));

        headerPanel.setOpaque(false);

        headerPanel.add(
                titleLabel,
                BorderLayout.WEST);

        headerPanel.add(
                helpLabel,
                BorderLayout.CENTER);

        JScrollPane scrollPane =
                new JScrollPane(textArea);

        scrollPane.setBorder(
                new LineBorder(BORDER, 1, true));

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(18);

        card.add(
                headerPanel,
                BorderLayout.NORTH);

        card.add(
                scrollPane,
                BorderLayout.CENTER);

        return card;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel =
                new JPanel(new BorderLayout(0, 9));

        footerPanel.setOpaque(false);

        JPanel buttonPanel =
                new JPanel(new FlowLayout(
                        FlowLayout.CENTER,
                        10,
                        0));

        buttonPanel.setOpaque(false);

        buttonPanel.add(executeButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(useResultButton);
        buttonPanel.add(clearButton);

        statusPanel.setOpaque(true);
        statusPanel.setBackground(
                SUCCESS_BACKGROUND);

        statusPanel.setBorder(
                new LineBorder(
                        new Color(167, 243, 208),
                        1,
                        true));

        statusPanel.add(
                statusLabel,
                BorderLayout.CENTER);

        footerPanel.add(
                buttonPanel,
                BorderLayout.NORTH);

        footerPanel.add(
                statusPanel,
                BorderLayout.SOUTH);

        return footerPanel;
    }

    private JPanel createCardPanel() {
        JPanel card = new JPanel();

        card.setOpaque(true);
        card.setBackground(CARD_BACKGROUND);

        card.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                BORDER,
                                1,
                                true),
                        new EmptyBorder(
                                10,
                                12,
                                10,
                                12)));

        return card;
    }

    private JLabel createFieldLabel(
            String text) {

        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_PRIMARY);

        return label;
    }

    private void configureTextField(
            JTextField textField) {

        textField.setFont(NORMAL_FONT);
        textField.setForeground(TEXT_PRIMARY);
        textField.setBackground(INPUT_BACKGROUND);

        textField.setPreferredSize(
                new Dimension(500, 38));

        textField.setMinimumSize(
                new Dimension(250, 38));

        textField.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                BORDER,
                                1,
                                true),
                        new EmptyBorder(
                                6,
                                10,
                                6,
                                10)));
    }

    private void configureTextArea(
            JTextArea textArea,
            boolean editable) {

        textArea.setEditable(editable);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setTabSize(4);

        textArea.setFont(TEXT_AREA_FONT);
        textArea.setForeground(TEXT_PRIMARY);

        textArea.setBackground(
                editable
                        ? Color.WHITE
                        : INPUT_BACKGROUND);

        textArea.setCaretColor(PRIMARY);

        textArea.setMargin(
                new Insets(10, 12, 10, 12));

        textArea.setRows(5);
        textArea.setColumns(60);
    }

    private void configureModeButton(
            JToggleButton button) {

        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_PRIMARY);
        button.setBackground(Color.WHITE);

        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR));

        /*
         * 충분한 고정 폭을 사용하여
         * Encrypt와 Decrypt가 ...으로 잘리지 않습니다.
         */
        button.setPreferredSize(
                new Dimension(115, 38));

        button.setMinimumSize(
                new Dimension(115, 38));

        button.setMargin(
                new Insets(6, 14, 6, 14));

        button.setBorder(
                new LineBorder(BORDER, 1, true));
    }

    private void configurePrimaryButton(
            JButton button,
            int width) {

        configureCommonButton(button, width);

        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);

        button.setBorder(
                new LineBorder(
                        PRIMARY_DARK,
                        1,
                        true));
    }

    private void configureSecondaryButton(
            JButton button,
            int width) {

        configureCommonButton(button, width);

        button.setForeground(TEXT_PRIMARY);
        button.setBackground(Color.WHITE);

        button.setBorder(
                new LineBorder(BORDER, 1, true));
    }

    private void configureCommonButton(
            JButton button,
            int width) {

        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR));

        button.setPreferredSize(
                new Dimension(width, 42));

        button.setMinimumSize(
                new Dimension(width, 42));

        button.setMargin(
                new Insets(8, 16, 8, 16));
    }

    private void registerEvents() {
        encryptModeButton.addActionListener(
                event -> updateModeUI());

        decryptModeButton.addActionListener(
                event -> updateModeUI());

        showPasswordCheckBox.addActionListener(
                event -> togglePasswordVisibility());

        executeButton.addActionListener(
                event -> executeOperation());

        copyButton.addActionListener(
                event -> copyResult());

        useResultButton.addActionListener(
                event -> useResultAsInput());

        clearButton.addActionListener(
                event -> clearFields());
    }

    private void updateModeUI() {
        boolean encryptMode =
                encryptModeButton.isSelected();

        updateModeButtonAppearance(
                encryptModeButton,
                encryptMode);

        updateModeButtonAppearance(
                decryptModeButton,
                !encryptMode);

        addEncWrapperCheckBox.setEnabled(
                encryptMode);

        if (encryptMode) {
            executeButton.setText("Encrypt");

            inputTitleLabel.setText(
                    "Plain Text");

            inputHelpLabel.setText(
                    "암호화할 원문을 입력하세요.");

            resultTitleLabel.setText(
                    "Encrypted Result");

            setStatus(
                    "Encrypt mode selected.",
                    false);
        } else {
            executeButton.setText("Decrypt");

            inputTitleLabel.setText(
                    "Encrypted Text");

            inputHelpLabel.setText(
                    "ENC(...) 또는 ENC 내부의 암호문을 입력하세요.");

            resultTitleLabel.setText(
                    "Decrypted Result");

            setStatus(
                    "Decrypt mode selected.",
                    false);
        }

        resultTextArea.setText("");

        executeButton.setForeground(Color.WHITE);
        executeButton.setBackground(PRIMARY);
        executeButton.repaint();

        inputTextArea.requestFocusInWindow();
    }

    private void updateModeButtonAppearance(
            JToggleButton button,
            boolean selected) {

        if (selected) {
            button.setBackground(PRIMARY_LIGHT);
            button.setForeground(PRIMARY_DARK);

            button.setBorder(
                    new LineBorder(
                            PRIMARY,
                            2,
                            true));
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(TEXT_PRIMARY);

            button.setBorder(
                    new LineBorder(
                            BORDER,
                            1,
                            true));
        }
    }

    private void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            passwordField.setEchoChar((char) 0);
        } else {
            passwordField.setEchoChar(
                    defaultPasswordEchoCharacter);
        }
    }

    private void executeOperation() {
        boolean encryptMode =
                encryptModeButton.isSelected();

        char[] passwordCharacters =
                passwordField.getPassword();

        String password =
                new String(passwordCharacters);

        Arrays.fill(
                passwordCharacters,
                '\0');

        String algorithm =
                algorithmField.getText().trim();

        String input =
                inputTextArea.getText().trim();

        resultTextArea.setText("");

        if (password.isBlank()) {
            showWarning(
                    "Encryptor Password를 입력하세요.");

            passwordField.requestFocusInWindow();
            return;
        }

        if (algorithm.isBlank()) {
            showWarning(
                    "Algorithm을 입력하세요.");

            algorithmField.requestFocusInWindow();
            return;
        }

        if (input.isBlank()) {
            showWarning(
                    encryptMode
                            ? "암호화할 원문을 입력하세요."
                            : "복호화할 암호문을 입력하세요.");

            inputTextArea.requestFocusInWindow();
            return;
        }

        try {
            setExecuteButtonBusy(true);

            StandardPBEStringEncryptor encryptor =
                    new StandardPBEStringEncryptor();

            encryptor.setPassword(password);
            encryptor.setAlgorithm(algorithm);

            if (encryptMode) {
                String encryptedText =
                        encryptor.encrypt(input);

                if (addEncWrapperCheckBox
                        .isSelected()) {

                    encryptedText =
                            "ENC("
                            + encryptedText
                            + ")";
                }

                resultTextArea.setText(
                        encryptedText);

                setStatus(
                        "Encryption completed successfully.",
                        false);
            } else {
                String normalizedText =
                        removeEncWrapper(input);

                String decryptedText =
                        encryptor.decrypt(
                                normalizedText);

                resultTextArea.setText(
                        decryptedText);

                setStatus(
                        "Decryption completed successfully.",
                        false);
            }

            resultTextArea.setCaretPosition(0);

        } catch (
                EncryptionOperationNotPossibleException
                        exception) {

            resultTextArea.setText(
                    "처리에 실패했습니다.\n\n"
                    + "다음 내용을 확인하세요.\n"
                    + "1. Encryptor Password\n"
                    + "2. Algorithm\n"
                    + "3. ENC(...) 암호문\n"
                    + "4. Salt 및 Iteration 설정");

            setStatus(
                    "Operation failed: "
                    + "configuration mismatch.",
                    true);

        } catch (Exception exception) {
            resultTextArea.setText(
                    "오류가 발생했습니다.\n\n"
                    + exception.getClass().getName()
                    + "\n\n"
                    + String.valueOf(
                            exception.getMessage()));

            setStatus(
                    "Operation failed.",
                    true);

        } finally {
            password = null;
            setExecuteButtonBusy(false);
        }
    }

    private void setExecuteButtonBusy(
            boolean busy) {

        executeButton.setEnabled(!busy);

        if (busy) {
            executeButton.setText(
                    encryptModeButton.isSelected()
                            ? "Encrypting..."
                            : "Decrypting...");

            executeButton.setBackground(
                    new Color(147, 197, 253));

            executeButton.setForeground(Color.WHITE);
        } else {
            executeButton.setText(
                    encryptModeButton.isSelected()
                            ? "Encrypt"
                            : "Decrypt");

            executeButton.setBackground(PRIMARY);
            executeButton.setForeground(Color.WHITE);
        }

        executeButton.repaint();
    }

    private String removeEncWrapper(
            String input) {

        String value = input.trim();

        if (value.startsWith("ENC(")
                && value.endsWith(")")) {

            return value.substring(
                    4,
                    value.length() - 1).trim();
        }

        return value;
    }

    private void copyResult() {
        String result =
                resultTextArea.getText();

        if (result.isBlank()) {
            showWarning(
                    "복사할 결과가 없습니다.");
            return;
        }

        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(
                        new StringSelection(result),
                        null);

        setStatus(
                "Result copied to clipboard.",
                false);
    }

    private void useResultAsInput() {
        String result =
                resultTextArea.getText();

        if (result.isBlank()) {
            showWarning(
                    "입력으로 사용할 결과가 없습니다.");
            return;
        }

        inputTextArea.setText(result);
        resultTextArea.setText("");

        if (encryptModeButton.isSelected()) {
            decryptModeButton.setSelected(true);
        } else {
            encryptModeButton.setSelected(true);
        }

        updateModeUI();

        setStatus(
                "Result moved to input. "
                + "Operation mode changed.",
                false);
    }

    private void clearFields() {
        passwordField.setText("");
        inputTextArea.setText("");
        resultTextArea.setText("");

        setStatus(
                "All values cleared.",
                false);

        passwordField.requestFocusInWindow();
    }

    private void setStatus(
            String message,
            boolean error) {

        statusLabel.setText(message);

        if (error) {
            statusLabel.setForeground(ERROR_TEXT);
            statusPanel.setBackground(
                    ERROR_BACKGROUND);

            statusPanel.setBorder(
                    new LineBorder(
                            new Color(254, 202, 202),
                            1,
                            true));
        } else {
            statusLabel.setForeground(SUCCESS_TEXT);
            statusPanel.setBackground(
                    SUCCESS_BACKGROUND);

            statusPanel.setBorder(
                    new LineBorder(
                            new Color(167, 243, 208),
                            1,
                            true));
        }

        statusPanel.repaint();
    }

    private void showWarning(
            String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Input Required",
                JOptionPane.WARNING_MESSAGE);
    }

    private static String findAvailableFont(
            String... fontNames) {

        String[] availableFonts =
                GraphicsEnvironment
                        .getLocalGraphicsEnvironment()
                        .getAvailableFontFamilyNames();

        for (String requestedFont : fontNames) {
            for (String availableFont :
                    availableFonts) {

                if (availableFont.equalsIgnoreCase(
                        requestedFont)) {

                    return availableFont;
                }
            }
        }

        return Font.DIALOG;
    }

    private static void configureGlobalUI() {
        FontUIResource normalFont =
                new FontUIResource(
                        UI_FONT_NAME,
                        Font.PLAIN,
                        14);

        FontUIResource buttonFont =
                new FontUIResource(
                        UI_FONT_NAME,
                        Font.BOLD,
                        14);

        UIManager.put("Label.font", normalFont);
        UIManager.put("Button.font", buttonFont);
        UIManager.put("ToggleButton.font", buttonFont);
        UIManager.put("CheckBox.font", normalFont);
        UIManager.put("TextField.font", normalFont);
        UIManager.put("PasswordField.font", normalFont);
        UIManager.put(
                "OptionPane.messageFont",
                normalFont);
        UIManager.put(
                "OptionPane.buttonFont",
                buttonFont);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager
                                .getCrossPlatformLookAndFeelClassName());
            } catch (Exception exception) {
                // 기본 Look and Feel을 사용합니다.
            }

            configureGlobalUI();

            JasyptConfigurationTools application =
                    new JasyptConfigurationTools();

            application.setVisible(true);
        });
    }
}